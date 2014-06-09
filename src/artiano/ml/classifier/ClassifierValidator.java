/**
 * ClassifierValidator.java
 */
package artiano.ml.classifier;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import artiano.core.operation.CSVLoader;
import artiano.core.structure.Attribute;
import artiano.core.structure.Capability;
import artiano.core.structure.NominalAttribute;
import artiano.core.structure.Table;

/**
 * <p>
 * </p>
 * 
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-12-22
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class ClassifierValidator {

	public class ValidateResult {
		/** 全部训练实例数 */
		int totalTrainInstances = 0;
		/** 全部测试集 */
		int totalTest = 0;
		/** 分类未正确实例数 */
		int incorrectNum = 0;
		/** 分类正确数 */
		int correctNum = 0;
		/** 混合矩阵头部描述 */
		String[] classesDescription;
		/** 混合矩阵 */
		int[][] confusionMatrix = null;
		/** 符号实例数 */
		int[] classesCount;
		/** 精度 */
		float precision = 0;
		/** 查准率 */
		HashMap<String, Float> precisionRatios = new HashMap<>();
		/** 查全率 */
		HashMap<String, Float> recallRatios = new HashMap<>();
		/** F-score */
		HashMap<String, Float> fScores = new HashMap<>();
		
		ValidateResult() { }
		
		public void computeResult() {
			// correct number
			for (int i=0; i<confusionMatrix.length; i++)
				correctNum += confusionMatrix[i][i];
			incorrectNum = totalTest - correctNum;
			// precision
			precision = (float)correctNum / (float)totalTest * 100;
			// recall & precision
			for (int i=0; i<confusionMatrix.length; i++) {
				float ratio = 0;
				int count = 0;
				// recall
				for (int j=0; j<confusionMatrix.length; j++)
					count += confusionMatrix[i][j];
				ratio = (float)confusionMatrix[i][i] / (float)count * 100;
				recallRatios.put(classesDescription[i], ratio);
				// precision
				float ratio1 = 0;
				count = 0;
				for (int j=0; j<confusionMatrix.length; j++)
					count += confusionMatrix[j][i];
				ratio1 = (float)confusionMatrix[i][i] / (float)count * 100;
				precisionRatios.put(classesDescription[i], ratio1);
				// f-score
				float f = 2 * ratio * ratio1 / (ratio + ratio1);
				fScores.put(classesDescription[i], f);
			}
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			//builder.append("<html>");
			//builder.append("<ul>");
			builder.append("<li>验证结果");
			builder.append("<pre>");
			builder.append("使用的训练实例数：");
			builder.append("["+totalTrainInstances+"]\n");
			builder.append("测试使用实例数：");
			builder.append("["+totalTest+"]\n");
			builder.append("分类正确实例数：");
			builder.append("["+correctNum+"]\n");
			builder.append("未分类正确实例数：");
			builder.append("["+incorrectNum+"]\n");
			builder.append("测试类别数目：");
			builder.append("</pre>");
			builder.append("<table border='1px' cellspacing='0px'>");
			for (int i = 0; i < classesCount.length; i++) {
				builder.append("<tr>");
				builder.append("<td>"+classesDescription[i]+"</td>");
				builder.append("<td>"+classesCount[i]+"</td>");
				builder.append("</tr>");
			}
			builder.append("</table>");
			// confusion matrix
			builder.append("<li>混合矩阵");
			builder.append("<table border='1' cellspacing='0px'>");
			builder.append("<caption>混合矩阵</caption>");
			// header
			builder.append("<tr>");
			for (int i=0; i<=confusionMatrix.length; i++) {
				if (0 == i)
					builder.append("<td>A\\C</td>");
				else
					builder.append("<td>"+classesDescription[i-1]+"</td>");
			}
			builder.append("</tr>");
			// content
			for (int i=1; i<=confusionMatrix.length; i++) {
				builder.append("<tr>");
				for (int j=0; j<=confusionMatrix.length; j++) {
					if (0 == j) {
						builder.append("<td>"+classesDescription[i-1]+"</td>");
					} else if (i != j && confusionMatrix[i-1][j-1] != 0) {
						builder.append("<td><font color=red>"+confusionMatrix[i-1][j-1]+"</font></td>");
					} else {
						builder.append("<td>"+confusionMatrix[i-1][j-1]+"</td>");
					}
				}
				builder.append("</tr>");
			}
			builder.append("</table>");
			// description
			builder.append("<pre>A = Actual表示实际类别，C = Classified as表示分类为");
			builder.append("</table>");
			// bad or good ?
			DecimalFormat format = new DecimalFormat(".0");
			builder.append("<li>分类评价");
			builder.append("<ul>");
			builder.append("<li>分类精度："+format.format(precision)+"%");
			builder.append("<li>查准率、查全率以及F-score：");
			builder.append("<table border='1' cellspacing='0px'>");
			builder.append("<tr><td>类别</td><td>查准率</td><td>查全率</td><td>F-score</td></tr>");
			ArrayList<String> list = new ArrayList<>(precisionRatios.keySet());
			for (int i=0; i<list.size(); i++) {
				builder.append("<tr>");
				builder.append("<td>"+list.get(i)+"</td>");
				double d = precisionRatios.get(list.get(i));
				builder.append("<td>"+format.format(d)+"</td>");
				d = recallRatios.get(list.get(i));
				builder.append("<td>"+format.format(d)+"</td>");
				d = fScores.get(list.get(i));
				builder.append("<td>"+format.format(d)+"</td>");
				builder.append("</tr>");
			}
			builder.append("</table>");
			builder.append("</ul>");
			builder.append("</ul>");
			builder.append("</html>");
			return builder.toString();
		}
	}

	public interface OnValidateListener {
		
		public void onPreValidate(Classifier classifier, String description);
		
		public void onValidateResult(Classifier classifier, boolean success,
				ValidateResult result, String description);
	}

	private int mCrossValidationCount;
	private double mPercent;
	private Table mDataSet;
	private Table mTrainSet;
	private Table mTestSet;
	private Classifier mClassifier;
	private ValidateResult mResult = new ValidateResult();
	private OnValidateListener mValidateListener;
	private String mPrepareDescription;
	private String mResultDescription;
	private String mFailReason;

	public static final int TEST_USE_TRAINSET = 0;
	public static final int TEST_USE_TESTSET = 1;
	public static final int TEST_RESAMPLE = 2;
	public static final int TEST_CROSS_VALIDATE = 3;
	private int mTestMethod = TEST_USE_TRAINSET;

	public ClassifierValidator(Table data, OnValidateListener listener) {
		mDataSet = data;
		mValidateListener = listener;
	}
	
	public String getFailReason() {
		return mFailReason;
	}
	
	/**
	 * 设置分类器
	 * 
	 * @param classifier
	 *            指定的分类器
	 */
	public void setClassifier(Classifier classifier) {
		mClassifier = classifier;
	}

	/**
	 * 获取验证器中的分类器
	 * 
	 * @return
	 */
	public Classifier getClassifier() {
		return mClassifier;
	}

	/**
	 * 设置训练集
	 * 
	 * @param data
	 */
	public void setTrainset(Table data) {
		mTrainSet = data;
	}

	/**
	 * 设置测试集
	 * 
	 * @param data
	 */
	public void setTestSet(Table data) {
		mTestSet = data;
	}

	/**
	 * 设置测试方法
	 * 
	 * @param method
	 */
	public void setTestMethod(int method) {
		mTestMethod = method;
		
	}
	
	/**
	 * 设置测试集文件路径
	 * @param path
	 * @return
	 */
	public boolean setTestSetPath(String path) {
		try {
			CSVLoader loader = new CSVLoader(path);
			Table data = loader.read();
			if (!data.compatible(mDataSet)) {
				mFailReason = "测试集与训练集不兼容。";
				return false;
			}
			setTrainset(mDataSet);
			setTestSet(data);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			mFailReason = "不能打开文件["+path+"]";
			return false;
		}
	}
	
	public void setSplitPercent(double percent) {
		mPercent = percent;
	}
	
	public void setCrossValidationCount(int count) {
		mCrossValidationCount = count;
	}
	
	private void initWithTestMethod() {
		switch (mTestMethod) {
		case TEST_USE_TRAINSET:
			setTestSet(mDataSet);
			setTrainset(mDataSet);
			break;
		case TEST_RESAMPLE: {
			Table[] tables = mDataSet.resample(mPercent);
			setTrainset(tables[1]);
			setTestSet(tables[0]);
			break;
		}
		default:
			break;
		}
	}
	
	public boolean testWithFail() {
		initWithTestMethod();
		// train-set has been setting ?
		if (null == mTrainSet) {
			mFailReason = "没有设置训练集。";
			return false;
		}
		// test-set has been setting ?
		if (null == mTestSet) {
			mFailReason = "没有设置测试集。";
			return false;
		}
		// classifier has been setting ?
		if (null == mClassifier) {
			mFailReason = "没有设置分类方法。";
			return false;
		}
		// handle train-set ?
		Capability capability = mClassifier.capability();
		if (!capability.handles(mTrainSet)) {
			mFailReason = "["+mClassifier.getClass()+"]不能对表["+mTrainSet.getName()+"]处理。\n";
			mFailReason += capability.failReason();
			return false;
		}
		// train-set & test-set compatible ?
		if (!mTrainSet.compatible(mTestSet)) {
			mFailReason = "训练集和测试集不兼容。";
			return false;
		}
		// class attribute
		if (!mTestSet.hasClass()) {
			mTestSet.setClassAttribute(mTrainSet.classIndex());
		}
		NominalAttribute att = (NominalAttribute) mTrainSet.classAttribute();
		NominalAttribute att1 = (NominalAttribute) mTestSet.classAttribute();
		if (!att.inARange(att1)) {
			mFailReason = "测试集和训练集类属性不在一个取值范围。";
			return false;
		}
		return true;
	}
	
	private void preValidate() {
		StringBuilder builder = new StringBuilder();
		builder.append("<html>");
		// run information
		builder.append("<ul>");
		builder.append("<li>运行信息");
		builder.append("<pre>");
		builder.append("分类方法："+mClassifier.getClass()+"\n");
		builder.append("训练数据集："+mTrainSet.getName()+"\n");
		builder.append("训练实例数："+mTrainSet.rows()+"\n");
		builder.append("属性：["+mTrainSet.columns()+"]");
		builder.append("</pre>");
		builder.append("<table border='0' cellspacing='2px'>");
		for (int i=0; i<mTrainSet.columns(); i++) {
			builder.append("<tr>");
			Attribute att = mTrainSet.attribute(i);
			builder.append("<td>"+att.getName()+"</td>");
			builder.append("<td><i>"+att.getType()+"</i></td>");
			builder.append("</tr>");
		}
		builder.append("</table>");
		builder.append("<pre>类标属性："+mTrainSet.classAttribute().getName()+"</pre>");
		builder.append("<table border='0' cellspacing='2px' caption='类取值'>");
		builder.append("<tr><td>取值</td><td>数目</td></tr>");
		NominalAttribute att = (NominalAttribute) mTrainSet.classAttribute();
		ArrayList<Object> nominals = att.nominals();
		for (int i=0; i<nominals.size(); i++) {
			builder.append("<tr>");
			builder.append("<td>"+nominals.get(i)+"</td>");
			builder.append("<td>"+att.countOfNominal(nominals.get(i))+"</td>");
			builder.append("</tr>");
		}
		builder.append("</table>");
		// test information
		builder.append("<li>测试信息");
		builder.append("<pre>");
		builder.append("测试方法：");
		switch (mTestMethod) {
		case TEST_USE_TESTSET:
			builder.append("使用训练集");
			break;
		case TEST_USE_TRAINSET:
			builder.append("使用测试集");
			break;
		case TEST_RESAMPLE:
			builder.append("重采样");
			break;
		case TEST_CROSS_VALIDATE:
			builder.append("交叉验证");
			break;
		default:
			break;
		}
		builder.append("\n");
		builder.append("测试实例数：["+mTestSet.rows()+"]\n");
		builder.append("</pre>");
		//builder.append("</ul>");
		//builder.append("</html>");
		mPrepareDescription = builder.toString();
	}

	private void validateOnce() {
		preValidate();
		if (null != mValidateListener) {
			mValidateListener.onPreValidate(mClassifier, mPrepareDescription);
		}
		// train
		try {
			boolean success = mClassifier.train(mTrainSet);
			if (!success && null != mValidateListener) {
				mValidateListener.onValidateResult(mClassifier, false, null, "["+mClassifier+"]训练出错。");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (null != mValidateListener)
				mValidateListener.onValidateResult(mClassifier, false, null, e.getMessage());
		}
		// validate
		mResult.totalTrainInstances = mTrainSet.rows();
		mResult.totalTest = mTestSet.rows();
		NominalAttribute predictions = mClassifier.predict(mTestSet);
		NominalAttribute classAttr = (NominalAttribute) mTestSet.classAttribute();
		ArrayList<Object> nominals = classAttr.nominals();
		final int classes = nominals.size();
		// confusion matrix
		mResult.confusionMatrix = new int[classes][classes];
		for (int i=0; i<predictions.size(); i++) {
			Object x = predictions.get(i);
			int c = nominals.indexOf(x);
			Object y = classAttr.get(i);
			int a = nominals.indexOf(y);
			mResult.confusionMatrix[a][c]++;
		}
		// classes description
		mResult.classesDescription = new String[classes];
		for (int i=0; i<classes; i++)
			mResult.classesDescription[i] = nominals.get(i).toString();
		// classes count
		mResult.classesCount = new int[classes];
		for (int i=0; i<classes; i++)
			mResult.classesCount[i] = classAttr.countOfNominal(nominals.get(i));
		// result
		mResult.computeResult();
		mResultDescription = mResult.toString();
		if (null != mValidateListener)
			mValidateListener.onValidateResult(getClassifier(), true, 
					mResult, mPrepareDescription + mResultDescription);
	}
	
	/**
	 * 验证分类器
	 */
	public void validate() {
		if (!testWithFail()) {
			if (null != mValidateListener)
				mValidateListener.onValidateResult(mClassifier, false, null, mFailReason);
			return;
		}
		if (mTestMethod == TEST_CROSS_VALIDATE) {
			crossValidate();
		} else {
			validateOnce();
		}
		return;
	}

	/**
	 * 交叉验证
	 */
	private void crossValidate() {
		
	}

}

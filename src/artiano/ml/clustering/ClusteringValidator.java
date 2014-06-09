/**
 * ClusteringValidator.java
 */
package artiano.ml.clustering;

import java.io.IOException;

import artiano.core.operation.CSVLoader;
import artiano.core.structure.Attribute;
import artiano.core.structure.NominalAttribute;
import artiano.core.structure.Table;
import artiano.ml.clustering.structure.ClusterModel;

/**
 * <p>
 * </p>
 * 
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2014-6-7
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class ClusteringValidator {

	public interface OnValidateListener {

		public void onPreValidate(Clustering clustering, String desc);

		public void onValidateResult(Clustering clustering, boolean success,
				ClusterModel model, String desc);
	}

	public static final int TEST_USE_TRAINSET = 0;
	public static final int TEST_USE_TESTSET = 1;
	public static final int TEST_SPLIT_DATASET = 2;
	public static final int TEST_WITH_CLASS = 3;

	private Clustering mClustering;
	private ClusterModel mClusterModel;
	private OnValidateListener mListener;
	private Table mData;
	private Table mTrainSet, mTestSet;
	private int mTestMethod = TEST_USE_TRAINSET;
	private double mPercent = 0;
	private String mFailReason = "";
	private String mDescription = "";
	private int[] mIngnoredAttrs;
	private int mClassIndex = -1;

	public ClusteringValidator(Table data, OnValidateListener listener) {
		mData = data;
		mTrainSet = data;
		mListener = listener;
	}

	public String getFailReason() {
		return mFailReason;
	}

	public void setClustering(Clustering clustering) {
		mClustering = clustering;
	}

	public void setTestMethod(int method) {
		mTestMethod = method;
	}

	public void setSplitPercent(double percent) {
		mPercent = percent;
	}

	public void setTrainSet(Table data) {
		mTrainSet = data;
	}

	public void setTestSet(Table data) {
		mTestSet = data;
	}

	public void setClassAttr(int i) {
		mClassIndex = i;
	}

	public void setIngnoredAttrs(int[] attrs) {
		mIngnoredAttrs = attrs;
	}

	public boolean setTestPath(String path) {
		try {
			CSVLoader loader = new CSVLoader(path);
			Table data = loader.read();
			if (!data.compatible(mData)) {
				mFailReason = "测试集与训练集不兼容。";
				return false;
			}
			setTrainSet(mData);
			setTestSet(data);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			mFailReason = "不能打开文件[" + path + "]";
			return false;
		}
	}

	private boolean initWithTestMethod() {
		switch (mTestMethod) {
		case TEST_USE_TRAINSET:
			setTrainSet(mData);
			return true;
		case TEST_USE_TESTSET: {
			if (null == mTrainSet) {
				mFailReason = "没有设置测试集。";
				return false;
			}
			return true;
		}
		case TEST_SPLIT_DATASET: {
			if (mPercent < 0 || mPercent > 1) {
				mFailReason = "分割数据集的比率应在范围(0,1)中。";
				return false;
			}
			Table[] tables = mData.resample(mPercent);
			setTrainSet(tables[1]);
			setTestSet(tables[0]);
			return true;
		}
		case TEST_WITH_CLASS: {
			if (-1 == mClassIndex) {
				mFailReason = "没有设置类标属性。";
				return false;
			}
			if (!(mData.attribute(mClassIndex) instanceof NominalAttribute)) {
				mFailReason = "类标属性必须为[NominalAttribute]类型。";
				return false;
			}
			setTrainSet(mData);
			return true;
		}
		default:
			break;
		}
		return false;
	}

	private int[][] generateSplitIndices(int[] mIngnoredAttrs) {
		int[][] indices = new int[2][];
		indices[0] = new int[mData.columns() - mIngnoredAttrs.length];
		indices[1] = new int[mIngnoredAttrs.length];
		int c1 = 0, c2 = 0;
		for (int i = 0; i < mData.columns(); i++) {
			if (isIn(i, mIngnoredAttrs))
				indices[1][c1++] = i;
			else
				indices[0][c2++] = i;
		}
		return indices;
	}

	private boolean isIn(int x, int[] y) {
		for (int i = 0; i < y.length; i++) {
			if (x == y[i])
				return true;
		}
		return false;
	}

	public boolean testWithFail() {
		if (!initWithTestMethod())
			return false;
		if (null == mClustering) {
			mFailReason = "没有设置聚类方法。";
			return false;
		}
		// handle train-set ?
		Table t = mTrainSet;
		if (null != mIngnoredAttrs) {
			int[][] indicesToSplit = generateSplitIndices(mIngnoredAttrs);
			t = t.split(indicesToSplit)[0];
		}
		if (mTestMethod == TEST_WITH_CLASS) {
			if (!(mData.attribute(mClassIndex) instanceof NominalAttribute)) {
				mFailReason = "类标属性必须为[Nominal]类型。";
			}
			int[][] indicesToSplit = generateSplitIndices(new int[] { mClassIndex });
			t = t.split(indicesToSplit)[0];
		}
		if (!mClustering.handleDataSet(t)) {
			mFailReason = "[" + mClustering.getClass() + "]不能对表["
					+ mTrainSet.getName() + "]处理。";
			return false;
		}
		setTrainSet(t);
		// train-set & test-set compatible ?
		if (null != mTestSet) {
			Table t2 = mTestSet;
			if (null != mIngnoredAttrs) {
				int[][] indicesToSplit = generateSplitIndices(mIngnoredAttrs);
				t2 = t2.split(indicesToSplit)[0];
			}
			if (mTestMethod == TEST_WITH_CLASS) {
				int[][] indicesToSplit = generateSplitIndices(new int[] { mClassIndex });
				t2 = t2.split(indicesToSplit)[0];
			}
			if (!t.compatible(t2)) {
				mFailReason = "训练集和测试集不兼容。";
				return false;
			}
			setTestSet(t);
		}
		return true;
	}

	public void validate() {
		if (!testWithFail()) {
			if (null != mListener)
				mListener.onValidateResult(mClustering, false, null,
						mFailReason);
			return;
		}
		preDescription();
		if (null != mListener)
			mListener.onPreValidate(mClustering, mDescription);
		mClusterModel = mClustering.cluster(mTrainSet);
		mDescription += mClusterModel.toString();
		if (null != mListener)
			mListener.onValidateResult(mClustering, true, mClusterModel, mDescription);
	}

	private void preDescription() {
		mDescription = "";
		StringBuilder builder = new StringBuilder();
		builder.append("<html>");
		// run information
		builder.append("<ul>");
		builder.append("<li>运行信息");
		builder.append("<pre>");
		builder.append("聚类方法：" + mClustering.getClass() + "\n");
		builder.append("数据集：" + mData.getName() + "\n");
		int x = mTrainSet.rows();
		if (null != mTestSet)
			x += mTestSet.rows();
		builder.append("实例数：" + x + "\n");
		builder.append("属性：[" + mTrainSet.columns() + "]");
		builder.append("</pre>");
		builder.append("<table border='0' cellspacing='2px'>");
		for (int i = 0; i < mTrainSet.columns(); i++) {
			builder.append("<tr>");
			Attribute att = mTrainSet.attribute(i);
			builder.append("<td>" + att.getName() + "</td>");
			builder.append("<td><i>" + att.getType() + "</i></td>");
			builder.append("</tr>");
		}
		builder.append("</table>");
		// ignored
		if (null != mIngnoredAttrs) {
			builder.append("忽略属性：");
			builder.append("<table border='0' cellspacing='2px'>");
			for (int i = 0; i < mIngnoredAttrs.length; i++) {
				builder.append("<tr>");
				Attribute att = mData.attribute(mIngnoredAttrs[i]);
				builder.append("<td>"+att.getName()+"</td>");
				builder.append("<td><i>"+att.getType()+"</i></td>");
				builder.append("</tr>");
			}
			builder.append("</table>");
		}
		// test information
		builder.append("<li>测试信息");
		builder.append("<pre>");
		builder.append("测试方法：");
		switch (mTestMethod) {
		case TEST_USE_TRAINSET:
			builder.append("使用训练集");
			break;
		case TEST_USE_TESTSET:
			builder.append("使用测试集");
			break;
		case TEST_SPLIT_DATASET:
			builder.append("重采样");
			break;
		case TEST_WITH_CLASS:
			builder.append("设置类标进行聚类评估");
			break;
		default:
			break;
		}
		builder.append("\n");
		builder.append("</pre>");
		// builder.append("</ul>");
		// builder.append("</html>");
		mDescription += builder.toString();
	}

}

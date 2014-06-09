/**
 * NuronClassifier.java
 */
package artiano.ml.classifier;

import java.util.ArrayList;
import java.util.HashMap;

import artiano.core.operation.OptionsHandler;
import artiano.core.structure.Attribute;
import artiano.core.structure.Capability;
import artiano.core.structure.Matrix;
import artiano.core.structure.NominalAttribute;
import artiano.core.structure.NumericAttribute;
import artiano.core.structure.Option;
import artiano.core.structure.Options;
import artiano.core.structure.Table;
import artiano.neural.actfun.ActivationFunction;
import artiano.neural.actfun.Sigmoid;
import artiano.neural.initializer.NguyenWidrow;
import artiano.neural.initializer.WeightsInitializer;
import artiano.neural.learning.StochasticBPLearning;
import artiano.neural.learning.SupervisedNeuralLearning;
import artiano.neural.network.ActivationNetwork;

/**
 * <p>
 * </p>
 * 
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2014-6-2
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class NuronClassifier extends Classifier {
	private static final long serialVersionUID = -457508262944056944L;

	@Override
	public Capability capability() {
		Capability cap = new Capability();
		// disable all
		cap.disableAll();
		// attribute capabilities
		cap.enableAttribute(NumericAttribute.class);
		// missing value in attribute is not allowed
		cap.allowAttributeMissing(false);
		// class capabilities
		cap.enableClass(NominalAttribute.class);
		// missing value in class is not allowed
		cap.allowClassMissing(false);
		// minimum instances
		cap.setMinimumInstances(2);
		return cap;
	}

	private Options options = null;
	@Override
	public String descriptionOfOptions() {
		return "设置神经网络学习参数，以便使用神经网络进行分类学习。";
	}

	private static final String KEY_HIDDEN_LAYERS = "hidden layers";
	private static final String KEY_NURONS_PER_LAYER = "nurons per h-layer";
	private static final String KEY_ACTFUN = "activition fun";
	private static final String KEY_INITIALIZE = "initializer";
	private static final String KEY_MIN_ERROR = "MSE";
	private static final String KEY_MAX_EPOCHS = "max epochs";

	private static final String KEY_LEARNING = "learning method";

	@Override
	public Options supportedOptions() {
		if (null != options)
			return options;
		options = new Options();
		// network
		Option option = new Option(KEY_HIDDEN_LAYERS, "隐藏层层数", Integer.class,
				1, true);
		options.put(KEY_HIDDEN_LAYERS, option);
		option = new Option(KEY_ACTFUN, "激活函数。", ActivationFunction
				.listAllFunctions().toArray(), Sigmoid.class, true);
		options.put(KEY_ACTFUN, option);
		option = new Option(KEY_NURONS_PER_LAYER,
				"每个隐藏层的神经元个数，以[]包裹，例如一个有3层的网络，"
						+ "参数值可以为[2,3,4]，分别表示有2个、3个、和4个神经元", String.class,
				"[5]", false);
		options.put(KEY_NURONS_PER_LAYER, option);
		option = new Option(KEY_INITIALIZE, "网络初始化方法。", WeightsInitializer
				.listAll().toArray(), NguyenWidrow.class, false);
		options.put(KEY_INITIALIZE, option);
		// error & epochs
		option = new Option(KEY_MIN_ERROR, "最小均方误差(min square error)", Double.class, 0.001, false);
		options.put(KEY_MIN_ERROR, option);
		option = new Option(KEY_MAX_EPOCHS, "最多迭代次数。", Integer.class, 500, false);
		options.put(KEY_MAX_EPOCHS, option);
		// learning
		ArrayList<Class<?>> learings = SupervisedNeuralLearning.listAll();
		option = new Option(KEY_LEARNING, "网络学习方法", learings.toArray(),
				StochasticBPLearning.class, false);
		for (int i = 0; i < learings.size(); i++) {
			Class<?> x = learings.get(i);
			OptionsHandler handler = null;
			try {
				handler = (OptionsHandler) x.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
			if (null != handler) {
				Options op = handler.supportedOptions();
				if (null != op)
					option.putOptionsWithValue(learings.get(i), op);
			}
		}
		options.put(KEY_LEARNING, option);
		return options;
	}

	@Override
	public boolean applyOptions(Options options) {
		int hLyaers = (int) options.get(KEY_HIDDEN_LAYERS).value();
		ActivationFunction fun = null;
		WeightsInitializer initializer = null;
		int[] nuronPerLayer = new int[hLyaers];
		try {
			Class<?> x;
			x = (Class<?>) options.get(KEY_ACTFUN).value();
			fun = (ActivationFunction) x.newInstance();
			x = (Class<?>) options.get(KEY_INITIALIZE).value();
			initializer = (WeightsInitializer) x.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
		// layers
		String str = (String) options.get(KEY_NURONS_PER_LAYER).value();
		String[] strs = str.substring(1, str.length() - 1).split("[,]");
		if (strs.length < hLyaers)
			return false;
		for (int i = 0; i < strs.length; i++) {
			try {
				nuronPerLayer[i] = Integer.valueOf(strs[i]);
			} catch (Exception e) {
				return false;
			}
		}
		// epochs & error
		double minError = (double) options.get(KEY_MIN_ERROR).value();
		int maxEpoch = (int) options.get(KEY_MAX_EPOCHS).value();
		// learning method
		Option option = options.get(KEY_LEARNING);
		Class<?> x = (Class<?>) options.get(KEY_LEARNING).value();
		SupervisedNeuralLearning learning = null;
		try {
			learning = (SupervisedNeuralLearning) x.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
		if (!learning.applyOptions(option.getOptionsWithValue(x)))
			return false;
		mFunction = fun;
		mWeightsInitializer = initializer;
		mNeuronsPerHLayer = nuronPerLayer;
		mMinError = minError;
		mMaxEpoch = maxEpoch;
		mLearning = learning;
		return true;
	}

	private ActivationFunction mFunction = new Sigmoid();
	private WeightsInitializer mWeightsInitializer = new NguyenWidrow();
	private int[] mNeuronsPerHLayer = new int[]{ 5 };

	private double mMinError = 0.001;
	private int mMaxEpoch = 500;

	private SupervisedNeuralLearning mLearning;
	private ActivationNetwork mNetwork;
	private HashMap<Object, Matrix> mClassMap = new HashMap<>();
	
	@Override
	public boolean train(Table trainSet) {
		if (!handleDataSet(trainSet))
			return false;
		applyOptions(supportedOptions());
		NominalAttribute attr = (NominalAttribute) trainSet.removeAttribute(trainSet.classIndex());
		Matrix input = trainSet.toMatrix();
		mClassMap.clear();
		Attribute[] attrs = attr.toBinary(mClassMap);
		mNetwork = new ActivationNetwork(input.columns(), attrs.length, mNeuronsPerHLayer, mFunction);
		mWeightsInitializer.initialize(mNetwork);
		mLearning.setNetwork(mNetwork);
		Table t = new Table();
		t.addAttributes(attrs);
		Matrix output = t.toMatrix();
		double e = Double.MAX_VALUE;
		for (int i = 0; (i < mMaxEpoch && e > mMinError); i++) {
			e = mLearning.runEpoch(input, output);
		}
		return true;
	}

	@Override
	public NominalAttribute predict(Table samples) {
		Matrix inputs = null;
		if (samples.classIndex() != -1)
			inputs = samples.toMatrix(new Attribute[]{ samples.classAttribute() });
		else
			inputs = samples.toMatrix();
		NominalAttribute att = new NominalAttribute("@class");
		for (int i = 0; i < inputs.rows(); i++) {
			Matrix out = mNetwork.compute(inputs.row(i));
			Object label = classifyAs(out);
			att.push(label);
		}
		return att;
	}
	
	private Object classifyAs(Matrix out) {
		double dif = Double.MAX_VALUE;
		Object[] keys = mClassMap.keySet().toArray();
		Object label = keys[0];
		for (int i=0; i < keys.length; i++){
			double d = out.difference(mClassMap.get(keys[i]));
			if (dif > d){
				dif = d;
				label = keys[i];
			}
		}
		return label;
	}

	public static void main(String[] args) {
		NuronClassifier classifier = new NuronClassifier();
		Options options = classifier.supportedOptions();
		Option option = options.get(KEY_LEARNING);
		System.out.println(option);
	}

}

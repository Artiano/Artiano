package artiano.ml.classifier;

import java.util.ArrayList;

import artiano.core.operation.CapabilityHandler;
import artiano.core.operation.OptionsHandler;
import artiano.core.operation.Preservable;
import artiano.core.structure.*;

public abstract class Classifier extends Preservable implements
		CapabilityHandler, OptionsHandler {
	private static final long serialVersionUID = 5186515619281612199L;

	public static ArrayList<Class<?>> listAllClassifiers() {
		ArrayList<Class<?>> classifiers = new ArrayList<>();
		classifiers.add(NuronClassifier.class);
		classifiers.add(DTreeClassifier.class);
		classifiers.add(DTreeClassifierUsingC4_5.class);
		classifiers.add(KNearest.class);
		classifiers.add(NaiveBayesClassifier.class);
		classifiers.add(NaiveBayesDiscreteClassifier.class);
		return classifiers;
	}
	
	/**
	 * 训练分类器
	 * 
	 * @param trainSet
	 *            训练集
	 * 
	 * @return 训练是否成功
	 */
	public abstract boolean train(Table trainSet);

	/**
	 * 对指定数据进行分类
	 * 
	 * @param samples
	 *            测试集
	 * @return 测试集的类标
	 */
	public abstract NominalAttribute predict(Table samples);

	@Override
	public boolean handleDataSet(Table data) {
		Capability capability = capability();
		return capability.handles(data);
	}
}

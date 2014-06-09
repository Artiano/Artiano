package artiano.ml.classifier;

import java.util.*;
import java.util.Map.Entry;

import artiano.core.structure.Attribute;
import artiano.core.structure.Capability;
import artiano.core.structure.Matrix;
import artiano.core.structure.NominalAttribute;
import artiano.core.structure.NumericAttribute;
import artiano.core.structure.Options;
import artiano.core.structure.Table;
import artiano.ml.classifier.KDTree.KDNode;

public class KNearest extends Classifier {
	private static final long serialVersionUID = 2277585000325381124L;

	private KDTree kdTree; // kd-tree
	private int k = 2; // 近邻的数目

	public KNearest() {
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	@Override
	public Capability capability() {
		Capability cap = new Capability();
		// disable all
		cap.disableAll();
		// attribute capabilities
		cap.enableAttribute(NumericAttribute.class);
		// missing value in attribute is allowed
		cap.allowAttributeMissing(true);
		// class capabilities
		cap.enableClass(NominalAttribute.class);
		// missing value in class is not allowed
		cap.allowClassMissing(false);
		// minimum instances
		cap.setMinimumInstances(2);
		return cap;
	}

	/**
	 * 训练数据
	 * 
	 * @param trainData
	 *            - 训练集
	 * @param trainLabel
	 *            - 类标
	 * @return - 训练是否成功
	 */
	public boolean train(Table trainData) {
		// 检验待训练的数据是否合法
		Capability capability = capability();
		if (!capability.handles(trainData)) {
			String why = capability.failReason();
			throw new UnsupportedOperationException("[" + getClass() + "]:"
					+ why);
		}
		kdTree = new KDTree(trainData); // 构造KD-Tree
		return true;
	}

	/**
	 * 使用构造的KD树对输入的数据进行分类
	 * 
	 * @param samples
	 *            待分类的数据集
	 * @return 输入数据的类标构成的向量
	 */
	public NominalAttribute predict(Table samples) {
		Matrix sampleMat = samples.toMatrix(new Attribute[] { samples
				.classAttribute() });
		NominalAttribute results = new NominalAttribute("label");
		for (int i = 0; i < samples.rows(); i++) {
			results.push(findKNearestForSingleSample(sampleMat.row(i), k));
		}
		return results;
	}

	/**
	 * 找到数据集的分类
	 * 
	 * @param samples
	 *            - 待分类数据
	 * @return - 数据所划分的类标
	 */
	private Object findKNearestForSingleSample(Matrix samples, int k) {
		List<KDNode> nearestNode = kdTree.findKNearest(samples, k);
		// Count each label
		Map<Object, Integer> eachLabelCount = countEackLabel(nearestNode);
		// 找出最频繁的类标
		Set<Entry<Object, Integer>> entrySet = eachLabelCount.entrySet();
		Object mostFreqLabel = null;
		int maxCount = 0;
		for (Entry<Object, Integer> entry : entrySet) {
			if (maxCount < entry.getValue()) {
				maxCount = entry.getValue();
				mostFreqLabel = entry.getKey();
			}
		}
		return mostFreqLabel;
	}

	/**
	 * 统计每一个类标出现的次数
	 * 
	 * @param nearestNode
	 *            - k-nearest邻居
	 * @return 类标计数
	 */
	private Map<Object, Integer> countEackLabel(List<KDNode> nearestNode) {
		Map<Object, Integer> eachLabelCount = new HashMap<Object, Integer>();
		for (int i = 0; i < nearestNode.size(); i++) {
			KDNode node = nearestNode.get(i);
			Object nodeLabel = node.nodeLabel;
			if (!eachLabelCount.containsKey(nodeLabel)) {
				eachLabelCount.put(nodeLabel, 1);
			} else {
				eachLabelCount
						.put(nodeLabel, eachLabelCount.get(nodeLabel) + 1);
			}
		}
		return eachLabelCount;
	}

	@Override
	public String descriptionOfOptions() {
		return null;
	}

	@Override
	public Options supportedOptions() {
		return null;
	}

	@Override
	public boolean applyOptions(Options options) {
		return true;
	}

}
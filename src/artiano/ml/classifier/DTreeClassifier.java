package artiano.ml.classifier;

import java.io.*;
import java.util.*;

import artiano.core.structure.*;
import artiano.core.structure.Table.TableRow;

/**
 * <p> 决策树分类器(使用ID3算法实现)</p>
 * @author JohnF Nash
 * @version 1.0.0
 * @date 2013-9-2
 * @function 
 * @since 1.0.0
 */
public class DTreeClassifier extends Classifier {
	private static final long serialVersionUID = 1016638292310337476L;
	
	private ArrayList<ArrayList<String>> data = 
			new ArrayList<ArrayList<String>>();   //训练集
	//数据属性列表
	private ArrayList<String> attributeList = new ArrayList<String>(); 	
	private DTreeNode root;		 //决策树的根节点
	
	public DTreeClassifier() {		
	}
			
	/** 决策树分类
	 * @param trainSet 训练集
	 * @param trainLabel 数据类标号
	 * @return 数据训练是否成功
	 */
	public boolean train(Table trainSet, NominalAttribute trainLabel) {		
		//检验待训练的数据是否合法
		try {
			isTrainDataInputedValid(trainSet, trainLabel);
		} catch(Exception e) {			
			return false;
		}
		initialize(trainSet);
		
		//深度复制属性列表
		ArrayList<String> copyOfAttList = new ArrayList<String>(attributeList);
		// 构造决策树
		root = constructDecisionTree(root, data, copyOfAttList, trainLabel);		
		return true; 
	}				
	
	//初始化属性列表及训练集
	private void initialize(Table trainSet) {
		int columns = trainSet.columns();
		/* 初始化属性列表 */
		attributeList = new ArrayList<String>();
		for(int j=0; j<columns; j++) {
			Attribute attr = trainSet.attribute(j);
			String attrName = attr.getName();
			attributeList.add(attrName);			
		}		
		this.data = tableToArrayList(trainSet);		
	}
		
	/**
	 * 预测 
	 * @param samples 待分类数据集
	 * @return 待分类数据的类标号
	 */
	public NominalAttribute predict(Table dataSet) {
		if(dataSet == null) { 
			return null;
		}
		ArrayList<ArrayList<String>> data = tableToArrayList(dataSet);
		NominalAttribute predictionList = new NominalAttribute();				
		for(int i=0; i<data.size(); i++) {
			List<String> singleItem = data.get(i);  //一条数据
			
			DTreeNode current = root;
			int matchNum = 0;		//到目前为止已经匹配的属性数目
			boolean searchComplete = false;
			while(current.nextNodes.size() >= 1) {
				String attribute = current.attribute;							
				int indexOfAttr = this.attributeList.indexOf(attribute);				
				String valueSearched = singleItem.get(indexOfAttr); //Value
				
				/* The discrete value is not exist. */
				ArrayList<ArrayList<String>> attributeValueList = 
					constructAttributeValueList(this.data, this.attributeList);
				if(!attributeValueList.get(indexOfAttr).contains(valueSearched)) {
					break;
				}
								
				searchComplete = false;
				//搜索一个属性的每一个可能值，来匹配待分类数据
				List<DTreeNode> childrenNodes = current.nextNodes;					
				for(int j=0; j<childrenNodes.size(); j++) {
					DTreeNode childNode = childrenNodes.get(j);
					//前一个属性得到匹配
					if(valueSearched.equals(childNode.previousDecision)) {
						//待分类的数据得到完全匹配
						if(childNode.nextNodes == null) { 							
							predictionList.push(childNode.label);
							searchComplete = true;
							break;
						}						
						current = childNode;	//准备匹配下一个属性的值
						matchNum++;												
						break;
					}
				}								
				
				if(searchComplete) {
					break;
				}					
			}
			 
			if(matchNum == attributeList.size() ) {   //匹配完成
				if(! "".equals(current.label)) {					
					predictionList.push(current.label);
				} 		
			} 
		}		
		return predictionList;
	}

	//将 Table类型的数据转化为 ArrayList类型的数据
	private ArrayList<ArrayList<String>> tableToArrayList(Table dataSet) {
		int rows = dataSet.rows();
		int columns = dataSet.columns();
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		/* 初始化数据 */
		for(int i=0; i<rows; i++) {
			ArrayList<String> rowData = new ArrayList<String>();
			TableRow tableRow = dataSet.row(i);
			for(int j=0; j<columns; j++) {
				rowData.add(tableRow.at(j).toString());
			}
			data.add(rowData);
		}
		return data;
	}
	
	
	/**
	 * 构造决策树
	 * @param p 决策树的根节点
	 * @param remainingData 剩下的有待分类的数据
	 * @param remainingAttribute 还未进行匹配的属性
	 * @param remainingTrainLabel 剩下的类标号
	 * @return 构造的决策树的根节点
	 */
	private DTreeNode constructDecisionTree(DTreeNode p, 
			ArrayList<ArrayList<String>> remainingData, 
			ArrayList<String> remainingAttribute,
			NominalAttribute remainingTrainLabel) {		
		if(p == null) {
			p = new DTreeNode();
		}
	
		//剩下的数据的类标号相同，将当前节点的类标号置为剩下的那个类标号，决策树构造完成
		if(allTheSameLabel(remainingTrainLabel)) {
			String label = remainingTrainLabel.get(0).toString();
			p.label = label;
			return p;
		}			
				
		 /* 所有的属性都已经进行匹配，但还没有完成分类，则将剩下的数据中出现次数最多的类标号
		 	作为当前节点的类标号，并且停止构造决策树  */
		if(remainingAttribute.size() == 0) {
			p.label = mostCommonLabel(remainingTrainLabel);
			return p;
		}
		
		/* 根据最大信息增益来决定哪个属性作为分裂属性 */
		int max_index = 
			getMaxInformationGainAttribute(remainingData, remainingAttribute, 
					remainingTrainLabel);
		p.attribute = remainingAttribute.get(max_index);
		// 构造子决策树												
		constructSubTree(p, remainingData,remainingAttribute, remainingTrainLabel);								
		return p;
	}
			
	//构造子决策树
	private void constructSubTree(DTreeNode p,
			ArrayList<ArrayList<String>> remainingData,
			ArrayList<String> remainingAttribute,
			NominalAttribute remainingTrainLabel) {		
		int indexOfAttr = remainingAttribute.indexOf(p.attribute);
		ArrayList<ArrayList<String>> attributeValueList = 
			constructAttributeValueList(remainingData, remainingAttribute);	
		ArrayList<String> attrValues = attributeValueList.get(indexOfAttr);
		
		//更新剩下的属性列表
		remainingAttribute.remove(p.attribute);
		ArrayList<String> newRemainingAttribute = remainingAttribute; 
		
		//属性的每一个值都将作为当前节点的子树
		int attrIndexInOrigin = this.attributeList.indexOf(p.attribute);
		for(int j=0; j<attrValues.size() && j<4; j++) {		
			ArrayList<ArrayList<String>> newRemainingData = 
				getNewRemainingData(remainingData, attrIndexInOrigin, attrValues.get(j));
			NominalAttribute newRemainingTrainLabel = 
				getNewRemainingTrainLabel(remainingData, remainingTrainLabel,
					attrIndexInOrigin,attrValues.get(j));
			
			DTreeNode new_node = new DTreeNode();  //子树的根节点
			new_node.previousDecision = attrValues.get(j);
			if(newRemainingData.size() == 0) {	//这个分支已经没有节点
				new_node.label = mostCommonLabel(remainingTrainLabel);
				if(p.nextNodes == null) {
					p.nextNodes = new ArrayList<DTreeNode>();
				}
				p.nextNodes.add(new_node);    //将子树的根节点加到节点p上
				break;
			} else {				
				constructDecisionTree(new_node, newRemainingData, 
						newRemainingAttribute, newRemainingTrainLabel);    
			}
			
			if(p.nextNodes == null) {
				p.nextNodes = new ArrayList<DTreeNode>();
			}
			p.nextNodes.add(new_node);    //将子树的根节点加到节点p上			
		}
	}

	//更新剩下的类标号
	private NominalAttribute getNewRemainingTrainLabel(
			ArrayList<ArrayList<String>> remainingData,
			NominalAttribute remainingTrainLabel, int indexOfAttr,
			String attrValue) {
		NominalAttribute newRemainingTrainLabel = 
			new NominalAttribute(remainingTrainLabel.getName());				
		for(int i=0; i<remainingData.size(); i++) {
			ArrayList<String> currentItem = remainingData.get(i);
			if(attrValue.equals(currentItem.get(indexOfAttr))) {				
				newRemainingTrainLabel.push(remainingTrainLabel.get(i));
			}				
		}
		return newRemainingTrainLabel;
	}
			
	//更新还未得到匹配的数据集
	private ArrayList<ArrayList<String>> getNewRemainingData(
			ArrayList<ArrayList<String>> remainingData, 
			int indexOfAttr,String attrValue) {
		ArrayList<ArrayList<String>> newRemainingData = 
				new ArrayList<ArrayList<String>>();
		for(int i=0; i<remainingData.size(); i++) {
			ArrayList<String> currentItem = remainingData.get(i);
			if(attrValue.equals(currentItem.get(indexOfAttr))) {
				newRemainingData.add(currentItem);
			}
		}	
		return newRemainingData;
	}
	
	// 得到剩下的属性中拥有最大信息增益的那个属性在属性列表中的下标
	private int getMaxInformationGainAttribute(
			ArrayList<ArrayList<String>> remainingData,
			ArrayList<String> remainingAttribute,
			NominalAttribute remainingTrainLabel) {
		double max_gain = 0;
		int max_index = 0;	//拥有最大信息增益的属性的下标  
		for(int i=0; i<remainingAttribute.size(); i++) {			
			//获取当前属性的信息增益
			double temp_gain = 
				computeInformationGain(remainingData, remainingAttribute.get(i),
						remainingTrainLabel);
			if(max_gain < temp_gain) {
				max_gain = temp_gain;
				max_index = i;
			}
		}
		return max_index;
	}
	
	/**
	 * 计算特定属性的信息增益
	 * @param remainingData 还未被分类的数据集
	 * @param attribute 当前要计算信息增益的属性
	 * @param remainingTrainLabel 剩下的类标号
	 * @return 属性的信息增益
	 */
	private double computeInformationGain(ArrayList<ArrayList<String>> remainingData, 
			String attribute, NominalAttribute remainingTrainLabel) {		
		double inforGain = 0;								
		//Add entropy(S);
		ArrayList<Integer> labelCounts =  
			countAttributeValuesApperances(remainingTrainLabel);
		for(int i=0; i<labelCounts.size(); i++) {
			double temp = labelCounts.get(i) * 1.0 / remainingData.size();
			inforGain += -1 * temp * Math.log10(temp) / Math.log10(2);
		}
		
		//统计该属性的每一个值的出现次数
		int indexOfAttr = attributeList.indexOf(attribute);
		ArrayList<Integer> eachCount =				
			countAttributeValuesApperances(remainingData, indexOfAttr);		
				
		//获取下标为indexOfAttr的属性剩下的值
		ArrayList<String> attrValues = new ArrayList<String>();
		for(int i=0; i<remainingData.size(); i++) {
			ArrayList<String> currentSample = remainingData.get(i);
			if(!attrValues.contains(currentSample.get(indexOfAttr))) {
				attrValues.add(currentSample.get(indexOfAttr));
			}
		}		
		for(int j=0; j<attrValues.size(); j++) {
			double entropy = 
				getEntropy(remainingData, indexOfAttr, attrValues.get(j),
						remainingTrainLabel);			
			inforGain += 
				(-1.0 * eachCount.get(j)) / remainingData.size() * entropy;
		}		
		return inforGain;
	}
	
	/**
	 * 计算属性的一个值的熵
	 * @param remainingData - 还未被分类的数据集
	 * @param attrIndex - 当前需要计算属性值的信息增益的属性在属性列表中的下标
	 * @param attrValue - 当前使用的属性值
	 * @param remainingTrainLabel 余下的类标号
	 * @return 该属性值的信息增益
	 */
	private double getEntropy(ArrayList<ArrayList<String>> remainingData, 
			int attrIndex, String attrValue, NominalAttribute remainingTrainLabel) {
		ArrayList<Integer> labelValueCounts =
			countLabelsForSpecifiedAttrValue(remainingData, attrIndex, 
					attrValue, remainingTrainLabel);		
		
		int attributeValueCount = 0;
		//如果某个类标号的计数为0，则熵为0
		for(int i=0; i<labelValueCounts.size(); i++) {
			attributeValueCount += labelValueCounts.get(i);
			if(labelValueCounts.get(i) == 0) {
				return 0;
			}
		}
		
		/* 计算熵 */		
		double entropy = 0;		//该属性值的熵		
		for(int i=0; i<labelValueCounts.size(); i++) {
			double temp = labelValueCounts.get(i) * 1.0 / attributeValueCount;
			entropy += -1 * temp * Math.log10(temp) / Math.log10(2);
		}		
		return entropy;
	}
	
	/**
	 * 检查数据集相应的类标号是不是全部相同
	 * @param remainingTrainLabel 剩余的类标
	 * @return 数据集的类表是不是完全相同，是则返回true;否则，返回false
	 */
	private boolean allTheSameLabel(NominalAttribute remainingTrainLabel) {
		Object firstLabel = remainingTrainLabel.get(0);
		int size = remainingTrainLabel.size();
		for(int i=1; i<size; i++) {
			Object currentLabel = remainingTrainLabel.get(i);
			if(!firstLabel.equals(currentLabel)) {
				return false;
			}
		}		
		return true;
	}
	
	/**
	 *  搜索数据集中出现最频繁的类标
	 * @param remainingTrainLabel 剩余的类标
	 * @return 数据集中出现最频繁的类标
	 */
	private String mostCommonLabel(NominalAttribute remainingTrainLabel) {
		Map<String, Integer> labelMap = new HashMap<String, Integer>();
		int rows = remainingTrainLabel.size();
		for(int i=0; i<rows; i++) {			
			String label = remainingTrainLabel.get(i).toString();
			if(!labelMap.containsKey(label)) {
				labelMap.put(label, 1);
			} else {
				labelMap.put(label, labelMap.get(label) + 1);
			}
		}

		String comomLabel = "";
		int maxCount = 0;
		for(Map.Entry<String, Integer> entry : labelMap.entrySet()) {
			if(entry.getValue().intValue() > maxCount) {
				maxCount = entry.getValue().intValue();
				comomLabel = entry.getKey();				
			}
		}
		return comomLabel;
	}
	
	//统计属性的每一个值的出现次数
	private ArrayList<Integer> countAttributeValuesApperances(
			NominalAttribute remainingTrainLabel) {
		Map<String, Integer> attrValueCountsMap =
				new HashMap<String, Integer>();
		for(int i=0; i<remainingTrainLabel.size(); i++) {
			String attrValue = remainingTrainLabel.get(i).toString();
			if(!attrValueCountsMap.containsKey(attrValue)) {
				attrValueCountsMap.put(attrValue, 1);
			} else {
				attrValueCountsMap.put(attrValue, attrValueCountsMap.get(attrValue) + 1);
			}
		}	
		ArrayList<Integer> attrValueCounts = 
			new ArrayList<Integer>(attrValueCountsMap.values());
		return attrValueCounts;
	}
	
	//统计属性的每一个值的出现次数
	private ArrayList<Integer> countAttributeValuesApperances(
			ArrayList<ArrayList<String>> remainingData, int indexOfAttr) {
		Map<String, Integer> attrValueCountsMap =
				new LinkedHashMap<String, Integer>();
		for(int i=0; i<remainingData.size(); i++) {
			ArrayList<String> singleData = remainingData.get(i);
			String attrValue = singleData.get(indexOfAttr);
			if(!attrValueCountsMap.containsKey(attrValue)) {
				attrValueCountsMap.put(attrValue, 1);
			} else {
				attrValueCountsMap.put(attrValue, attrValueCountsMap.get(attrValue) + 1);
			}
		}	
		ArrayList<Integer> attrValueCounts = 
			new ArrayList<Integer>(attrValueCountsMap.values());
		return attrValueCounts;
	}

	//统计特定属性取某一特定值是对应的数据集中，各个类标的出现次数
	private ArrayList<Integer> countLabelsForSpecifiedAttrValue(
			ArrayList<ArrayList<String>> remainingData, 
			int indexOfAttr, String attrValue, NominalAttribute trainLabel) {
		Map<String, Integer> attrValueCountsMap =
				new LinkedHashMap<String, Integer>();
		for(int i=0; i<remainingData.size(); i++) {
			ArrayList<String> singleData = remainingData.get(i);
			if(!singleData.get(indexOfAttr).equals(attrValue)) {
				continue;
			}
			
			String label = trainLabel.get(0).toString();
			if(!attrValueCountsMap.containsKey(label)) {
				attrValueCountsMap.put(label, 1);
			} else {
				attrValueCountsMap.put(label, attrValueCountsMap.get(label) + 1);
			}
		}	
		ArrayList<Integer> attrValueCounts = 
			new ArrayList<Integer>(attrValueCountsMap.values());
		return attrValueCounts;		
	}
		
	/**
	 *	检查输入的数据是否合法 
	 * @param trainSet 训练集
	 * @param trainLabel 类标
	 * @throws IllegalArgumentException
	 *      trainData or trainLabel is null,  data in parameter trainingLabel 
	 *      does not match with data in parameter trainingData
	 */
	private void isTrainDataInputedValid(Table trainSet, NominalAttribute trainLabel) {
		if(trainSet == null || trainLabel == null) {
			throw new IllegalArgumentException("Parameter inputed can not be null.");
		}
						
		if(!(trainLabel.size() == trainSet.rows())) {
			throw new IllegalArgumentException("Size of trainLabel does not match"
					+ " that of trainSet.");
		}					
	}
	
	/**
	 * 构造不含有重复的属性列表 attributeValueList
	 * @param data - 数据集
	 * @param attributeList - 属性列表
	 */
	private ArrayList<ArrayList<String>> constructAttributeValueList(
			ArrayList<ArrayList<String>> data, 
			ArrayList<String> attributeList) {
		ArrayList<ArrayList<String>> attributeValueList = 
			new ArrayList<ArrayList<String>>();
		for(int i=0; i<attributeList.size(); i++) {
			attributeValueList.add(new ArrayList<String>());
		}
		
		for(int i=0; i<data.size(); i++) {			
			ArrayList<String> item  = data.get(i);
			for(int j=0; j<attributeList.size(); j++) {
				int attrIndex = this.attributeList.indexOf(attributeList.get(j));
				if(!attributeValueList.get(j).contains(item.get(attrIndex))) {
					attributeValueList.get(j).add(item.get(attrIndex));
				}													
			}
		}		
		return attributeValueList;
	}
		
	/* 决策树节点类 */
	private static class DTreeNode implements Serializable {		
		private static final long serialVersionUID = 1L;
		
		String attribute = "";	  //节点对应的属性
		String previousDecision = "";   //前一个属性决策的值
		String label = "";	//类标(对于叶子节点)		
		ArrayList<DTreeNode> nextNodes;		//子决策树的引用	
	}

}

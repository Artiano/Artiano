package artiano.ml.classifier;
import java.io.*;
import java.util.*;

import artiano.core.operation.Preservable;

/**
 * <p>Description: Decision Tree classifier using C4.5 algorithm.</p>
 * @author JohnF Nash
 * @version 1.0.0
 * @date 2013-10-15
 * @function 
 * @since 1.0.0
 */
public class DTreeClassifierUsingC4_5 extends Preservable{
	private static final long serialVersionUID = 1L;
	
	private ArrayList<ArrayList<String>> data = 
			new ArrayList<ArrayList<String>>();
	private ArrayList<String> attributeList = 
			new ArrayList<String>(); 	// Attribute list	
	private boolean[] isAttributeContinuous;
	private String targetAttribute;  //target attribute
	private DTreeNode root;		 //Root of the decision tree constructed.
	
	/**
	 * Constructor with training data
	 * @param data - training data
	 * @param attributeList - list of attributes
	 * @param targetAttribute - label of data
	 */
	public DTreeClassifierUsingC4_5(ArrayList<ArrayList<String>> data, 
			ArrayList<String> attributeList, String targetAttribute) {
		this.data = data;
		this.attributeList = attributeList;
		this.targetAttribute = targetAttribute;
		isAttributeContinuous = new boolean[attributeList.size()];
		for(int i=0; i<isAttributeContinuous.length; i++) {
			isAttributeContinuous[i] = false;
		}		
	}
	
	/**
	 * Constructor with training data
	 * @param data - training data
	 * @param attributeList - list of attributes
	 * @param targetAttribute - label of data
	 * @param isAttributeContinuous - array of boolean that indicate whether 
	 *             corresponding attribute is continuous or discrete.
	 */
	public DTreeClassifierUsingC4_5(ArrayList<ArrayList<String>> data, 
			ArrayList<String> attributeList, String targetAttribute, 
			boolean[] isAttributeContinuous) {
		this.data = data;
		this.attributeList = attributeList;
		this.targetAttribute = targetAttribute;
		this.isAttributeContinuous = isAttributeContinuous;		
	}
	
	/**
	 * Train decision tree
	 * @return whether the training successes or not
	 */
	public boolean train() {		
		//Check whether the parameters inputed is valid
		try {
			isTrainDataInputedValid();
		} catch(Exception e) {			
			return false;
		}					
		//Construct a decision tree
		root = constructDecisionTree(root, data, attributeList);		
		return true;   //Data training successes.
	}				
		
	/**
	 * Classify data 
	 * @return classifications predicted of data.
	 */
	public List<String> classify(List<List<String>> data) {
		if(data == null) {  //Input empty
			return new ArrayList<String>();
		}
		
		List<String> predictionList = new ArrayList<String>(); //Store predictions
		for(int i=0; i<data.size(); i++) {
			List<String> singleItem = data.get(i);  //A sample
			
			DTreeNode current = root;
			int matchNum = 0;		//Number of attribute matched.
			boolean searchComplete = false;
			while(current.nextNodes.size() >= 1) {
				String attribute = current.attribute;							
				int indexOfAttr = this.attributeList.indexOf(attribute);				
				String valueSearched = singleItem.get(indexOfAttr); //Value
				int attrIndexInOrigin = this.attributeList.indexOf(attribute);
				
				/* The discrete value is not exist. */
				ArrayList<ArrayList<String>> attributeValueList = 
					constructAttributeValueList(this.data, this.attributeList);
				if(!attributeValueList.get(indexOfAttr).contains(valueSearched)
						&& !isAttributeContinuous[attrIndexInOrigin]) {
					break;
				}				
				
				if(!isAttributeContinuous[attrIndexInOrigin]) {
					searchComplete = false;
					//Search each branch of an decision variable to match the sample
					List<DTreeNode> childrenNodes = current.nextNodes;					
					for(int j=0; j<childrenNodes.size(); j++) {
						DTreeNode childNode = childrenNodes.get(j);
						if(valueSearched.equals(childNode.previousDecision)) {  //Previous attribute match
							if(childNode.nextNodes == null) {  //All same label or exactly matched.
								predictionList.add(childNode.label);
								searchComplete = true;
								break;
							}
							
							current = childNode;	//Match with next branch of previous attribute.
							matchNum++;						
							
							break;
						}
					}								
					
					if(searchComplete) {
						break;
					}
					
				} else {
					searchComplete = false;										
					matchNum++;
					
					//Search each branch of an decision variable to match the sample
					List<DTreeNode> childrenNodes = current.nextNodes;
					double middleValue = 
						Double.parseDouble(childrenNodes.get(0).previousDecision);
					double value = Double.parseDouble(valueSearched);
					if(value < middleValue) {			
						current = current.nextNodes.get(0);						
					} else {
						current = current.nextNodes.get(1);											
					}									
					if(current.nextNodes == null) {	
						predictionList.add(current.label);
						break;
					}
				}				
			}
			 
			if(matchNum == attributeList.size() - 1 ) {   //Search complete.				
				predictionList.add(current.label);
			} else {  				
				int attributeIndex = this.attributeList.indexOf(current.attribute);
				if(!searchComplete && !isAttributeContinuous[attributeIndex]) {					
					predictionList.add(null);   //Not exactly matched
				}							
			}
		}
		
		return predictionList;
	}
	
	/**
	 * Build a decision tree
	 * @param p - root of decision tree
	 * @param remainingData - remaining data to be classified.
	 * @param remainingAttribute - remaining to be considered.
	 * @return root of the decision tree to build.
	 */
	private DTreeNode constructDecisionTree(DTreeNode p, 
			ArrayList<ArrayList<String>> remainingData, 
			ArrayList<String> remainingAttribute) {		
		if(p == null) {
			p = new DTreeNode();
		}
		
		/* Check whether all the data has same label. If so, terminate. */		
		ArrayList<ArrayList<String>> attributeValueList = 
				constructAttributeValueList(remainingData, remainingAttribute);
		int targetAttrIndex = remainingAttribute.indexOf(targetAttribute);
		if(targetAttrIndex == -1) {
			return p;
		}
		
		ArrayList<String> targetAttributeValues = 
			attributeValueList.get(targetAttrIndex);		
		for(int i=0; i<targetAttributeValues.size(); i++) {
			String label = targetAttributeValues.get(i);
			if(allTheSameLabel(remainingData, label)) {
				p.label = label;
				return p;
			}
		}		
				
		//All the attributes has been considered ,yet not complete the classification
		if(remainingAttribute.size() == 1) {
			final int indexOfAttr = attributeList.indexOf(p.attribute);
			if(!isAttributeContinuous[indexOfAttr]) {
				p.label = mostCommonLabel(remainingData);
			}				
			return p;
		}
		
		/* Find decision variable by finding the max information gain. */
		int max_index = 
			getMaxGainRatioAttribute(remainingData, remainingAttribute);
		p.attribute = remainingAttribute.get(max_index);
		
		//Create sub tree
		final int indexOfAttr = attributeList.indexOf(p.attribute);		
		if(!isAttributeContinuous[indexOfAttr]) {													
			constructDiscreteAttributeSubTree(p, remainingData,remainingAttribute);			
		} else {  			
			constructContinuousSubTree(p, remainingData, remainingAttribute);
		}						
		return p;
	}
	
	private void constructContinuousSubTree(DTreeNode p,
		ArrayList<ArrayList<String>> remainingData, ArrayList<String> remainingAttribute) {					
						
		ArrayList<ArrayList<String>> copyOfData = 
			new ArrayList<ArrayList<String>>(remainingData);
		final int indexOfAttr = this.attributeList.indexOf(p.attribute);
		Collections.sort(copyOfData, new Comparator<ArrayList<String>>() {
			@Override
			public int compare(ArrayList<String> o1, ArrayList<String> o2) {
				double value1 = Double.parseDouble(o1.get(indexOfAttr));
				double value2 = Double.parseDouble(o2.get(indexOfAttr));
				if(value1 > value2) {
					return 1;
				} else if(value1 == value2) {
					return 0;
				} else {
					return -1;
				}
			}
		});	
				
		int targetAttrIndex = remainingAttribute.indexOf(targetAttribute);
		ArrayList<ArrayList<String>> newAttributeValueList = 
				constructAttributeValueList(remainingData, remainingAttribute);
		Set<String> labelValuesSet = 
			new HashSet<String>(newAttributeValueList.get(targetAttrIndex));
		ArrayList<String> remainingLabelValues = 
			new ArrayList<String>(labelValuesSet);
		
		int maxInfoGainIndex = getMaxAttributeInfoGainIndex(
				copyOfData, remainingLabelValues);										
		if(remainingData.size() == 0) {
			p.label = mostCommonLabel(remainingData);
		}
		
		ArrayList<ArrayList<String>> leftChildData = 
			new ArrayList<ArrayList<String>>();
		for(int i=0; i<=maxInfoGainIndex; i++) {
			leftChildData.add(copyOfData.get(i));
		}			
		
		ArrayList<ArrayList<String>> rightChildData = 
			new ArrayList<ArrayList<String>>();
		for(int i=maxInfoGainIndex+1; i<copyOfData.size(); i++) {
			rightChildData.add(copyOfData.get(i));
		}
						
		double leftData = 
			Double.parseDouble(copyOfData.get(maxInfoGainIndex).get(indexOfAttr));
		double rightData = 
			Double.parseDouble(copyOfData.get(maxInfoGainIndex + 1).get(indexOfAttr));
		double middle = (leftData + rightData) / 2;					
		
		//Update remaining attributes		
		ArrayList<String> newRemainingAttribute = 
			new ArrayList<String>(remainingAttribute);	
		newRemainingAttribute.remove(indexOfAttr);
		
		DTreeNode leftChildNode = new DTreeNode();  //Root of the sub tree
		leftChildNode.previousDecision = middle + "";
		leftChildNode.attribute = p.attribute;
		leftChildNode.label = mostCommonLabel(leftChildData);		
		if(leftChildData.size() == 0) {	//Now has no sample of this branch
			leftChildNode.label = mostCommonLabel(remainingData);
		} else {				
			constructDecisionTree(leftChildNode, leftChildData, newRemainingAttribute);    
		}
		
		DTreeNode rightChildNode = new DTreeNode();  //Root of the sub tree
		rightChildNode.attribute = p.attribute;
		rightChildNode.previousDecision = middle + "";
		rightChildNode.label = mostCommonLabel(rightChildData);
		if(rightChildData.size() == 0) {	//Now has no sample of this branch
			rightChildNode.label = mostCommonLabel(remainingData);
		} else {				
			constructDecisionTree(rightChildNode, rightChildData, newRemainingAttribute);    
		}	
		
		if(p.nextNodes == null) {
			p.nextNodes = new ArrayList<DTreeNode>();
		}
		p.nextNodes.add(leftChildNode);    //Add root of the sub tree to the node
		p.nextNodes.add(rightChildNode);    //Add root of the sub tree to the node			
	}	
	
	private void constructDiscreteAttributeSubTree(DTreeNode p,
			ArrayList<ArrayList<String>> remainingData,
			ArrayList<String> remainingAttribute) {
				
		int indexOfAttr = remainingAttribute.indexOf(p.attribute);
		ArrayList<ArrayList<String>> attributeValueList = 
			constructAttributeValueList(remainingData, remainingAttribute);	
		ArrayList<String> attrValues = attributeValueList.get(indexOfAttr);
		
		//Update remaining attributes		
		ArrayList<String> newRemainingAttribute = 
			new ArrayList<String>(remainingAttribute);
		newRemainingAttribute.remove(p.attribute);		
		
		//Each value of the attribute represents a branch of the decision tree
		int attrIndexInOrigin = this.attributeList.indexOf(p.attribute);
		for(int j=0; j<attrValues.size() && j<4; j++) {		
			ArrayList<ArrayList<String>> newRemainingData = 
				getNewRemainingData(remainingData, attrIndexInOrigin, attrValues.get(j));
			
			DTreeNode new_node = new DTreeNode();  //Root of the sub tree
			new_node.previousDecision = attrValues.get(j);
			if(newRemainingData.size() == 0) {	//Now has no sample of this branch
				new_node.label = mostCommonLabel(remainingData);
				if(p.nextNodes == null) {
					p.nextNodes = new ArrayList<DTreeNode>();
				}
				p.nextNodes.add(new_node);    //Add root of the sub tree to the node
				break;
			} else {				
				constructDecisionTree(new_node, newRemainingData, newRemainingAttribute);    
			}
			
			if(p.nextNodes == null) {
				p.nextNodes = new ArrayList<DTreeNode>();
			}
			p.nextNodes.add(new_node);    //Add root of the sub tree to the node			
		}
	}
	
	private int getMaxAttributeInfoGainIndex(
			ArrayList<ArrayList<String>> copyOfData,
			ArrayList<String> remainingLabelValues) {		
		int maxInfoGainIndex = 0;
		double maxInfoGain = 0;
		 int targetAttrIndex = this.attributeList.indexOf(targetAttribute);
		for(int i=0; i<copyOfData.size()-1; i++) {
			ArrayList<String> leftData = copyOfData.get(i);
			ArrayList<String> rightData = copyOfData.get(i+1);				
			String leftLabel = leftData.get(targetAttrIndex);
			String rightLabel = rightData.get(targetAttrIndex);
			if(leftLabel.equals(rightLabel)) {
				continue;
			}
			
			double leftInfoGain = 0;
			double[] leftEachLabelCount = new double[remainingLabelValues.size()];
			Arrays.fill(leftEachLabelCount, 0);
			for(int j=0; j<=i; j++) {
				ArrayList<String> currentData = copyOfData.get(j);
				int labelValueIndex = 
					remainingLabelValues.indexOf(currentData.get(targetAttrIndex));
				leftEachLabelCount[labelValueIndex]++;
			}
						
			for(int j=0; j<remainingLabelValues.size(); j++) {
				if(leftEachLabelCount[j] == 0) {
					continue;
				}
				double refactor = ((double)leftEachLabelCount[j]) / (i+1);
				leftInfoGain += -1 * refactor * Math.log(refactor) / Math.log(2);
			}
			
			double[] rightEachLabelCount = new double[remainingLabelValues.size()];
			Arrays.fill(rightEachLabelCount, 0);
			for(int j=i+1; j<copyOfData.size(); j++) {
				ArrayList<String> currentData = copyOfData.get(j);
				int labelValueIndex = 
					remainingLabelValues.indexOf(currentData.get(targetAttrIndex));
				rightEachLabelCount[labelValueIndex]++;
			}
			
			double rightInfoGain = 0;
			int rightTotalCount = (copyOfData.size()-i-1);
			for(int j=0; j<remainingLabelValues.size(); j++) {
				if(rightEachLabelCount[j] == 0) {
					continue;
				}
				double refactor = 
					((double)rightEachLabelCount[j]) / rightTotalCount;
				rightInfoGain += -1 * refactor * Math.log(refactor) / Math.log(2);
			}
			
			double infoGain = ((double)(i+1)) / copyOfData.size() * leftInfoGain + 
					((double)rightTotalCount) / copyOfData.size() * rightInfoGain;
			if(infoGain > maxInfoGain) {
				maxInfoGain = infoGain;
				maxInfoGainIndex = i;
			}
		}
		return maxInfoGainIndex;
	}
	
	private ArrayList<ArrayList<String>> getNewRemainingData(
			ArrayList<ArrayList<String>> remainingData, int indexOfAttr,String attrValue) {
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
	
	
	private int getMaxGainRatioAttribute(ArrayList<ArrayList<String>> remainingData,
			ArrayList<String> remainingAttribute) {
		double max_gain = 0;
		int max_index = 0;	//Attribute index where the attribute information gain max  
		for(int i=0; i<remainingAttribute.size(); i++) {
			if(remainingAttribute.get(i).equals(targetAttribute)) {
				continue;
			}
			
			//Get information gain of the attribute
			double temp_gain = 
				computeGainRatio(remainingData, remainingAttribute.get(i));
			if(max_gain < temp_gain) {
				max_gain = temp_gain;
				max_index = i;
			}
		}
		return max_index;
	}

	/**
	 * Compute information gain of a specified attribute.
	 * @param remainingData - remaining data to be classified.
	 * @param attribute - attribute to compute information gain.
	 * ***Attention: @param attrIndex - index of attribute to compute information gain.
	 * @return information gain of a specified attribute.
	 */
	private double computeGainRatio(ArrayList<ArrayList<String>> remainingData, 
			String attribute) {		
		double inforGain = computeInformationGain(remainingData, attribute);										
		double splitInfo = computeSplitInformation(remainingData, attribute);						
		return inforGain / splitInfo;
	}

	private double computeSplitInformation(
			ArrayList<ArrayList<String>> remainingData, String attribute) {
		/* Count each appearances of values of the attribute */
		int indexOfAttr = attributeList.indexOf(attribute);
		ArrayList<Integer> eachCount = 
			countAttributeValuesApperances(remainingData, indexOfAttr);		
				
		 /* Get remaining values of attribute in indexOfAttr
		 * Can not use
		 *   ArrayList<String> attrValues =attributeValueList.get(indexOfAttr)*/
		ArrayList<String> attrValues = new ArrayList<String>();
		for(int i=0; i<remainingData.size(); i++) {
			ArrayList<String> currentSample = remainingData.get(i);
			if(!attrValues.contains(currentSample.get(indexOfAttr))) {
				attrValues.add(currentSample.get(indexOfAttr));
			}
		}
		
		int allCount = remainingData.size();
		double splitInfo = 0;		
		for(int j=0; j<attrValues.size(); j++) {		
			double refactor = ((double)eachCount.get(j)) / allCount;
			splitInfo += -1 * refactor * (Math.log(refactor / Math.log(2.0)));
		}
		return splitInfo;
	}
	
	/**
	 * Compute information gain of a specified attribute.
	 * @param remainingData - remaining data to be classified.
	 * @param attribute - attribute to compute information gain.
	 * ***Attention: @param attrIndex - index of attribute to compute information gain.
	 * @return information gain of a specified attribute.
	 */
	private double computeInformationGain(ArrayList<ArrayList<String>> remainingData, 
			String attribute) {		
		double inforGain = 0;								
		//Add entropy(S);
		int targetIndex = attributeList.indexOf(targetAttribute);
		ArrayList<Integer> labelCounts = 
			countAttributeValuesApperances(remainingData, targetIndex);
		for(int i=0; i<labelCounts.size(); i++) {
			double temp = labelCounts.get(i) * 1.0 / remainingData.size();
			inforGain += -1 * temp * Math.log10(temp) / Math.log10(2);
		}
		
		/* Count each appearances of values of the attribute */
		int indexOfAttr = attributeList.indexOf(attribute);
		ArrayList<Integer> eachCount = 
			countAttributeValuesApperances(remainingData, indexOfAttr);		
				
		 /* Get remaining values of attribute in indexOfAttr
		 * Can not use
		 *   ArrayList<String> attrValues =attributeValueList.get(indexOfAttr)*/
		ArrayList<String> attrValues = new ArrayList<String>();
		for(int i=0; i<remainingData.size(); i++) {
			ArrayList<String> currentSample = remainingData.get(i);
			if(!attrValues.contains(currentSample.get(indexOfAttr))) {
				attrValues.add(currentSample.get(indexOfAttr));
			}
		}
		
		for(int j=0; j<attrValues.size(); j++) {
			double entropy = 
				getEntropy(remainingData, indexOfAttr, attrValues.get(j));			
			inforGain += 
				(-1.0 * eachCount.get(j)) / remainingData.size() * entropy;
		}		
		return inforGain;
	}
	
	/**
	 * Compute entropy of value of an attribute
	 * @param remainingData - remaining data to be classified.
	 * @param attrIndex - index of the attribute to compute entropy.
	 * @param attrValue - value of the attribute
	 * @return entropy of the attribute
	 */
	private double getEntropy(ArrayList<ArrayList<String>> remainingData, 
			int attrIndex, String attrValue) {
		int targetAttrIndex = attributeList.indexOf(targetAttribute);
		ArrayList<Integer> labelValueCounts =
			countAttributeValuesApperances(remainingData, targetAttrIndex);		
		
		int attributeValueCount = 0;
		/*If one label value count is 0, the entropy is 0.   */
		for(int i=0; i<labelValueCounts.size(); i++) {
			attributeValueCount += labelValueCounts.get(i);
			if(labelValueCounts.get(i) == 0) {
				return 0;
			}
		}
		
		/* Compute entropy */		
		double entropy = 0;		//Entropy of value of the attribute		
		for(int i=0; i<labelValueCounts.size(); i++) {
			double temp = labelValueCounts.get(i) * 1.0 / attributeValueCount;
			entropy += -1 * temp * Math.log10(temp) / Math.log10(2);
		}		
		return entropy;
	}
	
	/**
	 * Check whether all the labels in the data is the same.
	 * @param remainingData - remaining data to be classified.
	 * @param label - value that indicate the value of label
	 * @return whether all the label has the same value isYesStr
	 */
	private boolean allTheSameLabel(ArrayList<ArrayList<String>> remainingData, 
			String label) {
		for(int i=0; i<remainingData.size(); i++) {
			ArrayList<String> singleData = remainingData.get(i);   //a single test sample
			int targetAttrIndex = attributeList.indexOf(targetAttribute);
			if(!label.equals(singleData.get(targetAttrIndex))) {
				return false;				
		    } 	
		}		
		return true;
	}
	
	/**
	 *  Find the most common label in training data.
	 * @param remainingData - remaining data to be classified.
	 * @return The most common label in remaining data
	 */
	private String mostCommonLabel(ArrayList<ArrayList<String>> remainingData) {
		int targetAttrIndex = attributeList.indexOf(targetAttribute);
		Map<String, Integer> labelMap = new HashMap<String, Integer>();
		for(int i=0; i<remainingData.size(); i++) {
			ArrayList<String> singleData = remainingData.get(i);
			String label = singleData.get(targetAttrIndex);
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
	
	private ArrayList<Integer> countAttributeValuesApperances(
			ArrayList<ArrayList<String>> remainingData, int indexOfAttr) {
		Map<String, Integer> attrValueCountsMap =
				new HashMap<String, Integer>();
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
		
	/* Check whether the data inputed is valid. */
	private void isTrainDataInputedValid() {		
		//Check whether the parameters inputed is valid		
		if(attributeList.indexOf(targetAttribute) < 0) {
			throw new IllegalArgumentException("Parameter targetAttrIndex out of range.");
		}
		
		if(data == null || attributeList == null) {
			throw new IllegalArgumentException("Parameter inputed can not be null.");
		}
	}
	
	/**
	 * Construct attributeValueList(not repeat) using training data
	 * @param data - training data
	 * @param attributeList - list of attributes
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
				//Attention:
				if(!attributeValueList.get(j).contains(item.get(attrIndex))) {
					attributeValueList.get(j).add(item.get(attrIndex));
				}													
			}
		}		
		return attributeValueList;
	}
	
	/* Inner class for tree node. */
	private static class DTreeNode implements Serializable {		
		private static final long serialVersionUID = 1L;
		
		String attribute = "";	  //Attribute for this node
		String previousDecision = "";   //previous attribute decision
		String label = "";	//Value of target attribute for this node(for leaf nodes)		
		ArrayList<DTreeNode> nextNodes;		//Pointers to next decisions	
	} 
}
package artiano.machinelearning.classifier;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * <p>Description: Decision Tree classifier using ID3 algorithm.</p>
 * @author JohnF Nash
 * @version 1.0.0
 * @date 2013-9-2
 * @function 
 * @since 1.0.0
 */
public class DTreeClassifier {
	private ArrayList<String> attributeList = 
			new ArrayList<String>(); 	//Name of attributes
	private ArrayList<ArrayList<String>> attributeValueList = 
			new ArrayList<ArrayList<String>>(); 	//Value of attributes		
	private int targetAttrIdx = 0; 		//Index of target attribute in attribute list
	private DTreeNode root;		 //Root of the decision tree constructed.
	
	//Default constructor
	public DTreeClassifier() {		
	}
	
	/**
	 * Constructor with training data
	 * @param data - training data
	 * @param attributeList - list of attributes
	 * @param targetAttrIndex - index of target attribute in attributeList
	 */
	public DTreeClassifier(ArrayList<ArrayList<String>> data, 
			ArrayList<String> attributeList, int targetAttrIndex) {
		this.attributeList = attributeList;
		this.targetAttrIdx = targetAttrIndex;
		constructAttributeValueList(data, attributeList);  //Construct attributeValueList
	}
	
	/**
	 * Train decision tree
	 * @param data - data to train
	 * @param attributeList - list for attribute names
	 * @param targetAttrIndex - decision attribute index in attribute list
	 * @return whether the training successes or not
	 */
	public boolean train(ArrayList<ArrayList<String>> data, ArrayList<String> attributeList, 
			int targetAttrIndex) {
		//Check whether the parameters inputed is valid
		try {
			isTrainDataInputedValid(data, attributeList, targetAttrIndex);
		} catch(Exception e) {			
			return false;
		}		
		
		this.attributeList = attributeList;
		this.targetAttrIdx = targetAttrIndex;
		constructAttributeValueList(data, attributeList);  //Construct attributeValueList
				
		//Construct a decision tree
		root = constructDecisionTree(root, data, attributeList);
		
		return true;   //Data training successes.
	}				
	
	/**
	 * Store the decision tree
	 * @param fileName - name of file that stores the decision tree.
	 */
	public void save(String fileName) {
		/* Save the decision tree to an xml file */
		Document xmlDoc = DocumentHelper.createDocument();
		Element rootEle = xmlDoc.addElement("root");
		Element decisonNode = rootEle.addElement("DecisionTree");
		
		/* Broad first search of the tree to save it to the file. */
		Queue<DTreeNode> queue = new LinkedList<DTreeNode>();
		queue.add(root);   //Add root to the queue						
		while(!queue.isEmpty()) {
			DTreeNode node = queue.poll();
			ArrayList<DTreeNode> nextNodes = node.nextNodes;
		    if(nextNodes != null) {
		    	for(int i=0; i<nextNodes.size(); i++) {
			    	queue.add(nextNodes.get(i));
			    }
		    }									
		    
		    /* Write content of tree node to the xml file. */ 
			if(node.parent == root) { //Children nodes of root
				Element parentNode = 
						decisonNode.addElement(node.parent.attribute);
				parentNode.addAttribute("value", node.previouDecision);
				if(! ("".equals(node.attribute))) {
					parentNode.addElement(node.attribute);
				}	
				
				// Leaf node of the tree which contains label
				if(! "".equals(node.label)) {
					parentNode.addText(node.label);
				}
			} else if(node.parent != null) {		    
				String parentNodeName = node.parent.attribute;
								
				/* Search xml nodes to get parent of current node
				 * to add the node to its parent. */
				Queue<Element> eleQueue = new LinkedList<Element>();
				eleQueue.add(decisonNode);
				while(! eleQueue.isEmpty()) {
					Element ele = eleQueue.poll();
					//Get parent node
					if(ele.getName().equals(parentNodeName)) {
						/* For every value of an attribute, there is an 
						 * child node of parent decision node. */						
						if(ele.attributeCount() > 0) {
							//Create an new node for a new value of the attribute.
							ele = ele.getParent().addElement(ele.getName());
						}
						
						if("".equals(node.attribute)) { //The node is value of attribute 
							ele.addAttribute("value", node.previouDecision);
						} else {		//The node is attribute node
							ele.addElement(node.attribute);
						} 		
						
						if(! "".equals(node.label)) { //The node is label node.
							ele.addText(node.label);
						}
																							
						break;
					}
					
					//Get sub trees
					@SuppressWarnings("unchecked")
					List<Element> chidrenNodes = ele.elements();
					for(int i=0; i<chidrenNodes.size(); i++) {
						eleQueue.add(chidrenNodes.get(i));
					}
				}						
			}								   
		}
	   		
		//Save the document to file.
		saveXMLDocuemtnToFile(fileName, xmlDoc);					
	}

	//Save the document to file.
	private void saveXMLDocuemtnToFile(String fileName, Document xmlDoc) {
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
				FileWriter fw = new FileWriter(file);
				OutputFormat format = 
						OutputFormat.createPrettyPrint(); // ������ʽ
				XMLWriter output = new XMLWriter(fw, format);
				output.write(xmlDoc);
				output.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
		
		/* If all the data has the same label, tree building completes.
		 * Now only considering target attribute has only two results. */
		String firstLabel = attributeValueList.get(targetAttrIdx).get(0);
		String secondLabel = attributeValueList.get(targetAttrIdx).get(1);
		if(allTheSameLabel(remainingData, firstLabel)) {
			p.label = firstLabel;
			return p;
		} else if(allTheSameLabel(remainingData, secondLabel)) {
			p.label = secondLabel;
			return p;
		}
		
		//All the attributes has been considered ,yet not complete the classification
		if(remainingAttribute.size() == 0) {			
			p.label = mostCommonLabel(remainingData, null);
			return p;
		}
		
		/* Find decision variable by finding the max information gain. */
		double max_gain = 0;
		int max_index = 0;		//Attribute index where the attribute information gain max  
		for(int i=0; i<remainingAttribute.size(); i++) {
			//Attention: not using index
			if(remainingAttribute.get(i).equals(
					this.attributeList.get(targetAttrIdx))) {
				continue;
			}
			
			//Get information gain of the attribute
			double temp_gain = 
				computeInformationGain(remainingData, remainingAttribute.get(i));
			if(max_gain < temp_gain) {
				max_gain = temp_gain;
				max_index = i;
			}
		}
		p.attribute = remainingAttribute.get(max_index);		
		
		//Update remaining attribute		
		ArrayList<String> newRemainingAttribute = new ArrayList<String>();
		for(int i=0; i<remainingAttribute.size(); i++) {
			String currentAttr = remainingAttribute.get(i);
			if(!p.attribute.equals(currentAttr)) {
				newRemainingAttribute.add(currentAttr);
			}
		}
		
		/* Update remaining data */
		int indexOfAttr = attributeList.indexOf(p.attribute);		
		ArrayList<String> attrValues = 
				attributeValueList.get(indexOfAttr);  
		ArrayList<ArrayList<String>> newRemainingData = 
				new ArrayList<ArrayList<String>>();
		//Each value of the attribute represents a branch of the decision tree
		for(int j=0; j<attrValues.size(); j++) {
			for(int i=0; i<remainingData.size(); i++) {
				ArrayList<String> currentItem = remainingData.get(i);				
				if(attrValues.get(j).equals(currentItem.get(indexOfAttr))) {
					newRemainingData.add(currentItem);
				}
			}									
					
			DTreeNode new_node = new DTreeNode();  //Root of the sub tree
			new_node.previouDecision = attrValues.get(j);
			if(newRemainingData.size() == 0) {	//Now has no sample of this branch
				new_node.label = mostCommonLabel(remainingData, null);
			} else {				
				constructDecisionTree(new_node, newRemainingData, newRemainingAttribute);    
			}
			
			if(p.nextNodes == null) {
				p.nextNodes = new ArrayList<DTreeNode>();
			}
			p.nextNodes.add(new_node);    //Add root of the sub tree to the node
			new_node.parent = p;		//Pointer to its parent node 			
			
			newRemainingData.clear();   //Clear the list to prepare for next sample
		}
		
		return p;
	}			
	
	/**
	 * Check whether all the labels in the data is the same.
	 * @param remainingData - remaining data to be classified.
	 * @param isYesStr - value that indicate the value of label(can only be "yes" or "no")
	 * @return whether all the label has the same value isYesStr
	 */
	private boolean allTheSameLabel(ArrayList<ArrayList<String>> remainingData, 
			String isYesStr) {
		for(int i=0; i<remainingData.size(); i++) {
			ArrayList<String> singleSampleData = remainingData.get(i);   //a single test sample						
			if(!isYesStr.equals(singleSampleData.get(targetAttrIdx))) {
				return false;				
		    } 	
		}		
		return true;
	}
	
	/**
	 *  Find the most common label in training data.
	 * @param remainingData - remaining data to be classified.
	 * @param counts - use when counting labels
	 * @return The most common label in remaining data
	 */
	private String mostCommonLabel(ArrayList<ArrayList<String>> remainingData, 
			int[] counts) {
		String firstTargetAttrValue = 
				remainingData.get(0).get(targetAttrIdx);  //First label value
		String secondTargetAttrValue = "";		//Second label value
		
		//Count occurrences of the two labels
		int count = 1;
		for(int i=1; i<remainingData.size(); i++) {
			String currentTargetAttrValue = remainingData.get(i).get(targetAttrIdx);
			if(firstTargetAttrValue.equals(currentTargetAttrValue) ) {
				count++;
			} else {
				secondTargetAttrValue = currentTargetAttrValue;
			}
		}
		
		//Count tow labels
		if(counts == null) {
			counts = new int[2];
		}
		counts[0] = count;
		counts[1] = remainingData.size() - count;
		
		//Return the most common label
		if(count >= remainingData.size() / 2) {
			return firstTargetAttrValue;
		} else {
			return secondTargetAttrValue;
		}
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
		
		int indexOfAttr = attributeList.indexOf(attribute);
		
		//Add entropy(S);
		int[] labelCounts = new int[2];
		mostCommonLabel(remainingData, labelCounts);
		for(int i=0; i<labelCounts.length; i++) {
			double temp = labelCounts[i] * 1.0 / remainingData.size();
			inforGain += -1 * temp * Math.log10(temp) / Math.log10(2);
		}
					
		/* Count each appearances of values of the attribute */				
		ArrayList<String> valueList = new ArrayList<String>();
		for(int k=0; k<remainingData.size(); k++) {
			valueList.add(remainingData.get(k).get(indexOfAttr));
		}
		ArrayList<Integer> eachCount = 
				getEachValueCount(valueList);
				
		 /* Get remaining values of attribute in indexOfAttr
		 * Can not use
		 *   ArrayList<String> attrValues =attributeValueList.get(indexOfAttr)*/
		ArrayList<String> attrValues = new ArrayList<String>();
		for(int i=0; i<remainingData.size(); i++) {
			ArrayList<String> currentSample = 
					remainingData.get(i);
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
		/* Get remaining training data of which value of attribute 
		 * in attrIndex is attrValue */
		ArrayList<ArrayList<String>> valueItems = 
				new ArrayList<ArrayList<String>>();   //Remaining data that has value on attribute attrIndex
		for(int i=0; i<remainingData.size(); i++) {
			ArrayList<String> aSample = remainingData.get(i);			
			if(attrValue.equals(aSample.get(attrIndex))) {
				valueItems.add(aSample);
			}			
		}
		
		int[] counts = new int[2];		
		mostCommonLabel(valueItems, counts);   //Count labels
		
		/*If all true or all false, the entropy is 0.   */
		if(counts[0] == 0 || counts[1] == 0) {
			return 0;
		}
		
		/* Compute entropy */
		double entropy = 0;		//Entropy of value of the attribute
		for(int i=0; i<counts.length; i++) {
			double temp = counts[i] * 1.0 / valueItems.size();
			entropy += -1 * temp * Math.log10(temp) / Math.log10(2);
		}		
		return entropy;
	}

	/**
	 * Get count of each attribute value
	 * @param singleAttributeValues - values of an attribute 
	 * @return count list of each value of the attribute
	 */
	private ArrayList<Integer> getEachValueCount(ArrayList<String> singleAttributeValues) {
		ArrayList<Integer> valueCounts = new ArrayList<Integer>();
		
		/*  Get attribute values and their counts.
		 * Attention: Use LinkedHashMap instead of HashMap to maintain its sort*/
		Map<String, Integer> eachValueCounts = 
				new LinkedHashMap<String, Integer>(); 
		//Count appearances of each value
		for(int i=0; i<singleAttributeValues.size(); i++) {
			String key = singleAttributeValues.get(i);
			if(eachValueCounts.containsKey(key)) {       //The value already appear
				int previousCount = eachValueCounts.get(key);
				eachValueCounts.put(key, previousCount + 1);  //Update the count
			} else {	//The value appears for the first time
				eachValueCounts.put(key, 1);
			}
		}	
		
		Set<Entry<String, Integer>> entrySet = 
				eachValueCounts.entrySet();
		for(Entry<String, Integer> item : entrySet) {
			valueCounts.add(item.getValue());
		}
		
		return valueCounts;
	}		
	
	
	/* Check whether the data inputed is valid. */
	private void isTrainDataInputedValid(ArrayList<ArrayList<String>> data,
			ArrayList<String> attributeList, int decAttrIdx) {
		//Check whether the parameters inputed is valid
		if(decAttrIdx < 0 || decAttrIdx >= attributeList.size()) {
			throw new IllegalArgumentException("Parameter decAttrIdx out of range.");
		}
		
		if(data == null || attributeList == null) {
			throw new IllegalArgumentException("Parameter inputed can not be null.");
		}
	}
	
	/**
	 * Construct attributeValueList using training data
	 * @param data - training data
	 * @param attributeList - list of attributes
	 */
	private void constructAttributeValueList(ArrayList<ArrayList<String>> data, 
			ArrayList<String> attributeList) {
		attributeValueList.clear();
		for(int i=0; i<attributeList.size(); i++) {
			attributeValueList.add(new ArrayList<String>());
		}
		
		for(int i=0; i<data.size(); i++) {			
			ArrayList<String> item  = data.get(i);			
			for(int j=0; j<item.size(); j++) {				
				if(!attributeValueList.get(j).contains(item.get(j))) {  //Have not been added 
					attributeValueList.get(j).add(item.get(j));					
				}
			}
		}					
	}
	
	/* Inner class for tree node. */
	private static class DTreeNode {
		String attribute = "";	  //Attribute for this node
		String previouDecision = "";   //previous attribute decision
		String label = "";	//Value of target attribute for this node(for leaf nodes)
		DTreeNode parent;	//Pointer to its parent node.
		ArrayList<DTreeNode> nextNodes;		//Pointers to next decisions
				
		public DTreeNode() {
		}				
	} 
}

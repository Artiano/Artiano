package artiano.machinelearning.classifier;

import java.util.*;

import artiano.core.structure.Matrix;
import artiano.core.structure.Range;

/**
 * <p>Description: KD Tree for KNN.</p>
 * @author JohnF Nash
 * @version 1.0.0
 * @date 2013-9-6
 * @function 
 * @since 1.0.0
 */
public class KDTree {
	private KDNode root;  //Root of the kd-tree
	@SuppressWarnings("unused")
	private Matrix dataSet;		//Data set to use.
	
	/* Empty constructor*/
	public KDTree() {		
	}
	
	/* Constructor with data set */
	public KDTree(Matrix dataSet) {
		this.dataSet = dataSet;
	}
		
	/**
	 *  Build a kd-tree.
	 * @param dataSet - data set 
	 * @return root of the decision tree.
	 */
	public KDNode buildKDTree(Matrix dataSet) {
		if(dataSet.rows() < 1) {
			throw new IllegalArgumentException("Empty data set!");
		}		
		this.dataSet = dataSet;
		
		//Initialize the root node
		root = new KDNode(dataSet);		
		expandSubKDTree(root);   //KD Tree expand
		
		return root;
	}
	
	/**
	 * Insert data into kd-tree. 
	 * @param data - data to be inserted into the KD-Tree.
	 * @throws IllegalArgumentException - when data inputed is empty
	 * @return whether inserting successes
	 */
	public boolean insert(Matrix data) {
		if(data.rows() == 0) {
			throw new IllegalArgumentException("Empty data cannot be inserted.");
		}
		
		if(root == null) {
			root = new KDNode(data);
		} else {				
			KDNode parent = null;
			KDNode current = root;
			int featureIndex = current.featureIndex;
			while(current != null) {
				if(current.value >= data.at(featureIndex)) {
					parent = current;
					current = current.left;
				} else {
					parent = current;
					current = current.right;
				}
				
				//Update feature index
				if(! (current == null) ) {
					featureIndex = current.featureIndex;  
				}								
			}
			
			//The node to be inserted.
			KDNode newNode =
					new KDNode(featureIndex, data.at(featureIndex), data);
			if(parent.value > data.at(featureIndex)) {
				parent.left = newNode;
			} else if(parent.value == data.at(featureIndex)) {
				return false; //Node with same data has been inserted before.
			} else {
				parent.right = newNode;
			}
		}
		
		return true;
	}
	
	/**
	 * Delete tree node with specified data.  
	 * @param data - data of tree node.
	 * @return whether deleting successes. 
	 */
	public boolean delete(Matrix data) {
		//Locate the node to be deleted and also locate its parent node.
		KDNode parent = null;
		KDNode current = root;
		
		int featureIndex = current.featureIndex;
		int featherUsedCount = 0;	//
		while(current != null) {
			if(current.value > data.at(featureIndex)) {
				parent = current;
				current = current.left; 																
			} else if (current.value == data.at(featureIndex)) {
				featherUsedCount++;
				if(featherUsedCount == data.columns()) {
					break;
				}				
			} else {
				parent = current;
				current = current.right;
			}
			
			if(current != null) {
				featureIndex = current.featureIndex;
			}
		}
		
		if(current == null) {	//Not found the node.
			return false;
		}
		
		//Case 1: current has no left child
		if(current.left == null) {
			//Connect the parent with the right child of the current node
			if(parent == null) {
				root = current.right;				
			} else {
				if(data.at(featureIndex) > parent.value) {
					parent.right = current.right;
				} else {
					parent.left = current.right;
				}
			}
			
		} else {
			//Case 2: the current node has a left node
			KDNode parentOfRightMost = current;
			KDNode rightMost = current.left;
			
			while(rightMost.right != null 
					&& rightMost.right.featureIndex == current.featureIndex) {
				parentOfRightMost = rightMost;
				rightMost = rightMost.right;
			}
			
			//Replace the element in current by the element in rightMost
			current.data = rightMost.data;
			current.value = rightMost.value;
			
			//Eliminate rightMost node
			if(parentOfRightMost.left == rightMost) {
				parentOfRightMost.left = rightMost.left;
			} else {
				parentOfRightMost.right = rightMost.left;
			}
		}
		
		return true;
	}
	
	
	/**
	 * Search nearest of target
	 * @param target - the data point to search its nearest
	 * @return nearest data point
	 */
	public Matrix searchNearest(Matrix target) {
		if(root == null) {
			return null;
		}
		
		return null;
	}
	
	/**
	 * KD Tree expand.
	 * @param kdNode - node of sub KDTree
	 */
	private void expandSubKDTree(KDNode kdNode) {
		//Leaf node
		if(kdNode.left == null && kdNode.right == null) {	
			return;
		}
		
		partition_features(kdNode);  //Create left and right children.
		
		//Expand left sub tree of kdNode
		if(kdNode.left != null) {
			expandSubKDTree(kdNode.left);
		}
		
		//Expand right sub tree of kdNode
		if(kdNode.right != null) {
			expandSubKDTree(kdNode.right);
		}
	}
	
	//Create left and right children.
	private void partition_features(KDNode kdNode) {
		int partitionFeatureIndex = 
			   getPartitionFeatureIndex(kdNode.data); //Get partition feature index
		kdNode.featureIndex = partitionFeatureIndex;
		
		//Sort the feature matrix by feature with specified index 
		sortFeatureByIndex(kdNode, partitionFeatureIndex);    
		
		/* Assign data to left child and right child of kdNode. */
		Matrix data = kdNode.data; 		
		kdNode.value = 
				data.at(data.rows()/2, partitionFeatureIndex);  //Partition key value.		
		
		int leftDataRowNum = data.rows() / 2; 
		Matrix leftData = new Matrix(leftDataRowNum, data.columns());
		for(int i=0; i< leftDataRowNum; i++) {
			for(int j=0; j<data.columns(); j++) {
				leftData.set(i, j, data.at(i, j));
			}
		}
		
		int rightDataRowNum = data.rows() - leftDataRowNum - 1;
		Matrix rightData = new Matrix(rightDataRowNum, data.columns());
		for(int i=0; i<rightDataRowNum; i++) {
			for(int j=0; j<data.columns(); j++) {
				rightData.set(i, j, data.at(leftDataRowNum+i+1, j));
			}
		}
		
		//Set children of kdNode 
		KDNode leftChild =  new KDNode(leftData);
		KDNode rightChild = new KDNode(rightData);
		kdNode.left = leftChild;
		kdNode.right = rightChild;
	}	
	
	/**
	 * Get index of feature which has max variance.
	 * @param dataSet - data set 
	 * @return - index of feature which has max variance
	 */
	private int getPartitionFeatureIndex(Matrix dataSet) {
		double[] variances = 
				new double[dataSet.columns()];  //Store variances of each feature
		for(int j=0; j<dataSet.columns(); j++) {
			Matrix singlFeature = 
				dataSet.at(new Range(0,dataSet.rows()), new Range(j, j + 1));
			 variances[j] = computeVariance(singlFeature);
		}
		
		/* Get index of feature which has max variance. */
		double maxVariance = variances[0];
		int maxIndex = 0;
		for(int i=1; i<variances.length; i++) {
			if(variances[i] > maxVariance) {
				maxVariance = variances[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}
	
	/**
	 * Sort data set by feature with specified index 
	 * @param dataSet - data set
	 * @param featureIndex - index of feature used to sort the data set 
	 */
	private void sortFeatureByIndex(KDNode node, final int featureIndex) {
		Matrix dataSet = node.data;
		List<Matrix> dataList = new ArrayList<Matrix>();
		for(int i=0; i<dataSet.rows(); i++) {
			dataList.add(dataSet.at(new Range(i,i+1), new Range(0, dataSet.columns())));
		}
		
		//Sort the data set
		Collections.sort(dataList, new Comparator<Matrix>() {
			@Override
			public int compare(Matrix o1, Matrix o2) {
				if(o1.at(featureIndex) > o2.at(featureIndex)) {
					return 1;
				} else if(o1.at(featureIndex) == o2.at(featureIndex)) {
					return 0;
				} else {
					return -1;
				}
			}			
		});
		
		/* Push the sorted data to dataSet */
		for(int i=0; i<dataList.size(); i++) {
			Matrix currentItem = dataList.get(i);
			for(int j=0; j<dataSet.columns(); j++) {				
				dataSet.set(i, j, currentItem.at(j));
			}
		}
	}
	
	/**
	 * Compute variance.
	 * @param data - data matrix(column vector) to compute variance.
	 * @return variance of the data
	 */
	private double computeVariance(Matrix data) {
		double aver = computeAverage(data);  //Get average of numbers
		double variance = 0;
		for(int i=0; i<data.rows(); i++) {
			variance += Math.pow(data.at(i) - aver, 2);
		}
		return variance / data.rows();
	} 
	
	/**
	 * Compute average of numbers.
	 * @param data - data matrix(column vector) to compute average
	 * @return average of the data.
	 */
	private double computeAverage(Matrix data) {
		double sum = 0;
		for(int i=0; i<data.rows(); i++) {
			sum += data.at(i);
		}
		return sum / data.rows();
	}
	
	//Node of KDTree	
	private static class KDNode {		
		int featureIndex;	//partition key index
		double value;		//partition key value
		Matrix data;    //data of the node		
		KDNode left;	//Left child
		KDNode right;   //Right child
				
		KDNode(Matrix data) {
			this.data = data;
		}
		
		KDNode(int featureIndex, double value, Matrix data) {
			this.data = data;
		}
	}
}

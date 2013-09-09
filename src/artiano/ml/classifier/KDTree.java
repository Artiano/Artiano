package artiano.ml.classifier;

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
	
	/* Empty constructor*/
	public KDTree() {		
	}
	
	/**
	 * Constructor
	 * @param dataSet - data set
	 */
	public KDTree(Matrix dataSet) {
		root = buildKDTree(dataSet);
	}
	
	/**
	 *  Build a kd-tree.
	 * @param dataSet - data set 
	 * @return root of the decision tree.
	 */
	private KDNode buildKDTree(Matrix dataSet) {
		if(dataSet.rows() < 1) {
			throw new IllegalArgumentException("Empty data set!");
		}		
				
		KDNode root = new KDNode(dataSet); //Initialize the root node		
		expandSubKDTree(root);   //KD Tree expand
		
		return root;
	}
	
	//Broad first search of the tree
	public void bfs() {
		Queue<KDNode> queue = new LinkedList<KDNode>();
		queue.add(root);
		while(! queue.isEmpty()) {
			KDNode node = queue.poll();	
			System.out.print("(");
			for(int i=0; i<node.nodeData.columns(); i++) {
				if(i < node.nodeData.columns() - 1 ) {
					System.out.print(node.nodeData.at(i) + ", ");
				} else {
					System.out.print(node.nodeData.at(i));
				}				
			}
			System.out.println(")");
			
			if(node.left != null) {
				queue.add(node.left);
			}
			
			if(node.right != null) {
				queue.add(node.right);
			}			
		}
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
				if(current.partitionValue >= data.at(featureIndex)) {
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
			if(parent.partitionValue > data.at(featureIndex)) {
				parent.left = newNode;												
			} else if(parent.partitionValue == data.at(featureIndex)) {
				
			} else {
				parent.right = newNode;			
			}
			
			root.treeData.mergeAfterRow(data);   //Add the new data
			Matrix newTreeData = root.treeData;
			root = buildKDTree(newTreeData);		//Re-build the tree
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
		int featherUsedCount = 0;	//feature used count
		while(current != null) {
			if(current.partitionValue > data.at(featureIndex)) {
				parent = current;
				current = current.left; 																
			} else if (current.partitionValue == data.at(featureIndex)) {
				featherUsedCount++;
				if(featherUsedCount == data.columns()) {  //All attribute used
					break;
				}				
			} else {
				parent = current;
				current = current.right;
			}
			
			if(current != null) {  //Next feature comparison
				featureIndex = current.featureIndex;
			}
		}
		
		if(current == null) {	//Not found the node.
			return false;
		}
		
		//Case 1: current has no left child
		if(current.left == null) {
			if(current.right == null) {  //Leaf node
				if(parent.left == current) {
					parent.left = null;
				} else {
					parent.right = null;
				}
				
			} else {
				//Connect the parent with the right child of the current node
				if(parent == null) {
					root = current.right;				
				} else {
					if(data.at(featureIndex) > parent.partitionValue) {
						parent.right = current.right;
					} else {
						parent.left = current.right;
					}
				}
			}						
			
		} else {
			//Case 2: the current node has a left node
			KDNode parentOfRightMost = current;
			KDNode rightMost = current.left;
			
			//Find right most of the node to be deleted.
			while(rightMost.right != null 
					&& rightMost.right.featureIndex == current.featureIndex) {
				parentOfRightMost = rightMost;
				rightMost = rightMost.right;
			}
			
			//Replace the element in current by the element in rightMost
			//current.treeData = removeRow(current.nodeData);// rightMost.treeData;
			
			//Eliminate rightMost node
			if(parentOfRightMost.left == rightMost) {
				parentOfRightMost.left = rightMost.left;
			} else {
				parentOfRightMost.right = rightMost.left;
			}
		}						
		
		//Matrix newTreeData = removeRow(data);  //Remove the row with data
		root = buildKDTree(data);	//Re-build the tree.		
		
		return true;
	}

	/**
	 *  Remove a specified row in a matrix
	 * @param data - a row in matrix
	 * @return matrix after removing a specified row
	 */
/*	private Matrix removeRow(Matrix data) {
		Matrix newTreeData = new Matrix(root.treeData.rows()-1, root.treeData.columns());
		Matrix oldTreeData = root.treeData;
		int count = 0;
		for(int i=0; i<oldTreeData.rows(); i++) {
			Matrix currentRowData = oldTreeData.row(i);
			boolean found = true;
			for(int j=0; j<oldTreeData.columns(); j++) {
				if(currentRowData.at(j) != data.at(j)) {
					found = false;
					break;
				}
			}
			
			if(!found) {				
				for(int j=0; j<currentRowData.columns(); j++) {
					newTreeData.set(count, j, currentRowData.at(j));
				}
				count++;
			}
		}
		return newTreeData;
	}	
*/
	
	/**
	 * Search nearest of target
	 * @param target - the data point to search its nearest
	 * @return nearest data point
	 */
	public Matrix findNearest(KDNode root, Matrix target) {
		/* 1. Binary search to get search path */
		KDNode current = root;
		Matrix nearest = current.nodeData;	//Node data
		double minDist = distance(nearest, target);
		int featureIndex = current.featureIndex;
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Stack<KDNode> searchPath = new Stack();		
		searchPath.push(current);	//Push root to the stack
		while(!searchPath.empty()) {							
			if(current == null) {  //Search complete
				break;
			}		
			searchPath.push(current);	//Push searched node to the stack
			
			double dist1 = distance(current.nodeData, target);
			if(dist1 < minDist) {
				nearest = current.nodeData;		//Update nearest
				minDist = dist1;     //Update min distance
			}
			featureIndex = current.featureIndex;  //Get next partition index
			
			//Binary search
			if(target.at(featureIndex) <= current.partitionValue) {
				current = current.left;
			} else {
				current = current.right;
			}
		}
		
		/* 2. trace search */
		KDNode kd_point = null;		
		while(! searchPath.empty()) {
			KDNode back_point = searchPath.pop();			
			featureIndex = back_point.featureIndex;  //Partition feature
			
			double dist1 = distance(target, back_point.nodeData);
			if(dist1 < minDist) {  //Get next sub space
				if(target.at(featureIndex) <= back_point.partitionValue) {
					kd_point = back_point.right;
				} else {
					kd_point = back_point.left;
				}
				searchPath.push(kd_point);
			}
			
			if(kd_point == null) {  //Trace complete
				continue;
			}
			
			double dist2 = distance(nearest, target);
			double dist3 = distance(kd_point.nodeData, target);
			if(dist2 > dist3) {
				nearest = kd_point.nodeData;
				minDist = dist3;
			}
		}
		
		return nearest;
	}
	
	/**
	 * Find k-nearest of target data point
	 * @param target - the data point to search its k-nearest
	 * @param k - number of nearest to get
	 * @return - k-nearest data point of target data point
	 */
	public List<Matrix> findKNearest(Matrix target, int k) {
		List<Matrix> kNearest = new ArrayList<Matrix>();
		
		if(root == null) {
			return null;
		}			
										
		KDTree tree = new KDTree();
		Matrix copyData = root.treeData.clone();
		tree.root = tree.buildKDTree(copyData);
						
		//Find (1+1)-th nearest respectively.
		for(int i=0; i<k; i++) {
			Matrix nearest = tree.findNearest(tree.root, target);
			kNearest.add(nearest);
			tree.delete(nearest);
		}
						
		return kNearest;
	}
	
	/**
	 * Compute distance of two data points
	 * @param point1 - data point
	 * @param point2 - data point
	 * @return
	 */
	public double distance(Matrix point1, Matrix point2) {
		if(point1.columns() != point2.columns() || 
				point1.rows() != 1 || point2.rows() != 1) {
			throw new IllegalArgumentException(
				"Dimension of point1 and point2 should be the same " +
				"and their can only be one row.");
		}
		
		double distance = 0;
		for(int i=0; i<point1.columns(); i++) {
			distance += Math.pow(point1.at(i) - point2.at(i), 2);
		}
		return Math.sqrt(distance);
	}
	
	/**
	 * KD Tree expand.
	 * @param kdNode - node of sub KDTree
	 */
	private void expandSubKDTree(KDNode kdNode) {
		//Leaf node
		if(kdNode == null) {	
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
		if(kdNode.treeData == null) {   //Leaf node
			return;
		}
			
		int partitionFeatureIndex = 
			   getPartitionFeatureIndex(kdNode.treeData); //Get partition feature index
		kdNode.featureIndex = partitionFeatureIndex;
		
		//Sort the feature matrix by feature with specified index 
		sortFeatureByIndex(kdNode, partitionFeatureIndex);    
		
		/* Assign data to left child and right child of kdNode. */
		Matrix data = kdNode.treeData; 		
		kdNode.nodeData = 
			data.at(new Range(data.rows()/2, data.rows()/2 + 1),
				new Range(0, data.columns()));
		kdNode.partitionValue = 
				data.at(data.rows()/2, partitionFeatureIndex);  //Partition key value.		
		
		/* Get left sub tree data */
		int leftDataRowNum = data.rows() / 2; 		
		Matrix leftData = null;
		if(leftDataRowNum > 0) {
			leftData = new Matrix(leftDataRowNum, data.columns());
			for(int i=0; i< leftDataRowNum; i++) {
				for(int j=0; j<data.columns(); j++) {
					leftData.set(i, j, data.at(i, j));
				}
			}
		}		
		
		/* Get right sub tree data */
		int rightDataRowNum = data.rows() - leftDataRowNum - 1;
		Matrix rightData = null;
		if(rightDataRowNum > 0) {
			rightData = new Matrix(rightDataRowNum, data.columns());
			for(int i=0; i<rightDataRowNum; i++) {
				for(int j=0; j<data.columns(); j++) {
					rightData.set(i, j, data.at(leftDataRowNum+i+1, j));
				}
			}
		}		
		
		//Set children of kdNode 
		KDNode leftChild =  new KDNode(leftData);
		KDNode rightChild = new KDNode(rightData);
		if(leftChild.treeData != null) {
			kdNode.left = leftChild;
		}
		
		if(rightChild.treeData != null) {
			kdNode.right = rightChild;
		}		
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
		Matrix dataSet = node.treeData;
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
		
		//Attention: cannot use assign element in dataList to row of dataSet(point to same memeory...)
		double[] data = new double[dataSet.rows() * dataSet.columns()];
		for(int i=0; i<dataList.size(); i++) {
			Matrix current = dataList.get(i);
			for(int j=0; j<current.columns(); j++) {
				data[i * dataSet.columns() + j] = current.at(j);
			}
		}
		Matrix matrix = new Matrix(dataSet.rows(), dataSet.columns(), data);
		node.treeData = matrix;
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
		double partitionValue;		//partition key value
		Matrix treeData;    //data of the sub tree
		Matrix nodeData;	//data of this node
		KDNode left;	//Left child
		KDNode right;   //Right child
				
		KDNode(Matrix data) {
			this.treeData = data;
		}
		
		KDNode(int featureIndex, double value, Matrix nodeData) {
			this.nodeData = nodeData;
		}
	}
}

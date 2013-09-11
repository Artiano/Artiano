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
	public KDTree(Matrix dataSet, Matrix dataLabel) {
		root = buildKDTree(dataSet, dataLabel);
	}
	
	/**
	 *  Build a kd-tree.
	 * @param dataSet - data set 
	 * @return root of the decision tree.
	 */
	private KDNode buildKDTree(Matrix dataSet, Matrix dataLabel) {
		if(dataSet.rows() < 1) {
			throw new IllegalArgumentException("Empty data set!");
		}		
				
		KDNode root = new KDNode(dataSet, dataLabel); //Initialize the root		
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
	@SuppressWarnings("unused")
	private boolean insert(Matrix data, Matrix dataLabel) {
		if(data.rows() == 0) {
			throw new IllegalArgumentException("Empty data cannot be inserted.");
		}
		
		if(root == null) {
			root = new KDNode(data, dataLabel);
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
			root.treeLabel.mergeAfterRow(dataLabel);  //Add the new label
			Matrix newTreeData = root.treeData;
			Matrix newTreeLabel = root.treeLabel;
			//Re-build the tree
			root = buildKDTree(newTreeData, newTreeLabel); 
		}
		
		return true;
	}
	
	/**
	 * Delete tree node with specified data.  
	 * @param node - the node to be deleted.
	 * @return whether deleting successes. 
	 */
	private boolean delete(KDNode nodeToDelete) {
		//The node to delete is the last node in the tree
		if(root.treeData.rows() == 1) {	  
			root = null;
			return true;
		}
		
		Matrix newTreeData = 
				new Matrix(root.treeData.rows() - 1, root.treeData.columns());
		Matrix newTreeLabel = 
				new Matrix(root.treeLabel.rows() - 1, root.treeLabel.columns());
	
		/* Broad first search to get newTreeData and newTreeLabel */
		int count = 0;
		Queue<KDNode> nodeQueue = new LinkedList<KDNode>();
		nodeQueue.add(root);
		while(!nodeQueue.isEmpty()) {
			KDNode node = nodeQueue.poll();
			if(node != nodeToDelete) {  //not the node to delete
				newTreeData.setRow(count, node.nodeData);
				newTreeLabel.setRow(count, node.nodeLabel);
				
				count++;
			}						
			
			if(node.left != null) {
				nodeQueue.add(node.left);
			}
			
			if(node.right != null) {
				nodeQueue.add(node.right);
			}
		}
		
		if(count == root.treeData.rows()) {  //Not found the node to be deleted
			return false;
		} else {
			root = buildKDTree(newTreeData, newTreeLabel);			
			return true;
		}				
	}		
	
	/**
	 * Search nearest of target
	 * @param target - the data point to search its nearest
	 * @return nearest data point
	 */
	public KDNode findNearest(KDNode root, Matrix target) {
		/* 1. Binary search to get search path */
		KDNode current = root;		
		int featureIndex = current.featureIndex;
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Stack<KDNode> searchPath = new Stack();		
		while(current != null) {										
			searchPath.push(current);	//Push searched node to the stack			
		
			featureIndex = current.featureIndex;  //Get next partition index
			
			//Binary search
			if(target.at(featureIndex) <= current.partitionValue) {
				current = current.left;
			} else {
				current = current.right;
			}
		}
		KDNode nearestNode = searchPath.peek();	//Node data
		double max_dist = distance(nearestNode.nodeData, target);
		double min_dist = max_dist;		
		
		/* 2. trace search */
		KDNode kd_point = null;		
		while(! searchPath.empty()) {
			KDNode back_point = searchPath.pop();			
			double distance2 = distance(back_point.nodeData, target);
			if(min_dist > distance2) {
				min_dist = distance2;
				nearestNode = back_point;				
			}
			
			featureIndex = back_point.featureIndex;  //Partition feature
			
			double dist1 = 
				distance(target.col(featureIndex), 
					back_point.nodeData.col(featureIndex));
			if(dist1 < max_dist) {  //Get next sub space
				if(target.at(featureIndex) <= back_point.partitionValue) {
					kd_point = back_point.right;
				} else {
					kd_point = back_point.left;
				}
				
				if(kd_point != null) {
					searchPath.push(kd_point);
				}				
			}
			
			if(kd_point == null) {  //Trace complete
				continue;
			}
			
			double dist3 = distance(kd_point.nodeData, target);
			if(dist3 < min_dist) {
				nearestNode = kd_point;
				min_dist = dist3;
			}
		}
		return nearestNode;
	}
	
	/**
	 * Find k-nearest of target data point
	 * @param target - the data point to search its k-nearest
	 * @param k - number of nearest to get
	 * @return - k-nearest data point of target data point
	 */
	public List<KDNode> findKNearest(Matrix target, int k) {
		List<KDNode> kNearest = new ArrayList<KDNode>();
		
		if(root == null) {
			return null;
		}			
										
		KDTree tree = new KDTree();
		Matrix copyData = root.treeData.clone();
		Matrix copyLabel = root.treeLabel.clone();
		tree.root = tree.buildKDTree(copyData, copyLabel);
						
		//Find (1+1)-th nearest respectively.
		for(int i=0; i<k; i++) {
			KDNode nearestNode = tree.findNearest(tree.root, target);		
			nearestNode.nodeData.print();
			kNearest.add(nearestNode);
			tree.delete(nearestNode);
		}
						
		return kNearest;
	}
	
	/**
	 * Compute distance of two data points
	 * @param point1 - data point
	 * @param point2 - data point
	 * @return
	 */
	private double distance(Matrix point1, Matrix point2) {
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
		Matrix labels = kdNode.treeLabel;
		kdNode.nodeData = 
				data.row(data.rows() / 2);		
		kdNode.nodeLabel = labels.row(data.rows() / 2);
		kdNode.partitionValue = 
				data.at(data.rows()/2, partitionFeatureIndex);  //Partition key value.
		
		
		/* Get left sub tree data */
		int leftDataRowNum = data.rows() / 2; 		
		Matrix leftData = null;
		Matrix leftLabel = null;
		if(leftDataRowNum > 0) {
			leftData = new Matrix(leftDataRowNum, data.columns());
			leftLabel = new Matrix(leftDataRowNum, 1);
			for(int i=0; i< leftDataRowNum; i++) {
				for(int j=0; j<data.columns(); j++) {
					leftData.set(i, j, data.at(i, j));
					leftLabel.set(i, 0, labels.at(i, 0));
				}
			}
		}		
		
		/* Get right sub tree data */
		int rightDataRowNum = data.rows() - leftDataRowNum - 1;
		Matrix rightData = null;
		Matrix rightLabel = null;
		if(rightDataRowNum > 0) {
			rightData = new Matrix(rightDataRowNum, data.columns());
			rightLabel = new Matrix(rightDataRowNum, 1);
			for(int i=0; i<rightDataRowNum; i++) {
				for(int j=0; j<data.columns(); j++) {
					rightData.set(i, j, data.at(leftDataRowNum+i+1, j));
					rightLabel.set(i, 0, labels.at(leftDataRowNum+i+1, 0));
				}
			}
		}		
		
		//Set children of kdNode 
		KDNode leftChild =  new KDNode(leftData, leftLabel);
		KDNode rightChild = new KDNode(rightData, rightLabel);
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
			Matrix singlFeature = dataSet.col(j); 
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
		Matrix dataLabel = node.treeLabel;		
		
		List<Matrix> dataList = new ArrayList<Matrix>();
		List<Matrix> labelList =  new ArrayList<Matrix>();
		for(int i=0; i<dataSet.rows(); i++) {
			dataList.add(dataSet.at(new Range(i,i+1), new Range(0, dataSet.columns())));
			labelList.add(dataLabel.at(new Range(i,i+1), new Range(0, dataLabel.columns())));
		}
				
		//Sort treeData
		boolean needNextPass = true;		
		for(int k=1; k<dataList.size() && needNextPass; k++){
			//Array may be sorted and next pass not needed
			needNextPass = false;			
			for(int i=0; i<dataList.size()-k; i++){
				Matrix mat1 = dataList.get(i);				
				Matrix mat2 = dataList.get(i + 1);				
				if(mat1.at(featureIndex) > mat2.at(featureIndex)){
					//Swap mat1 with mat2											
					dataList.set(i, mat2);
					dataList.set(i + 1, mat1);
					
					//Swap labelMat1 with labelMat2
					Matrix labelMat1 = labelList.get(i);
					Matrix labelMat2 = labelList.get(i + 1);
					labelList.set(i, labelMat2);
					labelList.set(i + 1, labelMat1);
					needNextPass = true;  //Next pass still needed
				}				
			}
		}		
		
		//Attention: cannot use assign element in dataList to row of dataSet(point to same memeory...)
		//Get sorted tree data matrix
		double[] data = new double[dataSet.rows() * dataSet.columns()];		
		for(int i=0; i<dataList.size(); i++) {
			Matrix current = dataList.get(i);
			for(int j=0; j<current.columns(); j++) {
				data[i * dataSet.columns() + j] = current.at(j);
			}
		}
		Matrix sortedTreeData = 
				new Matrix(dataSet.rows(), dataSet.columns(), data);
		node.treeData = sortedTreeData;
		
		//Get sorted tree label matrix
		double[] label = new double[dataLabel.rows() * dataLabel.columns()];
		for(int i=0; i<dataLabel.rows(); i++) {
			Matrix currentLabel = labelList.get(i);
			for(int j=0; j<dataLabel.columns(); j++) {
				label[i * dataLabel.columns() + j] = currentLabel.at(j);
			}
		}
		Matrix sortedTreeLabel = 
				new Matrix(dataLabel.rows(), dataLabel.columns(), label);
		node.treeLabel = sortedTreeLabel;				
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
	public static class KDNode {		
		int featureIndex;	//partition key index
		double partitionValue;		//partition key value
		private Matrix treeData;    //data of the sub tree		
		public Matrix nodeData;	//data of this node
		Matrix treeLabel;	//class labels of the sub tree
		Matrix nodeLabel;		//class label of this node.
		KDNode left;	//Left child
		KDNode right;   //Right child
				
		KDNode(Matrix data, Matrix treeLabel) {
			this.treeData = data;
			this.treeLabel = treeLabel;
		}
		
		KDNode(int featureIndex, double value, Matrix nodeData) {
			this.nodeData = nodeData;
		}
	}
}

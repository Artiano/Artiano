package artiano.ml.clustering;

import java.util.*;

import artiano.core.structure.Matrix;
import artiano.core.structure.Range;
import artiano.ml.BaseKDTree;

/**
 * <p>Description: KD Tree for KMeans.</p>
 * @author JohnF Nash
 * @version 1.0.0
 * @date 2013-9-15
 * @function 
 * @since 1.0.0
 */
public class KDTree extends BaseKDTree {
	
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
	public BaseKDNode buildKDTree(Matrix dataSet) {
		if(dataSet.rows() < 1) {
			throw new IllegalArgumentException("Empty data set!");
		}		
				
		BaseKDTree.BaseKDNode root = new BaseKDTree.BaseKDNode(dataSet); //Initialize the root		
		expandSubKDTree(root);   //KD Tree expand
		
		return root;
	}
	
	/**
	 * Delete tree node with specified data.  
	 * @param node - the node to be deleted.
	 * @return whether deleting successes. 
	 */
	@Override
	protected boolean delete(BaseKDTree.BaseKDNode nodeToDelete) {
		//The node to delete is the last node in the tree
		if(root.treeData.rows() == 1) {	  
			root = null;
			return true;
		}
		
		Matrix newTreeData = 
				new Matrix(root.treeData.rows() - 1, root.treeData.columns());
	
		/* Broad first search to get newTreeData and newTreeLabel */
		int count = 0;
		Queue<BaseKDTree.BaseKDNode> nodeQueue = new LinkedList<BaseKDTree.BaseKDNode>();
		nodeQueue.add(root);
		while(!nodeQueue.isEmpty()) {
			BaseKDTree.BaseKDNode node = nodeQueue.poll();
			if(node != nodeToDelete) {  //not the node to delete
				newTreeData.setRow(count, node.nodeData);				
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
			root = buildKDTree(newTreeData);			
			return true;
		}				
	}		
	
	/**
	 * Search nearest of target
	 * @param target - the data point to search its nearest
	 * @return nearest data point
	 */
	public BaseKDTree.BaseKDNode findNearest(BaseKDTree.BaseKDNode root, Matrix target) {
		/* 1. Binary search to get search path */
		BaseKDTree.BaseKDNode current = root;		
		int featureIndex = current.featureIndex;
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Stack<BaseKDTree.BaseKDNode> searchPath = new Stack();		
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
		BaseKDTree.BaseKDNode nearestNode = searchPath.peek();	//Node data
		double max_dist = distance(nearestNode.nodeData, target);
		double min_dist = max_dist;		
		
		/* 2. trace search */
		BaseKDTree.BaseKDNode kd_point = null;		
		while(! searchPath.empty()) {
			BaseKDTree.BaseKDNode back_point = searchPath.pop();			
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
	 * KD Tree expand.
	 * @param kdNode - node of sub KDTree
	 */
	private void expandSubKDTree(BaseKDTree.BaseKDNode kdNode) {
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
	private void partition_features(BaseKDTree.BaseKDNode kdNode) {
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
				data.row(data.rows() / 2);		
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
		BaseKDTree.BaseKDNode leftChild =  new BaseKDTree.BaseKDNode(leftData);
		BaseKDTree.BaseKDNode rightChild = new BaseKDTree.BaseKDNode(rightData);
		if(leftChild.treeData != null) {
			kdNode.left = leftChild;
		}
		
		if(rightChild.treeData != null) {
			kdNode.right = rightChild;
		}		
	}	
	
	/**
	 * Sort data set by feature with specified index 
	 * @param dataSet - data set
	 * @param featureIndex - index of feature used to sort the data set 
	 */
	private void sortFeatureByIndex(BaseKDTree.BaseKDNode node, final int featureIndex) {
		Matrix dataSet = node.treeData;		
		
		List<Matrix> dataList = new ArrayList<Matrix>();
		for(int i=0; i<dataSet.rows(); i++) {
			dataList.add(dataSet.at(new Range(i,i+1), new Range(0, dataSet.columns())));
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
	}

	/**
	 * Find k-nearest of target data point
	 * @param target - the data point to search its k-nearest
	 * @param k - number of nearest to get
	 * @return - k-nearest data point of target data point
	 */
	public List<BaseKDNode> findKNearest(Matrix target, int k) {
		List<BaseKDNode> kNearest = new ArrayList<BaseKDNode>();
		
		if(root == null) {
			return null;
		}			
										
		KDTree tree = new KDTree();
		Matrix copyData = root.treeData.clone();
		tree.root = tree.buildKDTree(copyData);
						
		//Find (1+1)-th nearest respectively.
		for(int i=0; i<k; i++) {
			BaseKDNode nearestNode = tree.findNearest(tree.root, target);		
			kNearest.add(nearestNode);
			tree.delete(nearestNode);
		}
						
		return kNearest;
	}
}

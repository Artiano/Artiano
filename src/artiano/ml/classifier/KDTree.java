package artiano.ml.classifier;

import java.util.*;

import artiano.core.structure.Matrix;
import artiano.core.structure.NominalAttribute;
import artiano.core.structure.Range;
import artiano.core.structure.Table;
import artiano.core.structure.Table.TableRow;
import artiano.ml.BaseKDTree;

/**
 * <p>Description: KD Tree for KNN.</p>
 * @author JohnF Nash
 * @version 1.0.0
 * @date 2013-9-6
 * @function 
 * @since 1.0.0
 */
public class KDTree extends BaseKDTree {
	
	/* Empty constructor*/
	public KDTree() {		
	}
	
	public KDTree(Table dataSet, Table dataLabel) {
		Matrix dataSetMat = dataSet.toMatrix(); 
		root = buildKDTree(dataSetMat, dataLabel);
	}
	
	/**
	 *  Build a kd-tree.
	 * @param dataSet - data set 
	 * @return root of the decision tree.
	 *
	 */
	public KDNode buildKDTree(Matrix dataSet, Table dataLabel) {
		if(dataSet.rows() < 1) {
			throw new IllegalArgumentException("Empty data set!");
		}		
				
		KDNode root = new KDNode(dataSet, dataLabel); //Initialize the root		
		expandSubKDTree(root);   //KD Tree expand		
		return root;
	}
	
	/**
	 * Delete tree node with specified data.  
	 * @param node - the node to be deleted.
	 * @return whether deleting successes. 
	 */
	@Override
	public boolean delete(BaseKDNode nodeToDelete) {
		//The node to delete is the last node in the tree
		if(root.treeData.rows() == 1) {	  
			root = null;
			return true;
		}
		
		Matrix newTreeData = 
				new Matrix(root.treeData.rows() - 1, root.treeData.columns());
//		Matrix newTreeLabel = 
//		new Matrix(((KDNode)root).treeLabel.rows() - 1, ((KDNode)root).treeLabel.columns());
		Table newTreeLabel = new Table(); 			
		newTreeLabel.addAttribute(new NominalAttribute("label"));
		
		/* Broad first search to get newTreeData and newTreeLabel */
		int count = 0;
		Queue<KDNode> nodeQueue = new LinkedList<KDNode>();
		nodeQueue.add(((KDNode)root));
		while(!nodeQueue.isEmpty()) {
			KDNode node = nodeQueue.poll();
			if(node != nodeToDelete) {  //not the node to delete
				newTreeData.setRow(count, node.nodeData);				
				//newTreeLabel.setRow(count, node.nodeLabel);
				TableRow tableRow = newTreeLabel.new TableRow(); 
				tableRow.set(0, node.nodeLabel);
				newTreeLabel.push(tableRow);
				count++;
			}						
			
			if(node.left != null) {
				nodeQueue.add((KDNode) node.left);
			}
			
			if(node.right != null) {
				nodeQueue.add((KDNode) node.right);
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
				current = (KDNode) current.left;
			} else {
				current = (KDNode) current.right;
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
				distance(target.column(featureIndex), 
					back_point.nodeData.column(featureIndex));
			if(dist1 < max_dist) {  //Get next sub space
				if(target.at(featureIndex) <= back_point.partitionValue) {
					kd_point = (KDNode) back_point.right;
				} else {
					kd_point = (KDNode) back_point.left;
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
	private void expandSubKDTree(KDNode kdNode) {
		//Leaf node
		if(kdNode == null) {	
			return;
		}
		
		partition_features(kdNode);  //Create left and right children.
		
		//Expand left sub tree of kdNode
		if(kdNode.left != null) {
			expandSubKDTree((KDNode) kdNode.left);
		}		
		//Expand right sub tree of kdNode
		if(kdNode.right != null) {
			expandSubKDTree((KDNode) kdNode.right);
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
		Table labels = kdNode.treeLabel;
		kdNode.nodeData = 
				data.row(data.rows() / 2);		
		kdNode.nodeLabel = labels.row(data.rows()/2).at(0);
		kdNode.partitionValue = 
				data.at(data.rows()/2, partitionFeatureIndex);  //Partition key value.
				
		/* Get left sub tree data */
		int leftDataRowNum = data.rows() / 2; 		
		Matrix leftData = null;
		Table leftLabel = null;
		if(leftDataRowNum > 0) {
			leftData = new Matrix(leftDataRowNum, data.columns());
			leftLabel = new Table();
			leftLabel.addAttribute(new NominalAttribute("label"));
			for(int i=0; i< leftDataRowNum; i++) {
				TableRow tableRow = leftLabel.new TableRow(); 
				tableRow.set(0, labels.at(i, 0));
				leftLabel.push(tableRow);
				for(int j=0; j<data.columns(); j++) {
					leftData.set(i, j, data.at(i, j));					
				}
			}
		}		
		
		/* Get right sub tree data */
		int rightDataRowNum = data.rows() - leftDataRowNum - 1;
		Matrix rightData = null;
		Table rightLabel = null;
		if(rightDataRowNum > 0) {
			rightData = new Matrix(rightDataRowNum, data.columns());
			rightLabel = new Table();
			rightLabel.addAttribute(new NominalAttribute("label"));
			for(int i=0; i<rightDataRowNum; i++) {
				TableRow tableRow = leftLabel.new TableRow(); 
				tableRow.set(0, labels.at(leftDataRowNum+i+1, 0));
				rightLabel.push(tableRow);
				for(int j=0; j<data.columns(); j++) {
					rightData.set(i, j, data.at(leftDataRowNum+i+1, j));
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
	 * Sort data set by feature with specified index 
	 * @param dataSet - data set
	 * @param featureIndex - index of feature used to sort the data set 
	 */
	private void sortFeatureByIndex(KDNode node, final int featureIndex) {
		Matrix dataSet = node.treeData;
		Table dataLabel = node.treeLabel;		
		
		List<Matrix> dataList = new ArrayList<Matrix>();
		List<Object> labelList =  new ArrayList<Object>();
		for(int i=0; i<dataSet.rows(); i++) {
			dataList.add(dataSet.at(new Range(i,i+1), new Range(0, dataSet.columns())));
			labelList.add(dataLabel.row(i).at(0));
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
					Object labelMat1 = labelList.get(i);
					Object labelMat2 = labelList.get(i + 1);
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
		Table table = new Table();
		table.addAttribute(new NominalAttribute());
		for(int i=0; i<dataLabel.rows(); i++) {
			TableRow tableRow = table.new TableRow();
			Object currentLabel = labelList.get(i);
			tableRow.set(0, currentLabel);
			table.push(tableRow);			
		}
		node.treeLabel = table; 				
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
		Table copyLabel = (((KDNode)root).treeLabel) ;
		tree.root = tree.buildKDTree(copyData, copyLabel);
						
		//Find (1+1)-th nearest respectively.
		for(int i=0; i<k; i++) {
			KDNode nearestNode = tree.findNearest(((KDNode)tree.root), target);		
			nearestNode.nodeData.print();
			kNearest.add(nearestNode);
			tree.delete(nearestNode);
		}						
		return kNearest;
	}	
	
	//Node of KDTree	
	public static class KDNode extends BaseKDNode {		
		public Table treeLabel;	//class labels of the sub tree
		Object nodeLabel;		//class label of this node.
		
		KDNode(Matrix data, Table treeLabel) {
			super(data);
			this.treeLabel = treeLabel;
		}
		
		KDNode(int featureIndex, double value, Matrix nodeData) {
			super(featureIndex, value, nodeData);
		}
	}
}

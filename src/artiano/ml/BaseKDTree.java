package artiano.ml;

import java.util.LinkedList;
import java.util.Queue;

import artiano.core.structure.Matrix;

public abstract class BaseKDTree {
	
	protected BaseKDTree.BaseKDNode root;  //Root of the kd-tree		

	/**
	 * Compute variance.
	 * @param data - data matrix(column vector) to compute variance.
	 * @return variance of the data
	 */
	protected double computeVariance(Matrix data) {
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

	//Broad first search of kd-tree 
	public void bfs() {
		Queue<BaseKDNode> queue = new LinkedList<BaseKDNode>();
		queue.add(root);
		while(! queue.isEmpty()) {
			BaseKDNode node = queue.poll();	
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
	 * Get index of feature which has max variance.
	 * @param dataSet - data set 
	 * @return - index of feature which has max variance
	 */
	protected int getPartitionFeatureIndex(Matrix dataSet) {
		double[] variances = 
				new double[dataSet.columns()];  //Store variances of each feature
		for(int j=0; j<dataSet.columns(); j++) {
			Matrix singlFeature = dataSet.column(j); 
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
	 * Compute distance of two data points
	 * @param point1 - data point
	 * @param point2 - data point
	 * @return
	 */
	protected double distance(Matrix point1, Matrix point2) {
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
	 * Delete tree node with specified data.  
	 * @param node - the node to be deleted.
	 * @return whether deleting successes. 
	 */
	protected abstract boolean delete(BaseKDNode nodeToDelete);	
			
	//Node of KDTree	
	public static class BaseKDNode {		
		public int featureIndex;	//partition key index
		public double partitionValue;		//partition key value
		public Matrix treeData;    //data of the sub tree		
		public Matrix nodeData;	//data of this node
		public BaseKDNode left;	//Left child
		public BaseKDNode right;   //Right child
				
		public BaseKDNode(Matrix data) {
			this.treeData = data;
		}
		
		public BaseKDNode(int featureIndex, double value, Matrix nodeData) {
			this.nodeData = nodeData;
			this.featureIndex = featureIndex;
			this.partitionValue = value;
		}
	}
}

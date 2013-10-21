package artiano.ml.classifier;

import java.util.*;
import java.util.Map.Entry;

import artiano.core.operation.Preservable;
import artiano.core.structure.Matrix;
import artiano.ml.classifier.KDTree.KDNode;

public class KNearest extends Preservable {
	private static final long serialVersionUID = 1L;
			
	private KDTree kdTree;			//kd-tree	
	
	/* Empty constructor */
	public KNearest() {	
	}
	
	/**
	 * Train the model
	 * @param trainData - train data
	 * @param trainLabel - train labels
	 * @return - whether the train successes
	 */
	public boolean train(Matrix trainData, Matrix trainLabel) {
		try {
			isTrainDataValid(trainData, trainLabel);						
		} catch(NullPointerException e) {
			return false;
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		}				
		
		kdTree = new KDTree(trainData, trainLabel);  //construct kd-tree 
		
		return true;
	}

	/**
	 * Finds the neighbors and predicts responses for input vectors.
	 * @param samples - samples to get classification
	 * @param k - number of used nearest neighbors
	 * @return - Vector with results of prediction (regression or classification) 
	 * 				for each input sample.
	 */
	public Matrix findNearest(Matrix samples, int k) {
		Matrix results = new Matrix(samples.rows(), 1);
		for(int i=0; i<samples.rows(); i++) {
			results.set(i, 0, findNearestForSingleSample(samples.row(i), k));
		}
		return results;									
	}

	/**
	 * Find the most frequent label for a sample
	 * @param samples - samples to get classification
	 * @param k - number of used nearest neighbors
	 * @return - The most frequent label
	 */
	private double findNearestForSingleSample(Matrix samples, int k) {
		List<KDNode> nearestNode = kdTree.findKNearest(samples, k);
		
		//Count each label
		Map<Double, Integer> eachLabelCount = countEackLabel(nearestNode);
		
		//Find the most frequent label
		Set<Entry<Double, Integer>> entrySet = eachLabelCount.entrySet();
		double mostFreqLabel = -1;
		int maxCount = 0;
		for(Entry<Double, Integer> entry : entrySet) {
			if(maxCount < entry.getValue()) {
				maxCount = entry.getValue();
				mostFreqLabel = entry.getKey();
			}
		}
		
		return mostFreqLabel;
	}

	/**
	 * Get count of each label
	 * @param nearestNode - k-nearest neighbors 
	 * @return count of each label
	 */
	private Map<Double, Integer> countEackLabel(List<KDNode> nearestNode) {
		Map<Double, Integer> eachLabelCount = 
				new HashMap<Double, Integer>();
		for(int i=0; i<nearestNode.size(); i++) {
			KDNode node = nearestNode.get(i);
			double nodeLabel = node.nodeLabel.at(0);
			if(!eachLabelCount.containsKey(nodeLabel)) {
				eachLabelCount.put(nodeLabel, 1);
			} else {
				eachLabelCount.put(nodeLabel, eachLabelCount.get(nodeLabel) + 1);
			}
		}
		return eachLabelCount;
	}			 
	
	/**
	 * Check whether train data is valid
	 * @param trainData - train data
	 * @param trainLabel - train label
	 * @throws - NullPointerException
	 *         - IllegalArgumentException
	 */
	private void isTrainDataValid(Matrix trainData, Matrix trainLabel) {
		// Check whether train data is valid
		if (trainData == null || trainLabel == null) {
			throw new NullPointerException("Training data is null");
		}
		
		if(trainData.rows() != trainLabel.rows()) {
			throw new IllegalArgumentException("Size of trainData does not" +
					" match that of trainResponse");
		}
	} 	
}
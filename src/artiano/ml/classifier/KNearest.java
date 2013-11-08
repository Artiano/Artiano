package artiano.ml.classifier;

import java.util.*;
import java.util.Map.Entry;

import artiano.core.structure.Matrix;
import artiano.core.structure.NominalAttribute;
import artiano.core.structure.Table;
import artiano.core.structure.Table.TableRow;
import artiano.ml.classifier.KDTree.KDNode;

public class KNearest extends Classifier {
	private static final long serialVersionUID = 2277585000325381124L;
	
	private KDTree kdTree;			//kd-tree	
	
	/* Empty constructor */
	public KNearest() {	
	}
	
	/**
	 * Train the model
	 * @param trainData - train data
	 * @param trainLabel - train labels
	 * @param isAttributeContinuous  array of boolean that indicate whether 
	 *             corresponding attribute is continuous or discrete.
	 * @return - whether the train successes
	 */
	public boolean train(Table trainData, Table trainLabel, 
			boolean[] isAttributeContinuous) {
		try {
			isTrainingDataValid(trainData, trainLabel);						
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
	public Table predict(Table samples, int k) {
		Matrix sampleMat = samples.toMatrix();
		Table results = new Table();
		results.addAttribute(new NominalAttribute("label"));
		for(int i=0; i<samples.rows(); i++) {
			TableRow tableRow = results.new TableRow();
			tableRow.set(0, findNearestForSingleSample(sampleMat.row(i), k));
			results.push(tableRow);
		}
		return results;									
	}

	/**
	 * Find the most frequent label for a sample
	 * @param samples - samples to get classification
	 * @param k - number of used nearest neighbors
	 * @return - The most frequent label
	 */
	private Object findNearestForSingleSample(Matrix samples, int k) {
		List<KDNode> nearestNode = kdTree.findKNearest(samples, k);
		
		//Count each label
		Map<Object, Integer> eachLabelCount = countEackLabel(nearestNode);
		
		//Find the most frequent label
		Set<Entry<Object, Integer>> entrySet = eachLabelCount.entrySet();
		Object mostFreqLabel = null;
		int maxCount = 0;
		for(Entry<Object, Integer> entry : entrySet) {
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
	private Map<Object, Integer> countEackLabel(List<KDNode> nearestNode) {
		Map<Object, Integer> eachLabelCount = 
				new HashMap<Object, Integer>();
		for(int i=0; i<nearestNode.size(); i++) {
			KDNode node = nearestNode.get(i);
			Object nodeLabel = node.nodeLabel;
			if(!eachLabelCount.containsKey(nodeLabel)) {
				eachLabelCount.put(nodeLabel, 1);
			} else {
				eachLabelCount.put(nodeLabel, eachLabelCount.get(nodeLabel) + 1);
			}
		}
		return eachLabelCount;
	}			 
	
	/**
	 * Check the validation of training data inputed.
	 * 
	 * @throws NullPointerException - trainData or trainLabel is null.
	 * @throws IllegalArgumentException
	 *            - Data in parameter trainingLabel does not match with 
	 *              data in parameter trainingData
	 */
	private void isTrainingDataValid(Table trainData, Table trainLabel) {
		// Check whether train data is valid
		if (trainData == null || trainLabel == null) {
			throw new NullPointerException("Training data is null");
		}

		/* Check whether all data in trainData is numeric */
		int rows = trainData.rows();
		int columns = trainData.columns();
		for(int i=0; i<rows; i++) {
			TableRow tableRow = trainData.row(i);
			for(int j=0; j<columns; j++) {
				Object obj = tableRow.at(j);
				if(! (obj instanceof Double)) {
					throw new IllegalArgumentException("Train data can only be"
							+ " numeric.");
				}
			}
		}
		
		if(trainData.rows() != trainLabel.rows()) {
			throw new IllegalArgumentException("Size of TrainingLabel does not match " +
					"with that of trainingData.");
		}
	}
	
}
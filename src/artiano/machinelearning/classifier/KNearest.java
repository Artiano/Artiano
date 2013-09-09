package artiano.machinelearning.classifier;

import artiano.core.operation.Preservable;
import artiano.core.structure.Matrix;

public class KNearest extends Preservable {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private Matrix trainData;		//Train data
	@SuppressWarnings("unused")
	private Matrix trainLabel;		//Train label
	@SuppressWarnings("unused")
	private boolean isRegression;	//The class used for classifier or regression
	@SuppressWarnings("unused")
	private KDTree kdTree;			//kd-tree
	private int labelIndex;			//column index of label in train data matrix
	
	/* Empty constructor */
	public KNearest() {	
	}
	
	/**
	 * constructor
	 * @param trainData - train data
	 * @param trainLabel - train labels
	 * @param isRegression - the class for regression or classification
	 */
	public KNearest(Matrix trainData, Matrix trainLabel, 
			boolean isRegression) {
		this.trainData = trainData;
		this.trainLabel = trainLabel;
		this.isRegression = isRegression;
	}
	
	/**
	 * Train the model
	 * @param trainData - train data
	 * @param trainLabel - train labels
	 * @param isRegression - the class used for regression or classification
	 * @return - whether the train successes
	 */
	public boolean train(Matrix trainData, Matrix trainLabel, 
			boolean isRegression) {
		try {
			isTrainDataValid(trainData, trainLabel);
			
			//Get column index of train label
			int labelIndex = getLabelIndex(trainData, trainLabel);
			if(labelIndex == -1) {
				System.err.println("Train data does not match with train labels.");
				return false;
			} else {
				this.labelIndex = labelIndex;
			}
			
		} catch(NullPointerException e) {
			return false;
		} 				
		
		this.trainData = trainData;
		this.trainLabel = trainLabel;
		this.isRegression = isRegression;
		
		//Remove train label column from train data
		Matrix dataWithoutLabel = 
				removeLabelFromTrainData(trainData);  
		kdTree = new KDTree(dataWithoutLabel);  //construct kd-tree 
		
		return true;
	}
	
	/**
	 * Remove train label column from train data.
	 * @param trainData - train data
	 * @return train data without train label column
	 */
	private Matrix removeLabelFromTrainData(Matrix trainData) {
		Matrix dataWithoutLabel = 
				new Matrix(trainData.rows(), trainData.columns()-1);
		for(int i=0; i<trainData.rows(); i++) { 
			int count = 0;
			for(int j=0; j<trainData.columns(); j++) {
				if(j != labelIndex) {  //Not class label
					dataWithoutLabel.set(i, count, trainData.at(i, j));
					count++;
				}
			}
		}
		return dataWithoutLabel;
	}

	/**
	 * Finds the neighbors and predicts responses for input vectors.
	 * @param samples - samples to get classification
	 * @param k - number of used nearest neighbors
	 * @param results - Vector with results of prediction 
	 * 			(regression or classification) for each input sample
	 * @return - If only a single input vector is passed, 
	 * 				the predicted value is returned by the method
	 */
/*	public double findNearest(Matrix samples, int k, Matrix results) {
		//Matrix nearest = kdTree.findKNearest(samples, k);
	}		
*/	 
	/**
	 * Get column index of label in train data matrix
	 * @param trainData - samples to get classification
	 * @param trainLabel - train labels
	 * @return - column index of label in train data matrix(-1 if not found).
	 */
	private int getLabelIndex(Matrix trainData, Matrix trainLabel) {
		for(int j=0; j<trainData.columns(); j++) {
			boolean found = true;
			for(int i=0; i<trainData.rows(); i++) {
				if(trainData.at(i, j) != trainLabel.at(i)) {
					found = false;
					break;
				}
			}
			
			if(found) {  //Get column index of train label
				return j;
			}
		}	
		
		return -1;
	} 
	
	/**
	 * Check whether train data is valid
	 * @param trainData - train data
	 * @param trainLabel - train label
	 * @throws NullPointerException
	 */
	private void isTrainDataValid(Matrix trainData, Matrix trainLabel) {
		// Check whether train data is valid
		if (trainData == null || trainLabel == null) {
			throw new NullPointerException("Training data is null");
		}								
	} 
	
}

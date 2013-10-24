/**
 * 	NormalBayesClassifier.java
 */
package artiano.ml.classifier;

import java.util.*;
import java.util.Map.Entry;

import artiano.core.operation.Preservable;
import artiano.core.structure.Matrix;
import artiano.core.structure.Range;

/**
 * <p>
 * Description: Naive Bayes classifier
 * </p>
 * 
 * @author JohnF Nash
 * @version 1.0.0
 * @date 2013-8-27
 * @function
 * @since 1.0.0
 */
public class NaiveBayesClassifier extends Preservable {
	private static final long serialVersionUID = 1L;
	
	private Matrix trainData;
	private Matrix trainLabel;
	// Training result
	private Matrix trainResult;
	// Count of all labels
	private Map<Integer, Integer> eachlabelCount = 
			new LinkedHashMap<Integer, Integer>();	
	
	/**
	 * Train data
	 * @param trainData - training data
	 * @param trainLabel - labels for training data
	 * @return whether the training success.
	 */
	public boolean train(Matrix trainData, Matrix trainLabel) {
		try {
			// Check whether train data is valid
			isTrainingDataValid(trainData, trainLabel);
		} catch (Exception e) {
			return false; // The data training fails
		}
		this.trainData = trainData;
		this.trainLabel = trainLabel;

		// Group training data by class
		Map<Integer, Matrix> labelMap = groupTraningDataByLabel();	
		generateTrainingResult(labelMap); // Generate training result
		return true;
	}
	
	/**
	 * Classify samples
	 * 
	 * @param samples - sample to get its classification
	 * @return classification result.
	 */
	public Matrix classify(Matrix samples) {		
		Matrix result = new Matrix(samples.rows(), 1);
		for (int i = 0; i < samples.rows(); i++) {
			int predictResult = classifySingleData(samples.row(i));
			result.set(i, 0, predictResult);
		}
		return result;
	}

	/**
	 * Predict classification of a single sample
	 * 
	 * @param sample - sample to get its classification
	 * @return classification of the sample
	 * @throws Exception
	 */
	private int classifySingleData(Matrix sample) {
		if (sample.rows() > 1) {
			throw new IllegalArgumentException(
				"The test sample matrix can only be one row. For"
				+ " multiple rows, please use method predict(Matrix sample, Matrix result)");
		}

		/* Get the classification by finding the max probability */
		List<Integer> labelList = 
			new ArrayList<Integer>(eachlabelCount.keySet());						
		List<Double> probabilityList = 
			computeEachLabelProbabilityOfData(sample, labelList);
		double maxPorba = probabilityList.get(0);
		int maxIndex = 0;
		for (int m = 1; m < probabilityList.size(); m++) {
			if (probabilityList.get(m) > maxPorba) {
				maxPorba = probabilityList.get(m);
				maxIndex = m;
			}
		}
		return labelList.get(maxIndex); // Return the classification
	}

	private List<Double> computeEachLabelProbabilityOfData(Matrix sample,
			List<Integer> labelList) {
		List<Double> probabilityList = new ArrayList<Double>();		
		for (int j = 0; j < labelList.size(); j++) {
			double probabilitiy = 1;
			for (int k = 0; k < sample.columns(); k++) {
				double aver = 
						trainResult.at(j * sample.columns() + k, 1); 
				double stdDeviation = 
						trainResult.at(j * sample.columns()+ k, 2);
				double a = (1.0 / (Math.sqrt(Math.PI * 2) * stdDeviation));
				double b = Math.pow((sample.at(0, k) - aver), 2);
				double c = 2 * Math.pow(stdDeviation, 2);
				probabilitiy *= (a * Math.pow(Math.E, -1 * b / c)); 
			}
			double labelAppearProba = 
				eachlabelCount.get(labelList.get(j)) * 1.0 / trainData.rows(); // Probability of each label
			probabilitiy *= labelAppearProba;			
			probabilityList.add(probabilitiy);  // Probability with j-th classification 
		}
		return probabilityList;
	}
	
	/**
	 * Compute Standard Deviation(标准差) of a number list
	 * 
	 * @param numbers - matrix to compute Standard Deviation
	 * @return Variance of numbers
	 */
	private double computeStandardDeviation(Matrix numbers) {
		double average = computeAverage(numbers); // Get average of numbers
		double variance = 0; 		// Variance(方差) of numbers
		for (int j = 0; j < numbers.columns(); j++) {
			variance += Math.pow(numbers.at(0, j) - average, 2);
		}
		return Math.sqrt(variance / numbers.columns());
	}

	/**
	 * Compute average of numbers
	 * 
	 * @param numbers - numbers to compute numbers
	 * @return Average of numbers
	 */
	private double computeAverage(Matrix numbers) {
		double sum = 0;
		for (int j = 0; j < numbers.columns(); j++) {
			sum += numbers.at(0, j);
		}
		return sum / numbers.columns();
	}

	/**
	 * Generate training result
	 * 
	 * @param labelMap - labels and theirs appearances count.
	 */
	private void generateTrainingResult(Map<Integer, Matrix> labelMap) {
		int count = 0;
		int dataRowCount = trainData.columns() * labelMap.keySet().size();
		trainResult = new Matrix(dataRowCount, 3);
		Set<Entry<Integer, Matrix>> entrySet = labelMap.entrySet();		
		for(Entry<Integer, Matrix> entry : entrySet) {		
			Matrix dataWithSameLabel = entry.getValue();
			// Count occurrences of each label
			eachlabelCount.put(entry.getKey(), dataWithSameLabel.rows());
			
			Matrix reverse = dataWithSameLabel.t(); // Reverse of matrix aClass
			for (int i = 0; i < reverse.rows(); i++) {
				double aver = computeAverage(reverse.at(new Range(i, i + 1),
						new Range(0, reverse.columns())));
				double stdDeviation = computeStandardDeviation(reverse.at(
						new Range(i, i + 1), new Range(0, reverse.columns())));
				trainResult.add(count, 0, entry.getKey());
				trainResult.add(count, 1, aver);
				trainResult.add(count, 2, stdDeviation);
				
				count++;
			}
		}
	}

	/**
	 * Group training data by class
	 * 
	 * @return map, key for label, value for data with the label.
	 */
	private Map<Integer, Matrix> groupTraningDataByLabel() {
		Map<Integer, Matrix> labelMap = new HashMap<Integer, Matrix>();
		for (int i = 0; i < trainData.rows(); i++) {
			int label = (int) trainLabel.at(i, 0); // label of a class
			if(!labelMap.containsKey(label)) {
				labelMap.put(label, trainData.row(i));
			} else {
				Matrix oldMatrix = labelMap.get(label);
				oldMatrix.mergeAfterRow(trainData.row(i));
				labelMap.put(label, oldMatrix);
			}		
		}
		return labelMap;
	}

	/**
	 * Check the validation of training data inputed.
	 * 
	 * @throws NullPointerException - trainData or trainLabel is null.
	 * @throws IllegalArgumentException
	 *            - Data in parameter trainingLabel does not match with 
	 *              data in parameter trainingData
	 */
	private void isTrainingDataValid(Matrix trainData, Matrix trainLabel) {
		// Check whether train data is valid
		if (trainData == null || trainLabel == null) {
			throw new NullPointerException("Training data is null");
		}

		if(trainData.rows() != trainLabel.rows()) {
			throw new IllegalArgumentException("Size of TrainingLabel does not match " +
					"with that of trainingData.");
		}
	}
}
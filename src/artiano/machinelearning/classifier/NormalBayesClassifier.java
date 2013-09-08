/**
 * 	NormalBayesClassifier.java
 */
package artiano.machinelearning.classifier;

import java.util.*;

import artiano.core.operation.Preservable;
import artiano.core.structure.Matrix;
import artiano.core.structure.Range;

/**
 * <p>
 * Description: Normal Bayes classifier
 * </p>
 * 
 * @author JohnF Nash
 * @version 1.0.0
 * @date 2013-8-27
 * @function
 * @since 1.0.0
 */
public class NormalBayesClassifier extends Preservable {
	private static final long serialVersionUID = 1L;

	// Training data
	private Matrix trainingData;
	// class label to train data
	private Matrix trainingLabel;
	// Training result
	private Matrix trainingResult;
	// Count of all labels
	private Map<Integer, Integer> eachlabelCount = 
			new LinkedHashMap<Integer, Integer>();

	/**
	 * constructor
	 */
	public NormalBayesClassifier() {
	}

	/**
	 * constructor
	 * 
	 * @param trainingData  - training data
	 * @param trainLabel - Labels of training data.
	 */
	public NormalBayesClassifier(Matrix trainingData, Matrix trainLabel) {
		this.trainingData = trainingData;
		this.trainingLabel = trainLabel;
	}

	/**
	 * Train data
	 * 
	 * @param trainingData - training data
	 * @param trainingLabel - labels for training data
	 * @param var_idx - starting index of feature to train
	 * @param labelAttrIndex - column index of label for each training data
	 * @return whether the training success.
	 */
	public boolean train(Matrix trainingData, Matrix trainingLabel,
			int labelAttrIndex) {
		try {
			// Check whether train data is valid
			isTrainingDataValid(trainingData, trainingLabel, labelAttrIndex);
		} catch (Exception e) {
			return false; // The data training fails
		}

		this.trainingData = trainingData;
		this.trainingLabel = trainingLabel;

		Map<Integer, Matrix> labelMap = 
				new LinkedHashMap<Integer, Matrix>(); // Store labels and its data
		// Cluster training data by class
		groupTraningDataByClass(labelAttrIndex, labelMap); 
		generateTrainingResult(labelMap); // Generate training result
		return true;
	}

	/**
	 * Predict classification of a single sample
	 * 
	 * @param sample - sample to get its classification
	 * @return classification of the sample
	 * @throws Exception
	 */
	public int predict(Matrix sample) {
		if (sample.rows() > 1) {
			throw new IllegalArgumentException(
				"The test sample matrix can only be one row. For"
				+ " multiple rows, please use method predict(Matrix sample, Matrix result)");
		}

		/* Get the classification by finding the max probability */
		List<Double> probabilityList = new ArrayList<Double>();

		// Get label list
		List<Integer> labelList = new ArrayList<Integer>(
				eachlabelCount.keySet());
		for (int j = 0; j < labelList.size(); j++) {
			double probabilitiy = 1;
			for (int k = 0; k < sample.columns(); k++) {
				// Attention: j * sample.columns() + k
				double aver = 
						trainingResult.at(j * sample.columns() + k, 1); 
				double stdDeviation = 
						trainingResult.at(j * sample.columns()+ k, 2);

				double a = (1.0 / (Math.sqrt(Math.PI * 2) * stdDeviation));
				double b = Math.pow((sample.at(0, k) - aver), 2);
				double c = 2 * Math.pow(stdDeviation, 2);
				// Gaussian distribution
				probabilitiy *= (a * Math.pow(Math.E, -1 * b / c)); 
			}
			double labelAppearProba = eachlabelCount.get(labelList.get(j))
					* 1.0 / trainingData.rows(); // Probability of each label
			probabilitiy *= labelAppearProba;
			// Probability with j-th classification
			probabilityList.add(probabilitiy); 
		}

		/* Get the classification by searching the max probability */
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

	/**
	 * Predict classification of multiple samples
	 * 
	 * @param samples - sample to get its classification
	 * @param result - predict result
	 * @return
	 */
	public int predict(Matrix samples, Matrix result) {
		for (int i = 0; i < samples.rows(); i++) {
			int predictResult = predict(samples.row(i));
			result.set(i, 0, predictResult);
		}
		return 0;
	}

	/**
	 * Compute Standard Deviation(±ê×¼²î) of a number list
	 * 
	 * @param numbers - matrix to compute Standard Deviation
	 * @return Variance of numbers
	 */
	private double computeStandardDeviation(Matrix numbers) {
		double average = computeAverage(numbers); // Get average of numbers
		double variance = 0; // Variance(·½²î) of numbers
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
		Set<Integer> keys = labelMap.keySet(); // class labels
		Iterator<Integer> iter = keys.iterator();
		int count = 0;

		int dataRowCount = (trainingData.columns() - 1) * keys.size();
		trainingResult = new Matrix(dataRowCount, 3);
		while (iter.hasNext()) {
			Integer key = iter.next();
			Matrix aClass = labelMap.get(key); // Data with same label
			// Count occurrences of each label
			eachlabelCount.put(key, aClass.rows()); 

			Matrix reverse = aClass.t(); // Reverse of matrix aClass
			for (int i = 0; i < reverse.rows(); i++) {
				double aver = computeAverage(reverse.at(new Range(i, i + 1),
						new Range(0, reverse.columns())));
				double stdDeviation = computeStandardDeviation(reverse.at(
						new Range(i, i + 1), new Range(0, reverse.columns())));
				trainingResult.add(count, 0, key);
				trainingResult.add(count, 1, aver);
				trainingResult.add(count, 2, stdDeviation);

				count++;
			}
		}
	}

	/**
	 * Group training data by class
	 * 
	 * @param labeAttrlndex - column index of label for each training data.
	 * @param labelMap - labels and theirs appearances count.
	 */
	private void groupTraningDataByClass(int labeAttrlndex,
			Map<Integer, Matrix> labelMap) {
		for (int i = 0; i < trainingData.rows(); i++) {
			int label = (int) trainingLabel.at(i, 0); // label of a class
			// get feature vector
			Matrix matrix = new Matrix(1, trainingData.columns() - 1);
			for (int m = 0; m < matrix.columns(); m++) {
				if (m < labeAttrlndex) {
					matrix.set(0, m, trainingData.at(i, m));
				} else if (m >= labeAttrlndex) {
					matrix.set(0, m, trainingData.at(i, m + 1));
				}
			}

			if (!labelMap.containsKey(label)) { // A new class
				labelMap.put(label, matrix);

			} else {
				Matrix oldMatrix = labelMap.get(label);
				oldMatrix.mergeAfterRow(matrix);
			}
		}
	}

	/**
	 * Check the validation of training data inputed.
	 * 
	 * @param trainingData - training data
	 * @param trainingLabel - labels for training data
	 * @param labelAttrIndex - column index of label for each training data
	 * @throws IllegalArgumentException
	 *             - when labelAttrIndex out of range or Data in parameter
	 *             trainingResponse does not match with data in parameter
	 *             trainingData
	 */
	private void isTrainingDataValid(Matrix trainingData, Matrix trainingLabel,
			int labelAttrIndex) {
		// Check whether train data is valid
		if (trainingData == null || trainingLabel == null) {
			throw new NullPointerException("Training data is null");
		}

		if (labelAttrIndex < 0 || labelAttrIndex >= trainingData.columns()) {
			throw new IllegalArgumentException(
					"Parameter labelAttrIndex out of range.");
		}

		for (int i = 0; i < trainingData.rows(); i++) {
			if (trainingData.at(i, labelAttrIndex) != trainingLabel.at(i, 0)) {
				throw new IllegalArgumentException(
						"Data in parameter trainingResponse does not"
								+ " match with data in parameter trainingData.");
			}
		}
	}
}
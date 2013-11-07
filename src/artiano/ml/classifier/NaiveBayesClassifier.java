/**
 * 	NormalBayesClassifier.java
 */
package artiano.ml.classifier;

import java.util.*;
import java.util.Map.Entry;

import artiano.core.structure.*;
import artiano.core.structure.Table.TableRow;

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
public class NaiveBayesClassifier extends Classifier {
	private static final long serialVersionUID = -1923319469209965644L;
	
	private Matrix trainData;
	private Table trainLabel;
	// Training result
	private Table trainResult;
	// Count of all labels
	private Map<Object, Integer> eachlabelCount = 
			new LinkedHashMap<Object, Integer>();	
	
	/** Train data
	 * @param trainSet training data
	 * @param trainLabel label of training data
	 * @param isAttributeContinuous  array of boolean that indicate whether 
	 *             corresponding attribute is continuous or discrete.
	 * @return whether the training successes or not
	 */
	public boolean train(Table trainSet, Table trainLabel, 
			boolean[] isAttributeContinuous) {		
		try {
			// Check whether train data is valid
			isTrainingDataValid(trainSet, trainLabel);
		} catch (Exception e) {
			return false; // The data training fails
		}
		this.trainData = trainSet.toMatrix();
		this.trainLabel = trainLabel;

		// Group training data by class
		Map<Object, Matrix> labelMap = groupTraningDataByLabel();	
		generateTrainingResult(labelMap); // Generate training result
		return true;
	}
	
	/**
	 * Classify samples
	 * 
	 * @param samples - sample to get its classification
	 * @param k for KNearest only, here -1 is ok.
	 * @return classification result.
	 */
	public Table predict(Table samples, int k) {		
		Matrix samplesMat = samples.toMatrix();
		Table result = new Table();
		result.addAttribute(new NominalAttribute("label"));
		for (int i = 0; i < samples.rows(); i++) {			
			TableRow tableRow = result.new TableRow();
			Object predictResult = classifySingleData(samplesMat.row(i));
			tableRow.set(0, predictResult);
			result.push(tableRow);
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
	private Object classifySingleData(Matrix sample) {
		if (sample.rows() > 1) {
			throw new IllegalArgumentException(
				"The test sample matrix can only be one row. For"
				+ " multiple rows, please use method predict(Matrix sample, Matrix result)");
		}

		/* Get the classification by finding the max probability */
		List<Object> labelList = 
			new ArrayList<Object>(eachlabelCount.keySet());						
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
			List<Object> labelList) {
		List<Double> probabilityList = new ArrayList<Double>();		
		for (int j = 0; j < labelList.size(); j++) {
			double probabilitiy = 1;
			for (int k = 0; k < sample.columns(); k++) {				
				double aver = 
					(double) trainResult.at(j * sample.columns() + k , 0); 
				double stdDeviation = 
					(double) trainResult.at(j * sample.columns() + k, 1);
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
	private void generateTrainingResult(Map<Object, Matrix> labelMap) {
		trainResult = new Table();
		trainResult.addAttribute(new NumericAttribute("average"));
		trainResult.addAttribute(new NumericAttribute("stdDeviation"));
		trainResult.addAttribute(new NominalAttribute("label"));
		
		Set<Entry<Object, Matrix>> entrySet = labelMap.entrySet();		
		for(Entry<Object, Matrix> entry : entrySet) {		
			Matrix dataWithSameLabel = entry.getValue();
			// Count occurrences of each label
			eachlabelCount.put(entry.getKey(), dataWithSameLabel.rows());
			
			Matrix reverse = dataWithSameLabel.t(); // Reverse of matrix aClass	
			for (int i = 0; i < reverse.rows(); i++) {
				TableRow tableRow = trainResult.new TableRow(); 
				double aver = computeAverage(reverse.at(new Range(i, i + 1),
						new Range(0, reverse.columns())));
				double stdDeviation = computeStandardDeviation(reverse.at(
						new Range(i, i + 1), new Range(0, reverse.columns())));
				tableRow.set(0, aver);
				tableRow.set(1, stdDeviation);
				tableRow.set(2, entry.getKey());
				trainResult.push(tableRow);
			}
		}
	}

	/**
	 * Group training data by class
	 * 
	 * @return map, key for label, value for data with the label.
	 */
	private Map<Object, Matrix> groupTraningDataByLabel() {
		Map<Object, Matrix> labelMap = new HashMap<Object, Matrix>();
		for (int i = 0; i < trainData.rows(); i++) {
			Object label = trainLabel.at(i, 0); // label of a class
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
	private void isTrainingDataValid(Table trainData, Table trainLabel) {
		// Check whether train data is valid
		if (trainData == null || trainLabel == null) {
			throw new NullPointerException("Training data is null");
		}

		/* Check whether all data in trainData is numeric. */
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
/**
 * 	NormalBayesClassifier.java
 */
package artiano.machinelearning.classifier;

import java.io.*;
import java.util.*;

import artiano.core.structure.Matrix;
import artiano.core.structure.Range;

/**
 * <p>Description: Normal Bayes classifier</p>
 * @author JohnF Nash
 * @version 1.0.0
 * @date 2013-8-27
 * @function 
 * @since 1.0.0
 */
public class NormalBayesClassifier {
	//Training data
	private Matrix trainingData;
	//class label to train data
	private Matrix trainingResponse;
	//Training result
	private Matrix trainingResult;
	//Count of all labels
	private Map<Integer,Integer> eachlabelCount = 
			new LinkedHashMap<Integer,Integer>();
	//list of labels
	private List<Integer> labelList = new ArrayList<Integer>();
	//Count of class 
	private int countOfClasses = 0;
	//Count of training data rows
	private int trainingDataRowCount = 0; 
	
	/**
	 *  constructor
	 */
	public NormalBayesClassifier() {			
	}
	
	/**
	 *  constructor
	 *  @param trainingData - training data
	 *  @param trainResponse - Labels of training data.
	 */
	public NormalBayesClassifier(Matrix trainingData, Matrix trainResponse) {		
		this.trainingData = trainingData;
		this.trainingResponse = trainResponse;
	}
	
	/**
	 * Train data
	 * @param trainingData	-	training data
	 * @param trainingResponse - labels for training data
	 * @param var_idx - starting index of feature to train
	 * @param labelAttrIndex - column index of label for each training data 
	 * @return whether the training success.
	 */
	public boolean train(Matrix trainingData, Matrix trainingResponse,
			int labelAttrIndex) {		
		try {
			//Check whether train data is valid
			isTrainingDataValid(trainingData, trainingResponse, labelAttrIndex);
		} catch(Exception e) {
			return false;			//The data training fails
		}
		
		this.trainingData = trainingData;
		this.trainingResponse = trainingResponse;		
		this.trainingDataRowCount = trainingData.rows();
		
		Map<Integer, Matrix> labelMap = 
				new LinkedHashMap<Integer, Matrix>();     //Store labels and its data
		groupTraningDataByClass(labelAttrIndex, labelMap);	 //Cluster training data by class			    	   
	    generateTrainingResult(labelMap);	    //Generate training result	
		return true;
	}	
	
	/**
	 *  Predict classification of a single sample
	 * @param sample - sample to get its classification
	 * @return classification of the sample
	 * @throws Exception 
	 */
	public int predict(Matrix sample) {
		if(sample.rows() > 1) {
			throw new IllegalArgumentException("The test sample matrix can only be one row. For" +
					" multiple rows, please use method predict(Matrix sample, Matrix result)");
		}
		
		/* Get the classification by finding the max probability  */
		List<Double> probabilityList = 
				new ArrayList<Double>();
		for(int j=0; j<labelList.size(); j++) {
			double probabilitiy = 1;
			for(int k=0; k<sample.columns(); k++) {
				double aver = 
					trainingResult.at(j * sample.columns() + k, 1);    //Attention:j * sample.columns() + k					
				double stdDeviation = trainingResult.at(j * sample.columns() + k, 2);
				
				double a = (1.0 / ( Math.sqrt(Math.PI * 2) * stdDeviation));
				double b = Math.pow((sample.at(0, k)-aver) , 2);
				double c = 2 * Math.pow(stdDeviation, 2);
				probabilitiy *=  (a * Math.pow(Math.E, -1 * b / c)) ;   //Gaussian distribution
			}				
			double labelAppearProba =  eachlabelCount.get(labelList.get(j)) * 1.0 
					   / trainingDataRowCount;		//Probability of each label
			probabilitiy *= labelAppearProba ; 
			probabilityList.add(probabilitiy);		//Probability with j-th classification
		}
		
		/* Get the classification by searching the max probability */
		double maxPorba = probabilityList.get(0);
		int maxIndex = 0;
		for(int m=1; m<probabilityList.size(); m++) {
			if(probabilityList.get(m) > maxPorba) {
				maxPorba = probabilityList.get(m);
				maxIndex = m;
			}
		}			
		return labelList.get(maxIndex);		 	//Return the classification
	}
	
	/**
	 *  Predict classification of multiple samples
	 * @param samples - sample to get its classification
	 * @param result - predict result
	 * @return
	 */
	public int predict(Matrix samples, Matrix result) {		
		for(int i=0; i<samples.rows(); i++) {
		   int predictResult = predict(samples.row(i));
		   result.set(i, 0, predictResult);
		}
		return 0;
	}

	/**
	 * Save the training model to the file
	 * @param fileName - name of the file to store the training model.
	 */
	public void save(String fileName) {
		File file = new File(fileName);
		PrintWriter writer = null;		
		if(!file.exists()) {
			try {
				file.createNewFile();
				writer = new PrintWriter(file);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		} else {
			return;
		}
		
		writer.println("trainingDataRowCount:\t" + trainingDataRowCount); //training Data Row Count
		
		writer.println("var_count:\t" + trainingData.columns());   //Count of attributes		
		
		StringBuffer labels = new StringBuffer();		//Labels 
		StringBuffer labelCount = new StringBuffer();	//Each label count
		for(int i=0; i<labelList.size(); i++) {
			if(i < labelList.size() - 1) {
				labels.append(labelList.get(i) + "\t");
				labelCount.append(eachlabelCount.get(labelList.get(i)) + "\t");
			} else {
				labels.append(labelList.get(i));
				labelCount.append(eachlabelCount.get(labelList.get(i)));
			}			
		}								
		writer.println("labels:");		
		writer.println("\tcount:\t" + labelList.size());
		writer.println("\tdata:\t" + labels.toString());			//Labels
		writer.println("\tdata_count:\t" + labelCount.toString());			//Each label counts
		
		writer.println("training result:  ");				//Training result
		writer.println("\tLabel \t Avg \t	Standard Deviation");
		for(int i=0; i<trainingResult.rows(); i++) {
			writer.print("\t");
			for(int j=0; j<trainingResult.columns(); j++) {		
				if(j < trainingResult.columns() - 1) {
					writer.print(trainingResult.at(i, j) + "\t");
				} else {
					writer.print(trainingResult.at(i, j));
				}				
			}			
			writer.println();
		}
		
		writer.flush();
		writer.close();	
	}
	
	/**
	 * Load a training model
	 * @param fileName - name of the file where the training model stored.
	 */	
	public void load(String fileName) {
		File file = new File(fileName);
		if(!file.exists()) {
			System.out.println("Can not find the training model.");
			return;
		}
		
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();			
		}
				
		try {
			String trainingDataRowCountStr = input.readLine().trim();  			
			this.trainingDataRowCount = 
				Integer.parseInt((trainingDataRowCountStr.split("[\t]"))[1]);  //Get training Data Row Count
			
			String var_countStr = input.readLine().trim();			
			int var_count = 
					Integer.parseInt((var_countStr.split("[\t]"))[1]);  //Get number of attributes
			
			input.readLine();			
			String labelCountStr = input.readLine().trim();	//Count of label
			int labelCount = 
					Integer.parseInt((labelCountStr.split("[\t]"))[1]);
			
			String labelsStr = input.readLine().trim();	//Labels
			String[] tempArr = labelsStr.split("[\t]");
			labelList.clear();			
			for(int i=1; i<tempArr.length; i++) {
				labelList.add(Integer.parseInt(tempArr[i]));
			}
			
			String labelCounts = input.readLine().trim();		//Each label count
			String[] tempCountsArr = labelCounts.split("[\t]");
			eachlabelCount.clear();
			for(int i=1; i<tempCountsArr.length; i++) {
				eachlabelCount.put(labelList.get(i - 1), Integer.parseInt(tempCountsArr[i]));
			}
			
			input.readLine();
			input.readLine();
			int totalDataRow = (var_count - 1) * labelCount;	//number of rows for training data
			trainingResult = new Matrix(totalDataRow, 3);
			//Read training results from the file to construct training result matrix.
			for(int i=0; i<totalDataRow; i++) {		
				String line = input.readLine().trim();
				String[] dataItem = line.split("[\t]");				
				trainingResult.add(i, 0, Double.parseDouble(dataItem[0]));
				trainingResult.add(i, 1, Double.parseDouble(dataItem[1]));
				trainingResult.add(i, 2, Double.parseDouble(dataItem[2]));
			}
			input.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}					
	}
	
	/**
	 * Compute Standard Deviation(±ê×¼²î) of a number list
	 * @param numbers - matrix to compute Standard Deviation 
	 * @return Variance of numbers
	 */
	private double computeStandardDeviation(Matrix numbers) {
		double average = computeAverage(numbers);  //Get average of numbers
		double variance = 0;	//Variance(·½²î) of numbers
		for(int j=0; j<numbers.columns(); j++) {
			variance += Math.pow(numbers.at(0,j) - average, 2);			
		}		
		return Math.sqrt(variance / numbers.columns());
	}
	
	/**
	 * Compute average of numbers
	 * @param numbers - numbers to compute numbers
	 * @return Average of numbers
	 */
	private double computeAverage(Matrix numbers) {
		double sum = 0;		
		for(int j=0; j< numbers.columns(); j++) {
			sum += numbers.at(0, j);
		}
		return sum / numbers.columns();
	}	
	
	
	/**
	 * Generate training result 
	 * @param labelMap - labels and theirs appearances count.
	 */
	private void generateTrainingResult(Map<Integer, Matrix> labelMap) {
		Set<Integer> keys = labelMap.keySet();		//class labels
		Iterator<Integer> iter = keys.iterator();
	    int count = 0;
	    trainingResult = 
	    	new Matrix( (trainingData.columns() - 1) * countOfClasses, 3);
	    while(iter.hasNext()) {
	    	Integer key = iter.next();
	    	Matrix aClass = labelMap.get(key);		//Data with same label	
	    	eachlabelCount.put(key, aClass.rows());		//Count occurrences of each label
	    	
	    	Matrix reverse = aClass.t();		//Reverse of matrix aClass	    	
	    	for(int i=0; i<reverse.rows(); i++) {
	    		double aver = computeAverage(reverse.at(new Range(i, i+1),
	    					new Range(0, reverse.columns())) );
	    		double stdDeviation = 
	    				computeStandardDeviation(reverse.at(new Range(i, i+1),
		    					new Range(0, reverse.columns())));     	
	    		trainingResult.add(count, 0, key);
	    		trainingResult.add(count, 1, aver);
	    		trainingResult.add(count, 2, stdDeviation);
	    		
	    		count++;
	    	}
	    }
	}	
	
	
	/**
	 * Group training data by class 
	 * @param labeAttrlndex - column index of label for each training data.
	 * @param labelMap - labels and theirs appearances count. 
	 */
	private void groupTraningDataByClass(int labeAttrlndex,
			Map<Integer, Matrix> labelMap) {		
		for(int i=0; i<trainingData.rows(); i++) {		
			int label = (int)trainingResponse.at(i, 0);		//label of a class
			// get feature vector
			Matrix matrix = new Matrix(1, trainingData.columns() - 1);
			for(int m=0; m<matrix.columns(); m++) {
				if(m < labeAttrlndex) {
					matrix.set(0, m, trainingData.at(i, m));
				} else if(m >= labeAttrlndex) {
					matrix.set(0, m, trainingData.at(i, m + 1));
				}
			}
			
			if(!labelMap.containsKey(label)) {		//A new class
				labelMap.put(label, matrix);
				countOfClasses++;		//A new class appears;
				labelList.add(label);
				
			} else {
				Matrix oldMatrix = labelMap.get(label);
				oldMatrix.mergeAfterRow(matrix);
//				Matrix oldMatrix = labelMap.get(label).clone();
//				Matrix newMatrix = 
//						new Matrix(oldMatrix.rows() + 1, oldMatrix.columns());
//				for(int m=0; m<oldMatrix.rows(); m++) {
//					for(int n=0; n<oldMatrix.columns(); n++) {
//						newMatrix.set(m, n, oldMatrix.at(m, n));
//					}
//				}
//				
//				for(int m=0; m<newMatrix.columns(); m++) {
//					if(m < labeAttrlndex) {
//						newMatrix.set(newMatrix.rows() - 1, m, trainingData.at(i, m));
//					} else if(m >= labeAttrlndex) {
//						newMatrix.set(newMatrix.rows() - 1, m, trainingData.at(i, m + 1));
//					}
//				}		
//				labelMap.remove(label);
				//labelMap.put(label, newMatrix);		//Remove the old matrix and put the new one							
			}				
		}
	}

	/**
	 * Check the validation of training data inputed.
	 * @param trainingData - training data
	 * @param trainingResponse - labels for training data
	 * @param labelAttrIndex - column index of label for each training data
	 * @throws IllegalArgumentException - when labelAttrIndex out of range
	 * 		or  Data in parameter trainingResponse does not match with data in parameter trainingData
	 */
	private void isTrainingDataValid(Matrix trainingData,
			Matrix trainingResponse, int labelAttrIndex) {
		//Check whether train data is valid
		if(trainingData == null || trainingResponse == null) {
			throw new NullPointerException("Training data is null");
		}
		
		if(labelAttrIndex < 0 || labelAttrIndex >= trainingData.columns()) {
			throw new IllegalArgumentException("Parameter labelAttrIndex out of range.");
		}
		
		for(int i=0; i<trainingData.rows(); i++) {
			if(trainingData.at(i, labelAttrIndex) != trainingResponse.at(i, 0)) {
				throw new IllegalArgumentException("Data in parameter trainingResponse does not" +
						" match with data in parameter trainingData.");
			}
		}
	}	
}

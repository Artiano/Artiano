/**
 * 	NormalBayesClassifier.java
 */
package artiano.machinelearning.classifier;

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
	Matrix trainingData;
	//class label to train data
	Matrix trainingResponse;
	//Training result
	Matrix trainingResult;
	//Count of all labels
	Map<Integer,Integer> eachlabelCount = 
			new LinkedHashMap<Integer,Integer>();
	//list of labels
	List<Integer> labelList = new ArrayList<Integer>();
	//Count of class 
	int countOfClasses = 0;
	
	/**
	 *  constructor
	 */
	public NormalBayesClassifier() {			
	}
	
	/**
	 *  constructor
	 *  @param trainingData - training data
	 *  @param trainResponse - 构建训练样本的类别标签
	 */
	public NormalBayesClassifier(Matrix trainingData, Matrix trainResponse) {		
		this.trainingData = trainingData;
		this.trainingResponse = trainResponse;
	}
	
	/**
	 * Train data
	 * @param trainingData	-	training data
	 * @param trainingResponse - training label
	 * @param var_idx - starting index of feature to train
	 * @param labelAttrIndex - index of label attribute in training data 
	 * @return
	 */
	public boolean train(Matrix trainingData, Matrix trainingResponse,
			int labeAttrlndex) {		
		this.trainingData = trainingData;
		this.trainingResponse = trainingResponse;
		
		Map<Integer, Matrix> labelMap = 
				new LinkedHashMap<Integer, Matrix>();     //Store labels and its data
		clusterTraningDataByClass(labeAttrlndex, labelMap);	 //Cluster training data by class			    	   
	    generateTrainingResult(labelMap);	    //Generate training result	
		return true;
	}	
	
	/**
	 *  Predict classification of a sample
	 * @param sample - sample to get its classification
	 * @return classification of the sample
	 * @throws Exception 
	 */
	public int predict(Matrix sample) {
		if(sample.rows() > 1) {
			throw new IllegalArgumentException("The test sample matrix can only be one row. For" +
					" multiple rows, please use method predict(Matrix sample, Matrix result)");
		}
		
		List<Double> probabilityList = 
				new ArrayList<Double>();
		for(int j=0; j<countOfClasses; j++) {
			double probabilitiy = 1;
			for(int k=0; k<sample.columns(); k++) {
				double aver = 
					trainingResult.at(j * sample.columns() + k, 1);    //Attention:j * sample.columns() + k					
				double stdDeviation = trainingResult.at(j * sample.columns() + k, 2);
				
				double a = (1.0 / ( Math.sqrt(Math.PI * 2) * stdDeviation));
				double b = Math.pow((sample.at(0, k)-aver) , 2);
				double c = 2 * Math.pow(stdDeviation, 2);
				probabilitiy *=  (a * Math.pow(Math.E, -1 * b / c)) ;   //高斯分布
			}				
			double labelAppearProba =  eachlabelCount.get(labelList.get(j)) * 1.0 
					   / trainingData.rows();
			probabilitiy *= labelAppearProba ; 
			probabilityList.add(probabilitiy);		//Probability with j-th classification
	//		System.out.println("Classification " + labelList.get(j) + " : " + probabilitiy);
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
	 * Compute Standard Deviation(标准差) of a number list
	 * @param numbers - matrix to compute Standard Deviation 
	 * @return Variance of numbers
	 */
	private double computeStandardDeviation(Matrix numbers) {
		double average = computeAverage(numbers);  //Get average of numbers
		double variance = 0;	//Variance(方差) of numbers
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
	
	//Generate training result
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
	    
	    for(int i=0; i<trainingResult.rows(); i++) {
	    	for(int j=0; j<trainingResult.columns(); j++) {
	    		System.out.print(trainingResult.at(i, j) + "  ");
	    	}
	    	System.out.println();
	    }
	}	
	
	//Cluster training data by class
	private void clusterTraningDataByClass(int labeAttrlndex,
			Map<Integer, Matrix> labelMap) {		
		for(int i=0; i<trainingData.rows(); i++) {		
			int label = (int)trainingResponse.at(i, 0);		//label of a class
			if(!labelMap.containsKey(label)) {		//A new class
				Matrix matrix = new Matrix(1, trainingData.columns() - 1);
				for(int m=0; m<matrix.columns(); m++) {
					if(m < labeAttrlndex) {
						matrix.set(0, m, trainingData.at(i, m));
					} else if(m >= labeAttrlndex) {
						matrix.set(0, m, trainingData.at(i, m + 1));
					}
				}				
				labelMap.put(label, matrix);
				
				countOfClasses++;		//A new class appears;
				labelList.add(label);
				
			} else {
				Matrix oldMatrix = labelMap.get(label).clone();
				Matrix newMatrix = 
						new Matrix(oldMatrix.rows() + 1, oldMatrix.columns());
				for(int m=0; m<oldMatrix.rows(); m++) {
					for(int n=0; n<oldMatrix.columns(); n++) {
						newMatrix.set(m, n, oldMatrix.at(m, n));
					}
				}
				
				for(int m=0; m<newMatrix.columns(); m++) {
					if(m < labeAttrlndex) {
						newMatrix.set(newMatrix.rows() - 1, m, trainingData.at(i, m));
					} else if(m >= labeAttrlndex) {
						newMatrix.set(newMatrix.rows() - 1, m, trainingData.at(i, m + 1));
					}
				}		
				labelMap.remove(label);
				labelMap.put(label, newMatrix);		//Remove the old matrix and put the new one							
			}				
		}
	}
}

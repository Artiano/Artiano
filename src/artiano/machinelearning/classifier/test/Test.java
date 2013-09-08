package artiano.machinelearning.classifier.test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import artiano.core.structure.Matrix;
import artiano.core.structure.Range;
import artiano.machinelearning.classifier.DTreeClassifier;
import artiano.machinelearning.classifier.KDTree;
import artiano.machinelearning.classifier.NormalBayesClassifier;

/**
 * <p>Description: Classifiers Test</p>
 * @author JohnF Nash
 * @version 1.0.0
 * @date 2013-8-27
 * @function 
 * @since 1.0.0
 */
public class Test {
	
	//Training data(The first column is class label)
	static double[] inputArr = { 											
			1,14.1,2.02,2.4,18.8,103,2.75,2.92,.32,2.38,6.2,1.07,2.75,1060,
			1,13.94,1.73,2.27,17.4,108,2.88,3.54,.32,2.08,8.90,1.12,3.1,1260,
			1,13.05,1.73,2.04,12.4,92,2.72,3.27,.17,2.91,7.2,1.12,2.91,1150,				
			3,12.85,3.27,2.58,22,106,1.65,.6,.6,.96,5.58,.87,2.11,570,
			3,13.62,4.95,2.35,20,92,2,.8,.47,1.02,4.4,.91,2.05,550,
			1,13.56,1.71,2.31,16.2,117,3.15,3.29,.34,2.34,6.13,.95,3.38,795,
			1,13.41,3.84,2.12,18.8,90,2.45,2.68,.27,1.48,4.28,.91,3,1035,
			1,13.88,1.89,2.59,15,101,3.25,3.56,.17,1.7,5.43,.88,3.56,1095,
			1,13.24,3.98,2.29,17.5,103,2.64,2.63,.32,1.66,4.36,.82,3,680,
			1,13.05,1.77,2.1,17,107,3,3,.28,2.03,5.04,.88,3.35,885,
			1,14.21,4.04,2.44,18.9,111,2.85,2.65,.3,1.25,5.24,.87,3.33,1080,
			1,14.38,3.59,2.28,16,102,3.25,3.17,.27,2.19,4.9,1.04,3.44,1065,
			1,13.9,1.68,2.12,16,101,3.1,3.39,.21,2.14,6.1,.91,3.33,985,
			1,14.1,2.02,2.4,18.8,103,2.75,2.92,.32,2.38,6.2,1.07,2.75,1060,
			1,13.56,1.73,2.46,20.5,116,2.96,2.78,.2,2.45,6.25,.98,3.03,1120,
			2,12.6,1.34,1.9,18.5,88,1.45,1.36,.29,1.35,2.45,1.04,2.77,562,
			1,14.83,1.64,2.17,14,97,2.8,2.98,.29,1.98,5.2,1.08,2.85,1045,
			2,13.49,1.66,2.24,24,87,1.88,1.84,.27,1.03,3.74,.98,2.78,472,
			1,13.86,1.35,2.27,16,98,2.98,3.15,.22,1.85,7.22,1.01,3.55,1045,
			3,13.48,1.67,2.64,22.5,89,2.6,1.1,.52,2.29,11.75,.57,1.78,620,			
			2,12.33,1.1,2.28,16,101,2.05,1.09,.63,.41,3.27,1.25,1.67,680,
			2,12.64,1.36,2.02,16.8,100,2.02,1.41,.53,.62,5.75,.98,1.59,450,
			2,13.67,1.25,1.92,18,94,2.1,1.79,.32,.73,3.8,1.23,2.46,630,
			2,12.37,1.13,2.16,19,87,3.5,3.1,.19,1.87,4.45,1.22,2.87,420,
			2,12.17,1.45,2.53,19,104,1.89,1.75,.45,1.03,2.95,1.45,2.23,355,
			2,12.37,1.21,2.56,18.1,98,2.42,2.65,.37,2.08,4.6,1.19,2.3,678,
			2,13.11,1.01,1.7,15,78,2.98,3.18,.26,2.28,5.3,1.12,3.18,502,
			2,12.37,1.17,1.92,19.6,78,2.11,2,.27,1.04,4.68,1.12,3.48,510,
			2,13.34,.94,2.36,17,110,2.53,1.3,.55,.42,3.17,1.02,1.93,750,
			3,12.82,3.37,2.3,19.5,88,1.48,.66,.4,.97,10.26,.72,1.75,685,
			2,12.42,1.61,2.19,22.5,108,2,2.09,.34,1.61,2.06,1.06,2.96,345,
			2,12.77,3.43,1.98,16,80,1.63,1.25,.43,.83,3.4,.7,2.12,372,				
			3,12.2,3.03,2.32,19,96,1.25,.49,.4,.73,5.5,.66,1.83,510,	
			3,13.52,3.17,2.72,23.5,97,1.55,.52,.5,.55,4.35,.89,2.06,520,
			3,13.62,4.95,2.35,20,92,2,.8,.47,1.02,4.4,.91,2.05,550,
			3,12.25,3.88,2.2,18.5,112,1.38,.78,.29,1.14,8.21,.65,2,855,
			3,13.16,3.57,2.15,21,102,1.5,.55,.43,1.3,4,.6,1.68,830,
			3,13.88,5.04,2.23,20,80,.98,.34,.4,.68,4.9,.58,1.33,415,
			3,12.87,4.61,2.48,21.5,86,1.7,.65,.47,.86,7.65,.54,1.86,625,
			3,13.32,3.24,2.38,21.5,92,1.93,.76,.45,1.25,8.42,.55,1.62,650,
			3,13.08,3.9,2.36,21.5,113,1.41,1.39,.34,1.14,9.40,.57,1.33,550,
			3,13.5,3.12,2.62,24,123,1.4,1.57,.22,1.25,8.60,.59,1.3,500,
			3,12.79,2.67,2.48,22,112,1.48,1.36,.24,1.26,10.8,.48,1.47,480,
			3,13.27,4.28,2.26,20,120,1.59,.69,.43,1.35,10.2,.59,1.56,835,
			2,12.69,1.53,2.26,20.7,80,1.38,1.46,.58,1.62,3.05,.96,2.06,495,								
			2,12.08,2.08,1.7,17.5,97,2.23,2.17,.26,1.4,3.3,1.27,2.96,710,				
	};

	//Classification test samples
	static double testArr[] = { 									
			1,13.76,1.53,2.7,19.5,132,2.95,2.74,.5,1.35,5.4,1.25,3,1235,								
			2,12.33,1.1,2.28,16,101,2.05,1.09,.63,.41,3.27,1.25,1.67,680,				
			1,13.71,1.86,2.36,16.6,101,2.61,2.88,.27,1.69,3.8,1.11,4,1035,
			2,11.03,1.51,2.2,21.5,85,2.46,2.17,.52,2.01,1.9,1.71,2.87,407,
			1,14.83,1.64,2.17,14,97,2.8,2.98,.29,1.98,5.2,1.08,2.85,1045,
			1,13.86,1.35,2.27,16,98,2.98,3.15,.22,1.85,7.22,1.01,3.55,1045,
			1,14.1,2.16,2.3,18,105,2.95,3.32,.22,2.38,5.75,1.25,3.17,1510,
			1,14.12,1.48,2.32,16.8,95,2.2,2.43,.26,1.57,5,1.17,2.82,1280,
			1,13.75,1.73,2.41,16,89,2.6,2.76,.29,1.81,5.6,1.15,2.9,1320,
			2,11.82,1.47,1.99,20.8,86,1.98,1.6,.3,1.53,1.95,.95,3.33,495,
			2,12.42,1.61,2.19,22.5,108,2,2.09,.34,1.61,2.06,1.06,2.96,345,
			2,12.77,3.43,1.98,16,80,1.63,1.25,.43,.83,3.4,.7,2.12,372,
			2,12,3.43,2,19,87,2,1.64,.37,1.87,1.28,.93,3.05,564,
			3,12.6,2.46,2.2,18.5,94,1.62,.66,.63,.94,7.1,.73,1.58,695,
			3,12.25,4.72,2.54,21,89,1.38,.47,.53,.8,3.85,.75,1.27,720,
			3,12.53,5.51,2.64,25,96,1.79,.6,.63,1.1,5,.82,1.69,515,
			3,13.49,3.59,2.19,19.5,88,1.62,.48,.58,.88,5.7,.81,1.82,580,
			3,12.84,2.96,2.61,24,101,2.32,.6,.53,.81,4.92,.89,2.15,590,
			2,11.65,1.67,2.62,26,88,1.92,1.61,.4,1.34,2.6,1.36,3.21,562,	
			3,12.86,1.35,2.32,18,122,1.51,1.25,.21,.94,4.1,.76,1.29,630,
			3,12.88,2.99,2.4,20,104,1.3,1.22,.24,.83,5.4,.74,1.42,530,															
	};				
	
	
	public static void testNormalBayesClassifier() {
		int attrNum = 14;
		
		// ----------------------  Train data -------------------- 
		Matrix trainingData = 
				new Matrix(inputArr.length / attrNum, attrNum, inputArr);	 //Training data
		Matrix trainingResponse = new Matrix(trainingData.rows(), 1); // class labels
		for (int i = 0; i < trainingResponse.rows(); i++) {
			trainingResponse.set(i, 0, inputArr[i * trainingData.columns()]);
		}		
		Matrix samples = new Matrix(testArr.length / trainingData.columns(), 
				trainingData.columns(), testArr); // test examples
		
		NormalBayesClassifier classifier = new NormalBayesClassifier();
		boolean isTrainSucess = 
				classifier.train(trainingData, trainingResponse, 0);		//Train data
		if(!isTrainSucess) {
			System.out.println("Train fails.");
			return;
		}
		
		
		//----------------------   Save the training model -------------
		try {
			classifier.save("D:\\bayes.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		//----------------------  Predict -------------------------
		Matrix predictResult = new Matrix(samples.rows(), 1);
		classifier.predict(samples.at(new Range(0,samples.rows()),
				new Range(1, samples.columns())), predictResult); // Get the classification
		
		//Count correct rate of classification
		int count = 0;		
		for(int i=0; i<predictResult.rows(); i++) {			
			if(predictResult.at(i, 0) == samples.at(i, 0)) {
				count++;
			}
			System.out.println("Classification " + (int)predictResult.at(i, 0));
		}
		System.out.printf("Classification corrrection rate: %.2f%%", (count / (1.0 * samples.rows())) * 100);
		System.out.println();
		
		
		//Predict classification of a sample
		double[] sampleArr = {13.16,3.57,2.15,21,102,1.5,.55,.43,1.3,4,.6,1.68,830};
		Matrix testSample = new Matrix(1, trainingData.columns()-1, sampleArr);
		System.out.println("------------------------------------------");
		System.out.println("Classification of the test sample is " +
				classifier.predict(testSample) + ".");
		
		
		//-------------------- Load training model --------------------
		NormalBayesClassifier loadClassifier = null;				
		try {
			loadClassifier = 
				(NormalBayesClassifier) NormalBayesClassifier.load("D:\\bayes.txt");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("------------------------------------------");
		System.out.println("Prediction of the test sample is " +
				loadClassifier.predict(testSample) + ".");

	}


	public static void testDTreeClasifier() {					
		//File that store the training data
		String dataFilePath = 
				"src\\artiano\\machinelearning\\classifier\\test\\data.txt";
		
		ArrayList<String> attributeList = 
				new ArrayList<String>();
		ArrayList<ArrayList<String>> data = 
				new ArrayList<ArrayList<String>>();
		//Load training data
		int targetAttrIndex = 
				loadTrainingData(attributeList, data, dataFilePath); 		
		if(targetAttrIndex == -1) {
			System.out.println("Load training data fail.");
			return;
		}
		
		DTreeClassifier classifier = 
				new DTreeClassifier(data, attributeList, targetAttrIndex);
		classifier.train(data, attributeList, targetAttrIndex); //Train data
		try {
			classifier.save("D:\\decisionTree.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		 try {
			DTreeClassifier dTreeClassifier = 
				(DTreeClassifier) DTreeClassifier.load("D:\\decisionTree.txt");
			System.out.println(dTreeClassifier.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		 
		//-------------------------predict-------------------------------//
		String[] sampleArr = {"sunny", "hot", "high", "TRUE"};
		String[] sampleAttribute = {"outlook","temperature","humidity","windy"};
		List<String> sampleAttributeList = Arrays.asList(sampleAttribute);
		List<List<String>> sample = new ArrayList<List<String>>();
		sample.add(Arrays.asList(sampleArr));
		List<String> classificationList = 
				classifier.predict(sample, sampleAttributeList);
		for(int i=0; i<classificationList.size(); i++) {
			System.out.println("Classification: " + classificationList.get(i));
		}
				
	}

	//Load the training data
	//Return index of the target attribute	
	private static int loadTrainingData(ArrayList<String> attributeList,
			ArrayList<ArrayList<String>> data, String dataFilePath) {
		BufferedReader input = null;
		try {
			input = 
				new BufferedReader(new FileReader(dataFilePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		}
		
		int targetAttrIdx = -1;  //Index of target attribute index 
		try {
			//Target attribute index
			String targetAttrIdxStr = 
					(input.readLine().trim()).split("[\t]")[1];
			targetAttrIdx = Integer.parseInt(targetAttrIdxStr);

			//Attributes
			String[] attributes = 
					(input.readLine().trim()).split("[\t]");  //Attributes
			for(int i=0; i<attributes.length; i++) {
				attributeList.add(attributes[i].trim());
			}						
			
			//Training data
			String item = "";		
			int num = 0;
			while(! "".equals(item = input.readLine()) && 
					! (null == item) ) {
				data.add(new ArrayList<String>());  //Add a row of sample
				
				String[] attrValues = (item.trim()).split("[\t]");
				for(int i=0; i<attrValues.length; i++) {				
					data.get(num).add(attrValues[i].trim());
				}
				num++;
			}								
			input.close();						
			
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
		return targetAttrIdx;
	}	

	public static void testKDTree() {
		double[] trainingDataArr = {
				20, 20,
				10, 30,
				35, 25,
				55, 40,
				50, 30
		};
		
		int dimension = 2;
		Matrix traingingData = 
				new Matrix(trainingDataArr.length / dimension, dimension, trainingDataArr);
		KDTree tree = new KDTree();
		tree.buildKDTree(traingingData);
		System.out.println("bfs: ");
		tree.bfs();
		
		/*------------- Insert a data point to the kd-tree -------------*/
		double[] newPointArr = {35, 35};
		Matrix newPoint = new Matrix(1, dimension, newPointArr);
		tree.insert(newPoint);
		System.out.println("After inserting a point, bfs:");
		tree.bfs();		
	
		/*------------- Delete a data point -------------------------*/
		double[] pointDeletedArr = {55, 40};		
		Matrix point1 = new Matrix(1, dimension, pointDeletedArr);
		tree.delete(point1);
		System.out.println("After deleting a point, bfs:");
		tree.bfs();		
		
		/*------------------  Find nearest of a specified data point -----*/
		double[] point = {48.5, 30.6};
		Matrix target = new Matrix(1, dimension, point);
		Matrix nearest = tree.findNearest(target);   //Find nearest point of point target
		System.out.print("\nNearest data point is: (");
		for(int i=0; i<nearest.columns(); i++) {
			if(i < nearest.columns() - 1) {
				System.out.print(nearest.at(i) + ", ");
			} else {
				System.out.print(nearest.at(i));
			}			
		}
		System.out.println(")");					
	}
	
	public static void main(String[] args) {											
		testNormalBayesClassifier();
		//testDTreeClasifier();
		//testKDTree();
	}
}
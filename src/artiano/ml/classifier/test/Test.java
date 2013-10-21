package artiano.ml.classifier.test;

import java.io.*;
import java.util.*;

import artiano.core.structure.*;
import artiano.ml.classifier.*;

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
			2,12.77,3.43,1.98,16,80,1.63,1.25,.43,.83,3.4,.7,2.12,372,				
			3,12.2,3.03,2.32,19,96,1.25,.49,.4,.73,5.5,.66,1.83,510,				
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
			1,14.12,1.48,2.32,16.8,95,2.2,2.43,.26,1.57,5,1.17,2.82,1280,
			1,13.75,1.73,2.41,16,89,2.6,2.76,.29,1.81,5.6,1.15,2.9,1320,
			2,11.82,1.47,1.99,20.8,86,1.98,1.6,.3,1.53,1.95,.95,3.33,495,
			2,12.42,1.61,2.19,22.5,108,2,2.09,.34,1.61,2.06,1.06,2.96,345,			
			3,12.25,4.72,2.54,21,89,1.38,.47,.53,.8,3.85,.75,1.27,720,			
			3,12.86,1.35,2.32,18,122,1.51,1.25,.21,.94,4.1,.76,1.29,630,
			3,12.88,2.99,2.4,20,104,1.3,1.22,.24,.83,5.4,.74,1.42,530,															
	};				
	
	static double[] trainingDataArr = {
		/*
			20, 20,
			10, 30,
			2,  60,
			35, 25,
			55, 40,
			50, 30
		*/
		13.05,1.73,2.04,12.4,92,2.72,3.27,.17,2.91,7.2,1.12,2.91,				
		12.85,3.27,2.58,22,106,1.65,.6,.6,.96,5.58,.87,2.11,
		13.62,4.95,2.35,20,92,2,.8,.47,1.02,4.4,.91,2.05,
		13.56,1.71,2.31,16.2,117,3.15,3.29,.34,2.34,6.13,.95,3.38,			
		12.6,1.34,1.9,18.5,88,1.45,1.36,.29,1.35,2.45,1.04,2.77,
		12.2,3.03,2.32,19,96,1.25,.49,.4,.73,5.5,.66,1.83,					
		14.83,1.64,2.17,14,97,2.8,2.98,.29,1.98,5.2,1.08,2.85,		
		13.49,1.66,2.24,24,87,1.88,1.84,.27,1.03,3.74,.98,2.78,
		13.86,1.35,2.27,16,98,2.98,3.15,.22,1.85,7.22,1.01,3.55,
		13.48,1.67,2.64,22.5,89,2.6,1.1,.52,2.29,11.75,.57,1.78,			
		12.33,1.1,2.28,16,101,2.05,1.09,.63,.41,3.27,1.25,1.67,		
		12.64,1.36,2.02,16.8,100,2.02,1.41,.53,.62,5.75,.98,1.59,										
		13.56,1.73,2.46,20.5,116,2.96,2.78,.2,2.45,6.25,.98,3.03,
		13.32,3.24,2.38,21.5,92,1.93,.76,.45,1.25,8.42,.55,1.62,
		13.49,1.66,2.24,24,87,1.88,1.84,.27,1.03,3.74,.98,2.78,												
		13.08,3.9,2.36,21.5,113,1.41,1.39,.34,1.14,9.40,.57,1.33,
		13.5,3.12,2.62,24,123,1.4,1.57,.22,1.25,8.60,.59,1.3,		
		12.69,1.53,2.26,20.7,80,1.38,1.46,.58,1.62,3.05,.96,2.06,								
		12.08,2.08,1.7,17.5,97,2.23,2.17,.26,1.4,3.3,1.27,2.96,					
		13.76,1.53,2.7,19.5,132,2.95,2.74,.5,1.35,5.4,1.25,3,								
		12.33,1.1,2.28,16,101,2.05,1.09,.63,.41,3.27,1.25,1.67,							
		14.12,1.48,2.32,16.8,95,2.2,2.43,.26,1.57,5,1.17,2.82,
		13.75,1.73,2.41,16,89,2.6,2.76,.29,1.81,5.6,1.15,2.9,
		11.82,1.47,1.99,20.8,86,1.98,1.6,.3,1.53,1.95,.95,3.33,
		12.42,1.61,2.19,22.5,108,2,2.09,.34,1.61,2.06,1.06,2.96,			
		//12.25,4.72,2.54,21,89,1.38,.47,.53,.8,3.85,.75,1.27,			
		//12.86,1.35,2.32,18,122,1.51,1.25,.21,.94,4.1,.76,1.29,			
	};
	
	static double[] trainLabelArr = {
		/*    1, 
			2, 
			3,
			1,			 
			1,
			1
		*/
		
		1, 3, 3, 2, 3, 1, 2, 1, 3, 2, 2, 1, 3, 2, 1, 3, 3, 2, 2, 1,
		2, 1, 1, 2, 2		
	};
	
	public static void testNaiveBayesClassifier() {
		int attrNum = 14;
		
		// ----------------------  Train data -------------------- 
		Matrix trainingData = 
				new Matrix(inputArr.length / attrNum, attrNum, inputArr);	 //Training data
		Matrix trainingLabel = new Matrix(trainingData.rows(), 1); // class labels
		for (int i = 0; i < trainingLabel.rows(); i++) {
			trainingLabel.set(i, 0, inputArr[i * trainingData.columns()]);
		}		
		Matrix samples = new Matrix(testArr.length / trainingData.columns(), 
				trainingData.columns(), testArr); // test examples
		
		NaiveBayesClassifier classifier = new NaiveBayesClassifier();
		boolean isTrainSucess = 
				classifier.train(trainingData, trainingLabel, 0);		//Train data
		if(!isTrainSucess) {
			System.out.println("Train fails.");
			return;
		}
		
		
		//----------------------   Save the training model -------------
		try {
			classifier.save("bayesS.txt");
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
		@SuppressWarnings("unused")
		NaiveBayesClassifier loadClassifier = null;				
		try {
			loadClassifier = 
				(NaiveBayesClassifier) NaiveBayesClassifier.load("bayesS.txt");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("------------------------------------------");
//		System.out.println("Prediction of the test sample is " +
//				loadClassifier.predict(testSample) + ".");

	}

	
	public static void testDTreeClasifier() {					
		//File that store the training data
		String dataFilePath = 
				"src\\artiano\\ml\\classifier\\test\\data.txt";
		
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
		String targetAttribute = "play";
		DTreeClassifier classifier = 
			new DTreeClassifier(data, attributeList, targetAttribute);
		classifier.train(); //Train data
		try {
			classifier.save("D:\\decisionTree.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		 try {
			artiano.ml.classifier.DTreeClassifier dTreeClassifier = 
					(artiano.ml.classifier.DTreeClassifier)artiano.ml.classifier.DTreeClassifier.load("D:\\decisionTree.txt");
			System.out.println(dTreeClassifier.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		 
		//-------------------------predict-------------------------------//
		String[] sampleArr = {"rainy", "cool", "normal", "FALSE"};
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
	
	public static void testDTreeClassifierUsingC4_5() {
/*		
		//File that store the training data
		String dataFilePath = 
			"src\\artiano\\ml\\classifier\\test\\data.txt";		
		ArrayList<String> attributeList = new ArrayList<String>();
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		//Load training data
		int loadResult = 
			loadTrainingData(attributeList, data, dataFilePath); 		
		if(loadResult == -1) {
			System.out.println("Load training data fail.");
			return;
		}		

		String targetAttribute = "play";
		DTreeClassifier dtree = 
			new DTreeClassifier(data, attributeList, targetAttribute);
		dtree.train();		
		
		try {
			dtree.save("D:\\decisionTree.txt");
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
			dtree.predict(sample, sampleAttributeList);
		for(int i=0; i<classificationList.size(); i++) {
			System.out.println("Classification: " + classificationList.get(i));
		}
*/
		
		//File that store the training data
		String dataFilePath = 
			"src\\artiano\\ml\\classifier\\test\\data3.txt";		
		ArrayList<String> attributeList = new ArrayList<String>();
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		//Load training data
		int loadResult = 
			loadTrainingData(attributeList, data, dataFilePath); 		
		if(loadResult == -1) {
			System.out.println("Load training data fail.");
			return;
		}		

		String targetAttribute = "PlayGolf";
		boolean[] isContinuous = new boolean[]{
			false, true, true, false, false	
		};
		
		DTreeClassifierUsingC4_5 dtree = 
			new DTreeClassifierUsingC4_5(data, attributeList, targetAttribute, isContinuous);
		dtree.train();		
		try {
			dtree.save("D:\\decisionTree.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		//-------------------------predict-------------------------------//
		String[] sampleArr = {"rainy", "68", "79", "FALSE"};
		String[] sampleAttribute = 
			{"Outlook", "Temperature", "Humidity", "Windy", "PlayGolf"};
		List<String> sampleAttributeList = Arrays.asList(sampleAttribute);
		List<List<String>> sample = new ArrayList<List<String>>();
		sample.add(Arrays.asList(sampleArr));		
		List<String> classificationList = 
			dtree.predict(sample, sampleAttributeList);
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
		int dimension = 2;
		Matrix trainData = 
				new Matrix(trainingDataArr.length / dimension, dimension, trainingDataArr);
		Matrix trainLabel = 
				new Matrix(trainingDataArr.length / dimension, 1, trainLabelArr);
		KDTree tree = new KDTree(trainData, trainLabel);
		System.out.println("bfs: ");
		tree.bfs();
						
		/*------------------  Find nearest of a specified data point -----*/
		double[] point = {45, 32};
		Matrix target = new Matrix(1, dimension, point);
		List<KDTree.KDNode> kNearest = tree.findKNearest(target, 3);  //Find 3-nearest point of point target
		System.out.println("\n3 Nearest data point is:");
		for(int i=0; i<kNearest.size(); i++) {
			Matrix iNearest = kNearest.get(i).nodeData;
			System.out.print("(");
			for(int j=0; j<iNearest.columns(); j++) {
				if(j < iNearest.columns() - 1) {
					System.out.print(iNearest.at(j) + ", ");
				} else {
					System.out.print(iNearest.at(j));
				}		
			}
			System.out.println(")");
		}			
	}
	
	public static void testKNearest() {
		int attrNum = 12;
		//Train data
		Matrix trainData = 
			new Matrix(trainingDataArr.length / attrNum, attrNum, trainingDataArr);		
		//Train labels
		Matrix trainLabel = 
				new Matrix(trainLabelArr.length, 1, trainLabelArr);
		KNearest kNearest = new KNearest();
		kNearest.train(trainData, trainLabel, false);  //Train data
		
		double[] sampleArr = {
			/*	22, 40  */
				14.83,1.64,2.17,14,97,2.8,2.98,.29,1.98,5.2,1.08,2.85,
				13.49,1.66,2.24,24,87,1.88,1.84,.27,1.03,3.74,.98,2.78,
				13.86,1.35,2.27,16,98,2.98,3.15,.22,1.85,7.22,1.01,3.55,
				13.48,1.67,2.64,22.5,89,2.6,1.1,.52,2.29,11.75,.57,1.78,			
				12.33,1.1,2.28,16,101,2.05,1.09,.63,.41,3.27,1.25,1.67,				
				14.22,1.7,2.3,16.3,118,3.2,3,.26,2.03,6.38,.94,3.31,
				12.33,1.1,2.28,16,101,2.05,1.09,.63,.41,3.27,1.25,1.67,
				13.4,3.91,2.48,23,102,1.8,.75,.43,1.41,7.3,.7,1.56,
				13.72,1.43,2.5,16.7,108,3.4,3.67,.19,2.04,6.8,.89,2.87,
				13.27,4.28,2.26,20,120,1.59,.69,.43,1.35,10.2,.59,1.56,
				12.64,1.36,2.02,16.8,100,2.02,1.41,.53,.62,5.75,.98,1.59
		};	//actual labels: 1, 2, 1, 3, 2, 1, 2, 3, 1, 3, 2	
		Matrix samples = 
			new Matrix(sampleArr.length / attrNum, attrNum, sampleArr);
		
		//Find k-nearest		
		Matrix results = kNearest.findNearest(samples, 10);		
		if(results != null) {
			for(int i=0; i<results.rows(); i++) {
				System.out.println("classification " + (int)(results.at(i, 0)));
			}
		}
	}
	 
	public static void main(String[] args) {											
		//testNaiveBayesClassifier();
		//testDTreeClasifier();
		//testKDTree();
		//testKNearest();
		//testOLSRegression();
		testDTreeClassifierUsingC4_5();
	}
}

package artiano.ml.classifier.test;

import java.io.*;
import java.util.*;

import artiano.core.structure.*;
import artiano.core.structure.Table.TableRow;
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
	
	//Training data
	static double[] trainDataArr = { 					
		14.1,2.02,2.4,18.8,103,2.75,2.92,.32,2.38,6.2,1.07,2.75,1060,
		13.94,1.73,2.27,17.4,108,2.88,3.54,.32,2.08,8.90,1.12,3.1,1260,
		13.05,1.73,2.04,12.4,92,2.72,3.27,.17,2.91,7.2,1.12,2.91,1150,				
		12.85,3.27,2.58,22,106,1.65,.6,.6,.96,5.58,.87,2.11,570,
		13.62,4.95,2.35,20,92,2,.8,.47,1.02,4.4,.91,2.05,550,
		13.56,1.71,2.31,16.2,117,3.15,3.29,.34,2.34,6.13,.95,3.38,795,			
		14.1,2.02,2.4,18.8,103,2.75,2.92,.32,2.38,6.2,1.07,2.75,1060,
		13.56,1.73,2.46,20.5,116,2.96,2.78,.2,2.45,6.25,.98,3.03,1120,
		12.6,1.34,1.9,18.5,88,1.45,1.36,.29,1.35,2.45,1.04,2.77,562,
		14.83,1.64,2.17,14,97,2.8,2.98,.29,1.98,5.2,1.08,2.85,1045,
		13.49,1.66,2.24,24,87,1.88,1.84,.27,1.03,3.74,.98,2.78,472,
		13.86,1.35,2.27,16,98,2.98,3.15,.22,1.85,7.22,1.01,3.55,1045,
		13.48,1.67,2.64,22.5,89,2.6,1.1,.52,2.29,11.75,.57,1.78,620,			
		12.64,1.36,2.02,16.8,100,2.02,1.41,.53,.62,5.75,.98,1.59,450,
		13.67,1.25,1.92,18,94,2.1,1.79,.32,.73,3.8,1.23,2.46,630,
		12.37,1.13,2.16,19,87,3.5,3.1,.19,1.87,4.45,1.22,2.87,420,
		12.77,3.43,1.98,16,80,1.63,1.25,.43,.83,3.4,.7,2.12,372,				
		12.2,3.03,2.32,19,96,1.25,.49,.4,.73,5.5,.66,1.83,510,				
		13.32,3.24,2.38,21.5,92,1.93,.76,.45,1.25,8.42,.55,1.62,650,
		13.08,3.9,2.36,21.5,113,1.41,1.39,.34,1.14,9.40,.57,1.33,550,
		13.5,3.12,2.62,24,123,1.4,1.57,.22,1.25,8.60,.59,1.3,500,
		12.79,2.67,2.48,22,112,1.48,1.36,.24,1.26,10.8,.48,1.47,480,
		13.27,4.28,2.26,20,120,1.59,.69,.43,1.35,10.2,.59,1.56,835,
		12.69,1.53,2.26,20.7,80,1.38,1.46,.58,1.62,3.05,.96,2.06,495,								
	};

	static double[] trainLabelArr = {
		1, 1, 1, 3, 3, 1, 1, 1, 2, 1, 2, 1, 3, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 2
	};
	
	//Classification test samples
	static double testArr[] = { 										
		13.76,1.53,2.7,19.5,132,2.95,2.74,.5,1.35,5.4,1.25,3,1235,								
		12.33,1.1,2.28,16,101,2.05,1.09,.63,.41,3.27,1.25,1.67,680,							
		14.12,1.48,2.32,16.8,95,2.2,2.43,.26,1.57,5,1.17,2.82,1280,
		13.75,1.73,2.41,16,89,2.6,2.76,.29,1.81,5.6,1.15,2.9,1320,
		11.82,1.47,1.99,20.8,86,1.98,1.6,.3,1.53,1.95,.95,3.33,495,
		12.42,1.61,2.19,22.5,108,2,2.09,.34,1.61,2.06,1.06,2.96,345,			
		12.25,4.72,2.54,21,89,1.38,.47,.53,.8,3.85,.75,1.27,720,			
		12.86,1.35,2.32,18,122,1.51,1.25,.21,.94,4.1,.76,1.29,630,
		12.88,2.99,2.4,20,104,1.3,1.22,.24,.83,5.4,.74,1.42,530																
	}; // 1 2 1 1 2 2 3 3 3		
	

	static double[] trainingDataArr = {			
		2, 10,
		2, 5,
		8, 4,
		5, 8,
		7, 5,
		6, 4, 
		1, 2,
		4, 9
	};
	
	static double[] trainingLabelArr = {		
		1, 3, 2, 1, 2, 2, 3, 1
	};
	
	public static void testNaiveBayesClassifier() {		
		int attrNum = 13;
		Matrix trainDataMat = 
			new Matrix(trainDataArr.length/attrNum, attrNum, trainDataArr);
		Table trainingData = new Table(trainDataMat);		
		Matrix trainingLabelMat = new Matrix(trainLabelArr.length, 1, trainLabelArr); // class labels
		Table trainingLabel = new Table(trainingLabelMat);
		NaiveBayesClassifier classifier = new NaiveBayesClassifier();														
		//----------------------  Classify -------------------------
		classifier.train(trainingData, trainingLabel, null);
		
		//----------------------   Save the training model -------------
		try {
			classifier.save("D:\\bayes.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//----------------- Load the training model--------
		NaiveBayesClassifier loadedClassifier = null;
		try {
			loadedClassifier = 
				(NaiveBayesClassifier) NaiveBayesClassifier.load("D:\\bayes.txt");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//----------------------- Predict -------------------------
		Matrix samplesMat = new Matrix(testArr.length/attrNum, attrNum, testArr);
		Table samples = new Table(samplesMat);
		Table result = loadedClassifier.predict(samples, -1);   // Predict
		result.print();
	}

	
	public static void testDTreeClasifier() {					
		//File that store the training data
		String dataFilePath = 
				"src\\artiano\\ml\\classifier\\test\\data.txt";
				
		Table dataSet = new Table();
		Table trainLabel = new Table();
		//Load training data
		loadTrainingData(dataSet, trainLabel, dataFilePath); 										
		
		DTreeClassifier classifier = new DTreeClassifier();
		classifier.train(dataSet, trainLabel, null); //Train data
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
		Attribute attr1 = new NominalAttribute("outlook");
		Attribute attr2 = new NominalAttribute("temperature");
		Attribute attr3 = new NominalAttribute("humidity");
		Attribute attr4 = new NominalAttribute("windy");
		Attribute[] attributes = new Attribute[]{attr1, attr2, attr3, attr4};
		Table samples = new Table();
		samples.addAttributes(attributes);
		TableRow row = samples.new TableRow();
		row.set(0, sampleArr[0]);
		row.set(1, sampleArr[1]);
		row.set(2, sampleArr[2]);
		row.set(3, sampleArr[3]);
		samples.push(row);
		Table classificationList = classifier.predict(samples, -1);
		for(int i=0; i<classificationList.rows(); i++) {
			System.out.println("Classification: " + classificationList.row(i).at(0));
		}
			
	}
	
/*	
	public static void testDTreeClassifierUsingC4_5() {		
		//File that store the training data
		String dataFilePath = 
			"src\\artiano\\ml\\classifier\\test\\data3.txt";		
		Table dataSet = new Table();
		//Load training data
		int targetAttrIndex = 
			loadTrainingData(dataSet, dataFilePath); 		
		if(targetAttrIndex == -1) {
			System.out.println("Load training data fail.");
			return;
		}		

		double[] targetAttrIndexArr = new double[]{targetAttrIndex};
		Matrix targetAttributeIndex = new Matrix(1, 1, targetAttrIndexArr);
		boolean[] isContinuous = new boolean[]{
			false, true, true, false, false	
		};
		
		DTreeClassifierUsingC4_5 dtree = 
			new DTreeClassifierUsingC4_5(dataSet, targetAttributeIndex, isContinuous);
		dtree.train();		
		try {
			dtree.save("D:\\decisionTree.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		//-------------------------predict-------------------------------//
		String[] sampleArr = {"rainy", "68", "79", "FALSE"};		
		List<List<String>> sample = new ArrayList<List<String>>();
		sample.add(Arrays.asList(sampleArr));		
		List<String> classificationList = dtree.predict(sample);
		for(int i=0; i<classificationList.size(); i++) {
			System.out.println("Classification: " + classificationList.get(i));
		}		
	}
*/

	//Load the training data
	//Return index of the target attribute	
	private static void loadTrainingData(Table dataSet, Table trainLabel,
			String dataFilePath) {
		BufferedReader input = null;
		try {
			input = 
				new BufferedReader(new FileReader(dataFilePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		int targetAttrIdx = -1;  //IncrementIndex of target attribute index 
		try {
			//Target attribute index
			String targetAttrIdxStr = 
					(input.readLine().trim()).split("[\t]")[1];
			targetAttrIdx = Integer.parseInt(targetAttrIdxStr);
System.out.println("tagret index: " + targetAttrIdx);
			//Attributes
			String[] attributes = 
					(input.readLine().trim()).split("[\t]");  //Attributes
			for(int i=0; i<attributes.length; i++) {
				Attribute attr = new NominalAttribute(attributes[i]);
				if(i == targetAttrIdx) {  //Train label
					trainLabel.addAttribute(attr);
				} else {
					dataSet.addAttribute(attr);
				}								
			}						
			
			//Training data
			String item = "";			
			while(! "".equals(item = input.readLine()) && 
					! (null == item) ) {				
				TableRow tableRow = dataSet.new TableRow();
				TableRow labelRow = trainLabel.new TableRow();
				String[] attrValues = (item.trim()).split("[\t]");
				for(int i=0; i<attrValues.length; i++) {		
					if(i < targetAttrIdx) {
						tableRow.set(i, attrValues[i].trim());
					} else if(i == targetAttrIdx) {
						labelRow.set(0, attrValues[i].trim());
					} else {
						tableRow.set(i-1, attrValues[i].trim());
					}					 
				}
				dataSet.push(tableRow);
				trainLabel.push(labelRow);
			}								
			input.close();						
			
		} catch (IOException e) {			
			e.printStackTrace();
		}		
	}	

	public static void testKDTree() {				
		int dimension = 2;
		Matrix trainData = 
				new Matrix(trainDataArr.length / dimension, dimension, trainDataArr);
		Table trainSet = new Table(trainData);
		Matrix trainLabel = 
				new Matrix(trainDataArr.length / dimension, 1, trainLabelArr);
		Table labelTable = new Table(trainLabel);
		KDTree tree = new KDTree(trainSet, labelTable);
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
		int attrNum = 2;
		//Train data
		Matrix trainData = 
			new Matrix(trainingDataArr.length / attrNum, attrNum, trainingDataArr);
		Table trainSet = new Table(trainData);
		//Train labels
		Matrix trainLabelMat = 
				new Matrix(trainingLabelArr.length, 1, trainingLabelArr);
		Table trainLabel = new Table(trainLabelMat);
		KNearest kNearest = new KNearest();
		kNearest.train(trainSet, trainLabel, null);  //Train data
		
		double[] sampleArr = {		
				2, 8,
				6, 9,
				2, 4,
				4, 8,
				4, 5
		};	
		Matrix samplesMat = 
			new Matrix(sampleArr.length / attrNum, attrNum, sampleArr);
		Table samples = new Table(samplesMat);
		//Find k-nearest		
		Table results = kNearest.predict(samples, 4);		
		if(results != null) {
			for(int i=0; i<results.rows(); i++) {
				System.out.println("classification " + 
					Math.round((double)results.at(i, 0)));
			}
		}
	}
	 
	public static void main(String[] args) {											
		//testNaiveBayesClassifier();
		testDTreeClasifier();
		//testKDTree();
		//testKNearest();
		//testDTreeClassifierUsingC4_5();
	}
}

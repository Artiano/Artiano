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
	
	//Training data
	static double[] trainData = { 					
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

	static double[] label = {
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
	};
	
	static double[] trainLabelArr = {		
		1, 3, 3, 2, 3, 1, 2, 1, 3, 2, 2, 1, 3, 2, 1, 3, 3, 2, 2, 1,
		2, 1, 1, 2, 2		
	};
	
	public static void testNaiveBayesClassifier() {
		int attrNum = 13;		
		// ----------------------  Train data -------------------- 
		Matrix trainingData = 
				new Matrix(trainData.length / attrNum, attrNum, trainData);	 //Training data
		Matrix trainingLabel = new Matrix(label.length, 1, label); // class labels
		Matrix samples = new Matrix(testArr.length / trainingData.columns(), 
				trainingData.columns(), testArr); // test examples		
		NaiveBayesClassifier classifier = new NaiveBayesClassifier();												
		
		//----------------------  Classify -------------------------
		classifier.train(trainingData, trainingLabel);
		Matrix result = 
			classifier.classify(samples); // Get the classification
		result.print();									
		
		//----------------------   Save the training model -------------
		try {
			classifier.save("D:\\bayes.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		List<List<String>> sample = new ArrayList<List<String>>();
		sample.add(Arrays.asList(sampleArr));
		List<String> classificationList = classifier.classify(sample);
		for(int i=0; i<classificationList.size(); i++) {
			System.out.println("Classification: " + classificationList.get(i));
		}				
	}
	
	public static void testDTreeClassifierUsingC4_5() {		
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
		List<List<String>> sample = new ArrayList<List<String>>();
		sample.add(Arrays.asList(sampleArr));		
		List<String> classificationList = dtree.classify(sample);
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
		kNearest.train(trainData, trainLabel);  //Train data
		
		double[] sampleArr = {
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
	
	public static void testOLSRegression() {
		double[] a = {
				0.4, 23, 163, 
				3.1, 19, 37,
				0.6, 34, 157,
				4.7, 24, 59,
				1.7, 65, 123,
				9.4, 44, 46,				
				10.1, 31, 117,
				10.9, 37, 111,
    	};
    	double[] b = {
    			60, 71, 61, 54, 77, 81, 93, 76
    	};
    	Matrix X = new Matrix(a.length / 3, 3, a);
    	Matrix Y = new Matrix(b.length, 1, b);
    	Matrix dst = Regression.getOLSRegression(X, Y);
    	System.out.println("线性回归多项式是:");
    	for(int i=0; i<dst.rows(); i++) {
    		for(int j=0; j<dst.columns(); j++) {
    			if(i == 0) {
    				System.out.print(dst.at(i, j) + " ");
    			} else {
    				if(dst.at(i, j) < 0) {
    					System.out.print("- " + dst.at(i, j) + "*x" + i + " ");
    				} else {
    					System.out.print("+ " + dst.at(i, j) + "*x" + i + " ");
    				}    				
    			}    			
    		}
    	}
	}
	 
	public static void main(String[] args) {											
		testNaiveBayesClassifier();
		//testDTreeClasifier();
		//testKDTree();
		//testKNearest();
		//testOLSRegression();
		//testDTreeClassifierUsingC4_5();
	}
}

package artiano.machinelearning.classifier.test;

import artiano.core.Matrix;
import artiano.machinelearning.classifier.NormalBayesClassifier;

/**
 * <p>Description: Normal Bayes classifier Test</p>
 * @author JohnF Nash
 * @version 1.0.0
 * @date 2013-8-27
 * @function 
 * @since 1.0.0
 */
public class Test {
	
	public static void main(String[] args) {
		// 10��������������ά��Ϊ12��ѵ������������һ��Ϊ������������ǩ
		double[] inputArr = { 
				1, 5, 1, -0.320755, -0.105023, 
				-1, 6, 0.333333, -0.603774, 1, 
				1, 9, -0.333333, -0.433962, -0.383562, 
				-1, 7, 1, -0.358491, -0.374429, 
				1, 8, -0.333333, -0.509434, -0.347032,
				-1, 4, 1, -0.509434, -0.767123, 	
				1, 5, 1, -0.320755, -0.105023,
				-1, 6, 0.333333, -0.603774, 1, 
				1, 9, -0.333333, -0.433962, -0.383562, 
				-1, 7, 1, -0.358491, -0.374429, 
				1, 8, -0.333333, -0.509434, -0.347032, 
				-1, 4, 1, -0.509434, -0.767123,						
		};

		// һ��������������������
		double testArr[] = { 0.25, 1, -0.226415, -0.506849 };

		Matrix trainingData = new Matrix(12, 5, inputArr);	 //ѵ����
		Matrix trainingResponse = new Matrix(trainingData.rows(), 1); // ����
		for (int i = 0; i < trainingResponse.rows(); i++) {
			trainingResponse.set(i, 0, inputArr[i * trainingData.columns()]);
		}
		Matrix sample = new Matrix(1, trainingData.columns() - 1, testArr); // ����ʵ��

		NormalBayesClassifier classifier = new NormalBayesClassifier();
		classifier.train(trainingData, trainingResponse, 0);		//ѵ������
		int classification = classifier.predict(sample); // Get the classification
		System.out.println("The classification is " + classification);
	}

}

package artiano.ml.clustering.test;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import artiano.core.structure.Matrix;
import artiano.core.structure.Range;
import artiano.ml.clustering.KMeans;

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
	};
	
	public static void main(String[] args) {		
		int attrNum = 14;
		Matrix samples = new Matrix(inputArr.length / attrNum, 14, inputArr );	
		
		KMeans kMeans = new KMeans();
		Matrix data = samples.at(new Range(0, samples.rows()),new Range(1, samples.columns()));
		Map<Matrix, Matrix> clusterMap = kMeans.kmeans(data, 3);  //k-means
		Set<Entry<Matrix, Matrix>> entrySet = clusterMap.entrySet();
		int count = 1;
		for(Entry<Matrix, Matrix> entry: entrySet) {
			System.out.println("Cluster " + count);
			System.out.println("center: ");
			entry.getKey().print();
			System.out.println("Points in this cluster:");
			entry.getValue().print();
			System.out.println();
			count++;
		}
	}
}

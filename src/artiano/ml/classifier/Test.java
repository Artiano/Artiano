package artiano.ml.classifier;

import artiano.core.structure.Matrix;

public class Test {
	public static void main(String[] arg){
		NaiveBayesDiscreteClassifier cs=new NaiveBayesDiscreteClassifier();
		double[] a={
				1,3,3,
				1,3,3,
				1,7,7,
				2,5,5,
				3,6,6,
				3,7,7
		};
		Matrix mx=new Matrix(6,3,a);
		String[] strs=new String[2];
		strs[0]="(0,5] (5,10] (10,15]";
		strs[1]="(0,2] (2,6] (6,10]";
		cs.train(mx,strs, 0);
//		for(int i=0;i<cs.labeList.size();i++){
//			System.out.println("============="+cs.labeList.get(i));
//			cs.labelMap.get(cs.labeList.get(i)).print();
//		}
		for(int con=0;con<cs.trainingResults.length;con++){
			cs.trainingResults[con].print();
		}
		
	}

}

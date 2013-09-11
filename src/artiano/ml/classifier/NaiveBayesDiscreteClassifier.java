package artiano.ml.classifier;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import artiano.core.structure.Domain;
import artiano.core.structure.Matrix;

/***
 * 
 * @author BreezeDust
 *
 */
public class NaiveBayesDiscreteClassifier {
	private Matrix trainData;
	private Map<Integer, Matrix> labelMap = 
			new LinkedHashMap<Integer, Matrix>(); 
	private List<Integer> labeList=new LinkedList<Integer>();
	public boolean train(Matrix trainData,Domain[] domains,int lableColIndex){
		Matrix labeMx=trainData.c
		return false;
	}
}

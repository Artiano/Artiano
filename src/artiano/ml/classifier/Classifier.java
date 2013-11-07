package artiano.ml.classifier;

import artiano.core.operation.Preservable;
import artiano.core.structure.*;

public abstract class Classifier extends Preservable {	
	private static final long serialVersionUID = 5186515619281612199L;

	/**
	 * Train data
	 * @param trainSet data to train
	 * @param trainLabel label of training data
	 * @param isAttributeContinuous whether an attribute is continuous or discrete,
	 * 		 (for DTreeClassifierUsingC4_5, if not DTreeClassifierUsingC4_5 ,null is OK.)
	 * @return whether the training successes or not
	 */
	public abstract boolean train(Table trainSet, Table trainLabel, 
			boolean[] isAttributeContinuous);
	
	/**
	 * Predict label of each sample case
	 * @param samples samples to test
	 * @param k number of neighbors to get label, for KNearest algorithm.If not KNearest,
	 * 		just assigning -1 to it.  
	 * @return predications of label of each sample case   
	 */
	public abstract Table predict(Table samples, int k);
}

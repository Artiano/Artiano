/**
 * LenvenbergMarquardtLearning.java
 */
package artiano.neural.learning;

import artiano.core.structure.Matrix;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-30
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class LenvenbergMarquardtLearning implements SupervisedLearning {

	
	
	/* (non-Javadoc)
	 * @see artiano.neural.learning.SupervisedLearning#runEpoch(artiano.core.structure.Matrix[], artiano.core.structure.Matrix[])
	 */
	@Override
	public double runEpoch(Matrix[] inputs, Matrix[] targetOutputs) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see artiano.neural.learning.SupervisedLearning#run(artiano.core.structure.Matrix, artiano.core.structure.Matrix)
	 */
	@Override
	public double run(Matrix input, Matrix targetOutput) {
		return 0;
	}

}

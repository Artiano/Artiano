/**
 * UnsupervisedNeuralLearning.java
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
public interface UnsupervisedNeuralLearning {
	/**
	 * run an epoch
	 * @param inputs - samples of every input vector
	 * @return double - error
	 */
	public double runEpoch(Matrix inputs);
	
	/**
	 * runs a learning iteration
	 * @param input - input vector
	 * @return - error
	 */
	public double run(Matrix input);
}

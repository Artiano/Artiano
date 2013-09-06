/**
 * SupervisedLearning.java
 */
package artiano.neural.learning;

import artiano.core.structure.Matrix;

/**
 * <p>Description: interface of every supervised learning algorithm</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-14
 * @author (latest modification by Nano.Michael)
 * @function 
 * @since 1.0
 */
public interface SupervisedLearning {
	/**
	 * run an iteration on every sample
	 * @param inputs samples of every input vector
	 * @param targetOutputs labels of target output
	 * @return error term
	 */
	public double runEpoch(Matrix[] inputs, Matrix[] targetOutputs);
	
	/**
	 * run an iteration on specified sample
	 * @param input input vector
	 * @param targetOutput target output vector
	 * @return error term
	 */
	public double run(Matrix input, Matrix targetOutput);
}

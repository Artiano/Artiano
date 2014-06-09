/**
 * SupervisedNeuralLearning.java
 */
package artiano.neural.learning;

import java.util.ArrayList;

import artiano.core.operation.OptionsHandler;
import artiano.core.structure.Matrix;
import artiano.neural.network.ActivationNetwork;

/**
 * <p>Description: interface of every supervised learning algorithm</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-14
 * @author (latest modification by Nano.Michael)
 * @function 
 * @since 1.0
 */
public abstract class SupervisedNeuralLearning implements OptionsHandler {
	
	public static ArrayList<Class<?>> listAll() {
		ArrayList<Class<?>> s = new ArrayList<>();
		s.add(StochasticBPLearning.class);
		s.add(LevenbergMarquardtLearning.class);
		return s;
	}
	
	public abstract void setNetwork(ActivationNetwork network);
	
	/**
	 * run an iteration on every sample
	 * @param inputs samples of every input vector
	 * @param targetOutputs labels of target output
	 * @return error term
	 */
	public abstract double runEpoch(Matrix inputs, Matrix targetOutputs);
	
	/**
	 * run an iteration on specified sample
	 * @param input input vector
	 * @param targetOutput target output vector
	 * @return error term
	 */
	public abstract double run(Matrix input, Matrix targetOutput);
}

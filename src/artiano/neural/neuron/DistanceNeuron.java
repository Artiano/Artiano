/**
 * DistanceNeuron.java
 */
package artiano.neural.neuron;

import artiano.core.structure.Matrix;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-30
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class DistanceNeuron extends Neuron {

	/**
	 * @param inputs
	 */
	public DistanceNeuron(int inputs) {
		super(inputs);
	}

	/* (non-Javadoc)
	 * @see artiano.neural.neuron.Neuron#compute(artiano.core.structure.Matrix)
	 */
	@Override
	public double compute(Matrix input) {
		if (input.rows() != 1)
			throw new IllegalArgumentException("Accept row vector only.");
		if (input.columns() != weights.length)
			throw new IllegalArgumentException("Inputs size not match.");
		double sum = 0.;
		//calculate the difference of the input and weight
		for (int i = 0; i < input.columns(); i++){
			sum  += Math.abs((weights[i] - input.at(i)));
		}
		output = sum;
		return output;
	}

}

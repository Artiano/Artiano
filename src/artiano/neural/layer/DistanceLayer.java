/**
 * DistanceLayer.java
 */
package artiano.neural.layer;

import artiano.neural.neuron.DistanceNeuron;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-30
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class DistanceLayer extends Layer {

	/**
	 * @param inputs
	 * @param neuronsCount
	 */
	public DistanceLayer(int inputs, int neuronsCount) {
		super(inputs, neuronsCount);
		for (int i = 0; i < neuronsCount; i++)
			neurons[i] = new DistanceNeuron(inputs);
	}
}

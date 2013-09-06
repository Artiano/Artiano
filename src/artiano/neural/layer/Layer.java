/**
 * 
 */
package artiano.neural.layer;


import artiano.core.structure.Matrix;
import artiano.neural.neuron.Neuron;
import artiano.neural.randomizer.Randomizer;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-13
 * @author (latest modification by Nano.Michael)
 * @function 
 * @since 1.0
 */
public abstract class Layer {
	public Matrix outputs = null;
	public int inputsCount = 0;
	public int neuronsCount = 0;
	public Neuron[] neurons = null;
	
	/**
	 * constructor
	 * @param inputs number of input
	 * @param neuronsCount number of neurons
	 */
	public Layer(int inputs, int neuronsCount){
		this.inputsCount = inputs;
		this.neuronsCount = neuronsCount;
		neurons = new Neuron[neuronsCount];
		outputs = new Matrix(1, neuronsCount);
	}
	
	/**
	 * randomize the layer
	 * @param r randomizer
	 */
	public void randomize(Randomizer r){
		for (int i = 0; i < neurons.length; i++)
			neurons[i].randomize(r);
	}
	
	/**
	 * compute the output
	 * @param inputs inputs of the layer
	 * @return output
	 */
	public Matrix compute(Matrix inputs){
		for (int i = 0; i < neuronsCount; i++)
			outputs.set(0, i, neurons[i].compute(inputs));
		return outputs;
	}
}

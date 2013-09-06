/**
 * 
 */
package artiano.neural.neuron;

import java.io.Serializable;

import artiano.core.structure.Matrix;
import artiano.randomizer.Randomizer;

/**
 * <p>Description: basic class of every neuron</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-13
 * @author (latest modification by Nano.Michael)
 * @function 
 * @since 1.0
 */
public abstract class Neuron implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public double[] weights = null;
	public int inputNum = 0;
	transient public double output = 0;
	transient public double e = 0;
	
	/**
	 * constructor
	 * @param inputs number of inputs
	 */
	public Neuron(int inputs){
		this.inputNum = inputs;
		this.weights = new double[inputs];
	}
	
	/**
	 * randomize the weights
	 * @param r randomizer for write data to neuron's weights
	 */
	public void randomize(Randomizer r){
		r.writeTo(weights);
	}
	
	/**
	 * compute the output of the neuron
	 * @param input the input vector
	 * @return the output
	 */
	public abstract double compute(Matrix input);
}

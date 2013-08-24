/**
 * 
 */
package artiano.neural.neuron;


import artiano.neural.actfun.ActivationFunction;
import artiano.neural.randomizer.Randomizer;

/**
 * <p>Description: activation neuron</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-13
 * @author (latest modification by Nano.Michael)
 * @function 
 * @since 1.0
 */
public class ActivationNeuron extends Neuron {
	
	public double bias = 0;
	public ActivationFunction actfun = null;
	
	public ActivationNeuron(int inputs){
		super(inputs);
	}
	
	public ActivationNeuron(int inputs, ActivationFunction actfun){
		super(inputs);
		this.actfun = actfun;
	}

	public void setActivationFunction(ActivationFunction actfun){
		this.actfun = actfun;
	}
	
	/* (non-javadoc)
	 * @see artiano.neuron.Neuron#randomize(Randomizer)
	 */
	@Override
	public void randomize(Randomizer r){
		super.randomize(r);
		bias = r.next();
	}
	
	/* (non-Javadoc)
	 * @see artiano.neuron.Neuron#compute(double[])
	 */
	@Override
	public double compute(double[] input) {
		if (input.length != weights.length)
			throw new IllegalArgumentException("Inputs size not match.");
		double sum = 0.;
		for (int i = 0; i < input.length; i++)
			sum += weights[i] * input[i];
		sum += bias;
		output = actfun.calculate(sum);
		return output;
	}
}

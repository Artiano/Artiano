/**
 * 
 */
package artiano.neural.layer;


import artiano.neural.actfun.ActivationFunction;
import artiano.neural.neuron.ActivationNeuron;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0
 * @date 2013-8-14
 * @author (latest modification by Nano.Michael)
 * @function 
 * @since 1.0.0
 */
public class ActivationLayer extends Layer {
	public ActivationFunction actfun = null;
	
	/**
	 * 
	 * @param inputs
	 * @param neuronsCount
	 */
	public ActivationLayer(int inputs, int neuronsCount){
		this(inputs, neuronsCount, null);
	}
	
	/**
	 * @param inputs
	 * @param neuronsCount
	 */
	public ActivationLayer(int inputs, int neuronsCount, ActivationFunction actfun) {
		super(inputs, neuronsCount);
		this.actfun = actfun;
		for (int i = 0; i < neurons.length; i++)
			neurons[i] = new ActivationNeuron(inputs, actfun);
	}
	
	/**
	 * set activation function
	 * @param actfun specified activation function
	 */
	public void setActivationFunction(ActivationFunction actfun){
		this.actfun = actfun;
		for (int i = 0; i < neuronsCount; i++){
			ActivationNeuron neuron = (ActivationNeuron)neurons[i];
			neuron.setActivationFunction(actfun);
		}
	}
}

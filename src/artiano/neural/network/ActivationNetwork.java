/**
 * ActivationNetwork.java
 */
package artiano.neural.network;


import artiano.neural.actfun.ActivationFunction;
import artiano.neural.layer.ActivationLayer;

/**
 * <p>Description: activation neural network</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-14
 * @author (latest modification by Nano.Michael)
 * @function 
 * @since 1.0
 */
public class ActivationNetwork extends Network {
	public int hiddenLayersCount = 0;
	public int[] neuronsPerHiddenLayer = null;
	public double squreError = 1e10;
	
	/**
	 * @param inputs dimension of input vector
	 * @param outputs dimension of output vector
	 * @param neuronsPerHiddenLayer neurons per hidden layer
	 * @param actfun specified activation function
	 */
	public ActivationNetwork(int inputs, int outputs, int[] neuronsPerHiddenLayer, ActivationFunction actfun) {
		super(inputs, outputs, neuronsPerHiddenLayer.length + 1);
		this.neuronsPerHiddenLayer = neuronsPerHiddenLayer;
		this.hiddenLayersCount = neuronsPerHiddenLayer.length;
		//initialize hidden layers
		for (int i = 0; i < neuronsPerHiddenLayer.length; i++){
			//make sure have one neuron at least
			layers[i] = new ActivationLayer(inputs, neuronsPerHiddenLayer[i] > 0 ? neuronsPerHiddenLayer[i]: 1);
			inputs = neuronsPerHiddenLayer[i];
		}
		//initialize output layer
		layers[hiddenLayersCount] = new ActivationLayer(inputs, outputs);
		//set activation function
		setActivationFunction(actfun);
	}
	
	/**
	 * constructor
	 * @param inputs dimension of input vector
	 * @param outputs dimension of output vector
	 * @param neuronsPerHiddenLayer neurons per hidden layer
	 */
	public ActivationNetwork(int inputs, int outputs, int[] neuronsPerHiddenLayer){
		this(inputs, outputs, neuronsPerHiddenLayer, null);
	}
	
	/**
	 * set specified activation function for every layer
	 * @param actfun specified activation function
	 */
	public void setActivationFunction(ActivationFunction actfun){
		for (int i = 0; i < layerCount; i++)
			setActivationFunction(actfun, i);
	}
	
	/**
	 * set specified activation function for specified layer
	 * @param actfun specified activation function
	 * @param layerIndex index of specified layer
	 */
	public void setActivationFunction(ActivationFunction actfun, int layerIndex){
		ActivationLayer layer = (ActivationLayer)layers[layerIndex];
		layer.setActivationFunction(actfun);
	}
}

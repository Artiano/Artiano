package artiano.neural.learning;

import artiano.core.structure.Matrix;
import artiano.neural.actfun.ActivationFunction;
import artiano.neural.layer.ActivationLayer;
import artiano.neural.network.ActivationNetwork;
import artiano.neural.neuron.ActivationNeuron;

/**
 * <p>Description: Stachostic backpropagation learning algorithm, is unsupervised leaning algorithm</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-14
 * @author (latest modification by Nano.Michael)
 * @function 
 * @since 1.0
 */
public class StochasticBPLearning implements SupervisedLearning {
	
	protected ActivationNetwork network = null;
	protected double learningRate = 0.5;
	protected double momentum = 0.05;
	
	/**
	 * constructor
	 * @param network - the network to training
	 */
	public StochasticBPLearning(ActivationNetwork network){
		this.network = network;
	}
	
	/**
	 * set learning parameter
	 * @param learningRate learning rate
	 * @param momentum
	 */
	public void setParameter(double learningRate, double momentum){
		this.learningRate = learningRate > 0 && learningRate < 1 ? learningRate: this.learningRate;
		this.momentum = momentum > 0 && momentum < 1 ? momentum: this.momentum;
	}
	
	/**
	 * calculate the error term
	 * @param input input vector
	 * @param targetOutput target output vector
	 */
	protected double calculateErrorTerm(Matrix input, Matrix targetOutput){
		//output layer
		ActivationLayer layer = (ActivationLayer)network.layers[network.layerCount - 1];
		//function of current layer
		ActivationFunction function = layer.actfun;
		
		//step 1. calculate the error term of output layer
		ActivationNeuron neuron;
		double se = 0.;
		for (int i = 0; i < layer.neurons.length; i++){
			//current neuron
			neuron = (ActivationNeuron)layer.neurons[i];
			double derivativeNeti = function.derivativeByY(neuron.output);
			double e = targetOutput.at(0, i) - neuron.output;
			//error term of current neuron
			neuron.e = e * derivativeNeti;
			//output error
			se += e * e / 2.;
		}
		
		//step 2. calculate the error term of hidden layers
		ActivationLayer nextLayer = layer;
		for (int i = network.hiddenLayersCount - 1; i >= 0; i--){
			//current layer
			layer = (ActivationLayer)network.layers[i];
			//function of current layer
			function = layer.actfun;
			
			//calculate the error term of current layer
			for (int j = 0; j < layer.neuronsCount; j++){
				//downstream of neuron j
				double sumError = 0.;
				ActivationNeuron dsNeuron;
				for (int k = 0; k < nextLayer.neuronsCount; k++){
					dsNeuron = (ActivationNeuron)nextLayer.neurons[k];
					sumError += dsNeuron.e * dsNeuron.weights[j];
				}
				
				//current neuron
				neuron = (ActivationNeuron)layer.neurons[j];
				//derivative of the current output by activation function
				double derivativeNetj = function.derivativeByY(neuron.output);
				//error term of current neuron
				neuron.e = sumError * derivativeNetj;
			}
			nextLayer = layer;
		}
		return se;
	}
	
	/**
	 * update the network
	 * @param input input vector
	 */
	protected void update(Matrix input){
		ActivationLayer layer;
		Matrix v = input;
		for (int i = 0; i < network.layerCount; i++)
		{
			//current layer
			layer = (ActivationLayer)network.layers[i];
			ActivationNeuron neuron;
			for (int j = 0; j < layer.neuronsCount; j++)
			{
				//current neuron
				neuron = (ActivationNeuron)layer.neurons[j];
				//update weights
				for (int k = 0; k < neuron.weights.length; k++){
					neuron.weights[k] += learningRate * neuron.e * v.at(0, k);
				}
				//update bias
				neuron.bias += learningRate * neuron.e;
			}
			v = layer.outputs;
		}
	}
	
	/* (non-Javadoc)
	 * @see artiano.learning.SupervisedLearning#run(double[], double[])
	 */
	@Override
	public double run(Matrix input, Matrix targetOutput) {
		network.compute(input);
		double se = calculateErrorTerm(input, targetOutput);
		update(input);
		return se;
	}
	
	/* (non-Javadoc)
	 * @see artiano.learning.SupervisedLearning#runEpoch(double[][], double[][])
	 */
	@Override
	public double runEpoch(Matrix[] inputs, Matrix[] targetOutputs) {
		network.epochs++;
		double e = 0.;
		for (int i = 0; i < inputs.length; i++)
			e += run(inputs[i], targetOutputs[i]);
		network.squreError = e;
		return e;
	}
	
}

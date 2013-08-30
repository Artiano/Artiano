/**
 * StochasticBPLearning.java
 * This file implements the stachostic backpropagation learning algorithm
 *   1. Definitions
 *   x(ji): i(th) input of neuron j
 *   w(ji): weight of neuron j associated with x(ji)
 *   net(j) = ¦²i[x(ji)*w(ji)]: net ouput of j
 *   ¦È: activation function
 *   o(j) = ¦È(net(j))
 *   t(j): target output of j
 *   DS(j): direct downstream of j
 *   E(d) = 1/2[¦²k(t(k) - o(k))^2]: error term of sample d
 *   ¦Á: partial derivative
 *   ¦Ç: learning rate, usually set to (0, 1]
 *   ¦Ä(j): error term of j
 *
 *   2. When evaluate the performance of a classifier or something like that, we usually use the error 
 * term to describe it, stochastic gradient descent method provides an common method for optimal problems, 
 * when it use on artificial neural network, the error term is calculated by expression:
 *   2.1. E(d) = 1/2[¦²k(t(k) - o(k))^2] (k belongs to the outputs of d)
 *   we find out the optimal parameter w by minimize the error term E(d).
 *                   ¦Á(Ed) 
 *   expression g = ------- is so-called gradient of w which is the steepest descent direction.
 *                   ¦Á(w)
 *
 *   so, the update of w is calculated by following expression:
 *                 ¦Á(Ed)..........the partial derivative of E(d) for each parameter w = [w(11), w(12)....]
 *   2.2. ¦¤w = -¦Ç -------
 *                 ¦Á(w)
 *   update weights vector: w + ¦¤w ¡ú w
 *          ¦Á(Ed)        ¦Á(Ed)
 *   2.3. --------- = -----------x(ji)......¢Ù evaluate the expression
 *         ¦Á(w(ji))     ¦Á(net(j))
 *
 *   3. We find out that the relationship of every factor of the network can simply describe with the 
 * expression: w(ji) ¡ú net(j) ¡ú o(j) ¡ú w(kj) ¡ú net(k) ¡ú o(k)...... ( k belongs to DS(j), "¡ú" means affect )
 *   when calculate the value of expression ¢Ù, the following conditions need to be considered:
 *   3.1. neuron j belongs to the output layer, net(j) ¡ú o(j)
 *      ¦Á(Ed)                        ¦Á(o(j))
 *   ---------- = -(t(j) - o(j)) * ---------- = -¦Ä(j)
 *    ¦Á(net(j))                     ¦Á(net(j))
 *   3.2. neuron j belongs to the hidden layer, o(j) ¡ú w(kj) ¡ú net(k) ¡ú o(k), ( k belongs to DS(j) )
 *     ¦Á(Ed)        ¦Á(o(j))  
 *   ---------- = ---------- * ¦²k[-¦Ä(k)*w(kj)] = -¦Ä(j)
 *    ¦Á(net(j))    ¦Á(net(j))
 * 
 *   update weights vector: w + ¦¤w ¡ú w, ¦¤w = ¦Ç*¦Ä*x
 *
 *   4. Extensions
 *   4.1. Add momentum term, the update is calculated by the following expression:
 *   ¦¤w(n) = ¦Ç*¦Ä*x + ¦Á*¦¤w(n-1)......¦¤w(n) is the update of n(th) iteration, 0 <= ¦Á < 1, called momentum
 *   4.3. Learning arbitrary non-loop network, the ¦Ä is calculated by the following expression:
 *            ¦Á(o(r))
 *   ¦Ä(r) = -----------* ¦²s[w(sr)*¦Ä(s)]......¢Ú s belongs to DS(r)
 *           ¦Á(net(r))
 *   we can see the ¦Ä(j) is a special case of expression ¢Ú when neuron j belongs to the output layer (because
 * neuron j have no direct downstream). 
 *
 *   see the page "http://en.wikipedia.org/wiki/Stochastic_gradient_descent" for more information about 
 * stochastic gradient descent
 *   see the page "http://en.wikipedia.org/wiki/Backpropagation" for more information about backpropagation
 * learning algorithm
 */
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

/**
 * SOMLearning.java
 */
package artiano.neural.learning;

import artiano.core.structure.Matrix;
import artiano.neural.layer.DistanceLayer;
import artiano.neural.network.DistanceNetwork;
import artiano.neural.neuron.DistanceNeuron;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-30
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class SOMLearning implements UnsupervisedLearning {

	double learningRate = 0.1;
	double radius = 0;
	//dumping factor
	double eta1 = 0;
	double eta2 = 1000;
	//width of the network
	int width;
	DistanceNetwork network;
	
	/**
	 * constructor
	 * @param network - distance network
	 */
	public SOMLearning(DistanceNetwork network){
		int neurons = network.layers[0].neuronsCount;
		int w = (int)Math.sqrt((double)neurons);
		if (w*w != neurons)
			throw new IllegalArgumentException("SOMLearning, invalid network.");
		width = w;
		radius = w / 2;
		eta1 = eta2 / Math.log(radius);
		this.network = network;
	}
	
	/**
	 * set learning parameter
	 * @param learningRate - learning rate
	 * @param learningRadius - learning radius
	 */
	public void setParameter(double learningRate, double learningRadius){
		this.learningRate = learningRate < 0 || learningRate >= 1? 0.1: learningRate;
		this.radius = learningRadius > width/2 || learningRadius < 0? width/2: learningRadius;
	}
	
	/* (non-Javadoc)
	 * @see artiano.neural.learning.UnsupervisedLearning#runEpoch(artiano.core.structure.Matrix[])
	 */
	@Override
	public double runEpoch(Matrix inputs) {
		double error = 0.;
		for (int i = 0; i < inputs.rows(); i++)
			error += run(inputs.row(i));
		return error;
	}

	/* (non-Javadoc)
	 * @see artiano.neural.learning.UnsupervisedLearning#run(artiano.core.structure.Matrix)
	 */
	@Override
	public double run(Matrix inputs) {
		double error = 0.;
		network.compute(inputs);
		
		// get winner
		int winner = network.winner();
		DistanceLayer layer = (DistanceLayer)network.layers[0];
		//add epochs
		network.epochs++;
		DistanceNeuron neuron = (DistanceNeuron)layer.neurons[winner];

		//update radius
		double r = radius * Math.exp(-(double)network.epochs / eta1);
		//update learning rate
		double lr = learningRate * Math.exp(-(double)network.epochs / eta2);

		final double TINY = 1e-10;
		if (TINY == r || lr <= 0.01)
		{
			//update the weights of the winner only
			for (int i = 0; i < neuron.weights.length; i++)
			{
				double e = inputs.at(i) - neuron.weights[i];
				error += Math.abs(e);
				neuron.weights[i] += learningRate * e;
			}
		}
		else
		{
			double sr = 2* r* r;
			//coordinate of the winner
			int wx = (int)winner % width;
			int wy = (int)winner / width;
			// walk through all neurons
			for (int i = 0; i < layer.neuronsCount; i++)
			{
				neuron = (DistanceNeuron)layer.neurons[i];
				//distance between winner and current neuron i
				int dx = i % width - wx;
				int dy = i / width - wy;
				double domain = Math.exp(-(double)(dx * dx + dy * dy) / sr);

				//update weights
				for (int j = 0; j < neuron.weights.length; j++)
				{
					double e = (inputs.at(j) - neuron.weights[j]) * domain;
					error += Math.abs(e);
					neuron.weights[j] += lr * e;
				}
			}
		}
		return error;
	}

}

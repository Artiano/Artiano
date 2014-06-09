/**
 * NguyenWidrow.java
 */
package artiano.neural.initializer;

import artiano.neural.layer.Layer;
import artiano.neural.network.Network;
import artiano.randomizer.GuassianRandomizer;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-30
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class NguyenWidrow extends WeightsInitializer {
	
	public NguyenWidrow(){ }
	
	/* (non-Javadoc)
	 * @see artiano.neural.initializer.WeightsInitializer#initialize()
	 */
	@Override
	public void initialize(Network network) {
		if (network.layerCount < 2)
			throw new IllegalArgumentException("NguyenWidrow initialize, at least two layers in the network.");
		double gama = 0.;
		int neuronsInput = network.inputCount;
		int neuronsHidden = 0;
		//randomize the network
		network.randomize(new GuassianRandomizer(0, 0.5));
		//calculate the sum of the hidden neurons' weights
		double sum = 0.;
		for (int i = 0; i < network.layers.length - 1; i++){
			Layer layer = network.layers[i];
			for (int j = 0; j < layer.neuronsCount; j++){
				neuronsHidden++;
				for (int k = 0; k < layer.neurons[j].weights.length; k++){
					double x = layer.neurons[j].weights[k];
					sum += x*x;
				}
			}
		}
		//initialize
		sum = Math.sqrt(sum);
		gama = 0.7 * Math.pow(neuronsHidden, 1./(double)neuronsInput);
		for (int i = 0; i < network.layers.length; i++){
			Layer layer = network.layers[i];
			for (int j = 0; j < layer.neuronsCount; j++){
				double[] weights = layer.neurons[j].weights;
				for (int k = 0; k < weights.length; k++)
					weights[k] = gama * weights[k] / sum;
			}
		}
	}
}

/**
 * DistanceNetwork.java
 */
package artiano.neural.network;

import artiano.neural.layer.DistanceLayer;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-30
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class DistanceNetwork extends Network {

	/**
	 * @param inputs
	 * @param neuronsCount
	 */
	public DistanceNetwork(int inputs, int neuronsCount) {
		super(inputs, neuronsCount, 1);
		layers[0] = new DistanceLayer(inputs, neuronsCount);
	}
	
	/**
	 * get winner of the network
	 * @return - winner index of the network
	 */
	public int winner(){
		double min = outputs.at(0);
		int idx = 0;
		for (int i = 1; i < outputs.columns(); i++){
			if (min > outputs.at(i)){
				min = outputs.at(i);
				idx = i;
			}
		}
		return idx;
	}
}

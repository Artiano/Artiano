/**
 * WeightsInitializer.java
 */
package artiano.neural.initializer;

import artiano.neural.network.Network;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-30
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public interface WeightsInitializer {
	/**
	 * initialize the weight
	 */
	public void initialize(Network network);
}

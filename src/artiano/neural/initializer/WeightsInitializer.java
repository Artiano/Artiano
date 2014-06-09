/**
 * WeightsInitializer.java
 */
package artiano.neural.initializer;

import java.util.ArrayList;

import artiano.neural.network.Network;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-30
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public abstract class WeightsInitializer {
	
	public static ArrayList<Class<?>> listAll() {
		ArrayList<Class<?>> w = new ArrayList<>();
		w.add(NguyenWidrow.class);
		return w;
	}
	
	/**
	 * initialize the weight
	 */
	public abstract void initialize(Network network);
}

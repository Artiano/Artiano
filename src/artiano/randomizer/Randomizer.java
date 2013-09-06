/**
 * 
 */
package artiano.randomizer;

import artiano.core.structure.Matrix;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-13
 * @author (latest modification by Nano.Michael)
 * @function 
 * @since 1.0
 */
public interface Randomizer {
	/**
	 * next random
	 * @return the next random
	 */
	public double next();
	/**
	 * write random to array
	 * @param d data need to be randomized
	 */
	public void writeTo(double[] d);
	
	/**
	 * write random to matrix
	 * @param x - matrix
	 */
	public void writeTo(Matrix x);
}

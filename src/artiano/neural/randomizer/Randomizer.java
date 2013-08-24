/**
 * 
 */
package artiano.neural.randomizer;

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
	 * 
	 * @return the next random
	 */
	public double next();
	/**
	 * 
	 * @param d data need to be randomized
	 */
	public void writeTo(double[] d);
}

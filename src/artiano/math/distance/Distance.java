/**
 * Distance.java
 */
package artiano.math.distance;

import artiano.core.structure.Matrix;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-10-15
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public interface Distance {
	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public double calculate(Matrix a, Matrix b);
}

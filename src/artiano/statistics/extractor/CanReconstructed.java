/**
 * CanReconstructed.java
 */
package artiano.statistics.extractor;

import artiano.core.structure.Matrix;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-25
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public interface CanReconstructed {
	/**
	 * reconstruct the matrix according to the feature extracted
	 * @param feature - the feature extracted
	 * @return - the matrix reconstructed
	 */
	public Matrix reconstruct(Matrix feature);
}

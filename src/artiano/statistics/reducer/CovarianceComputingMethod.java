/**
 * CovarianceComputingMethod.java
 */
package artiano.statistics.reducer;

import artiano.core.structure.Matrix;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-29
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public interface CovarianceComputingMethod {
	/**
	 * compute the covariance
	 * @param matrices
	 * @param mean
	 * @return - covariance matrix
	 */
	public Matrix compute(Matrix[] matrices, Matrix mean);
}

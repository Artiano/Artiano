/**
 * SolvingAlgorithm.java
 */
package artiano.statistics.extractor;

import artiano.core.structure.Matrix;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-29
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public interface SolvingAlgorithm {
	/**
	 * find solution
	 * @param cov1 - the covariance matrix between the classes
	 * @param cov2 - the covariance matrix within the classes
	 */
	public void solve(Matrix cov1, Matrix cov2);
	
	/**
	 * get the projection matrix
	 * @return - the projection matrix
	 */
	public Matrix getProjectionMatrix();
	
	/**
	 * get the eigen value
	 * @return - the eigen value
	 */
	public Matrix getEigenValue();
}

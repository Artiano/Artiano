/**
 * DirectedCovariance.java
 */
package artiano.statistics.extractor;

import artiano.core.operation.MatrixOpt;
import artiano.core.structure.Matrix;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-29
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class DirectedCovariance implements CovarianceComputingMethod {
	
	public DirectedCovariance(){ }
	
	/* (non-Javadoc)
	 * @see artiano.statistics.extractor.CovarianceComputingMethod#compute(artiano.core.structure.Matrix[], artiano.core.structure.Matrix)
	 */
	@Override
	public Matrix compute(Matrix[] matrices, Matrix mean) {
		Matrix cov = null;
		if (matrices[0].columns() > matrices.length)
			cov = MatrixOpt.computeCovarianceByRow(matrices, mean, 1.);
		else
			cov = MatrixOpt.computeCovarianceByCol(matrices, mean, 1.);
		return cov;
	}

}

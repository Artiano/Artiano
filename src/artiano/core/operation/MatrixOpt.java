/**
 * MatrixOpt.java
 */
package artiano.core.operation;

import artiano.core.structure.Matrix;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-25
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class MatrixOpt {
	
	/**
	 * calculate the mean matrix of a set of matrices
	 * @param matrices - a set of matrices to compute mean
	 * @return - mean matrix
	 */
	public Matrix computeMean(Matrix[] matrices){
		Matrix mean = new Matrix(matrices[0].rows(), matrices[0].columns());
		for (int i = 0; i < matrices.length; i++)
			mean.add(matrices[i]);
		mean.divide(matrices.length);
		return mean;
	}
	
	/**
	 * 
	 * @param matrices
	 * @param mean
	 * @param scale
	 * @return
	 */
	public Matrix computeCovarianceByRow(Matrix[] matrices, Matrix mean, double scale){
		Matrix cov = new Matrix(matrices.length, matrices.length);
		for (int i = 0; i < matrices.length; i++){
		}
		return cov;
	}
}

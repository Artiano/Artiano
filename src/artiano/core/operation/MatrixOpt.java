/**
 * MatrixOpt.java
 */
package artiano.core.operation;

import artiano.core.structure.Matrix;

/**
 * <p>Description: Operation of the matrix</p>
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
	 * @param start - start index of the matrices
	 * @param length - length of the matrices to compute
	 * @return - mean matrix
	 */
	public static Matrix computeMean(Matrix[] matrices, int start, int length){
		if (start < 0 || start + length > matrices.length)
			throw new IllegalArgumentException("MatrixOpt computMean, out of range.");
		Matrix mean = new Matrix(matrices[0].rows(), matrices[0].columns());
		for (int i = start; i < length; i++)
			mean.add(matrices[i]);
		mean.divide(matrices.length);
		return mean;
	}
	
	/**
	 * calculate the mean matrix of a set of matrices
	 * @param matrices
	 * @return - mean matrix
	 */
	public static Matrix computeMean(Matrix[] matrices){
		return computeMean(matrices, 0, matrices.length);
	}
	
	/**
	 * compute the covariance of the vectors consist of a set of row vector by row, this is an economical way to 
	 * save storage space while the dimension of the vectors is very large.
	 * @param vectors
	 * @param mean - mean matrix
	 * @param start - start to compute
	 * @param length - length to compute
	 * @param scale - scale, the covariance computed will multiply the scale
	 * @return - covariance matrix of the matrices
	 */
	public static Matrix computeCovarianceByRow(Matrix[] vectors, Matrix mean, int start, int length, double scale){
		if (vectors[0].rows() != 1)
			throw new IllegalArgumentException("MatrixOpt computeCovarianceByRow, accept row vectors only.");
		if (start < 0 || start + length > vectors.length)
			throw new IllegalArgumentException("MatrixOpt computeCovarianceByRow, out of range.");
		Matrix cov = new Matrix(length, length);
		for (int i = start; i < length; i++){
			Matrix t = vectors[i].subtract(mean, true);
			for (int j = 0; j < length; j++){
				Matrix r = vectors[j].subtract(mean, true).t();
				cov.set(i, j, t.multiply(r).data()[0]*scale);
			}
		}
		return cov;
	}
	
	/**
	 * compute the covariance of the vectors consist of a set of row vectors by row, this is an economical way to 
	 * save storage space while the dimension of the vectors is very large.
	 * @param vectors
	 * @param mean - mean matrix
	 * @return - covariance matrix of the matrices
	 */
	public static Matrix computeCovarianceByRow(Matrix[] vectors, Matrix mean, double scale){
		return computeCovarianceByRow(vectors, mean, 0, vectors.length, scale);
	}
	
	/**
	 * compute the covariance of the vectors consist of a set of row vectors by column, this is an economical way
	 * to save storage space while the dimension of the vectors is very large.
	 * @param vectors - row vectors
	 * @param mean - mean vector
	 * @param scale
	 * @return - covariance
	 */
	public static Matrix computeCovarianceByCol(Matrix[] vectors, Matrix mean, double scale){
		if (vectors[0].rows() != 1)
			throw new IllegalArgumentException("MatrixOpt computeCovarianceByCol, accept row vectors only.");
		Matrix cov = new Matrix(vectors[0].columns(), vectors[0].columns());
		Matrix t = new Matrix(1, vectors.length);
		Matrix r = new Matrix(vectors.length, 1);
		for (int i = 0; i < vectors[0].columns(); i++){
			for (int k = 0; k < vectors.length; k++)
				t.set(0, k, vectors[k].at(0, i) - mean.at(0, i));
			for (int j = 0; j < vectors[0].columns(); j++){
				for (int h = 0; h < vectors.length; h++)
					r.set(h, 0, vectors[h].at(0, j) - mean.at(0, j));
				cov.set(i, j, t.multiply(r).data()[0]*scale);
			}
		}
		return cov;
	}
	
	/**
	 * calculate the 2-dimensional covariance of the matrices
	 * @param matrices
	 * @param mean - mean matrix
	 * @param scale
	 * @return - covariance matrix
	 */
	public static Matrix compute2DCovariance(Matrix[] matrices, Matrix mean, double scale){
		Matrix cov = new Matrix(mean.columns(), mean.columns());
		for (int i = 0; i < matrices.length; i++){
			Matrix t = matrices[i].subtract(mean, true);
			cov.add(t.t().multiply(t));
		}
		cov.multiply(scale);
		return cov;
	}
	
	/**
	 * calculate the 2-dimensional covariance of the matrices
	 * @param matrices
	 * @param mean - mean matrix
	 * @param scale
	 * @return - covariance matrix
	 */
	public static Matrix compute2DCovariance(Matrix[] matrices, Matrix mean){
		return compute2DCovariance(matrices, mean, 1.);
	}
}

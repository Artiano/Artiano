/**
 * CholeskyDecomposition.java
 */
package artiano.math.linearalgebra;

import artiano.core.structure.Matrix;


/**
 * <p>Description: Cholesky decomposition, this class for find the inversion of a matrix or solve the linear equation 
 * system like A*x=b while the matrix is symmetric and positive-definite, this method if very efficient. </p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-18
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class CholeskyDecomposition {
	
	protected Matrix a = null;
	protected double[] p = null;
	protected boolean isDef = true;
	
	/**
	 * constructor
	 * @param a - coefficient matrix
	 * @param reserve - to indicate reserve A whether or not
	 */
	public CholeskyDecomposition(Matrix a, boolean reserve){
		if (a.rows() != a.columns())
			throw new IllegalArgumentException("Accept square matrix only.");
		if (reserve)
			this.a = a.clone();
		else
			this.a = a;
		p = new double[a.rows()];
		decompose();
	}
	
	/* (non-Javadoc)
	 * @see artiano.math.linearalgebra.CholeskyDecomposition#CholeskyDecomposition(double[][], boolean)
	 */
	public CholeskyDecomposition(Matrix a){
		this(a, false);
	}
	
	/**
	 * get inversion of the coefficient matrix A
	 * @return - inversion matrix
	 */
	public Matrix inverse(){
		//unit matrix;
		Matrix I = Matrix.unit(a.rows(), 1);
		return solve(I);
	}
	
	/**
	 * judge if the matrix A is positive-definite
	 * @return - true if is positive-definite or false otherwise
	 */
	public boolean isDefinite(){
		return isDef;
	}
	
	/**
	 * decompose the coefficient matrix to the form like L*L' = A
	 */
	protected void decompose(){
		double sum = 0.;
		int i, j, k;
		
		int n = a.rows();
		for (i = 0; i < n; i++){
			for (j = i; j < n; j++){
				for (sum = a.at(i,j), k = i - 1; k >= 0; k--) sum -= a.at(i,k) * a.at(j, k);
				if (i == j){
					//not positive-definite
					if (sum <= 0.){
						isDef = false;
						return;
					}
					p[i] = Math.sqrt(sum);
				}else 
					a.set(j, i, sum / p[i]);
			}
		}
	}
	
	/**
	 * solve the matrix equation like A*x = B, B is row vector
	 * @param b - constant matrix
	 * @return - solution matrix
	 */
	public Matrix solve(Matrix b){
		if (b.rows() != a.rows())
			throw new IllegalArgumentException("Cholesky decomposition solve, Size not match.");
		if (!isDef)
			throw new UnsupportedOperationException("Cholesky decompositon, matrix is not positive-definite.");
		
		int i, k;
		double sum = 0.;
		Matrix x = new Matrix(b.rows(), b.columns());
		
		int n = a.rows();
		int m = b.columns();
		for (int j = 0; j < m; j++){
			for (i = 0; i < n; i++){
				for (sum = b.at(i, j), k = i - 1; k >= 0; k--) sum -= a.at(i,k) * x.at(k, j);
				x.set(i, j, sum / p[i]);
			}
			for (i = n - 1; i >= 0; i--){
				for (sum = x.at(i, j), k = i + 1; k < n; k++) sum -= a.at(k, i) * x.at(k, j);
				x.set(i, j, sum / p[i]);
			}
		}
		return x;
	}
}

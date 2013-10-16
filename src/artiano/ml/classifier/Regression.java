package artiano.ml.classifier;

import artiano.core.structure.Matrix;
import artiano.math.algebra.LUDecomposition;

public class Regression {	    
    /**
     * generalized least squares to complete linear regression
     * 
     * Returns the parameters 'w0', 'w1', 'w2', ..., 'wk' for a polynomial 
     * function of order k, y = w0 + w1 * x1 + w2 * x2 + ... + wk * xk,
     * fitted to the data using a multiple linear regression equation.
     * The result is returned as an matrix with a length of k + 1,
     * where matrix.at(0,0) --> w0, matrix.at(0, 1) --> w1, .., matrix.at(0)(k) --> wk.
     *   
     * @param XData - data points of  (x1, x2, ..., xk) 
     * @param YData - matrix of values for each (x1, x2, ..., xk)     
     *
     * @return The parameters.
     */
    public static Matrix getOLSRegression(Matrix XData, Matrix YData) {    	   
    	Matrix coefficients = generateCoefficientMatrix(XData);
    	/* Solve AW=b to coefficients of the linear regression Polynomial */
    	Matrix A = genearteLeftHandSide(coefficients);    	
    	Matrix b = generateRightHandSide(YData, coefficients);
    	LUDecomposition luDecomposition = new LUDecomposition(A);
    	return luDecomposition.solve(b); 	 
    }	       

	private static Matrix generateRightHandSide(Matrix right_hand, Matrix coefficients) {
		int rows = coefficients.rows();
		int cols = coefficients.columns() - 1;
		Matrix b = new Matrix(cols + 1, 1);
    	for(int i=0; i<cols+1; i++) {
    		double sumY = 0;
    		for(int n=0; n<rows; n++) {
				sumY += right_hand.at(n, 0) * coefficients.at(n, i);    				
			}    			    			
			b.set(i, 0, sumY);
    	}
		return b;
	}

	private static Matrix genearteLeftHandSide(Matrix coefficients) {
		int rows = coefficients.rows();
		int cols = coefficients.columns() - 1;
		Matrix a = new Matrix(cols + 1, cols + 1);    	
    	for(int i=0; i<a.rows(); i++) {
    		for(int j=0; j<a.columns(); j++) {
    			double sumX = 0;    			
    			for(int n=0; n<rows; n++) {
    				sumX += coefficients.at(n, j) * coefficients.at(n, i);    				
    			}
    			a.set(i, j, sumX);    			    		
    		}    		    		
    	}
		return a;
	}

	private static Matrix generateCoefficientMatrix(Matrix left_hand) {
		Matrix coefficients = 
			new Matrix(left_hand.rows(), left_hand.columns() + 1);
    	for(int i=0; i<coefficients.rows(); i++) {
    		for(int j=0; j<coefficients.columns(); j++) {
    			if(j == 0) {
    				coefficients.set(i, j, 1);
    			} else {
    				coefficients.set(i, j, left_hand.at(i, j - 1));
    			}    			
    		}
    	}
		return coefficients;
	}
         		
}
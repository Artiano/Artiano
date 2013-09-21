package artiano.ml.classifier;

import artiano.core.structure.Matrix;
import artiano.core.structure.Range;

public class Regression {

	/**
     * generalized least squares to complete single-variable linear regression
     * 
     * Returns the parameters 'a' and 'b' for an equation y = ax + b, fitted to
     * the data using ordinary least squares regression.  The result is
     * returned as a double[], where result[0] --> a, and result[1] --> b.  
     * 
	 * y = a x + b  
	 * 
	 * a = ( n * sum( xy ) - sum( x ) * sum( y ) ) / ( n * sum( x^2 ) - sum(x) ^ 2 )
	 * b = sum( y ) / n - a * sum( x ) / n 
	 *  
     * @param data  the data.
     *
     * @return The parameters.
     */
    public static double[] getOLSRegression(double[][] data) {
        int n = data.length;
        if (n < 2) {
            throw new IllegalArgumentException("Not enough data.");
        }

        double sumX = 0;
        double sumY = 0;
        double sumXX = 0;
        double sumXY = 0;
        for (int i = 0; i < n; i++) {
            double x = data[i][0];
            double y = data[i][1];
            sumX += x;
            sumY += y;
            double xx = x * x;
            sumXX += xx;
            double xy = x * y;
            sumXY += xy;
        }
        
        double[] result = new double[2];                    
        result[0] = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX)  ; 
        result[1] = sumY / n - result[0] * sumX / n;        

        return result;
    }
    
    /**
     * generalized least squares to complete multiple-variable linear regression
     * 
     * Returns the parameters 'a0', 'a1', 'a2', ..., 'an' for a polynomial 
     * function of order n, y = a0 + a1 * x1 + a2 * x2 + ... + an * xn,
     * fitted to the data using a multiple linear regression equation.
     * The result is returned as an matrix with a length of n + 1,
     * where matrix.at(0,0) --> a0, matrix.at(0, 1) --> a1, .., double[0, n] --> an.
     *   
     * 	
     * @param data - the data. data.at(i,0) for x1,data.at(i, 1) for x2, ...,
     * data.at(i,n-1) for xn, dat.at(i, n) for corresponding function value
     *
     * @return The parameters.
     */
    public static Matrix getMultipleLinearRegression(Matrix data) {
    	//Matrix dataTranverse = data.t();
    	
    	Matrix coefficientMatrix = data.at(new Range(0, data.rows()), new Range(0, data.columns()-1));
    	Matrix temp1 = new Matrix(coefficientMatrix.rows(), coefficientMatrix.columns()+1);
    	for(int i=0; i<temp1.rows(); i++) {
    		for(int j=0; j<temp1.columns(); j++) {
    			if(j == 0) {
    				temp1.set(i, j, 1);
    			} else {
    				temp1.set(i, j, coefficientMatrix.at(i, j - 1));
    			}
    		}
    	}    	
    	temp1.print();
    	
    	Matrix constantMatrix = 
    		data.at(new Range(0, data.rows()), new Range(data.columns()-1, data.columns()));    
    	
    	Matrix temp = temp1.t().multiply(temp1);
    	Matrix inverse = 
    		(InverseMatrix.getMatrixInversion(temp).multiply(temp1.t())).multiply(constantMatrix);
    	return inverse;
    }
    
    public static void main(String[] args) {
/*		//sample points
		double[][] data = {
				{2.0, 56.27}, {3.0, 41.32}, {4.0, 31.45}, {5.0, 30.05},
				{6.0, 24.69}, {7.0, 19.78}, {8.0, 20.94}, {9.0, 16.73},
				{10.0, 14.21}, {11.0, 12.44}
		};
			
		double[] resultUsingOLR = getOLSRegression(data);
		System.out.println("a= " + resultUsingOLR[0] + ", b= " + resultUsingOLR[1]);
*/
/*		double[] data3 = { 
				//2, 2, 1, 3, 1, 5, 3, 2, 3
				//8, 4, 9, 2, 3, 5, 7, 6, 1
				5, -2, 1, 3
		};  
		Matrix matrix = new Matrix(2, 2, data3);
		Matrix inversion = InverseMatrix.getMatrixInversion(matrix);
		inversion.print();
*/
    	double[] data1 = {
    		0.4, 52, 158, 64,
    		0.4, 23, 163, 60,
    		3.1, 19, 37, 71,
    		0.6, 34, 157, 61,
    		4.7, 24, 59, 54,
    		1.7, 65, 123, 77,
    		9.4, 44, 46, 81,
    		10.1, 31, 117, 93,
    		11.6, 29, 173, 93,
    		126, 58, 112, 51,
    		10.9, 37, 111, 76,
    		23.1, 46, 114, 96,
    		23.1, 50,134, 77,
    		21.6, 44, 73, 93,
    		23.1, 56, 168, 95,
    		1.9, 36, 143, 54, 
    		26.8, 58, 202, 168,
    		29.9, 51, 124, 99
    	};
    	
    	Matrix src = new Matrix(data1.length / 4 , 4, data1);
    	Matrix dst = getMultipleLinearRegression(src);
    	dst.print();
	}    		
}
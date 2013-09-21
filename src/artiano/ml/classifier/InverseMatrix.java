package artiano.ml.classifier;

import artiano.core.structure.Matrix;

public class InverseMatrix {

	/**
	 * Get algebraic for element at (rowSplit, colSplit)
	 * @param data - input data
	 * @param rowSplit - the row to split the matrix to top half and bottom half
	 * @param colSplit - the column to split the matrix to left half and right half
	 * @return algebraic for element at (rowSplit, colSplit)
	 */
	private static Matrix getAlgebraic(Matrix data, int rowSplit, int colSplit) {
		Matrix newData = new Matrix(data.rows() - 1, data.columns() - 1);
		for(int i=0; i<data.rows(); i++) {
			if(i < rowSplit) {
				InverseMatrix.copyLeftHalfMatrixToAlgebraic(data, colSplit, newData, i);
			} else if(i > rowSplit) {
				InverseMatrix.copyRightHalfMatrixToAlgebraic(data, colSplit, newData, i);
			}    		    		
		}
		
		return newData;
	}

	/**
	 * Get value of the relative determinant
	 * @param data - input data 
	 * @return value of the relative determinant
	 */
	private static double getValueOfDeterminant(Matrix data) {
		if(data.rows() == 1) {
			return data.at(0, 0);
		}
		
		if(data.rows() == 2) {
			return data.at(0, 0) * data.at(1, 1) - data.at(0, 1) * data.at(1, 0);
		}
		
		double valueOfDeterminant = 0;
		double[] determinantValues = new double[data.columns()];
		for(int j=0; j<data.columns(); j++) {
			if(j % 2 == 0) {
				determinantValues[j] = data.at(0, j) * 
	    				getValueOfDeterminant(getAlgebraic(data, 0, j));
			} else {
				determinantValues[j] = -1 * data.at(0, j) * 
	    				getValueOfDeterminant(getAlgebraic(data, 0, j));
			}    		
			valueOfDeterminant += determinantValues[j];
		}
		
		return valueOfDeterminant;
	}

	/**
	 * Get inversion of a specified matrix
	 * @param data - the data input
	 * @return - Inversion of a specified matrix
	 */
	public static Matrix getMatrixInversion(Matrix data) {
		//Get value of determinant for the matrix
		double valueOfDeterminant = getValueOfDeterminant(data);
		
		Matrix inversion = new Matrix(data.rows(), data.columns());
		for(int i=0; i<inversion.rows(); i++) {
			for(int j=0; j<inversion.columns(); j++) {
				double num = getValueOfDeterminant(getAlgebraic(data, i, j));
				if( (i + j) % 2 == 0 ) {
					inversion.set(i, j, num / valueOfDeterminant);
				} else {
					inversion.set(i, j, -1 * num / valueOfDeterminant);
				}    			
			}
		}    	
		
		return inversion.t();
	}

	/**
	 * 
	 * @param data - input data
	 * @param colSplit - the column to split the matrix to left half and right half
	 * @param newData - matrix of specified algebraic
	 * @param i - row num of the split element
	 */
	private static void copyRightHalfMatrixToAlgebraic(Matrix data, int colSplit, Matrix newData, int i) {
		for(int j=0; j<data.columns(); j++) {
			if(j< colSplit) {
				newData.set(i - 1, j, data.at(i, j));
			} else if(j > colSplit) {
				newData.set(i - 1, j - 1, data.at(i, j));
			}
		}
	}

	/**
	 * 
	 * @param data - input data
	 * @param colSplit - the column to split the matrix to left half and right half
	 * @param newData -  matrix of specified algebraic
	 * @param i - row num of the split element
	 */
	private static void copyLeftHalfMatrixToAlgebraic(Matrix data, int colSplit, Matrix newData, int i) {
		for(int j=0; j<data.columns(); j++) {
			if(j< colSplit) {
				newData.set(i, j, data.at(i, j));
			} else if(j > colSplit) {
				newData.set(i, j - 1, data.at(i, j));
			}
		}
	}

}

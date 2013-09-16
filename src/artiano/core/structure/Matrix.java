/**
 * Matrix.java
 */
package artiano.core.structure;

import java.io.Serializable;


/**
 * <p>Description: Basic structure matrix, contains amount of operation on matrix.</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-20
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class Matrix implements Serializable{
	
	private static final long serialVersionUID = 1L;
	/**
	 * columns of the matrix
	 */
	protected int cols = 0;
	/**
	 * rows of the matrix
	 */
	protected int rows = 0;
	/**
	 * data of the matrix
	 */
	protected double[] d = null;
	/**
	 * data columns, the original columns of the matrix
	 */
	protected int dCols = 0;
	/**
	 * row range of the original matrix, while rowRange is specified, the start of the row index is
	 * rowRange.begin() relative to the original matrix, and the end index of the row index is rowRange.end(). 
	 * For example: while rowRange.start==2 and rowRange.end==5, the start and the end of the row index is 2
	 * and 4 relatively.
	 */
	protected Range rowRange = null;
	/**
	 * column range of the original matrix.
	 */
	protected Range colRange = null;
	
	private Matrix(){ }
	
	/**
	 * Create a matrix with specified rows and columns.
	 * @param rows Rows of the matrix.
	 * @param cols Columns of the matrix.
	 */
	public Matrix(int rows, int cols){
		this(rows, cols, new double[cols * rows]);
	}
	
	/**
	 * Create a matrix with specified rows and columns that holds data.
	 * <li><b><i>NOTICE:</i></b> If the size (size=rows*columns) of the matrix is less than the length of the data, the program 
	 * can normally running, but that is not recommended.</li>
	 * @param rows Rows of the matrix.
	 * @param cols Columns of the matrix.
	 * @param data Data to hold.
	 */
	public Matrix(int rows, int cols, double[] data){
		if (cols <= 0 || rows <= 0)
			throw new IllegalArgumentException("Matrix, columns and rows must be positive integer.");
		if (cols * rows > data.length)
			throw new IllegalArgumentException("Matrix, the size of the matrix does not match the length of the data.");
		this.cols = cols;
		this.rows = rows;
		this.d = data;
		this.dCols = cols;
		rowRange = new Range(0, rows);
		colRange = new Range(0, cols);
	}
	
	/**
	 * set the zero to the matrix
	 */
	public void clear(){
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				set(i, j, 0);
	}
	
	/**
	 * Copy current matrix to destination.
	 * @param x Destination matrix
	 */
	public void copyTo(Matrix x){
		if (x.rows != rows || x.cols != cols)
			throw new IllegalArgumentException("Matrix copy, size not match.");
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				x.set(i, j, at(i, j));
	}
	
	/**
	 * Get data stored in the matrix.
	 * @return Data
	 */
	public double[] data(){
		return this.d;
	}
	
	/**
	 * Get columns.
	 * @return Columns of the matrix.
	 */
	public int columns(){
		return this.cols;
	}
	
	/**
	 * Get rows
	 * @return - Rows of the matrix.
	 */
	public int rows(){
		return this.rows;
	}
	
	/**
	 * Get the row vector at row i of the matrix.
	 * @param i Row index
	 * @return Row vector with specified row index.
	 */
	public Matrix row(int i){
		return at(new Range(i, i+1), Range.all());
	}
	
	/**
	 * Get the column vector at column i of the matrix.
	 * @param i Column index
	 * @return Column vector with specified column index.
	 */
	public Matrix col(int i){
		return at(Range.all(), new Range(i, i+1));
	}
	
	/**
	 * Create a matrix like A=u*I, I is unit matrix, u is a scale.
	 * @param size Matrix size (size=rows=columns)
	 * @param scale Value to set
	 * @return A diagonal matrix.
	 */
	public static Matrix unit(int size, double scale){
		Matrix x = new Matrix(size, size);
		for (int i = 0; i < size; i++)
			x.set(i, i, scale);
		return x;
	}
	
	/**
	 * Create a unit matrix
	 * @param size Matrix size (size=rows=columns)
	 * @return Unit matrix
	 */
	public static Matrix unit(int size){
		return unit(size, 1.);
	}
	
	/**
	 * Create a matrix that all the element hold the same number.
	 * @param rows Rows of the matrix
	 * @param cols Columns of the matrix
	 * @param scale The scale want to set
	 * @return A matrix that all the element hold the same number.
	 */
	public static Matrix ones(int rows, int cols, double scale){
		Matrix x = new Matrix(rows, cols);
		for (int i = 0; i < x.rows; i++)
			for (int j = 0; j < x.cols; j++)
				x.set(i, j, scale);
		return x;
	}
	
	/**
	 * Create a matrix that all the element is 1
	 * @param rows Rows of the matrix
	 * @param cols Columns of the matrix
	 * @return
	 */
	public static Matrix ones(int rows, int cols){
		return ones(rows, cols, 1.);
	}
	
	/**
	 * Calculate the trace of the matrix
	 * @return Trace of the matrix
	 */
	public double trace(){
		if (rows != cols)
			throw new UnsupportedOperationException("Matrix trace, only squre matrix has trace.");
		double tr = 0.;
		for (int i = 0; i < rows; i++)
			tr += at(i, i);
		return tr;
	}
	
	/**
	 * Get element value at index i while the matrix is a vector (both row vector and column vector).
	 * @param i Index
	 * @return Value at the index i
	 */
	public double at(int i){
		if (rows != 1 && cols != 1)
			throw new UnsupportedOperationException("Matrix at, only vector takes one parameter.");
		if (rows == 1)
			return at(0, i);
		else
			return at(i, 0);
	}
	
	/**
	 * Get the element value at row i and column j.
	 * @param i Row index
	 * @param j Column index
	 * @return Value of row i column j
	 */
	public double at(int i, int j){
		if (i < 0 || i >= rows || j < 0 || j >= cols)
			throw new IndexOutOfBoundsException("Matrix at, index out of range.");
		return d[(i + rowRange.begin()) * dCols + j + colRange.begin()];
	}
	
	/**
	 * Get the sub-matrix determined by the row range and column range
	 * <li><b><i>NOTICE:</i></b> The data will not be copied. If you want to get a copy of the sub-matrix, you should
	 * write code like:
	 * <code><br>Matrix y=new Matrix(2,2); //create a new matrix with 2 rows and 2 columns
	 * <br>x.at(new Range(1,3), new Range(2,4).copyTo(y); //copy the sub-matrix of x to y</code></li>
	 * @param row  Row range
	 * @param col Column range
	 * @return A sub-matrix of the matrix
	 */
	public Matrix at(Range row, Range col){
		row = row.equals(Range.all()) ? this.rowRange:
			new Range(row.begin() + this.rowRange.begin(), row.end() + this.rowRange.begin());
		col = col.equals(Range.all()) ? this.colRange: 
			new Range(col.begin() + this.colRange.begin(), col.end() + this.colRange.begin());
		
		if (!this.rowRange.isContain(row) || !this.colRange.isContain(col))
			throw new IllegalArgumentException("Matrix at, out of range.");
		Matrix x = new Matrix();
		// row range of x
		x.rowRange = row;
		//column range of x
		x.colRange = col;
		x.dCols = dCols;
		x.d = d;
		x.rows = x.rowRange.length();
		x.cols = x.colRange.length();
		return x;
	}
	
	/**
	 * Set the value to the index i of the matrix while it is a vector (both row vector and column vector)
	 * @param i Index
	 * @param value Value to set
	 */
	public void set(int i, double value){
		if (rows != 1 && cols != 1)
			throw new UnsupportedOperationException("Matrix set, only vector takes one parameter.");
		if (rows == 1)
			set(0, i, value);
		else
			set(i, 0, value);
	}
	
	/**
	 * Set a value to row i column j
	 * @param i Row index
	 * @param j Column index
	 * @param value Value to set
	 */
	public void set(int i, int j, double value){
		if (i < 0 || i >= rows || j < 0 || j >= cols)
			throw new IndexOutOfBoundsException("Matrix set, index out of range."+" i:"+i+"    j:"+j);
		d[(i + rowRange.begin()) * dCols + j + colRange.begin()] = value;
	}
	
	/**
	 * Set the value of the specified matrix to the specified sub-matrix of current matrix.
	 * @param row Row range
	 * @param col Column range
	 * @param value Matrix to set
	 */
	public void set(Range row, Range col, Matrix value){
		Matrix x = at(row, col);
		if (value.rows != x.rows || value.cols != x.cols)
			throw new IllegalArgumentException("Matrix set, size not match.");
		for (int i = 0; i < x.rows; i++)
			for (int j = 0; j < x.cols; j++)
				x.set(i, j, value.at(i, j));
	}
	
	/**
	 * Set the value of the specified row to the matrix
	 * @param i Row index
	 * @param value Row vector to set
	 */
	public void setRow(int i, Matrix value){
		if (value.rows != 1)
			throw new IllegalArgumentException("Matrix setRow, accept row vector only.");
		if (value.cols != cols)
			throw new IllegalArgumentException("Matrix setRow, size not match.");
		for (int j = 0; j < value.cols; j++)
			set(i, j, value.at(0, j));
	}
	
	/**
	 * Set the value of the specified column to the matrix.
	 * @param i Column index
	 * @param value Column vector to set
	 */
	public void setCol(int i, Matrix value){
		if (value.cols != 1)
			throw new IllegalArgumentException("Matrix setCol, accept column vector only.");
		if (value.rows != rows)
			throw new IllegalArgumentException("Matrix setCol, size not match.");
		for (int j = 0; j < value.rows; j++)
			set(j, i, value.at(j, 0));
	}
	
	/**
	 * Transpose the matrix
	 * @return Transpose of the matrix
	 */
	public Matrix t(){
		Matrix x = new Matrix(cols, rows);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j <cols; j++)
				x.set(j, i, at(i, j));
		return x;
	}
	
	/**
	 * Matrix addition (z = x + y)
	 * <li><b><i>NOTICE:</i></b> The method will replace the matrix with the new matrix after added.</li>
	 * @param x Matrix to add
	 * @return Result
	 */
	public Matrix add(Matrix x){
		return add(x,false);
	}
	
	/**
	 * Matrix addition
	 * @param x Matrix to add
	 * @param reserve Indicate replace the matrix whether or not, if parameter <code>reserve</code> is false,
	 * the program will replace the matrix with the new matrix after added.
	 * @return Result
	 */
	public Matrix add(Matrix x, boolean reserve){
		if (rows != x.rows || cols != x.cols)
			throw new IllegalArgumentException("Matrix add, size not match.");
		Matrix y = reserve ? new Matrix(rows, cols): this;
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				y.set(i, j, at(i, j) + x.at(i, j));
		return y;
	}
	
	/**
	 * Matrix addition (z=x+y, x is a scale)
	 * @param x Number to add
	 * @param reserve Indicate replace the matrix whether or not, if parameter <code>reserve</code> is false,
	 * the program will replace the matrix with the new matrix after added.
	 * @return - result
	 */
	public Matrix add(Number x, boolean reserve){
		Matrix y = reserve ? new Matrix(rows, cols): this;
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				y.set(i, j, at(i, j) + x.doubleValue());
		return y;
	}
	
	/**
	 * Matrix addition (z=x+y, x is scale)
	 * <li><b><i>NOTICE:</i></b> The method will replace the matrix with new matrix after added.</li>
	 * @param x Number to add
	 * @return Result
	 */
	public Matrix add(Number x){
		return add(x,false);
	}
	
	/**
	 * Add a value to row i column j.
	 * @param i Row index
	 * @param j Column index
	 * @param value Value to add
	 */
	public void add(int i, int j, Number value){
		if (i < 0 || i >= rows || j < 0 || j >= cols)
			throw new IndexOutOfBoundsException("Matrix at, index out of range.");
		d[(i + rowRange.begin()) * dCols + j + colRange.begin()] += value.doubleValue();
	}
	
	/**
	 * Element of row i column j subtract the specified number.
	 * @param i Row index
	 * @param j Column index
	 * @param value Value to subtract
	 */
	public void subtract(int i, int j, Number value){
		if (i < 0 || i >= rows || j < 0 || j >= cols)
			throw new IndexOutOfBoundsException("Matrix at, index out of range.");
		d[(i + rowRange.begin()) * dCols + j + colRange.begin()] -= value.doubleValue();
	}
	
	/**
	 * Matrix subtraction (z=x-y)
	 * <li><b><i>NOTICE:</i></b> The method will replace the matrix with new matrix after subtracted.</li>
	 * @param x Matrix to subtract
	 * @return Result
	 */
	public Matrix subtract(Matrix x){
		return subtract(x,false);
	}
	
	/**
	 * Matrix subtraction (z=x-y)
	 * @param x Matrix to subtract
	 * @param reserve Indicate replace the matrix whether or not, if parameter <code>reserve</code> is false,
	 * the program will replace the matrix with the new matrix after subtracted.
	 * @return Result
	 */
	public Matrix subtract(Matrix x, boolean reserve){
		if (x.rows != rows || x.cols != cols)
			throw new IllegalArgumentException("Matrix subtract, size not match.");
		Matrix y = reserve ? new Matrix(rows, cols): this;
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				y.set(i, j, at(i, j) - x.at(i, j));
		return y;
	}
	
	/**
	 * Matrix subtraction (z=x-y, x is a scale)
	 * <li><b><i>NOTICE:</i></b> The method will replace the matrix with new matrix after subtracted.</li>
	 * @param x Number to subtract
	 * @return Result
	 */
	public Matrix subtract(Number x){
		return subtract(x, false);
	}
	
	/**
	 * Matrix subtraction (z=x-y, x is a scale)
	 * @param x Number to subtract
	 * @param reserve Indicate replace the matrix whether or not, if parameter <code>reserve</code> is false,
	 * the program will replace the matrix with the new matrix after subtracted.
	 * @return Result
	 */
	public Matrix subtract(Number x, boolean reserve){
		Matrix y = reserve ? new Matrix(rows, cols): this;
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				y.set(i, j, at(i, j) - x.doubleValue());
		return y;
	}
	
	/**
	 * Matrix multiplication (z=x*y)
	 * @param x Right side matrix, y
	 * @return Result
	 */
	public Matrix multiply(Matrix x){
		if (x.rows != cols)
			throw new IllegalArgumentException("Matrix multiplication, size not match.");
		int m = rows;
		int s = x.rows;
		int n = x.cols;
		Matrix y = new Matrix(m, n);
		for (int i = 0; i < m; i++)
			for (int j = 0; j < n; j++)
				for (int k = 0; k < s; k++)
					y.add(i, j ,at(i, k) * x.at(k, j));
		return y;
	}
	
	/**
	 * matrix multiplication (z=x*y, x is scale)
	 * @param x Specified number to multiply.
	 * @param reserve Indicate replace the matrix whether or not, if parameter <code>reserve</code> is false,
	 * the program will replace the matrix with the new matrix after multiplied.
	 * @return Result
	 */
	public Matrix multiply(Number x, boolean reserve){
		Matrix y = reserve ? new Matrix(rows, cols): this;
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				y.set(i, j, at(i, j) * x.doubleValue());
		return y;
	}
	
	/**
	 * Matrix multiplication (z=x*y, x is scale)
	 * <li><b><i>NOTICE:</i></b> The method will replace the matrix with new matrix after multiplied.</li>
	 * @param x Specified number to multiply.
	 * @return Result.
	 */
	public Matrix multiply(Number x){
		return multiply(x,false);
	}
	
	/**
	 * Value at row i column j multiply specified number.
	 * @param i Row index
	 * @param j Column index
	 * @param x Number to multiply
	 */
	public void multiply(int i, int j, Number x){
		if (i < 0 || i >= rows || j < 0 || j >= cols)
			throw new IndexOutOfBoundsException("Matrix at, index out of range.");
		set(i, j, at(i, j) * x.doubleValue());
	}
	
	/**
	 * Matrix value at row i column j divide a number.
	 * @param i Row index
	 * @param j Column index
	 * @param x Number to divide
	 */
	public void divide(int i, int j, Number x){
		if (i < 0 || i >= rows || j < 0 || j >= cols)
			throw new IndexOutOfBoundsException("Matrix at, index out of range.");
		set(i, j, at(i, j)/x.doubleValue());
	}
	
	/**
	 * Matrix division (z=x/y, y is scale)
	 * <li><b><i>NOTICE:</i></b> The method will replace the matrix with new matrix after divided</li>
	 * @param x Number to divide
	 * @return Result
	 */
	public Matrix divide(Number x){
		return divide(x,false);
	}
	
	/**
	 * Matrix division (z=x/y, y is scale)
	 * @param x Number to divide
	 * @param reserve Indicate replace the matrix whether or not, if parameter <code>reserve</code> is false,
	 * the program will replace the matrix with the new matrix after divided.
	 * @return Result
	 */
	public Matrix divide(Number x, boolean reserve){
		if (x.doubleValue() == 0.)
			throw new ArithmeticException("Matrix divide, divisor is 0.");
		Matrix y = reserve? new Matrix(rows, cols): this;
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				y.set(i, j, at(i, j)/x.doubleValue());
		return y;
	}
	
	/**
	 * Calculate the mean vector of every row.
	 * @return Mean vector
	 */
	public Matrix rowMean(){
		Matrix mean = new Matrix(1, cols);
		for (int i = 0; i < rows; i++)
			mean.add(row(i));
		mean.divide(rows);
		return mean;
	}
	
	/**
	 * Calculate the mean vector of every column.
	 * @return Mean vector
	 */
	public Matrix colMean(){
		Matrix mean = new Matrix(rows, 1);
		for (int i = 0; i < cols; i++)
			mean.add(col(i));
		mean.divide(cols);
		return mean;
	}
	
	/**
	 * Calculate the square root of the matrix
	 * @param reserve Indicate replace the matrix whether or not, if parameter <code>reserve</code> is false,
	 * the program will replace the matrix with the new matrix after calculated.
	 * @return Result
	 */
	public Matrix sqrt(boolean reserve){
		Matrix x = reserve? new Matrix(rows, cols): this;
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				x.set(i, j, Math.sqrt(at(i, j)));
		return x;
	}
	
	/**
	 * Calculate the square root of the matrix
	 * <li><b><i>NOTICE:</i></b> The method will replace the matrix with new matrix after calculated.</li>
	 * @return Result
	 */
	public Matrix sqrt(){
		return sqrt(false);
	}
	
	/**
	 * Calculate the difference between current matrix and specified matrix x.
	 * <li>Given matrix x and y, the method to calculate the difference is:
	 * <br>difference=sum[abs(x(i,j)-y(i,j)]</li>
	 * @param x Input matrix x
	 * @return Difference
	 */
	public double difference(Matrix x){
		if (x.rows != rows || x.cols != cols)
			throw new IllegalArgumentException("Matrix difference, size not match.");
		double dif = 0.;
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				dif += Math.abs(at(i, j) - x.at(i, j));
		return dif;
	}
	
	/**
	 * Calculate the l2-norm between current matrix and specified matrix x
	 * <li>Given matrix x and y, the method to calculate the l2-norm is:
	 * <br>l2-norm=sqrt{sum[x(i, j) - y(i, j)]}</li>
	 * @param x
	 * @return - l2-norm
	 */
	public double l2Norm(Matrix x){
		double norm = 0.;
		//store the scale, avoid underflow or overflow
		final double TINY = 1e-10;
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++){
				double t = at(i, j) - x.at(i, j);
				norm += t*t*TINY;
			}
		norm = Math.sqrt(norm)/TINY;
		return norm;
	}
	
	/**
	 * clone a matrix, data will copied
	 */
	@Override
	public Matrix clone(){
		Matrix x = new Matrix(rows, cols);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				x.set(i, j, at(i, j));
		return x;
	}
	
	/**
	 * Check whether content of the two matrixes are the same
	 * @param anotherMat - another matrix
	 * @return whether content of the two matrixes are the same
	 */
	@Override
	public boolean equals(Object obj) {
		Matrix anotherMat = (Matrix)obj;
		if(this.rows() != anotherMat.rows() || this.columns() != anotherMat.columns()) {
			return false;
		}
		
		//Check whether content of the two matrixes are the same
		for(int i=0; i<this.rows(); i++) {
			for(int j=0; j<this.columns(); j++) {
				if(this.at(i, j) != anotherMat.at(i, j)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return this.columns() * this.rows();
	}
	
	/**
	 * secondary function, print the matrix to console
	 */
	public void print(){
		System.out.println("-------------------------");
		java.text.DecimalFormat f = new java.text.DecimalFormat("#.## ");
		for (int i = 0; i < rows; i++){
			for (int j = 0; j < cols; j++)
				System.out.print(f.format(at(i, j)) + " ");
			System.out.println();
		}
		System.out.println("-------------------------");
	}
	/***
	 * 不格式化
	 */
	public void printAll(){
		System.out.println("-------------------------");;
		for (int i = 0; i < rows; i++){
			for (int j = 0; j < cols; j++)
				System.out.print(at(i, j) + " ");
			System.out.println();
		}
		System.out.println("-------------------------");		
	}
	/**
	 * Merge two matrices and store it to current matrix.
	 * <li>Given matrix x and y, and 
	 * <br>x=
	 * <br>|1 2 3|  
	 * <br>|4 5 6|
	 * <br>|7 8 9| ,
	 * <br>y=
	 * <br>|1 1 2|
	 * <br>|2 2 1|
	 * <br>The matrix after merge is:
	 * <br>x=
	 * <br>|1 2 3|  
	 * <br>|4 5 6|
	 * <br>|7 8 9|
	 * <br>|1 1 2|
	 * <br>|2 2 1|
	 * @param otherMx Matrix to merge.
	 */
	public void mergeAfterRow(Matrix otherMx){
		if (otherMx.cols != cols)
			throw new IllegalArgumentException("Matrix merge, size not match.");
		//create a new matrix
		Matrix x = new Matrix(otherMx.rows+this.rows, this.cols);
		//copy this to x
		this.copyTo(x.at(new Range(0, rows), Range.all()));
		//copy otherMx to x
		otherMx.copyTo(x.at(new Range(rows, x.rows), Range.all()));
		this.rows = x.rows;
		this.cols = x.cols;
		this.dCols = x.dCols;
		this.d = x.d;
		this.rowRange = x.rowRange;
		this.colRange = x.colRange;
	}
	/***
	 * 得到其中的一列
	 * @param colIndex 列的index
	 * @return
	 */
	public Matrix getSingerCol(int colIndex){
		Matrix mx=new Matrix(this.rows,1);
		for(int i=0;i<this.rows;i++){
			mx.set(i, 0, this.at(i,colIndex));
		}
		return mx;
	}
}

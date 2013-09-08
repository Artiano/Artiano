/**
 * Matrix.java
 */
package artiano.core.structure;

import java.io.Serializable;


/**
 * <p>Description: Basic structure matrix, contains amount of operation on matrices.</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-20
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class Matrix implements Serializable{
	
	private static final long serialVersionUID = 1L;
	//columns of the matrix
	protected int cols = 0;
	//rows of the matrix
	protected int rows = 0;
	//data of the matrix
	protected double[] d = null;
	//data rows
	protected int dCols = 0;
	//row range
	protected Range rowRange = null;
	//column range
	protected Range colRange = null;
	
	private Matrix(){ }
	
	/**
	 * constructor
	 * @param rows - rows of the matrix
	 * @param cols - columns of the matrix
	 */
	public Matrix(int rows, int cols){
		this(rows, cols, new double[cols * rows]);
	}
	
	/**
	 * constructor
	 * @param rows - rows of the matrix
	 * @param cols - columns of the matrix
	 * @param data - data
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
	 * copy current matrix to destination
	 * @param x - destination matrix
	 */
	public void copyTo(Matrix x){
		if (x.rows != rows || x.cols != cols)
			throw new IllegalArgumentException("Matrix copy, size not match.");
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				x.set(i, j, at(i, j));
	}
	
	/**
	 * get data stored in the matrix
	 * @return - data
	 */
	public double[] data(){
		return this.d;
	}
	
	/**
	 * get columns
	 * @return - columns
	 */
	public int columns(){
		return this.cols;
	}
	
	/**
	 * get rows
	 * @return - rows
	 */
	public int rows(){
		return this.rows;
	}
	
	/**
	 * row vector at row i of the matrix
	 * @param i - row index
	 * @return - row vector
	 */
	public Matrix row(int i){
		return at(new Range(i, i+1), Range.all());
	}
	
	/**
	 * column vector at column i of the matrix
	 * @param i - column index
	 * @return - column vector
	 */
	public Matrix col(int i){
		return at(Range.all(), new Range(i, i+1));
	}
	
	/**
	 * create an matrix like A=u*I, I is unit matrix, u is scale
	 * @param size - matrix size
	 * @param scale - the value to set
	 * @return - diagonal matrix
	 */
	public static Matrix unit(int size, double scale){
		Matrix x = new Matrix(size, size);
		for (int i = 0; i < size; i++)
			x.set(i, i, scale);
		return x;
	}
	
	/**
	 * create a unit matrix
	 * @param size - matrix size
	 * @return - unit matrix
	 */
	public static Matrix unit(int size){
		return unit(size, 1.);
	}
	
	/**
	 * create a matrix that all the element hold the same number
	 * @param rows - rows of the matrix
	 * @param cols - columns of the matrix
	 * @param scale - the scale want to set
	 * @return - a matrix that all the element hold the same number
	 */
	public static Matrix ones(int rows, int cols, double scale){
		Matrix x = new Matrix(rows, cols);
		for (int i = 0; i < x.rows; i++)
			for (int j = 0; j < x.cols; j++)
				x.set(i, j, scale);
		return x;
	}
	
	/**
	 * create a matrix that all the element is 1
	 * @param rows - rows of the matrix
	 * @param cols - columns of the matrix
	 * @return
	 */
	public static Matrix ones(int rows, int cols){
		return ones(rows, cols, 1.);
	}
	
	/**
	 * calculate the trace of the matrix
	 * @return - trace of the matrix
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
	 * get value at index i while the matrix is a vector (both row vector and column vector)
	 * @param i - index
	 * @return - value at the index i
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
	 * get the value of row i and column j
	 * @param i - row index
	 * @param j - column index
	 * @return - value of row i column j
	 */
	public double at(int i, int j){
		if (i < 0 || i >= rows || j < 0 || j >= cols)
			throw new IndexOutOfBoundsException("Matrix at, index out of range.");
		return d[(i + rowRange.begin()) * dCols + j + colRange.begin()];
	}
	
	/**
	 * get the sub-matrix determined by the row range and column range, and data will not be copied
	 * @param row - row range
	 * @param col - column range
	 * @return - a sub-matrix of the matrix
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
	 * set the value to the index i of the matrix while it is a vector (both row vector and column vector)
	 * @param i - index
	 * @param value - value to set
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
	 * set a value to row i column j
	 * @param i - row index
	 * @param j - column index
	 * @param value - value to set
	 */
	public void set(int i, int j, double value){
		if (i < 0 || i >= rows || j < 0 || j >= cols)
			throw new IndexOutOfBoundsException("Matrix set, index out of range.");
		d[(i + rowRange.begin()) * dCols + j + colRange.begin()] = value;
	}
	
	/**
	 * set value to the sub-matrix of the matrix
	 * @param row - row range
	 * @param col - column range
	 * @param value - matrix to set
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
	 * set value to row i of the matrix
	 * @param i - row index
	 * @param value - row vector to set
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
	 * set value to column i of the matrix
	 * @param i - column index
	 * @param value - column vector to set
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
	 * transpose the matrix
	 * @return - transpose of the matrix
	 */
	public Matrix t(){
		Matrix x = new Matrix(cols, rows);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j <cols; j++)
				x.set(j, i, at(i, j));
		return x;
	}
	
	/**
	 * matrix addition (z = x + y)
	 * @param x - matrix to add
	 * @return - result
	 */
	public Matrix add(Matrix x){
		return add(x,false);
	}
	
	/**
	 * matrix addition
	 * @param x - matrix to add
	 * @param reserve - indicate reserve the matrix whether or not
	 * @return - result
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
	 * matrix addition (z=x+y, x is a scale)
	 * @param x - number to add
	 * @param reserve - indicate reserve the matrix whether or not
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
	 * matrix addition (z=x+y, x is scale)
	 * @param x - number to add
	 * @return - result
	 */
	public Matrix add(Number x){
		return add(x,false);
	}
	
	/**
	 * add a value to row i column j
	 * @param i - row index
	 * @param j - column index
	 * @param value - value to add
	 */
	public void add(int i, int j, Number value){
		if (i < 0 || i >= rows || j < 0 || j >= cols)
			throw new IndexOutOfBoundsException("Matrix at, index out of range.");
		d[(i + rowRange.begin()) * dCols + j + colRange.begin()] += value.doubleValue();
	}
	
	/**
	 * value of row i column j subtract a number
	 * @param i - row index
	 * @param j - column index
	 * @param value - value to subtract
	 */
	public void subtract(int i, int j, Number value){
		if (i < 0 || i >= rows || j < 0 || j >= cols)
			throw new IndexOutOfBoundsException("Matrix at, index out of range.");
		d[(i + rowRange.begin()) * dCols + j + colRange.begin()] -= value.doubleValue();
	}
	
	/**
	 * matrix subtraction (z=x-y)
	 * @param x - matrix to subtract
	 * @return - result
	 */
	public Matrix subtract(Matrix x){
		return subtract(x,false);
	}
	
	/**
	 * matrix subtraction (z=x-y)
	 * @param x - matrix to subtract
	 * @param reserve - indicate reserve the matrix whether or not
	 * @return - result
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
	 * matrix subtraction (z=x-y, x is a scale)
	 * @param x - number to subtract
	 * @return - result
	 */
	public Matrix subtract(Number x){
		return subtract(x, false);
	}
	
	/**
	 * matrix subtraction (z=x-y, x is a scale)
	 * @param x - number to subtract
	 * @param reserve - indicate reserve the matrix whether or not
	 * @return - result
	 */
	public Matrix subtract(Number x, boolean reserve){
		Matrix y = reserve ? new Matrix(rows, cols): this;
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				y.set(i, j, at(i, j) - x.doubleValue());
		return y;
	}
	
	/**
	 * matrix multiplication (z=x*y)
	 * @param x - right side matrix, y
	 * @return - result
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
	 * @param x
	 * @param reserve - indicate reserve the matrix whether or not
	 * @return - result
	 */
	public Matrix multiply(Number x, boolean reserve){
		Matrix y = reserve ? new Matrix(rows, cols): this;
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				y.set(i, j, at(i, j) * x.doubleValue());
		return y;
	}
	
	/**
	 * matrix multiplication (z=x*y, x is scale)
	 * @param x
	 * @return - result
	 */
	public Matrix multiply(Number x){
		return multiply(x,false);
	}
	
	/**
	 * value at row i column j multiply a number
	 * @param i - row index
	 * @param j - column index
	 * @param x - number to multiply
	 */
	public void multiply(int i, int j, Number x){
		if (i < 0 || i >= rows || j < 0 || j >= cols)
			throw new IndexOutOfBoundsException("Matrix at, index out of range.");
		set(i, j, at(i, j) * x.doubleValue());
	}
	
	/**
	 * matrix value at row i column j divide a number
	 * @param i - row index
	 * @param j - column index
	 * @param x - number to divide
	 */
	public void divide(int i, int j, Number x){
		if (i < 0 || i >= rows || j < 0 || j >= cols)
			throw new IndexOutOfBoundsException("Matrix at, index out of range.");
		set(i, j, at(i, j)/x.doubleValue());
	}
	
	/**
	 * matrix divide a number
	 * @param x - number to divide
	 * @return - result
	 */
	public Matrix divide(Number x){
		return divide(x,false);
	}
	
	/**
	 * matrix divide a number
	 * @param x - number to divide
	 * @param reserve - indicate reserve the matrix whether or not
	 * @return - result
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
	 * calculate the mean vector of every row vector
	 * @return - mean vector
	 */
	public Matrix rowMean(){
		Matrix mean = new Matrix(1, cols);
		for (int i = 0; i < rows; i++)
			mean.add(row(i));
		mean.divide(rows);
		return mean;
	}
	
	/**
	 * calculate the mean vector of every column vector
	 * @return - mean vector
	 */
	public Matrix colMean(){
		Matrix mean = new Matrix(rows, 1);
		for (int i = 0; i < cols; i++)
			mean.add(col(i));
		mean.divide(cols);
		return mean;
	}
	
	/**
	 * calculate the square root of the matrix
	 * @param reserve - indicate reserve the matrix whether or not
	 * @return - result
	 */
	public Matrix sqrt(boolean reserve){
		Matrix x = reserve? new Matrix(rows, cols): this;
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				x.set(i, j, Math.sqrt(at(i, j)));
		return x;
	}
	
	/**
	 * calculate the square root of the matrix
	 * @return - result
	 */
	public Matrix sqrt(){
		return sqrt(false);
	}
	
	/**
	 * calculate the difference between this and matrix x
	 * @param x - input matrix
	 * @return - difference
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
	 * calculate the l2-norm between this and x
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
	
	/**
	 * transpose x
	 * @param x - input matrix
	 * @return - the transposition of x
	 */
	public static double[][] transpose(double[][] x){
		double[][] y = new double[x[0].length][x.length];
		for (int i = 0; i < y.length; i++){
			for (int j = 0; j < x.length; j++)
				y[i][j] = x[j][i];
		}
		return y;
	}
	
	/**
	 * copy the specified matrix
	 * @param x - source matrix
	 * @return - the copy of x
	 */
	public static double[][] copy(double[][] x){
		double[][] y = new double[x.length][x[0].length];
		for (int i = 0; i < x.length; i++)
			for (int j = 0; j < x[0].length; j++)
				y[i][j] = x[i][j];
		return y;
	}
	
	/**
	 * matrix addition (z = x+y)
	 * @param x - matrix x
	 * @param y - matrix y
	 * @param reserveX - indicate reserve x whether or not
	 * @return - result
	 */
	public static double[][] add(double[][] x, double[][] y, boolean reserveX){
		if (x.length != y.length || x[0].length != y[0].length)
			throw new IllegalArgumentException("Matrix add, size not match.");
		double[][] z;
		z = reserveX ?new double[x.length][x[0].length]: x;
		for (int i = 0; i < x.length; i++)
			for (int j = 0; j < x[0].length; j++)
				z[i][j] = x[i][j] + y[i][j];
		return z;
	}
	
	/**
	 * matrix subtract (z = x - y)
	 * @param x - matrix x
	 * @param y - matrix y
	 * @param reserveX - indicate reserve x whether or not
	 * @return - result
	 */
	public static double[][] subtract(double[][] x, double[][] y, boolean reserveX){
		if (x.length != y.length || x[0].length != y[0].length)
			throw new IllegalArgumentException("Matrix add, size not match.");
		double[][] z;
		z = reserveX ?new double[x.length][x[0].length]: x;
		for (int i = 0; i < x.length; i++)
			for (int j = 0; j < x[0].length; j++)
				z[i][j] = x[i][j] - y[i][j];
		return z;
	}
	
	/**
	 * matrix multiplication (z = x * y, y is scale)
	 * @param x - matrix x
	 * @param y - matrix y
	 * @param reserveX - indicate reserve x or not
	 * @return - result
	 */
	public static double[][] multiply(double[][] x, double y, boolean reserveX){
		double[][] z;
		z = reserveX ? new double[x.length][x[0].length]: x;
		for (int i = 0; i < x.length; i++)
			for (int j = 0; j < x[0].length; j++)
				z[i][j] = x[i][j] * y;
		return z;
	}
	
	/**
	 * compute the multiplication of x and y
	 * @param x - left matrix
	 * @param y - right matrix
	 * @return - multiplication of x and y
	 */
	public static double[][] multiply(double[][] x, double[][] y){
		if (x[0].length != y.length)
			throw new IllegalArgumentException("Matrix multiply, size not match.");
		
		int m = x.length;
		int s = y.length;
		int n = y[0].length;
		double[][] t = new double[x.length][y[0].length];
		for (int i = 0; i < m; i++){
			for (int j = 0; j < n; j++){
				for (int k = 0; k < s; k++)
					t[i][j] += x[i][k] * y[k][j];
			}
		}
		return t;
	}
	/***
	 * merger a equal cols Matrix after this Matrix
	 * @param otherMX other Matrix 
	 */
	public void mergeAfterRow(Matrix otherMX){
		if(otherMX.cols!=this.cols)
			throw new IllegalArgumentException("Matrix merge, size not match.");
		int newRows=this.rows+otherMX.rows;
		double[] newData=new double[this.cols*newRows];
		//copy old d[]
		for(int i=0;i<this.rows();i++){
			for(int j=0;j<this.cols;j++){
				newData[i*this.cols+j]=this.d[i*this.cols+j];
			}
		}
		// merge the other Matrix
		for(int i=0;i<otherMX.rows;i++){
			for(int j=0;j<this.cols;j++){
				newData[(i+this.rows)*this.cols+j]=otherMX.d[i*this.cols+j];
			}
		}
		this.d=newData;
		this.rows=newRows;
	}
	/***
	 * 从当前的矩阵得到资矩阵
	 * @param startRow 从第几行开始
	 * @param countRows 获得几行
	 * @return
	 */
	public Matrix getSubMatrix(int rowIndex,int countRows){
		if(!(rowIndex>=0 && countRows>0 && rowIndex<this.rows && (rowIndex+countRows-1)<this.rows))
			throw new IndexOutOfBoundsException("Matrix getSubMatrix, index over flow.");
		Matrix newMatrix=new Matrix(countRows,this.cols);
		for(int i=0;i<countRows;i++){
			for(int j=0;j<this.cols;j++){
				newMatrix.set(i, j, this.at(rowIndex+i,j));
			}
		}
		return newMatrix;	
	}
}

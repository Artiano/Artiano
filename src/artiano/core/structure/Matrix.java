/**
 * Matrix.java
 */
package artiano.core.structure;

import java.io.Serializable;


/**
 * <p>基础数据类，表示矩阵。向量、矩阵都由此类表示。</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-20
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class Matrix implements Serializable{
	
	private static final long serialVersionUID = 1L;
	/**
	 * 矩阵的列数
	 */
	protected int cols = 0;
	/**
	 * 矩阵的行数
	 */
	protected int rows = 0;
	/**
	 * 保存矩阵的数据
	 */
	protected double[] d = null;
	/**
	 * 数据列数，最原始矩阵的列数
	 */
	protected int dCols = 0;
	/**
	 * 相对于最原始矩阵（由构造方法得到）的行范围，最初这个字段应该等于Range.all()，当rowRange被声明，则这个矩阵的行开始
	 * 下标相对于最初的矩阵的行为rowRange.begin()，矩阵的行结束下标相对于最初的矩阵行为rowRange.end()。举个列子：
	 * 当<code>rowRange.start==2</code>且<code>rowRange.end==5</code>时，那么这个矩阵相对于最原始矩阵的行的开始
	 * 和结束下标为2和5。这个字段一般由：
	 * @see #at(Range, Range)
	 * @see #set(Range, Range, Matrix)
	 * 使用
	 */
	protected Range rowRange = null;
	/**
	 * 相对于最原始矩阵的列范围，解释同
	 * @see #rowRange
	 */
	protected Range colRange = null;
	
	private Matrix(){ }
	
	/**
	 * 使用声明的行数rows和列数cols构造一个矩阵，此时内存已经被分配
	 * @param rows 矩阵行数。
	 * @param cols 矩阵列数
	 */
	public Matrix(int rows, int cols){
		this(rows, cols, new double[cols * rows]);
	}
	
	/**
	 * 使用声明的行数和列数以及预先分配的双精度浮点型数据构造一个矩阵。
	 * <br><b><i>NOTICE:</i></b> 如果矩阵的大小（等于行数*列数）小于预先存储的数据的长度，程序能够正常的运行，但是并不
	 * 推荐这样做。
	 * @see #Matrix(int, int)
	 * @param rows 矩阵行数。
	 * @param cols 矩阵列数。
	 * @param data 预先存储的数据。
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
	 * 判断一个矩阵是否为向量（列向量或行向量）
	 * @return
	 */
	public boolean isVector(){
		return (rows!= 1&&cols!=1);
	}
	
	/**
	 * 判断矩阵是否为行向量
	 * @return
	 */
	public boolean isRowVector(){
		return rows==1;
	}
	
	/**
	 * 判断矩阵是否为列向量
	 * @return
	 */
	public boolean isColumnVector(){
		return cols==1;
	}
	
	/**
	 * 判断两个矩阵是否是同型矩阵（即行列相等）
	 * @param x
	 * @return 同型返回<code>true</code>，反之则反
	 */
	public boolean sameType(Matrix x){
		return (rows==x.rows&&cols==x.cols);
	}
	
	/**
	 * 判断是否为方阵。
	 * @return
	 */
	public boolean isSquare(){
		return rows==cols;
	}
	
	/**
	 * 得到矩阵大小（size=rows*columns）
	 * @return
	 */
	public int size(){
		return rows*cols;
	}
	
	/**
	 * 计算矩阵的绝对值
	 * @param reserve 指示是否保留原始矩阵，若<code>reserve==false</code>，方法将使用绝对值矩阵替换原始矩阵。
	 * @return 矩阵的绝对值矩阵
	 * @see #abs()
	 */
	public Matrix abs(boolean reserve){
		Matrix x = reserve?new Matrix(rows,cols):this;
		for (int i=0; i<rows; i++)
			for (int j=0; j<cols; j++){
				double d = at(i, j);
				x.set(i, j, d>=0.?d:-d);
			}
		return x;
	}
	
	/**
	 * 计算矩阵的绝对值。
	 * <br><br><b><i>NOTICE:</i></b>方法将替换原始矩阵。
	 * @return 矩阵的绝对值矩阵。
	 * @see #abs(boolean)
	 */
	public Matrix abs(){
		return abs(false);
	}
	
	/**
	 * 将矩阵清零。
	 */
	public void clear(){
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				set(i, j, 0);
	}
	
	/**
	 * 将矩阵拷贝到另外一个矩阵，此时，这两个矩阵必须是同型的，数据将完全拷贝到新的矩阵，但是新的矩阵将和当前的矩阵
	 * 是完全独立的。
	 * @param x 目标矩阵。
	 */
	public void copyTo(Matrix x){
		if (x.rows != rows || x.cols != cols)
			throw new IllegalArgumentException("Matrix copy, size not match.");
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				x.set(i, j, at(i, j));
	}
	
	/**
	 * 获取存储于矩阵中的数据。
	 * @return 数据
	 */
	public double[] data(){
		return this.d;
	}
	
	/**
	 * 获取矩阵的列数。
	 * @return 矩阵的列数。
	 */
	public int columns(){
		return this.cols;
	}
	
	/**
	 * 获取矩阵的行数。
	 * @return 矩阵行数。
	 */
	public int rows(){
		return this.rows;
	}
	
	/**
	 * 获取矩阵在行下标为i处的行向量。
	 * @param i 行下标。
	 * @return 特定的行向量。
	 */
	public Matrix row(int i){
		return at(new Range(i, i+1), Range.all());
	}
	
	/**
	 * 获取矩阵在列下标为i处的列向量。
	 * @param i 列下标。
	 * @return 特定的列向量。
	 */
	public Matrix col(int i){
		return at(Range.all(), new Range(i, i+1));
	}
	
	/**
	 * 构造一个从以pitch为间隔从begin到end递增的向量。
	 * @param begin 向量开始数据
	 * @param pitch 间隔
	 * @param end 向量结束
	 * @return
	 */
	public static Matrix increment(double begin, double pitch, double end){
		int r = (int)((end-begin)/pitch);
		if (r<=0)
			throw new IllegalArgumentException("Matrix increment, end must greater than begin.");
		Matrix x = new Matrix(r, 1);
		double pre = begin;
		for (int i=0; i<x.rows; i++){
			x.set(i, pre);
			pre += pitch;
		}
		return x;
	}
	
	/**
	 * 构造一个矩阵形如 A=u*I, I 是单位向量, u 是一个标量。
	 * @param size 矩阵的边长。 (size=rows=columns)
	 * @param scale 缩放比列。
	 * @return A 
	 * @see #unit(int)
	 */
	public static Matrix unit(int size, double scale){
		Matrix x = new Matrix(size, size);
		for (int i = 0; i < size; i++)
			x.set(i, i, scale);
		return x;
	}
	
	/**
	 * 构造一个单位矩阵。
	 * @param size 矩阵边长。(size=rows=columns)
	 * @return 单位矩阵。
	 * @see #unit(int, double)
	 */
	public static Matrix unit(int size){
		return unit(size, 1.);
	}
	
	/**
	 * 构造一个所有元素具有相同数据的矩阵，形如：A=N*scale，N为1矩阵，scale为缩放比例。
	 * @param rows 矩阵行数。
	 * @param cols 矩阵列数。
	 * @param scale 缩放比例。
	 * @return A 
	 * @see #ones(int, int)
	 */
	public static Matrix ones(int rows, int cols, double scale){
		Matrix x = new Matrix(rows, cols);
		for (int i = 0; i < x.rows; i++)
			for (int j = 0; j < x.cols; j++)
				x.set(i, j, scale);
		return x;
	}
	
	/**
	 * 构造一个所有元素为1的矩阵。
	 * @param rows 矩阵行数。
	 * @param cols 矩阵列数。
	 * @return A
	 * @see #ones(int, int, double)
	 */
	public static Matrix ones(int rows, int cols){
		return ones(rows, cols, 1.);
	}
	
	/**
	 * 计算矩阵的迹。
	 * @return 矩阵的迹。
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
	 * 获取矩阵在下标i处的值（行向量或列向量均可）。
	 * @param i 下标。
	 * @return 值。
	 * @see #at(int, int)
	 * @see #at(Range, Range)
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
	 * 获取矩阵在行下标为i列下标为j处的值。
	 * @param i 行下标。
	 * @param j 列下标。
	 * @return 值。
	 */
	public double at(int i, int j){
		if (i < 0 || i >= rows || j < 0 || j >= cols)
			throw new IndexOutOfBoundsException("Matrix at, index out of range.");
		return d[(i + rowRange.begin()) * dCols + j + colRange.begin()];
	}
	
	/**
	 * 获取由行范围和列范围决定的子矩阵。
	 * <br><b><i>NOTICE:</i></b> 由这种方式获取的子矩阵将不会被拷贝，如果你想获得矩阵的子矩阵，并且需要修改其中的值，但是
	 * 又不希望原始矩阵中的值被改变，你应该编写像如下代码：
	 * <code><br>Matrix y=new Matrix(2,2); //create a new matrix with 2 rows and 2 columns
	 * <br>x.at(new Range(1,3), new Range(2,4).copyTo(y); //copy the sub-matrix of x to y</code>
	 * @param row  Row range
	 * @param col Column range
	 * @return A sub-matrix of the matrix
	 * 
	 * @see #copyTo(Matrix)
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
	 * 设置<i>向量</i>在下标i处的值（行向量或列向量。
	 * @param i 下标。
	 * @param value 需要设置的值。
	 * 
	 * @see #set(int, int, double)
	 * @see #set(Range, Range, Matrix)
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
	 * 设置矩阵在行下标i以及列下标j出的值。
	 * @param i 行下标。
	 * @param j 列下标。
	 * @param value 需要设置的值。
	 * 
	 * @see #set(int, double)
	 * @see #set(Range, Range, Matrix)
	 */
	public void set(int i, int j, double value){
		if (i < 0 || i >= rows || j < 0 || j >= cols)
			throw new IndexOutOfBoundsException("Matrix set, index out of range."+" i:"+i+"    j:"+j);
		d[(i + rowRange.begin()) * dCols + j + colRange.begin()] = value;
	}
	
	/**
	 * 设置有行范围和列范围决定的子矩阵的值。
	 * <b><i>NOICE:</i><b>只是将声明的矩阵值拷贝到子矩阵。
	 * @param row 行范围。
	 * @param col 列范围。
	 * @param value 需要设置的矩阵的值。
	 * 
	 * @see #set(int, double)
	 * @see #set(int, int, double)
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
	 * 设置矩阵在行i处的值。
	 * @param i 行下标。
	 * @param value 要设置的值。
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
	 * 设置矩阵在列i处的值。
	 * @param i 列下标。
	 * @param value 要设置的值。
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
	 * 转置当前矩阵。
	 * @return 矩阵的转置。
	 */
	public Matrix t(){
		Matrix x = new Matrix(cols, rows);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j <cols; j++)
				x.set(j, i, at(i, j));
		return x;
	}
	
	/**
	 * 矩阵加法 (z = x + y)
	 * <br><b><i>NOTICE:</i></b> 这个方法将用相加后的结果替换原始矩阵。
	 * @param x 
	 * @return 结果。
	 * 
	 * @see #add(Number)
	 * @see #add(Matrix, boolean)
	 * @see #add(Number, boolean)
	 * @see #add(int, int, Number)
	 */
	public Matrix add(Matrix x){
		return add(x,false);
	}
	
	/**
	 * 矩阵加法 (z = x + y)
	 * @param x 
	 * @param reserve 指示是否保留原始矩阵。如果<code>reserve==true</code>，方法同
	 * @see #add(Matrix)
	 * 否则，程序将保留原始矩阵。
	 * @return 结果
	 * 
	 * @see #add(Matrix)
	 * @see #add(Number)
	 * @see #add(Number, boolean)
	 * @see #add(int, int, Number)
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
	 * 矩阵加法 (z=x+y, x 为标量)
	 * @param x 
	 * @param reserve 指示是否保留原始矩阵。如果<code>reserve==true</code>，方法同
	 * @see #add(Number)
	 * 否则，程序将保留原始矩阵。
	 * @return 结果
	 * 
	 * @see #add(Matrix)
	 * @see #add(Number)
	 * @see #add(Matrix, boolean)
	 * @see #add(int, int, Number)
	 */
	public Matrix add(Number x, boolean reserve){
		Matrix y = reserve ? new Matrix(rows, cols): this;
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				y.set(i, j, at(i, j) + x.doubleValue());
		return y;
	}
	
	/**
	 * 矩阵加法 (z=x+y, x 为标量)
	 *  <br><b><i>NOTICE:</i></b> 这个方法将用相加后的结果替换原始矩阵。
	 * @param x 
	 * @return 结果。
	 * 
	 * @see #add(Matrix)
	 * @see #add(Matrix, boolean)
	 * @see #add(Number, boolean)
	 * @see #add(int, int, Number)
	 */
	public Matrix add(Number x){
		return add(x,false);
	}
	
	/**
	 * 在矩阵行i列j处加上一个数。
	 * @param i 行下标。
	 * @param j 列下标。
	 * @param value 需要加的数。
	 */
	public void add(int i, int j, Number value){
		if (i < 0 || i >= rows || j < 0 || j >= cols)
			throw new IndexOutOfBoundsException("Matrix at, index out of range.");
		d[(i + rowRange.begin()) * dCols + j + colRange.begin()] += value.doubleValue();
	}
	
	/**
	 * 在矩阵行i列j处减去一个数。
	 * @param i 行下标。
	 * @param j 列下标。
	 * @param value 需要减的数。
	 */
	public void subtract(int i, int j, Number value){
		if (i < 0 || i >= rows || j < 0 || j >= cols)
			throw new IndexOutOfBoundsException("Matrix at, index out of range.");
		d[(i + rowRange.begin()) * dCols + j + colRange.begin()] -= value.doubleValue();
	}
	
	/**
	 * 矩阵减法 (z=x-y)
	 * <br><b><i>NOTICE:</i></b> 方法将用结果替换原始矩阵。如果想保留原始矩阵，使用如下代码：
	 * <pre><code>
	 * Matrix z=x.subtract(y);
	 * </code>
	 * </pre>
	 * @param x 
	 * @return 结果
	 * 
	 * @see #subtract(Number)
	 * @see #subtract(Matrix, boolean)
	 * @see #subtract(int, int, Number)
	 * @see #subtract(Number, boolean)
	 */
	public Matrix subtract(Matrix x){
		return subtract(x,false);
	}
	
	/**
	 * 矩阵减法 (z=x-y)
	 * @param x 
	 * @param reserve 指示是否保留原始矩阵。如果参数 <code>reserve==true</code>，方法将保留原始矩阵，并返回计算
	 * 结果。否则，方法同
	 * @see #subtract(Matrix)
	 * @return 结果
	 * 
	 * @see #subtract(Number)
	 * @see #subtract(Matrix)
	 * @see #subtract(int, int, Number)
	 * @see #subtract(Number, boolean)
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
	 * 矩阵减法 (z=x-y, y 为标量)
	 * <br><b><i>NOTICE:</i></b> 方法将用结果替换原始矩阵。如果想保留原始矩阵，使用如下代码：
	 * <pre><code>
	 * Matrix z=x.subtract(y);
	 * </code>
	 * </pre>
	 * @param x 
	 * @return 结果
	 * 
	 * @see #subtract(Matrix)
	 * @see #subtract(Matrix, boolean)
	 * @see #subtract(int, int, Number)
	 * @see #subtract(Number, boolean)
	 */
	public Matrix subtract(Number x){
		return subtract(x, false);
	}
	
	/**
	 * 矩阵减法 (z=x-y, x is a scale)
	 * @param x
	 * @param reserve 指示是否保留原始矩阵， 如果参数 <code>reserve==true</code>，方法将保留原始矩阵。
	 * 否则，方法同:
	 * @see #subtract(Number)
	 * @return 结果
	 * @see #subtract(Matrix)
	 * @see #subtract(Matrix, boolean)
	 * @see #subtract(int, int, Number)
	 * @see #subtract(Number)
	 */
	public Matrix subtract(Number x, boolean reserve){
		Matrix y = reserve ? new Matrix(rows, cols): this;
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				y.set(i, j, at(i, j) - x.doubleValue());
		return y;
	}
	
	/**
	 * 矩阵乘法 (z=x*y)
	 * @param x
	 * @return 结果
	 * @see #multiply(Number)
	 * @see #multiply(Number, boolean)
	 * @see #multiply(int, int, Number)
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
	 * 矩阵数乘
	 * @param x
	 * @param reserve 指示是否保留原始矩阵， 如果参数 <code>reserve==true</code>，方法将保留原始矩阵。
	 * 否则，方法同:
	 * @see #multiply(Number)
	 * @return 结果
	 * @see #multiply(Matrix)
	 * @see #multiply(Number)
	 * @see #multiply(int, int, Number)
	 */
	public Matrix multiply(Number x, boolean reserve){
		Matrix y = reserve ? new Matrix(rows, cols): this;
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				y.set(i, j, at(i, j) * x.doubleValue());
		return y;
	}
	
	/**
	 * 矩阵数乘
	 * <br><b><i>NOTICE:</i></b> 方法将用结果替换原始矩阵。
	 * @param x 
	 * @param x
	 * @return 结果
	 * @see #multiply(Matrix)
	 * @see #multiply(Number)
	 * @see #multiply(int, int, Number)
	 */
	public Matrix multiply(Number x){
		return multiply(x,false);
	}
	
	/**
	 * 在行i列j处乘以一个数。
	 * @param i 行下标。
	 * @param j 列下标。
	 * @param x
	 * @see #multiply(Matrix)
	 * @see #multiply(Number)
	 * @see #multiply(Number, boolean)
	 */
	public void multiply(int i, int j, Number x){
		if (i < 0 || i >= rows || j < 0 || j >= cols)
			throw new IndexOutOfBoundsException("Matrix at, index out of range.");
		set(i, j, at(i, j) * x.doubleValue());
	}
	
	/**
	 * 在矩阵行i列j处除去一个数。
	 * @param i 行下标。
	 * @param j 列下标。
	 * @param x 
	 * @see #divide(Number)
	 * @see #divide(Number, boolean)
	 */
	public void divide(int i, int j, Number x){
		if (i < 0 || i >= rows || j < 0 || j >= cols)
			throw new IndexOutOfBoundsException("Matrix at, index out of range.");
		set(i, j, at(i, j)/x.doubleValue());
	}
	
	/**
	 * 矩阵除法 (z=x/y, y 为标量)
	 * <br><b><i>NOTICE:</i></b> 方法将用结果替换原始矩阵。
	 * @param x 
	 * @return 结果。
	 * @see #divide(Number, boolean)
	 * @see #divide(int, int, Number)
	 */
	public Matrix divide(Number x){
		return divide(x,false);
	}
	
	/**
	 * 矩阵除法(z=x/y, y 为标量)
	 * @param x 
	 * @param reserve 指示是否保留原始矩阵，如果参数 <code>reserve==true</code>，方法将保留原始矩阵，否则，方法同：
	 * @see #divide(Number)
	 * @return 结果
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
	 * 计算矩阵的行向量集合的均值向量。
	 * @return 均值向量。
	 * @see #colMean()
	 */
	public Matrix rowMean(){
		Matrix mean = new Matrix(1, cols);
		for (int i = 0; i < rows; i++)
			mean.add(row(i));
		mean.divide(rows);
		return mean;
	}
	
	/**
	 * 计算矩阵的列向量集合的均值向量。
	 * @return 均值向量。
	 * @see #rowMean()
	 */
	public Matrix colMean(){
		Matrix mean = new Matrix(rows, 1);
		for (int i = 0; i < cols; i++)
			mean.add(col(i));
		mean.divide(cols);
		return mean;
	}
	
	/**
	 * 对矩阵开根。
	 * @param reserve 指示是否保留原始矩阵，如果参数 <code>reserve==true</code>，方法将保留原始矩阵，否则，方法同：
	 * {@link #sqrt()}
	 * @return 结果。
	 * @see #sqrt()
	 */
	public Matrix sqrt(boolean reserve){
		Matrix x = reserve? new Matrix(rows, cols): this;
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				x.set(i, j, Math.sqrt(at(i, j)));
		return x;
	}
	
	/**
	 * 对矩阵开根。
	 * <br><b><i>NOTICE:</i></b> 方法将使用计算结果替换原始矩阵。
	 * @return 结果。
	 * @see #sqrt(boolean)
	 */
	public Matrix sqrt(){
		return sqrt(false);
	}
	
	/**
	 * 计算两个矩阵之间的差别。
	 * <p>给定两个矩阵x、y，它们之间的差别计算为：
	 * <br><i>差别<code>=sum[abs(x(i,j)-y(i,j)]</code></i></p>
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
	 * 计算两个矩阵的l2范数。
	 * <p>给定两个矩阵x、y，l2范数的计算方法为：
	 * <br><i><code>l2-norm=sqrt{sum[x(i, j) - y(i, j)]}</code></i></p>
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
	 * 克隆一个矩阵。
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
	 * 检查两个矩阵是否相等。
	 * @param anotherMat
	 * @return
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
	 * 辅助方法，将矩阵打印到控制台
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
	 * 在行后合并一个矩阵。
	 * <p>给定两个矩阵x、y： 
	 * <br>x=
	 * <br>|1 2 3|  
	 * <br>|4 5 6|
	 * <br>|7 8 9| ,
	 * <br>y=
	 * <br>|1 1 2|
	 * <br>|2 2 1|
	 * <br>则经过合并后：
	 * <br>x=
	 * <br>|1 2 3|  
	 * <br>|4 5 6|
	 * <br>|7 8 9|
	 * <br>|1 1 2|
	 * <br>|2 2 1|
	 * </p>
	 * @param otherMx 
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

/**
 * Range.java
 */
package artiano.core.structure;

import java.io.Serializable;

/**
 * <p>Description: To describe an ultra-tail range. It is very useful while some structure has a range in 
 * most case. For example: when you want to get a sub-matrix from a matrix, you can write code like:
 * <code><br>Matrix x = y.at(new Range(1,3), Range.all());</br></code>
 * That means x has the 1st row to 3rd row (but not including the 3rd row) and all columns of the matrix y.</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-23
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class Range implements Serializable{
	
	private static final long serialVersionUID = 1L;
	//range start
	protected int start = 0;
	//range end
	protected int end = 0;
	
	/**
	 * private constructor
	 */
	private Range(){ 
		start = 0;
		end = 0;
	}
	
	/**
	 * constructor
	 * @param start - range start
	 * @param end - range end
	 */
	public Range(int start, int end){
		if (start >= end)
			throw new IllegalArgumentException("Range, range end must greater than range begin.");
		this.start = start;
		this.end = end;
	}
	
	/**
	 * get the range length
	 * @return - range length
	 */
	public int length(){
		return end - start;
	}
	
	/**
	 * range all
	 * @return - a special range express the whole range
	 */
	public static Range all(){
		return new Range();
	}
	
	/**
	 * range begin
	 * @return - the start of the range
	 */
	public int begin(){
		return start;
	}
	
	/**
	 * range end
	 * @return - end of the range
	 */
	public int end(){
		return end;
	}
	
	/**
	 * judge if x is in the range
	 * @param x - an integer
	 * @return - true if x is in the range or false otherwise
	 */
	public boolean isContain(int x){
		return (x >= start && x <= end);
	}
	
	/**
	 * judge if another range is in the range
	 * @param x - another range
	 * @return - true if x is in the range or false otherwise
	 */
	public boolean isContain(Range x){
		return (x.start >= start && x.end <= end);
	}
	
	/**
	 * judge if another range is equals to the range
	 * @param x - another range
	 * @return - true if x is equals to the range or false otherwise
	 */
	public boolean equals(Range x){
		return (x.start == start && x.end == end);
	}
}

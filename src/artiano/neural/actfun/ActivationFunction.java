
package artiano.neural.actfun;

import java.io.Serializable;

/**
 * <p>Description: interface of every activation function</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-13
 * @author (latest modification by Nano.Michael)
 * @function offer an common interface for every activation function
 * @since 1.0
 */
public abstract class ActivationFunction implements Serializable {
	
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 * @param x the input value
	 * @return the function value of x
	 */
	public abstract double calculate(double x);
	/**
	 * 
	 * @param x the input value
	 * @return the function's derivative value of x
	 */
	public abstract double derivativeByX(double x);
	/**
	 * 
	 * @param y the function value calculated by the method calculate
	 * @return the function's derivative value of function value y
	 */
	public abstract double derivativeByY(double y);
}

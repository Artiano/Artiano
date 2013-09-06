/**
 * 
 */
package artiano.neural.actfun;

/**
 * <p>Description: sigmoid function</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-13
 * @author (latest modification by Nano.Michael)
 * @function 
 * @since 1.0
 */
public class Sigmoid extends ActivationFunction {

	private static final long serialVersionUID = 1L;
	/**
	 * parameter alpha
	 */
	protected double alpha = 2.0;
	
	public Sigmoid(){ }
	/**
	 * 
	 * @param alpha parameter of sigmoid function
	 */
	public Sigmoid(double alpha){
		this.alpha = alpha;
	}
	
	/* (non-Javadoc)
	 * @see artiano.actfun.ActivationFunction#calculate(double)
	 */
	@Override
	public double calculate(double x) {
		return (1./ (1. + Math.exp(-alpha * x)));
	}

	/* (non-Javadoc)
	 * @see artiano.actfun.ActivationFunction#derivativeByX(double)
	 */
	@Override
	public double derivativeByX(double x) {
		double y = calculate(x);
		return derivativeByY(y);
	}

	/* (non-Javadoc)
	 * @see artiano.actfun.ActivationFunction#derivativeByY(double)
	 */
	@Override
	public double derivativeByY(double y) {
		return alpha * y * (1 - y);
	}

}

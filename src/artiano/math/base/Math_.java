/**
 * Math_.java
 */
package artiano.math.base;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-9-7
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class Math_ {
	/**
	 * sign function of a with b
	 * @param a
	 * @param b
	 * @return sign function value
	 */
	public static double sign(final double a, final double b){
		return b >= 0 ? (a >= 0 ? a: -a): (a >= 0 ? -a: a);
	}
	
	/**
	 * compute (a^2 + b^2)^0.5 = abs(a)*(1 + (b/a)^2)^0.5, in this form, will not destroy overflow or underflow
	 * @param a
	 * @param b
	 * @return - the function value
	 */
	public static double pythag(final double a, final double b){
		double absa, absb;
		absa = java.lang.Math.abs(a);
		absb = java.lang.Math.abs(b);
		if (absa > absb) return absa*java.lang.Math.sqrt(1.+(absb/absa)*(absb/absa));
		else return (absb==0.?0.:absb*java.lang.Math.sqrt(1.+(absa/absb)*(absa/absb)));
	}
	
	/**
	 * compute the square of a
	 * @param a - input
	 * @return - result
	 */
	public static double square(final double a){
		return a*a;
	}
}

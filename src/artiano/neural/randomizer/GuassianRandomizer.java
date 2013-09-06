/**
 * GuassianRandomizer.java
 */
package artiano.neural.randomizer;

import java.util.Random;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-14
 * @author (latest modification by Nano.Michael)
 * @function 
 * @since 1.0
 */
public class GuassianRandomizer implements Randomizer {

	private Random ram = new Random();
	private double mean = 0., stdv = 1.;
	public GuassianRandomizer(double mean, double stdv){
		this.mean = mean;
		this.stdv = stdv;
	}
	
	/* (non-Javadoc)
	 * @see artiano.randomizer.Randomizer#next()
	 */
	@Override
	public double next() {
		double random = ram.nextGaussian();
		random = random * stdv * stdv + mean;
		return random;
	}

	/* (non-Javadoc)
	 * @see artiano.randomizer.Randomizer#writeTo(double[])
	 */
	@Override
	public void writeTo(double[] d) {
		for (int i = 0; i < d.length; i++)
			d[i] = next();
	}

}

/**
 * PCAExtractor.java
 */
package artiano.statistics.extractor;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-22
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class PCAExtractor implements FeatureExtractor{
	//samples
	protected double[][] samples = null;
	protected double[] mean = null;
	//eigen
	protected double[][] eigenVectors = null;
	protected double[]   eigenValues = null;
	
	protected int samplesNumber = 0;
	protected int sampleLength = 0;
	
	public PCAExtractor(double[][] samples){
		this.samples = samples;
		this.samplesNumber = samples.length;
		this.sampleLength = samples[0].length;
		mean = new double[samples[0].length];
	}
	
	protected void computeMean(){
		
	}
	
	/* (non-Javadoc)
	 * @see artiano.statistics.extractor.FeatureExtractor#load(java.lang.String)
	 */
	@Override
	public void load(String filename) {
		
	}

	/* (non-Javadoc)
	 * @see artiano.statistics.extractor.FeatureExtractor#save(java.lang.String)
	 */
	@Override
	public void save(String filename) {
		
	}

	/* (non-Javadoc)
	 * @see artiano.statistics.extractor.FeatureExtractor#train()
	 */
	@Override
	public void train() {
		
	}

	/* (non-Javadoc)
	 * @see artiano.statistics.extractor.FeatureExtractor#extract(double[])
	 */
	@Override
	public void extract(double[] sample) {
		
	}

	/* (non-Javadoc)
	 * @see artiano.statistics.extractor.FeatureExtractor#getModel()
	 */
	@Override
	public double[][] getModel() {
		return null;
	}

}

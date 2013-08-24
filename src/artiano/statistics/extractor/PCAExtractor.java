/**
 * PCAExtractor.java
 */
package artiano.statistics.extractor;

import artiano.core.Matrix;

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
	protected Matrix[] samples = null;
	protected Matrix mean = null;
	//eigen
	protected Matrix eigenVectors = null;
	protected Matrix eigenValues = null;
	
	protected int samplesNumber = 0;
	
	public PCAExtractor(Matrix[] samples){
		this.samples = samples;
		this.samplesNumber = samples.length;
		mean = new Matrix(samples[0].rows(), samples[0].columns());
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
	public Matrix getModel() {
		return null;
	}

}

/**
 * PCAExtractor.java
 */
package artiano.statistics.extractor;

import artiano.core.structure.Matrix;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-22
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class PCAExtractor implements FeatureExtractor, CanReconstructed{
	//samples
	protected Matrix[] samples = null;
	protected Matrix mean = null;
	//eigen
	protected Matrix eigenVectors = null;
	protected Matrix eigenValues = null;
	protected int eigens;
	protected int samplesNumber = 0;
	protected int sampleLength = 0;
	
	public PCAExtractor(Matrix[] samples){
		if (samples[0].rows() != 1)
			throw new IllegalArgumentException("PCAExtractor, accept row vectors only.");
		this.samples = samples;
		this.samplesNumber = samples.length;
		this.sampleLength = samples[0].columns();
		mean = new Matrix(samples[0].rows(), samples[0].columns());
		eigens = Math.min(samplesNumber, sampleLength);
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

	/* (non-Javadoc)
	 * @see artiano.statistics.extractor.CanReconstructed#reconstruct(artiano.core.structure.Matrix)
	 */
	@Override
	public Matrix reconstruct(Matrix feature) {
		// TODO Auto-generated method stub
		return null;
	}

}

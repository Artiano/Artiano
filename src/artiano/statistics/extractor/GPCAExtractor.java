/**
 * PCAExtractor.java
 */
package artiano.statistics.extractor;

import artiano.core.operation.MatrixOpt;
import artiano.core.structure.Matrix;
import artiano.core.structure.Range;
import artiano.math.linearalgebra.SingularValueDecomposition;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-22
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class GPCAExtractor extends FeatureExtractor implements UnsupervisedExtractor, CanReconstructed{
	
	//the mean matrix
	protected Matrix mean = null;
	//eigen
	protected Matrix eigenVectors = null;
	protected Matrix eigenValues = null;
	protected int eigens;
	protected int samplesNumber = 0;
	//sample size
	protected int sampleWidth = 0;
	protected int sampleHeight = 0;
	
	protected boolean covarianceByRow = false;
	protected boolean isVectors = false;
	
	/**
	 * constructor
	 */
	public GPCAExtractor(){ }
	
	/**
	 * compute the covariance of the samples
	 * @param samples
	 * @return - covariance matrix
	 */
	protected Matrix computeCovariance(Matrix[] samples){
		Matrix cov = null;
		mean = MatrixOpt.computeMean(samples);
		if (isVectors){
			if (samples[0].columns() > samples.length){
				eigens = samples.length;
				covarianceByRow = true;
				cov = MatrixOpt.computeCovarianceByRow(samples, mean, 1.);
			} else {
				eigens = samples[0].columns();
				covarianceByRow = false;
				cov = MatrixOpt.computeCovarianceByCol(samples, mean, 1.);
			}
		}else
			cov = MatrixOpt.compute2DCovariance(samples, mean, 1.);
		return cov;
	}
	
	/**
	 * compute the eigen-vectors
	 * @param samples
	 */
	protected void computeEigens(Matrix[] samples){
		Matrix cov = computeCovariance(samples);
		//the covariance calculated by row, fast PCA algorithm
		if (covarianceByRow){
			SingularValueDecomposition svd = new SingularValueDecomposition(cov, false);
			svd.sort();
			Matrix w = svd.W().sqrt().clone();
			int zeroIdx = 0;
			//w := w^(-1/2)
			final double TINY = 1e-7;
			for (int i = 0; i < w.columns(); i++){
				if (w.at(0, i) < TINY){
					zeroIdx = i;
					break;
				}
				w.set(0, i, 1./w.at(0, i));
			}
			//wipe out the null space
			if (zeroIdx == 0)
				zeroIdx = w.columns();
			w = w.at(Range.all(), new Range(0, zeroIdx));
			//eigen values
			eigenValues = svd.W().at(Range.all(), new Range(0, zeroIdx)).clone();
			Matrix v = svd.U().at(Range.all(), new Range(0, zeroIdx));
			/**
			 * u=A*[v*w^(-1/2)]
			 * calculate v := v*w^(-1/2) first to speed up computing
			 */
			for (int i = 0; i < v.rows(); i++)
				for (int j = 0; j < v.columns(); j++)
					v.set(i, j, v.at(i, j) * w.at(0, j));
			/**
			 * eigen-vectors = A*v
			 */
			Matrix t_a = new Matrix(1, samplesNumber);
			eigenVectors = new Matrix(sampleWidth, w.columns());
			for (int i = 0; i < samples[0].columns(); i++)
			{
				/**
				 * t_a: temporary row-vector
				 * that t_a = column i of A
				 */
				for (int j = 0; j < samplesNumber; j++)
					t_a.set(0, j, samples[j].at(0, i) - mean.at(0, i));
				eigenVectors.setRow(i, t_a.multiply(v));
			}
		} else {
			SingularValueDecomposition svd = new SingularValueDecomposition(cov, false);
			svd.sort();
			eigenVectors = svd.U();
			eigenValues = svd.W().sqrt();
		}
		//transpose the eigen vectors to row vector
		eigenVectors = eigenVectors.t();
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
	 * @see artiano.statistics.extractor.UnsupervisedExtractor#train(artiano.core.structure.Matrix[])
	 */
	@Override
	public void train(Matrix[] samples) {
		if (samples[0].columns() == 1)
			throw new IllegalArgumentException("GPCAExtractor train, accept row vectors only while samples is vectors.");
		if (samples[0].rows() == 1)
			isVectors = true;
		else
			isVectors = false;
		samplesNumber = samples.length;
		sampleWidth = samples[0].columns();
		sampleHeight = samples[0].rows();
		computeEigens(samples);
	}

	/* (non-Javadoc)
	 * @see artiano.statistics.extractor.FeatureExtractor#extract(double[])
	 */
	@Override
	public Matrix extract(Matrix sample) {
		if (sample.rows() != sampleHeight || sample.columns() != sampleWidth)
			throw new IllegalArgumentException("GPCAExtractor extract, size not match.");
		Matrix feature;
		if (isVectors)
			feature = eigenVectors.multiply(sample.subtract(mean, true).t()).t();
		else
			feature = eigenVectors.multiply(sample.subtract(mean, true));
		return feature;
	}

	/* (non-Javadoc)
	 * @see artiano.statistics.extractor.FeatureExtractor#getModel()
	 */
	@Override
	public Matrix getModel() {
		return eigenVectors;
	}

	/* (non-Javadoc)
	 * @see artiano.statistics.extractor.CanReconstructed#reconstruct(artiano.core.structure.Matrix)
	 */
	@Override
	public Matrix reconstruct(Matrix feature) {
		Matrix sample = null;
		if (isVectors)
			sample = feature.multiply(eigenVectors).add(mean);
		else
			sample = eigenVectors.t().multiply(feature).add(mean);
		return sample;
	}

}

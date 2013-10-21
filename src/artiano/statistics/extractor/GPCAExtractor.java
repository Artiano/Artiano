/**
 * GPCAExtractor.java
 */
package artiano.statistics.extractor;

import artiano.core.operation.MatrixOpt;
import artiano.core.structure.Matrix;
import artiano.core.structure.Range;
import artiano.math.algebra.SingularValueDecomposition;

/**
 * <p>广义主成分分析特征提取器。</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-22
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class GPCAExtractor extends FeatureExtractor implements UnsupervisedExtractor{
	
	private static final long serialVersionUID = 1L;
	//平均矩阵
	protected Matrix mean = null;
	//特征矩阵
	protected Matrix eigenVectors = null;
	protected Matrix eigenValues = null;
	protected int eigens = 0;
	protected int samplesNumber = 0;
	//sample size
	protected int sampleWidth = 0;
	protected int sampleHeight = 0;
	//judge if the sample is vector
	protected boolean covarianceByRow = false;
	protected boolean isVectors = false;
	/**
	 * parameter of the extractor
	 */
	protected int eigensNeeded = 0;
	//rate of contribution
	protected double roc = 0.;
	
	/**
	 * 构造器
	 */
	public GPCAExtractor(){ }
	
	/**
	 * 设置贡献率。
	 * <br><b><i>NOTICE:</i></b> 必须在训练之前设置贡献率，提取器在训练时将根据贡献率来决定保存多少数据。默认值为1。
	 * @param roc 贡献率。
	 */
	public void setRoc(double roc){
		this.roc = roc>0.&&roc<1.?roc:1.;
	}
	
	/**
	 * 设置用来重构的特征向量个数。
	 * @param eigensNeeded 特征向量个数。
	 */
	public void setEigens(int eigensNeeded){
		this.eigensNeeded = eigensNeeded>0&&eigensNeeded<eigens?eigensNeeded:eigens;
	}
	
	/**
	 * 获取特征值向量。
	 * @return 特征值向量。
	 */
	public Matrix getEigenValue(){
		return this.eigenValues;
	}
	
	/**
	 * 计算贡献率
	 */
	protected void computeRoc(Matrix eigenValues){
		double sum = 0.;
		if (eigens == 0)
			eigens = eigenValues.columns();
		for (int i = 0; i < eigenValues.columns(); i++)
			sum += eigenValues.at(0, i);
		int i;
		double t = sum;
		for (i = eigenValues.columns() - 1; i >= 0; i--){
			if ((t-eigenValues.at(0, i))/sum <= roc)
				break;
			t -= eigenValues.at(0, i);
		}
		eigens = i + 1;
	}
	
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
			Matrix t = svd.W().sqrt();
			computeRoc(t);
			Matrix w = t.at(Range.all(), new Range(0, eigens)).clone();
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
				 * that t_a = column i of the covariance matrix
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
	 * @see artiano.statistics.extractor.UnsupervisedExtractor#train(artiano.core.structure.Matrix[], double)
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
			feature = eigenVectors.multiply(sample.minus(mean, true).t()).t();
		else
			feature = eigenVectors.multiply(sample.minus(mean, true));
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
	 * @see artiano.statistics.extractor.FeatureExtractor#reconstruct(artiano.core.structure.Matrix)
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

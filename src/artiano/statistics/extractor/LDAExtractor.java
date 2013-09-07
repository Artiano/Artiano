/**
 * LDAExtractor.java
 */
package artiano.statistics.extractor;

import artiano.core.structure.Matrix;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-29
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class LDAExtractor extends FeatureExtractor implements SupervisedExtractor {

	/* (non-Javadoc)
	 * @see artiano.statistics.extractor.SupervisedExtractor#train(artiano.core.structure.Matrix[], artiano.core.structure.Matrix)
	 */
	@Override
	public void train(Matrix[] samples, Matrix labels) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see artiano.statistics.extractor.FeatureExtractor#extract(artiano.core.structure.Matrix)
	 */
	@Override
	public Matrix extract(Matrix sample) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see artiano.statistics.extractor.FeatureExtractor#reconstruct(artiano.core.structure.Matrix)
	 */
	@Override
	public Matrix reconstruct(Matrix feature) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see artiano.statistics.extractor.FeatureExtractor#getModel()
	 */
	@Override
	public Matrix getModel() {
		// TODO Auto-generated method stub
		return null;
	}

}

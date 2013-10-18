/**
 * FastPCA.java
 */
package artiano.statistics.extractor;

import artiano.core.structure.Matrix;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-10-10
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class FastPCA extends FeatureExtractor {

	private static final long serialVersionUID = 1L;

	public FastPCA(){}
	
	/* (non-Javadoc)
	 * @see artiano.statistics.extractor.FeatureExtractor#extract(artiano.core.structure.Matrix)
	 */
	@Override
	public Matrix extract(Matrix sample) {
		return null;
	}

	/* (non-Javadoc)
	 * @see artiano.statistics.extractor.FeatureExtractor#reconstruct(artiano.core.structure.Matrix)
	 */
	@Override
	public Matrix reconstruct(Matrix feature) {
		return null;
	}

	/* (non-Javadoc)
	 * @see artiano.statistics.extractor.FeatureExtractor#getModel()
	 */
	@Override
	public Matrix getModel() {
		return null;
	}

}

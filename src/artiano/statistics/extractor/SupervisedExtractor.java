/**
 * SupervisedExtractor.java
 */
package artiano.statistics.extractor;

import artiano.core.structure.Matrix;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-26
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public interface SupervisedExtractor {
	/**
	 * train an extractor with labels
	 * @param samples - samples to train
	 * @param labels - the labels related with samples
	 * @param roc - rate of contribution
	 */
	public void train(Matrix[] samples, Matrix labels);
}

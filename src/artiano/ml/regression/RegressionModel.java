/**
 * RegressionModel.java
 */
package artiano.ml.regression;

import artiano.core.operation.Preservable;
import artiano.core.structure.Matrix;

/**
 * <p></p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-10-24
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public abstract class RegressionModel extends Preservable {
	public abstract Matrix predict(Matrix x);
}

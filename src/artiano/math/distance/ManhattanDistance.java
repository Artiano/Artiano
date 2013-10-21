/**
 * ManhattanDistance.java
 */
package artiano.math.distance;

import artiano.core.structure.Matrix;

/**
 * <p>计算给定两个向量a、b的曼哈顿距离（街区距离）。</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-10-17
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class ManhattanDistance implements Distance {

	/* (non-Javadoc)
	 * @see artiano.math.distance.Distance#calculate(artiano.core.structure.Matrix, artiano.core.structure.Matrix)
	 */
	@Override
	public double calculate(Matrix a, Matrix b) {
		if (!a.isVector() || !b.isVector())
			throw new UnsupportedOperationException("Distance calculate, accept vector only.");
		if (!a.sameType(b))
			throw new UnsupportedOperationException("Distance calculate, type not same.");
		double d = 0.;
		Matrix c = a.minus(b).abs();
		for (int i=0; i<c.size(); i++)
			d += c.at(i);
		return d;
	}

}

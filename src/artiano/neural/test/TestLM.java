/**
 * TestLM.java
 */
package artiano.neural.test;

import artiano.neural.learning.LevenbergMarquardtLearning;
import artiano.neural.network.ActivationNetwork;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-31
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class TestLM {
	public static void main(String[] arg){
		ActivationNetwork network = new ActivationNetwork(2, 2, new int[]{3,2});
		LevenbergMarquardtLearning teacher = new LevenbergMarquardtLearning(network);
		int x = teacher.absCol(2, 1, 0);
		System.out.println(x + " ");
	}
}

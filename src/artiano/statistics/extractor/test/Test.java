/**
 * Test.java
 */
package artiano.statistics.extractor.test;

import java.text.DecimalFormat;

import artiano.core.structure.Matrix;
import artiano.statistics.extractor.GPCAExtractor;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-27
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class Test {
	static double[][] d =  {{ 2.5,  2.4 },
			{ 0.5,  0.7 },
		    { 2.2,  2.9 },
		    { 1.9,  2.2 },
		    { 3.1,  3.0 },
		    { 2.3,  2.7 },
		    { 2.0,  1.6 },
		    { 1.0,  1.1 },
		    { 1.5,  1.6 },
		    { 1.1,  0.9 }};
	
	static double[][] b = {{2.5000 ,   0.5000,    2.2000 ,   1.9000  ,  3.1000  ,  2.3000  ,  2.0000  ,  1.0000 ,   1.5000 ,   1.1000},
	    {2.4000 ,   0.7000  ,  2.9000 ,   2.2000 ,   3.0000   , 2.7000    ,1.6000,    1.1000 ,   1.6000 ,   0.9000},
	    {1.1000  ,  3.1000  ,  2.3000  ,  1.4000  ,  2.7000  ,  1.2000 ,   2.4000  ,  1.8000 ,   2.1000  ,  1.5000}};
	public static void printMatrix(Matrix x){
		System.out.println("---------------------------");
		DecimalFormat f = new DecimalFormat("#.##");
		for (int i = 0; i < x.rows(); i++){
			for (int j = 0; j < x.columns(); j++)
				System.out.print(f.format(x.at(i, j)) + " ");
			System.out.println();
		}
		System.out.println("---------------------------");
	}
	
	public static void testPCA(){
		GPCAExtractor extractor = new GPCAExtractor();
		Matrix[] m = new Matrix[3];
		for (int i = 0; i < 3; i++)
			m[i] = new Matrix(1,10, b[i]);
		extractor.train(m);
		Matrix model = extractor.getModel();
		Matrix eign = extractor.getEigenValue();
		System.out.println("Eigen Vectors:");
		printMatrix(model);
		System.out.println("Eigen Values:");
		printMatrix(eign);
	}
	
	public static void main(String[] arg){
		testPCA();
	}
	
}

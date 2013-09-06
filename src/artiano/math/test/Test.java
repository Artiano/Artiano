/**
 * Test.java
 */
package artiano.math.test;

import java.text.DecimalFormat;

import artiano.core.structure.Matrix;
import artiano.math.algebra.CholeskyDecomposition;
import artiano.math.algebra.GaussJordan;
import artiano.math.algebra.LUDecomposition;
import artiano.math.algebra.SingularValueDecomposition;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0
 * @date 2013-8-18
 * @author (latest modification by Nano.Michael)
 * @function 
 * @since 1.0
 */
public class Test {
	
	static double a[][] = {{2,1,-3},
			{1,2,-2},
			{-1,3,2}};
	static double b[][] = {{1,-1},
			{2,0},{-2,5}};
	
	public static void printX(double[][] x){
		DecimalFormat f = new DecimalFormat("#.##");
		for (int i = 0; i < x.length; i++){
			for (int j = 0; j < x[i].length; j++)
				System.out.print(f.format(x[i][j]) + " ");
			System.out.println();
		}
	}
	
	static double GA[] = 
		{2,1,-3,
		 1,2,-2,
		 -1,3,2
		};
	static double GB[] = 
		{1,-1,
		2,0,
		-2,5
		};
	public static void testGJ(){
		System.out.println("\n-------Gauss-Jordan------");
		GaussJordan gj = new GaussJordan(new Matrix(3, 3, GA), new Matrix(3, 2, GB), true);
		Matrix inv = gj.getInversion();
		System.out.println("Inversion:");
		printMatrix(inv);
		Matrix s = gj.getSolution();
		System.out.println("Solution:");
		printMatrix(s);
		System.out.println();
		System.out.println("---------------------------");
	}
	
	public static void testLU(){
		System.out.println("\n------LU decomposition------");
		LUDecomposition d = new LUDecomposition(new Matrix(3, 3, GA), true);
		Matrix inv = d.inverse();
		System.out.println("Inversion:");
		printMatrix(inv);
		Matrix s = d.solve(new Matrix(3,2, GB));
		System.out.println("Solution:");
		printMatrix(s);
		System.out.println("-------------------------------");
	}
	
	public static void printMatrix(Matrix x){
		DecimalFormat f = new DecimalFormat("#.##");
		System.out.println("--------------------------------");
		for (int i = 0; i < x.rows(); i++){
			for (int j = 0; j < x.columns(); j++)
				System.out.print(f.format(x.at(i, j)) + " ");
			System.out.println();
		}
	}
	
	static double sym[] = {8,6,6,8};//{{14,10,-5},{10,9,1},{-5,1,14}};
	static double[] x = {10,20,11,22};
	public static void testCD(){
		System.out.println("\n-------Cholesky-decomposition------");
		CholeskyDecomposition cd = new CholeskyDecomposition(new Matrix(2, 2, sym));
		if (cd.isDefinite()){
			Matrix inv = cd.inverse();
			System.out.println("Inversion:");
			printMatrix(inv);
			Matrix y = cd.solve(new Matrix(2, 2, x));
			System.out.println("Solution:");
			printMatrix(y);
		}
		else 
			System.out.println("Matrix is not positive-definite.");
		System.out.println("------------------------------------");
	}
	
	static double[] A = {14,10,-5,10,9,1,-5,1,14};
	public static void testSVD(){
		System.out.println("\n------Singular Value Decomposition------");
		SingularValueDecomposition svd = new SingularValueDecomposition(new Matrix(3, 3, A));
		svd.sort();
		System.out.println("U");
		Matrix u = svd.U();
		printMatrix(u);
		System.out.println("W");
		Matrix w = svd.W();
		printMatrix(w);
		Matrix v = svd.V();
		System.out.println("\nV");
		printMatrix(v);
		System.out.println("pseudo-inversion:");
		Matrix pinv = svd.pseudoInverse();
		printMatrix(pinv);
		System.out.println("----------------------------------------");
	}
	
	public static void main(String[] args){
		testGJ();
		testLU();
		testCD();
		testSVD();
	}
}

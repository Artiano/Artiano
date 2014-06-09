package artiano.ml.clustering.test;

import java.util.List;

import org.junit.Test;

import artiano.core.structure.Matrix;
import artiano.core.structure.Table;
import artiano.ml.clustering.DBSCAN;

public class DBSCANTest {

	@Test
	public static void main(String[] args) {
		int dimension = 2;
		double[] dataPointsArr = new double[]{
			-1, 0,   1, 2,   2, 0,   2, 2,
			2,  3,   3, 1,   5, 3,   6, 1,
			6, 2 ,   6, 5,   7, 1,   7, 3,    
			7, 5,    8, 3,   9, 3,   11, 1
		}; 
		Matrix dataPoints = 
			new Matrix(dataPointsArr.length/dimension, dimension, dataPointsArr);
		dataPoints.print();
		double eps = 0.7;
		int minNeighborsNum = 3;
		DBSCAN dbscan = new DBSCAN(eps, minNeighborsNum, new Table(dataPoints));
		List<Table> clusterList = dbscan.cluster();
		System.out.println("所有的簇如下所示:");
		int i = 0;
		for(Table cluster: clusterList) {
			System.out.println("簇 " + (i+1));
			cluster.print();
			i++;
		}
		
		List<Table> noisePointList = dbscan.getNoisePoints();
		System.out.println("被划分为噪声点的点如下:");
		for(Table noisePoint: noisePointList) {
			noisePoint.print();
		}
	}

}

package artiano.ml.clustering;

import java.util.*;
import java.util.Map.Entry;
import java.text.DecimalFormat;

import artiano.core.operation.Preservable;
import artiano.core.structure.Matrix;
import artiano.core.structure.Range;
import artiano.ml.BaseKDTree.BaseKDNode;

/**
 * <p>Description: KMeans.</p>
 * @author JohnF Nash
 * @version 1.0.0
 * @date 2013-9-15
 * @function 
 * @since 1.0.0
 */
public class KMeans extends Preservable {
	private static final long serialVersionUID = 1L;

	private Matrix data;  //clustering data		
	
	//constructor
	public KMeans() {		
	}
	
	/**
	 * Find centers of clusters and groups input samples around the clusters.
	 * @param data - Data for clustering
	 * @param k - Number of clusters to split the set by
	 * @return clusterMap - mapping between centers and members of each center 
	 */
	public Map<Matrix, Matrix> kmeans(Matrix data, int k) {
		this.data = data;		
		dataValidationCheck(data, k); //Check validation of parameters inputed. 
		
		//Get centers with min evaluations in several exercises
		return findFinalCenters(data, k);
	}

	/**
	 * Get centers with min evaluations in several exercises
	 * @param data - Data for clustering
	 * @param k - Number of clusters to split the set by
	 * @return clusterMap - mapping between centers and members of each center
	 */
	private Map<Matrix, Matrix> findFinalCenters(Matrix data, int k) {
		//Generating random centers for several times
		final int NUMOFGENERATINGCENTERS = 
				computeNumOfCombinations(data.rows(), k) / 7;  
		//Store all final centers of NUMOFGENERATINGCENTERS times computing
		Matrix allExerCenters = new Matrix(NUMOFGENERATINGCENTERS * k, data.columns());
		//Store all cluster Maps
		List<Map<Matrix, Matrix>> allClusterMaps = 
				new ArrayList<Map<Matrix, Matrix>>();			
		//Store evaluations of each generating of random centers
		Matrix allEvaluations = new Matrix(NUMOFGENERATINGCENTERS, 1);  
		
		//Store each exercise results in matrix 
		for(int i=0; i<NUMOFGENERATINGCENTERS; i++) {
			Matrix initialCenters = initializeCenters(data, k); //Initialize clustering split centers
			Map<Matrix, Matrix> tempClusterMap = kmeans(initialCenters);  //clusters and theirs centers
			Matrix finalCenters = getCentersFromClusterMap(k, tempClusterMap);  //Get centers from cluster map
			
			allExerCenters.set(new Range(i*3, i*3+3), new Range(0, data.columns()), finalCenters);
			allClusterMaps.add(tempClusterMap);
			allEvaluations.set(i, 0, evaluate(tempClusterMap));
		}
		
		//Get min evaluation in all the exercises
		double minEvaluation = allEvaluations.at(0, 0);
		int minIndex = 0;
		for(int i=1; i<allEvaluations.rows(); i++) {
			if(allEvaluations.at(i, 0) < minEvaluation) {
				minEvaluation = allEvaluations.at(i, 0);
				minIndex = i;
			}
		}		
				
		return allClusterMaps.get(minIndex);
	}
	
	/**
	 * Find output matrix of the cluster centers, one row per cluster center
	 * @param centers - center of clusters
	 * @return Output matrix of the cluster centers, one row per cluster center
	 */
	private Map<Matrix, Matrix> kmeans(Matrix centers) {		
		//Get non-center data
		Matrix remaining = getNonCenterData(centers);
	
		KDTree kdTree = new KDTree(centers);  //Build a tree with centers
		//Use LinkedHashMap to maintain the order of centers
		Map<Matrix, Matrix> clusterMap = new LinkedHashMap<Matrix, Matrix>();
		for(int i=0; i<centers.rows(); i++) {
			boolean isPart = false;
			for(int m=0; m<data.rows(); m++) {
				if(centers.row(i).equals(data.row(m))) {
					isPart = true;
					break;
				}
			}
			
			if(isPart) {
				clusterMap.put(centers.row(i), centers.row(i));
			}			
		}
		
		for(int i=0; i<remaining.rows(); i++) {
			Matrix currentData = remaining.row(i);  //current data to get center
			BaseKDNode node = kdTree.findKNearest(currentData, 1).get(0);
			Matrix center = node.nodeData;   //Nearest center for remaining.row(i);
			if(clusterMap.containsKey(center)) {
				clusterMap.get(center).mergeAfterRow(currentData);				
			} else {
				clusterMap.put(center, currentData);
			}						
		}
		
		//Get updated centers
		Matrix updatedCenters = getUpdatedCenters(clusterMap, data.columns());	
		if(centers.equals(updatedCenters)) {   //Centers already stable
			return clusterMap;
		} else {
			return kmeans(updatedCenters);
		}
	}
	
	/**
	 * Get updated centers
	 * @param clusterMap - map of centers and their cluster members   
	 * @param columns - column number of data
	 * @return updated centers
	 */
	private Matrix getUpdatedCenters(Map<Matrix, Matrix> clusterMap, int columns) {
		int centerNum = clusterMap.keySet().size();
		Matrix newCenters = new Matrix(centerNum, columns);
		Set<Entry<Matrix, Matrix>> entrySet = clusterMap.entrySet();
		int count = 0;
		DecimalFormat f = new DecimalFormat("#.###");   //Format double value 
		for(Entry<Matrix, Matrix> entry: entrySet) {	
			Matrix aCluster = entry.getValue();
			Matrix newCenter = new Matrix(1, columns);  //Compute average matrix
			for(int j=0; j<columns; j++) {
				double sum = 0;
				for(int i=0; i<aCluster.rows(); i++) {
					sum += aCluster.at(i, j); 
				}				
				double value = Double.parseDouble(f.format(sum / aCluster.rows()));  //Format double value
				newCenter.set(0, j, value);
			}									
			newCenters.setRow(count, newCenter);
			count++;
		}
		return newCenters;				
	}
	
	/**
	 * Get non-center data
	 * @param centers - cluster split centers
	 * @return remaining - non-center data
	 */
	private Matrix getNonCenterData(Matrix centers) {				
		//Attention: size of remaining not necessarily data.rows()-centers.rows() ??
		int centerInDataNum = 0; 
		for(int i=0; i<centers.rows(); i++) {
			boolean isInData = false;
			for(int m=0; m<data.rows(); m++) {
				if(centers.row(i).equals(data.row(m))) {
					isInData = true;
				}
			}
			
			if(isInData) {
				centerInDataNum++;
			}
		}
		
		Matrix remaining = new Matrix(data.rows()-centerInDataNum, data.columns());
		int count = 0;
		for(int i=0; i<data.rows(); i++) {
			boolean isSplit = false;    //Whether the data is a cluster split
			for(int m=0; m<centers.rows(); m++) {
				if(data.row(i).equals(centers.row(m))) {
					isSplit = true;
					break;
				}
			}
			
			if(!isSplit) {
				remaining.setRow(count, data.row(i));
				count++;
			}					
		}
		return remaining;
	}

	/**
	 * Get evaluation value of clustering quality  
	 * @param clusterMap - cluster map 
	 * @return - Evaluation value for this clustering to get its quality of clustering
	 */
	private double evaluate(Map<Matrix, Matrix> clusterMap) {
		Set<Entry<Matrix, Matrix>> entrySet = clusterMap.entrySet();
		double evaluation = 0;
		for(Entry<Matrix, Matrix> entry: entrySet) {	
			Matrix center = entry.getKey();  //center of a cluster
			Matrix membersOfCluster = entry.getValue();
			evaluation += evaluate(center, membersOfCluster);
		}
		return evaluation;
	} 
	
	/**
	 * Get evaluation value for a specified cluster
	 * @param center - cluster center
	 * @param members - members of the cluster(including the center)
	 * @return - Evaluation value for a cluster
	 */
	private double evaluate(Matrix center, Matrix members) {
		double evaluation = 0;
		for(int i=0; i<members.rows(); i++) {
			evaluation += distance(center, members.row(i));
		}
		return evaluation;
	}
	
	/**
	 * Get Euclidean distance of two matrix
	 * @param mat1 - matrix 1
	 * @param mat2 - matrix 2
	 * @return Euclidean distance of two matrix
	 */
	private double distance(Matrix mat1, Matrix mat2) {
		if(mat1.columns() != mat2.columns() || mat1.rows() != 1 || mat2.rows() != 1) {
			throw new IllegalArgumentException("Matrixes input can only be one row and should " +
					"have same size.");
		}
		
		double distance = 0;
		for(int j=0; j<mat1.columns(); j++) {
			distance += Math.pow(mat1.at(0, j) - mat2.at(0, j), 2);
		}
		return distance;
	}
	
	/**
	 * Initialize clustering split centers
	 * @param data - Data for clustering
	 * @param k - Number of clusters to split the set by
	 * @return Random clustering split centers
	 */
	private Matrix initializeCenters(Matrix data, int k) {
		Matrix centers = new Matrix(k, data.columns());
		int sampleCount = data.rows();
		Random random = new Random(System.currentTimeMillis());
		int count = 0;
		while(count < k) {			
			int randomRowIndex = random.nextInt(sampleCount);
			Matrix currentData = data.row(randomRowIndex);
			
			/* Check whether current data has been existed in matrix centers */
			boolean alreadyExist = false;
			for(int i=0; i<count; i++) {
				if(currentData.equals(centers.row(i))) {
					alreadyExist = true;
					break;
				}
			}
			
			if(!alreadyExist) {
				centers.setRow(count, currentData);
				count++;
			}			
		}
		return centers;
	}

	/**
	 * Get centers from clusters map
	 * @param k - Data for clustering
	 * @param clusters - Number of clusters to split the set by
	 * @return - centers
	 */
	private Matrix getCentersFromClusterMap(int k, Map<Matrix, Matrix> clusters) {
		Matrix finalCenters = new Matrix(k, data.columns());
		Set<Entry<Matrix, Matrix>> entrySet = clusters.entrySet();
		int count = 0;
		for(Entry<Matrix, Matrix> entry: entrySet) {
			finalCenters.setRow(count, entry.getKey());
			count++;
		}
		return finalCenters;
	}
	
	/**
	 * Compute number of combinations of selecting r from n
	 * @param n - total situations
	 * @param r - r of total situations
	 * @return number of combinations of selecting r from n
	 */
	private int computeNumOfCombinations(int n, int r) {
		int result = 1;
		for(int i=0; i<r; i++) {
			result *= (n - i);
		}
		
		for(int i=r; i>0; i--) {
			result /= i;
		}
		return result;
	}
	
	/**
	 * Check validation of parameters inputed.
	 * @param data - Data for clustering
	 * @param k - Number of clusters to split the set by
	 * @throws IllegalArgumentException
	 */
	private void dataValidationCheck(Matrix data, int k) {
		if(data == null) {
			throw new IllegalArgumentException("Clustering data should not be null.");
		}
		
		if(k <= 1) {
			throw new IllegalArgumentException("Number of clusters should not be less than 2.");
		} else if(k > data.columns()) {
			throw new IndexOutOfBoundsException("k should be less than column number of data.");
		}
	}
}
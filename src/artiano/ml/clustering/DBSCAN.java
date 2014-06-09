package artiano.ml.clustering;

import java.util.*;

import artiano.core.structure.Capability;
import artiano.core.structure.NumericAttribute;
import artiano.core.structure.Options;
import artiano.core.structure.Table;
import artiano.ml.clustering.structure.AbstractGraph.Edge;
import artiano.ml.clustering.structure.ClusterModel;
import artiano.ml.clustering.structure.Graph;
import artiano.ml.clustering.structure.UnWeightedGraph;
import artiano.ml.clustering.structure.Vertex;

public class DBSCAN extends Clustering {
	private double eps; // 半径
	private int minNeighborsNum; // 在指定半径内相邻的点的最少数目
	private Table dataPoints; // 数据点
	private List<List<Double>> distances; // 数据点之间的距离
	private List<Integer> pointType; // 数据点的类型(核心，边界，噪声)
	private int numberOfPoints; // 数据点的个数

	public DBSCAN() {
	}

	public DBSCAN(double eps, int minNeighborsNum, Table dataPoints) {
		super();
		this.eps = eps;
		this.minNeighborsNum = minNeighborsNum;
		this.dataPoints = dataPoints;
		this.numberOfPoints = dataPoints.rows();
	}

	/**
	 * 对输入的数据点进行聚类处理
	 * 
	 * @return 划分的所有簇
	 */
	public List<Table> cluster() {
		Table copyOfDataPoints = this.dataPoints.clone();
		// 计算所有数据点之间的距离
		getDistanceBetweenAnyTwoPoints(copyOfDataPoints);
		// 根据数据点之间的距离指定数据点的类型
		assignTypeForAllPoints();
		// 获取来自核心点的簇
		List<Map<Integer, Table>> clustersList = getClustersOfCorePoints(copyOfDataPoints);
		// 为所有的边界点随机地指派一个与之关联的簇
		putBorderPointsToRelativeCluster(copyOfDataPoints, clustersList);
		// 得到最终的簇
		List<Table> finalClusters = getFinalClusters(clustersList);
		return finalClusters;
	}

	// 获取来自核心点的簇
	private List<Map<Integer, Table>> getClustersOfCorePoints(
			Table copyOfDataPoints) {
		// 构造核心点构成的图
		Graph<Vertex> graph = constructGraphForCorePoints(copyOfDataPoints);
		// 获取图的所有连通分支
		List<List<Integer>> connectedBranches = graph.getConnectedBranches();
		// 把每一个连通分支作为一个簇
		List<Map<Integer, Table>> pointsOfCoreClusters = new ArrayList<>();
		for (int i = 0; i < connectedBranches.size(); i++) {
			List<Integer> branch = connectedBranches.get(i);
			Map<Integer, Table> pointsOfACluster = new HashMap<>();
			for (int j = 0; j < branch.size(); j++) {
				Vertex vertex = graph.getVertex(branch.get(j));
				int indexInDataPoint = vertex.getIndexInDataPoints();
				Table t = copyOfDataPoints.cloneWithHeader();
				t.push(copyOfDataPoints.row(indexInDataPoint));
				pointsOfACluster.put(indexInDataPoint,
						t);
			}
			pointsOfCoreClusters.add(pointsOfACluster);
		}
		return pointsOfCoreClusters;
	}

	// 为所有的边界点随机地指派一个与之关联的簇
	private void putBorderPointsToRelativeCluster(Table copyOfDataPoints,
			List<Map<Integer, Table>> pointsOfCoreClusters) {
		for (int i = 0; i < numberOfPoints; i++) {
			if (pointType.get(i) == PointType.BORDER) { // 边界点
				for (int j = 0; j < numberOfPoints; j++) {
					double distance = getDistance(i, j);
					if (pointType.get(j) == PointType.CORE && distance <= eps) {
						for (Map<Integer, Table> clusterMap : pointsOfCoreClusters) {
							// 该边界点与此核心点相邻，加到这个核心点所在的簇中
							if (clusterMap.containsKey(j)) {
								Table t = copyOfDataPoints.cloneWithHeader();
								t.push(copyOfDataPoints.row(i));
								clusterMap.put(i, t);
							}
						}
					}
				}
			}
		}
	}

	// 构造核心点构成的图
	private Graph<Vertex> constructGraphForCorePoints(Table copyOfDataPoints) {
		// 获取核心点
		Map<Integer, Table> corePointsMap = getCorePoints(copyOfDataPoints);
		Set<Integer> indices = corePointsMap.keySet();
		// 构造图的顶点
		List<Edge> edges = constructEdges(indices);
		// 构造图的顶点
		List<Vertex> corePoints = constructVertices(corePointsMap, indices);
		Graph<Vertex> graph = new UnWeightedGraph<Vertex>(edges, corePoints);
		return graph;
	}

	// 得到最终的簇
	private List<Table> getFinalClusters(
			List<Map<Integer, Table>> clustersList) {
		List<Table> finalClusters = new ArrayList<>();
		for (int i = 0; i < clustersList.size(); i++) {
			Map<Integer, Table> clusterMap = clustersList.get(i);
			Set<Integer> indices = clusterMap.keySet();
			int count = 0;
			Table firstPointInMap = null;
			for (int index : indices) {
				if (count == 0) {
					firstPointInMap = clusterMap.get(index);
					count++;
					continue;
				} else {
					firstPointInMap.append(clusterMap.get(index));
				}
			}
			finalClusters.add(firstPointInMap);
		}
		return finalClusters;
	}

	// 构造图的顶点
	private List<Vertex> constructVertices(Map<Integer, Table> corePointsMap,
			Set<Integer> indices) {
		List<Vertex> corePoints = new ArrayList<Vertex>();
		for (int index : indices) {
			Vertex vertex = new Vertex(index, corePointsMap.get(index));
			corePoints.add(vertex);
		}
		return corePoints;
	}

	// 构造图的边
	private List<Edge> constructEdges(Set<Integer> indices) {
		List<Edge> edges = new ArrayList<Edge>();
		int i = 0;
		for (Integer index_1 : indices) {
			int j = 0;
			for (Integer index_2 : indices) {
				if (index_1 < index_2) {
					double distance = getDistance(index_1, index_2);
					if (distance <= eps) {
						// 两个核心点之间的距离小于eps,则在它们之间添加一条边
						Edge edge = new Edge(i, j);
						edges.add(edge);
					}
				}
				j++;
			}
			i++;
		}
		return edges;
	}

	// 计算所有数据点之间的距离
	private void getDistanceBetweenAnyTwoPoints(Table dataPoints) {
		distances = new ArrayList<List<Double>>(numberOfPoints);
		for (int i = 0; i < numberOfPoints; i++) {
			distances.add(new ArrayList<Double>());
			for (int j = 0; j <= i; j++) {
				double distance;
				if (j == i) {
					distance = 0;
				} else {
					distance = dataPoints.distanceOf(i, j);
				}
				distances.get(i).add(distance);
			}
		}
	}

	// 得到第i个数据点到第j个数据点的距离
	private double getDistance(int i, int j) {
		double distance = 0;
		if (j <= i) {
			distance = distances.get(i).get(j);
		} else {
			distance = distances.get(j).get(i);
		}
		return distance;
	}

	// 根据数据点之间的距离来指定数据点的类型
	private void assignTypeForAllPoints() {
		pointType = new ArrayList<Integer>();
		for (int i = 0; i < numberOfPoints; i++) {
			pointType.add(PointType.UNASSIGNED);
		}
		assignTypeForCorePoints(); // 根据距离确定核心点
		assignTypeForOtherPoints(); // 确定边界点和噪声点
	}

	// 根据距离确定核心点
	private void assignTypeForCorePoints() {
		for (int i = 0; i < numberOfPoints; i++) {
			int numOfPointsInEps = 0; // 数据点i半径eps之内的数据点数
			for (int j = 0; j < numberOfPoints; j++) {
				if (j == i) {
					continue;
				}
				double distance = getDistance(i, j);
				if (distance <= eps) {
					numOfPointsInEps += 1;
				}
			}
			if (numOfPointsInEps >= minNeighborsNum) {
				pointType.set(i, PointType.CORE); // 核心点
			}
		}
	}

	// 确定边界点和噪声点
	private void assignTypeForOtherPoints() {
		for (int i = 0; i < numberOfPoints; i++) {
			if (pointType.get(i) == PointType.UNASSIGNED) { // 还未指定类型
				boolean isNoisePoint = true;
				for (int j = 0; j < numberOfPoints; j++) {
					double distance = getDistance(i, j);
					if (pointType.get(j) == PointType.CORE && distance <= eps) {
						// 与核心点的距离小于eps，为边界点
						isNoisePoint = false;
						break;
					}
				}
				if (isNoisePoint) {
					pointType.set(i, PointType.NOISE);
				} else {
					pointType.set(i, PointType.BORDER);
				}
			}
		}
	}

	// 获取核心点
	private Map<Integer, Table> getCorePoints(Table dataPoints) {
		Map<Integer, Table> corePoints = new HashMap<Integer, Table>();
		int numOfPoints = dataPoints.rows();
		for (int i = 0; i < numOfPoints; i++) {
			if (pointType.get(i) == PointType.CORE) {
				Table t = dataPoints.cloneWithHeader();
				t.push(dataPoints.row(i));
				corePoints.put(i, t);
			}
		}
		return corePoints;
	}

	// 获取被划分为噪声点的数据点
	public List<Table> getNoisePoints() {
		List<Table> noisePointList = new ArrayList<>();
		for (int i = 0; i < numberOfPoints; i++) {
			if (pointType.get(i) == PointType.NOISE) {
				Table t = dataPoints.cloneWithHeader();
				t.push(dataPoints.row(i));
				noisePointList.add(t);
			}
		}
		return noisePointList;
	}

	// 数据点的类型类
	private final class PointType {
		final static int UNASSIGNED = 0; // 没有指定时为UNASSIGNED
		final static int CORE = 1; // 核心点
		final static int BORDER = 2; // 边缘点
		final static int NOISE = 3; // 噪声点
	}

	@Override
	public ClusterModel cluster(Table data) {
		return null;
	}

	@Override
	public Capability capability() {
		Capability cap = new Capability();
		// disable all
		cap.disableAll();
		// attribute capabilities
		cap.enableAttribute(NumericAttribute.class);
		// missing value in attribute is not allowed
		cap.allowAttributeMissing(true);
		// minimum instances
		cap.setMinimumInstances(2);
		return cap;
	}

	@Override
	public String descriptionOfOptions() {
		return null;
	}

	@Override
	public Options supportedOptions() {
		return null;
	}

	@Override
	public boolean applyOptions(Options options) {
		return false;
	}
}

package artiano.ml.clustering;

import java.text.DecimalFormat;
import java.util.*;

import artiano.core.structure.Capability;
import artiano.core.structure.NominalAttribute;
import artiano.core.structure.NumericAttribute;
import artiano.core.structure.Options;
import artiano.core.structure.Table;
import artiano.core.structure.Table.TableRow;
import artiano.ml.clustering.structure.Cluster;
import artiano.ml.clustering.structure.ClusterModel;

public class Agenes extends Clustering {
	/**
	 * 对数据点使用Agenes算法进行聚类(类间相似度使用MIN进行描述)
	 * 
	 * @param dataPoints
	 *            要进行聚类的数据点
	 * @param clusterNumber
	 *            要得到的类的数目
	 * @return 最终的聚类
	 */
	public static List<Cluster> cluster(Table dataPoints, int clusterNumber) {
		
		List<Cluster> finalClusters = new ArrayList<Cluster>();
		// 初始情况下，每个数据点都作为一个簇
		finalClusters = initializeClsters(dataPoints);
		/**
		 * 合并簇间距离最近的两个簇，知道簇的数目等于clusterNumber 类间相似性用MIN来描述
		 */
		while (finalClusters.size() > clusterNumber) {
			// 获取当前距离最近的两个簇在簇列表中的下标
			List<Integer> closestClusterIndices = getCurrentTwoClosestClusters(finalClusters);
			// 合并这两个簇
			mergeTwoClusters(finalClusters, closestClusterIndices);
		}
		return finalClusters;
	}

	// 合并当前距离最近的两个簇
	private static void mergeTwoClusters(List<Cluster> finalClusters,
			List<Integer> closestClusterIndices) {
		int clusterIndex1 = closestClusterIndices.get(0);
		int clusterIndex2 = closestClusterIndices.get(1);
		Cluster cluster1 = finalClusters.get(clusterIndex1);
		Cluster cluster2 = finalClusters.get(clusterIndex2);
		// 将两个簇合并后得到的簇
		Cluster clusterMerged = new Cluster(cluster1, cluster2);
		finalClusters.remove(cluster1);
		finalClusters.remove(cluster2);
		finalClusters.add(clusterMerged);
	}

	// 初始情况下，每个数据点都作为一个簇
	private static List<Cluster> initializeClsters(Table dataPoints) {
		List<Cluster> initialClusters = new ArrayList<Cluster>();
		int rows = dataPoints.rows();
		for (int i = 0; i < rows; i++) {
			TableRow dataPoint = dataPoints.row(i);
			Table t = dataPoints.cloneWithHeader();
			t.push(dataPoint);
			Cluster cluster = new Cluster(t);
			initialClusters.add(cluster);
		}
		return initialClusters;
	}

	// 获取当前最接近的两个簇
	private static List<Integer> getCurrentTwoClosestClusters(
			List<Cluster> currentClusters) {
		int clusterIndex_1 = 0;
		int clusterIndex_2 = 0;
		double minDistance = Double.MAX_VALUE;
		for (int i = 0; i < currentClusters.size(); i++) {
			for (int j = 0; j < currentClusters.size(); j++) {
				if (i != j) {
					Cluster cluster_1 = currentClusters.get(i);
					Cluster cluster_2 = currentClusters.get(j);
					Table dataPoints_1 = cluster_1.getDataPoints();
					Table dataPoints_2 = cluster_2.getDataPoints();
					// 获取当前两个簇最小的簇间距离
					for (int m = 0; m < dataPoints_1.rows(); m++) {
						for (int n = 0; n < dataPoints_2.rows(); n++) {
							// 簇1中的点到簇2中的点的距离
							double tempDistance = Table.distanceOf(dataPoints_1.row(m),
									dataPoints_2.row(n));
							if (tempDistance < minDistance) {
								minDistance = tempDistance;
								clusterIndex_1 = i;
								clusterIndex_2 = j;
							}
						}
					}
				}
			}
		}
		return Arrays.asList(clusterIndex_1, clusterIndex_2);
	}

	public class AgensCluster extends ClusterModel {
		
		private List<Cluster> mClusters;
		
		public AgensCluster(Table data) {
			super(data);
		}
		
		public void setClusters(List<Cluster> clusters) {
			mClusters = clusters;
			// assign
			for (int i = 0; i < clusters.size(); i++) {
				Cluster cluster = clusters.get(i);
				cluster.setClusterName("#"+i);
				Table t = cluster.getDataPoints();
				for (int j = 0; j < t.rows(); j++) {
					int row = mDataSet.indexOfRow(t.row(j));
					if (row != -1)
						assignClusterToInstance(row, cluster.getClusterName());
				}
			}
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			DecimalFormat format = new DecimalFormat("#.#");
			builder.append("<br><p>聚类结果</p>");
			for (int i = 0; i < mClusters.size(); i++) {
				Cluster cluster = mClusters.get(i);
				builder.append("<p>cluster"+cluster.getClusterName()+"</p>");
				Table t = cluster.getDataPoints();
				builder.append("<pre>");
				for (int j = 0; j < t.rows(); j++) {
					String str = "[";
					for (int k = 0; k < t.columns(); k++) {
						if (t.at(j, k) instanceof Double)
							str += format.format((double)t.at(j, k));
						else
							str += t.at(j, k);
						if (k == t.columns() - 1)
							str += "]";
						else
							str += ",";
					}
					builder.append(str+"\n");
				}
				builder.append("</pre>");
				builder.append("<hr>");
			}
			builder.append(super.toString());
			return builder.toString();
		}
	}
	
	private int k = 3;
	
	@Override
	public ClusterModel cluster(Table data) {
		if (!handleDataSet(data))
			throw new IllegalArgumentException();
		AgensCluster cluster = new AgensCluster(data);
		List<Cluster> clusters = cluster(data, k);
		cluster.setClusters(clusters);
		return cluster;
	}

	@Override
	public Capability capability() {
		Capability cap = new Capability();
		// disable all
		cap.disableAll();
		// attribute capabilities
		cap.enableAttribute(NumericAttribute.class);
		cap.enableAttribute(NominalAttribute.class);
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
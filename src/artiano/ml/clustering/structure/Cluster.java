package artiano.ml.clustering.structure;

import artiano.core.structure.Table;

public class Cluster {
	private Table dataPoints; // 簇内的数据集
	private String clusterName; // 簇的名字

	public Cluster(Table dataPoints) {
		this(dataPoints, "");
	}

	public Cluster(Table dataPoints, String clusterName) {
		super();
		this.dataPoints = dataPoints;
		this.clusterName = clusterName;
	}

	// 合并两个簇为一个簇
	public Cluster(Cluster cluster1, Cluster cluster2) {
		this(cluster1, cluster2, "");
	}

	// 合并两个簇为一个簇并指定新簇的名字
	public Cluster(Cluster cluster1, Cluster cluster2, String newClusterName) {
		mergeTwoCluster(cluster1, cluster2, newClusterName);
	}

	// 合并两个簇为一个簇并指定新簇的名字
	private void mergeTwoCluster(Cluster cluster1, Cluster cluster2,
			String newClusterName) {
		Table data_1 = cluster1.dataPoints.clone(); // clone
		Table data_2 = cluster2.dataPoints.clone();
		data_1.append(data_2);
		this.dataPoints = data_1;
		this.clusterName = newClusterName;
	}

	public Table getDataPoints() {
		return dataPoints;
	}

	public void setDataPoints(Table dataPoints) {
		this.dataPoints = dataPoints;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Cluster)) {
			return false;
		} else {
			Cluster cluster = (Cluster) obj;
			if (cluster.getDataPoints().equals(this.dataPoints)) {
				return true;
			} else {
				return false;
			}
		}
	}
}

/**
 * ClusterModel.java
 */
package artiano.ml.clustering.structure;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import artiano.core.structure.Table;

/**
 * <p>
 * </p>
 * 
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2014-6-4
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public abstract class ClusterModel {

	/**
	 * 实例和簇的映射
	 */
	protected HashMap<Integer, Object> mInstanceWithCluster = new HashMap<>();

	private HashMap<Object, Integer> mCountOfPerCluster = new HashMap<>();

	private int mNumClusters = 0;
	
	private int mClassIndex = -1;
	/**
	 * 被聚类的数据集
	 */
	protected Table mDataSet;

	public ClusterModel(Table data) {
		mDataSet = data;
	}

	/**
	 * 分配一个簇到指定实例
	 * 
	 * @param i
	 *            指定实例在数据集中的下标
	 * @param cluster
	 *            被分配的簇
	 */
	public void assignClusterToInstance(int i, Object cluster) {
		if (i < 0 || i >= mDataSet.rows())
			throw new IllegalArgumentException("index out of range.");
		Integer count = mCountOfPerCluster.get(cluster);
		if (count == null) {
			count = 0;
			mNumClusters++;
		}
		count++;
		mCountOfPerCluster.put(cluster, count);
		mInstanceWithCluster.put(i, cluster);
	}

	/**
	 * 获取指定实例的簇
	 * 
	 * @param i
	 *            指定的实例在数据集中的下标
	 * @return
	 */
	public Object getClusterAssignedToInstance(int i) {
		return mInstanceWithCluster.get(i);
	}
	
	public void setClassAndEvaluate(int att) {
		mClassIndex = att;
		
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		DecimalFormat format = new DecimalFormat("#");
		// count of per cluster
		builder.append("<br><P>总共形成聚类：[" + mNumClusters + "个]");
		builder.append("<table border='0' cellspacing='2px'>");
		builder.append("<tr><td>cluster</td><td>count</td></tr>");
		Set<Entry<Object, Integer>> entries = mCountOfPerCluster.entrySet();
		for (Iterator<Entry<Object, Integer>> iterator = entries.iterator(); iterator.hasNext();) {
			Entry<Object, Integer> entry = (Entry<Object, Integer>) iterator.next();
			builder.append("<tr>");
			builder.append("<td>"+entry.getKey()+"</td>");
			double percent = (double)entry.getValue() / (double)mDataSet.rows() * 100;
			builder.append("<td>"+entry.getValue()+"("+format.format(percent)+"%)"+"</td>");
			builder.append("</tr>");
		}
		return builder.toString();
	}

}

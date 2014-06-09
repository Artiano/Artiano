/**
 * Clustering.java
 */
package artiano.ml.clustering;

import java.util.ArrayList;

import artiano.core.operation.CapabilityHandler;
import artiano.core.operation.OptionsHandler;
import artiano.core.structure.Attribute;
import artiano.core.structure.Table;
import artiano.ml.clustering.structure.ClusterModel;

/**
 * <p></p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2014-6-4
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public abstract class Clustering implements CapabilityHandler, OptionsHandler {
	
	public static ArrayList<Class<?>> listAll() {
		ArrayList<Class<?>> clusterings = new ArrayList<>();
		clusterings.add(KMeans.class);
		clusterings.add(SOM.class);
		clusterings.add(Agenes.class);
		clusterings.add(DBSCAN.class);
		return clusterings;
	}
	
	@Override
	public boolean handleDataSet(Table table) {
		return capability().handles(table);
	}
	
	public abstract ClusterModel cluster(Table data);
	
}

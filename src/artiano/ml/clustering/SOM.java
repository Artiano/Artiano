/**
 * SOM.java
 */
package artiano.ml.clustering;

import artiano.core.structure.Capability;
import artiano.core.structure.NumericAttribute;
import artiano.core.structure.Options;
import artiano.core.structure.Table;
import artiano.ml.clustering.structure.ClusterModel;

/**
 * <p></p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2014-6-5
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class SOM extends Clustering {

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

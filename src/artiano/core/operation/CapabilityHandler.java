/**
 * CapabilityHandler.java
 */
package artiano.core.operation;

import artiano.core.structure.Capability;
import artiano.core.structure.Table;

/**
 * <p></p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-12-19
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public interface CapabilityHandler {
	/**
	 * 得到本类实例的数据处理能力
	 * @return 本类实例的数据处理能力
	 */
	public Capability capability();
	
	/**
	 * 是否具备处理数据的能力
	 * @param data 具体的数据集合
	 * @return 是否处理数据
	 */
	public boolean handleDataSet(Table data);
}

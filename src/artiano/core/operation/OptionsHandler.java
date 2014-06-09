/**
 * OptionsHandler.java
 */
package artiano.core.operation;

import artiano.core.structure.Options;

/**
 * <p>
 * </p>
 * 
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2014-5-29
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public interface OptionsHandler {

	/**
	 * 参数描述
	 * 
	 * @return 参数描述
	 */
	public String descriptionOfOptions();

	/**
	 * 得到支持的参数，此时参数的值没有被设置（默认值被设置），可以为{@code null}，表示没有参数
	 * 
	 * @return 受支持的参数
	 */
	public Options supportedOptions();

	/**
	 * 设置参数，参数必须由{@link #supportedOptions()}得到
	 * 
	 * @param options
	 */
	public boolean applyOptions(Options options);

}

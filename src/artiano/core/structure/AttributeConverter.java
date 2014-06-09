/**
 * AttributeConverter.java
 */
package artiano.core.structure;

import artiano.core.operation.OptionsHandler;

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
public abstract class AttributeConverter implements OptionsHandler {

	/**
	 * 转换属性
	 * 
	 * @return 转换后的属性
	 */
	public abstract Attribute[] convert(AttributeConvertion convertion);

	/**
	 * 受支持的属性转换
	 * 
	 * @return
	 */
	public abstract AttributeConvertion[] supportedConvertion();

	/**
	 * 一个转换是否被支持
	 * 
	 * @param attr
	 *            待转换属性
	 * @return
	 */
	public boolean isConvertionSupported(AttributeConvertion con) {
		AttributeConvertion[] convertions = supportedConvertion();
		for (AttributeConvertion convertion : convertions)
			if (convertion.isSupported(con.getType()))
				return true;
		return false;
	}

}

/**
 * Capability.java
 */
package artiano.core.structure;

import java.util.HashSet;
import java.util.Iterator;

/**
 * 表示处理能力的类。
 * <p>
 * 这种“能力”表示仅限于对数据实例的处理。默认情况下，构造的本类实例
 * 不具备任何实例处理能力，你可能需要手工添加。</p>
 * 
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-12-16
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class Capability {
	/** 属性处理能力 */
	protected HashSet<Class<?>> capbilities = new HashSet<>();
	/** 是否允许属性值有缺失 */
	protected boolean allowMissing = false;
	/** 最少实例数 */
	protected int minInstaces = 0;
	/**
	 * 构造一个不具备任何“能力”的实例
	 */
	public Capability() {
		capbilities.clear();
	}
	/**
	 * 使用给定的“能力”构造一个实例
	 * @param c 指定实例
	 */
	public Capability(Capability c) {
		this();
		or(c);
	}
	/**
	 * 设定是否允许属性值有缺失
	 * @param canHadle 是否能处理
	 */
	public void setAllowMissing(boolean allowMissing) {
		this.allowMissing = allowMissing;
	}
	/**
	 * 是否允许缺失值
	 * @return
	 */
	public boolean isAllowMissing() {
		return this.allowMissing;
	}
	/**
	 * 设置最小能处理实例数
	 * @param min 最小实例数
	 */
	public void setMinimumInstances(int min) {
		this.minInstaces = min;
	}
	/**
	 * 获取能处理的最小实例数
	 * @return 能处理的最小实例数
	 */
	public int getMinimumInstances() {
		return this.minInstaces;
	}
	/**
	 * 开启对某个类的“能力”
	 * @param att
	 */
	public void enable(Class<?> att) {
		capbilities.add(att);
	}
	/**
	 * 禁用对某个类的“能力”<br>
	 * <b><i>NOTICE:</b></i>如果本类实例中并不包含这个类的“能力”，将不做任何处理
	 * @param att 指定需禁用的能力
	 */
	public void disable(Class<?> att) {
		capbilities.remove(att);
	}
	/**
	 * 禁用所有能力
	 */
	public void disableAll() {
		capbilities.clear();
		allowMissing = false;
	}
	/**
	 * 判断是否具有处理某个类的处理能力
	 * @param att 指定的类
	 * @return 若具备指定类的处理能力则返回{@code true}
	 */
	public boolean handles(Class<?> att) {
		return capbilities.contains(att);
	}
	/**
	 * 判断是否具备处理某个表的能力
	 * @param t 待判断的表
	 * @return 如果能处理则返回{@code true}
	 */
	public boolean handles(Table t) {
		// can handle the attributes ?
		for (Iterator<?> it=t.attributes(); it.hasNext();) {
			if (!handles(it.next().getClass()))
				return false;
		}
		// can handle with missing values ?
		if (t.hasMissing() & !allowMissing) return false;
		// minimum instances
		if (t.rows() < minInstaces) return false;
		return true;
	}
	/**
	 * 与某个能力进行“或”运算
	 * @param c 指定的“能力”
	 */
	public void or(Capability c) {
		// attribute capabilities
		capbilities.addAll(c.capbilities);
		// can handle with missing values ?
		allowMissing = allowMissing || c.allowMissing;
		minInstaces = minInstaces<c.minInstaces?c.minInstaces:minInstaces;
	}
	/**
	 * 与某个能力进行“与”运算
	 * @param c 指定“能力”
	 */
	public void and(Capability c) {
		// attribute capabilities
		for (Class<?> cs: capbilities) 
			if (!(handles(cs) && c.handles(cs)))
				capbilities.remove(cs);
		// can handle with missing values ?
		allowMissing = allowMissing && c.allowMissing;
		minInstaces = minInstaces<c.minInstaces?minInstaces:c.minInstaces;
	}

	public static void main(String[] args) {
		Capability capability = new Capability();
		capability.enable(NumericAttribute.class);
		capability.disable(NominalAttribute.class);
		System.out.println("handles nominal attribute: "
				+ capability.handles(NominalAttribute.class));
	}
}

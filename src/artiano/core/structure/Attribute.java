/**
 * Attribute.java
 */
package artiano.core.structure;

/**
 * <p>基本数据结构，表示任何属性的超类。</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-10-28
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public abstract class Attribute {
	/** 属性缺失取值 */
	public static final double MISSING_VALUE = Double.NaN;
	/** 符号属性缺失取值 */
	public static final String NOMINAL_MISSING_VALUE = "?";
	/** 数值属性缺失取值 */
	public static final double NUMERIC_MISSING_VALUE = Double.NaN;
	/** 属性类型 */
	protected String type = "";
	/** 属性名称 */
	protected String name = null;
	/**	存放属性值的向量 */
	protected IncrementVector vector = new IncrementVector();
	/**
	 * 构造一个属性
	 */
	public Attribute(){}
	/**
	 * 使用声明的名称构造一个属性
	 * @param name 属性名称
	 */
	public Attribute(String name){
		this.name = name;
	}
	/**
	 * 使用声明的名称和属性值向量构造一个属性
	 * @param name 名称
	 * @param vector 属性值向量
	 */
	public Attribute(String name, IncrementVector vector){
		this.name = name;
		this.vector = vector;
	}
	/**
	 * 获取属性名称
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * 获取属性类型
	 * @return
	 */
	public String getType() {
		return type;
	}
	/**
	 * 设置属性名称
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 获取属性值向量
	 * @return the vector
	 */
	public IncrementVector getVector() {
		return vector;
	}
	/**
	 * 设置属性值向量
	 * @param vector
	 */
	public void setVector(IncrementVector vector){
		this.vector = vector;
	}
	/**
	 * 获取属性值向量在i处的值
	 * @param i 指定下标
	 * @return
	 */
	public abstract Object get(int i);
	/**
	 * 将属性向量转换为数组
	 * @return
	 */
	public abstract Object toArray();
}




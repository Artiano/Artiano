/**
 * NumericAttribute.java
 */
package artiano.core.structure;

/**
 * <p>表示数值属性</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-10-30
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class NumericAttribute extends Attribute {
	/**
	 * 构造一个数值属性
	 */
	public NumericAttribute(){
		this.type = "Numeric";
	}
	/**
	 * 使用声明的名称构造一个数值属性
	 * @param name
	 */
	public NumericAttribute(String name){
		super(name);
		this.type = "Numeric";
	}
	
	public NumericAttribute(String name, IncrementVector vector){
		super(name, vector);
		this.type = "Numeric";
	}
	/* (non-Javadoc)
	 * @see artiano.core.structure.Attribute#get(int)
	 */
	@Override
	public Double get(int i) {
		return (Double) this.vector.at(i);
	}
}


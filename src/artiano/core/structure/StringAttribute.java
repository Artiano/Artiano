/**
 * StringAttribute.java
 */
package artiano.core.structure;


/**
 * <p></p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-11-3
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class StringAttribute extends Attribute {
	/**
	 * 构造一个字符串属性
	 */
	public StringAttribute(){
		this.type = "String";
	}
	/**
	 * 使用指定名称构造一个字符串属性
	 * @param name
	 */
	public StringAttribute(String name){
		super(name);
		this.type = "String";
	}
	/**
	 * 使用指定名称和属性值向量构造一个字符串属性
	 * @param name
	 * @param vector
	 */
	public StringAttribute(String name, IncrementVector vector){
		super(name, vector);
		this.type = "String";
	}
	/**
	 * 将字符串属性转换为符号属性
	 * @return 转换后形成的字符串属性
	 */
	public NominalAttribute toNominal(){
		NominalAttribute att = new NominalAttribute(this.name, this.vector.copy());
		for (int i=0; i<vector.size(); i++)
			att.addNominal((String) vector.at(i));
		return att;
	}
	
	@Override
	public String get(int i){
		return (String) this.vector.at(i);
	}
	/* (non-Javadoc)
	 * @see artiano.core.structure.Attribute#toArray()
	 */
	@Override
	public String[] toArray() {
		String[] array = new String[this.vector.size()];
		for (int i=0; i<array.length; i++)
			array[i] = (String)this.vector.at(i);
		return array;
	}
}

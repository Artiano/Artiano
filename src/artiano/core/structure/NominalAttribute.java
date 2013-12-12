/**
 * NominalAttribute.java
 */
package artiano.core.structure;

import java.util.*;

/**
 * <p>
 * 表示符号属性
 * </p>
 * 
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-11-2
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class NominalAttribute extends Attribute {
	private static final long serialVersionUID = 7943037598867848231L;
	
	/** 存储符号属性的取值范围 */
	private List<Object> nominals = new ArrayList<Object>();
	/** 符号属性的值类型 */
	public enum NominalValueType{
		NumericValued,
		StringValued
	}
	
	public NominalAttribute() {
		this.type = "Nominal";
	}

	/**
	 * 构造一个符号属性
	 * 
	 * @param name
	 */
	public NominalAttribute(String name) {
		super(name);
		this.type = "Nominal";
	}

	public NominalAttribute(String name, IncrementVector vector) {
		super(name, vector);
		this.type = "Nominal";
	}
	/**
	 * 添加一个符号取值
	 * 
	 * @param nominal
	 *            待添加的取值
	 */
	public void addNominal(Object nominal) {
		if (!nominals.contains(nominal))
			nominals.add(nominal);
	}

	/**
	 * 移除一个符号取值
	 * 
	 * @param nominal
	 *            待移除的符号取值
	 * @return 移除成功返回真，反之则反
	 */
	public boolean removeNominal(Object nominal) {
		return nominals.remove(nominal);
	}

	/**
	 * 获取属性中的符号取值
	 * 
	 * @return
	 */
	public List<Object> getNominals() {
		return this.nominals;
	}

	/**
	 * 获取符号取值为nominal的个数
	 * 
	 * @param nominal
	 * @return
	 */
	public int getNominalCount(Object nominal) {
		int count = 0;
		for (int i = 0; i < vector.size(); i++) {
			if (nominal.equals(get(i)))
				count++;
		}
		return count;
	}
	/**
	 * 使用指定字符串替换缺失值
	 * @param missing 替换的值
	 */
	public void replaceMissing(String missing){
		for (int i=0; i<vector.size(); i++)
			if (isMissing(i))
				vector.set(i, missing);
	}
	/**
	 * 替换缺失值
	 */
	public void replaceMissing(){
		replaceMissing("?");
	}
	/**
	 * 将符号属性转换为数值属性
	 * 
	 * @param nominalToNumericMap
	 *            符号取值与数值取值之间的映射关系
	 * @return
	 */
	public NumericAttribute toNumeric(Map<String, Double> nominalToNumericMap) {
		// check valid
		if (nominalToNumericMap.size() < this.nominals.size())
			throw new IllegalArgumentException("map not completed.");
		for (int i = 0; i < nominals.size(); i++)
			if (!nominalToNumericMap.containsKey(nominals.get(i)))
				throw new IllegalArgumentException("map not completed.");
		NumericAttribute attribute = new NumericAttribute(getName(),
				new IncrementVector(vector.size()));
		for (int i = 0; i < vector.size(); i++)
			attribute.vector.push(nominalToNumericMap.get(this.get(i)));
		return attribute;
	}

	/**
	 * 将属性转换为布尔属性
	 * 
	 * @return 转换后形成的布尔属性（由数值属性表示）集合
	 */
	public Attribute[] toBinary() {
		Attribute[] binaryAttributes = new NumericAttribute[nominals.size()];
		// add binary attribute
		int ss = 0;
		for (Iterator<Object> it = nominals.iterator(); it.hasNext();) {
			Object n = it.next();
			NumericAttribute att = new NumericAttribute(this.name + "=" + n.toString());
			binaryAttributes[ss++] = att;
		}
		// push elements (convert nominal to binary)
		for (int i = 0; i < this.vector.size(); i++) {
			if (isMissing(i)){
				for (int j=0; j<binaryAttributes.length; j++)
					binaryAttributes[j].getVector().push(0.);
				continue;
			}
			Object x = vector.at(i);
			int idx = nominals.indexOf(x);
			binaryAttributes[idx].getVector().push(1.);
			for (int j = 0; j < binaryAttributes.length; j++)
				if (j != idx)
					binaryAttributes[j].getVector().push(0.);
		}
		return binaryAttributes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see artiano.core.structure.Attribute#get(int)
	 */
	@Override
	public Object get(int i) {
		return this.vector.at(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see artiano.core.structure.Attribute#toArray()
	 */
	@Override
	public Object[] toArray() {
		Object[] array = new Object[this.vector.size()];
		for (int i = 0; i < array.length; i++)
			array[i] = get(i);
		return array;
	}

	public static void main(String[] args) {
		NominalAttribute att = new NominalAttribute("week");
		att.addNominal("sunday");
		att.addNominal("monday");
		att.addNominal("tuesday");

		// get value range
		List<Object> value = att.getNominals();
		System.out.println("value range:");
		for (int i = 0; i < value.size(); i++)
			System.out.print(value.get(i) + " ");
		System.out.println();

		// random generate
		Random r = new Random();
		List<Object> strings = att.getNominals();
		for (int i = 0; i < 10; i++) {
			int idx = r.nextInt(3);
			att.getVector().push(strings.get(idx));
		}
		System.out.println("random generated:");
		att.getVector().print();
		// push missing
		att.getVector().push(Attribute.MISSING_VALUE);
		System.out.println("after push ? -------------");
		att.getVector().print();
		System.out.println("has missing: " + att.hasMissing());
		System.out.println("is missing: "
				+ att.isMissing(att.getVector().size() - 1));
		System.out.println("missing count: " + att.countMissing());
		// convert to nominal
		Attribute[] atts = att.toBinary();
		System.out.println("convert to binary:");
		for (int i = 0; i < atts.length; i++)
			System.out.print(atts[i].getName() + "\t");
		System.out.println();
		for (int i = 0; i < att.getVector().size(); i++) {
			for (int j = 0; j < atts.length; j++)
				System.out.print(atts[j].getVector().at(i) + "\t\t");
			System.out.println();
		}
	}

}

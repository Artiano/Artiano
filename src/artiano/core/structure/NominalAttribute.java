/**
 * NominalAttribute.java
 */
package artiano.core.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * <p>表示符号属性</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-11-2
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class NominalAttribute extends Attribute {
	/** 存储符号属性的取值范围*/
	private List<String> nominals = new ArrayList<String>();
	
	public NominalAttribute(){
		this.type = "Nominal";
	}
	/**
	 * 构造一个符号属性
	 * @param name
	 */
	public NominalAttribute(String name){
		super(name);
		this.type = "Nominal";
	}
	public NominalAttribute(String name, IncrementVector vector){
		super(name, vector);
		this.type = "Nominal";
	}
	/**
	 * 添加一个符号取值
	 * @param nominal 待添加的取值
	 */
	public void addNominal(String nominal){
		if (!nominals.contains(nominal))
			nominals.add(nominal);
	}
	/**
	 * 移除一个符号取值
	 * @param nominal 待移除的符号取值
	 * @return 移除成功返回真，反之则反
	 */
	public boolean removeNominal(String nominal){
		return nominals.remove(nominal);
	}
	/**
	 * 获取属性中的符号取值
	 * @return
	 */
	public List<String> getNominals(){
		return this.nominals;
	}
	
	/**
	 * 将属性转换为布尔属性
	 * @return 转换后形成的布尔属性（由数值属性表示）集合
	 */
	public Attribute[] toBinary(){
		Attribute[] binaryAttributes = new NumericAttribute[nominals.size()];
		//add binary attribute
		int ss = 0;
		for (Iterator<String> it = nominals.iterator(); it.hasNext();){
			String n = it.next();
			NumericAttribute att = new NumericAttribute(this.name+"="+n);
			binaryAttributes[ss++] = att;
		}
		//push elements (convert nominal to binary)
		for (int i=0; i<this.vector.size(); i++){
			String x = (String) vector.at(i);
			int idx = nominals.indexOf(x);
			binaryAttributes[idx].getVector().push(1.);
			for (int j=0; j<binaryAttributes.length; j++)
				if (j != idx)
					binaryAttributes[j].getVector().push(0.);
		}
		return binaryAttributes;
	}
	/* (non-Javadoc)
	 * @see artiano.core.structure.Attribute#get(int)
	 */
	@Override
	public String get(int i) {
		return (String) this.vector.at(i);
	}
	
	public static void main(String[] args){
		NominalAttribute att = new NominalAttribute("week");
		att.addNominal("sunday");
		att.addNominal("monday");
		att.addNominal("tuesday");
		
		//get value range
		List<String> value = att.getNominals();
		System.out.println("value range:");
		for (int i=0; i<value.size(); i++)
			System.out.print(value.get(i)+" ");
		System.out.println();
		
		//random generate
		Random r = new Random();
		List<String> strings = att.getNominals();
		for (int i=0; i<10; i++){
			int idx = r.nextInt(3);
			att.getVector().push(strings.get(idx));
		}
		System.out.println("random generated:");
		att.getVector().print();
		//convert to nominal
		Attribute[] atts = att.toBinary();
		System.out.println("convert to binary:");
		for (int i=0; i<atts.length; i++)
			System.out.print(atts[i].getName()+"\t");
		System.out.println();
		for (int i=0; i<att.getVector().size(); i++){
			for (int j=0; j<atts.length; j++)
				System.out.print(atts[j].getVector().at(i)+"\t\t");
			System.out.println();
		}
	}
}










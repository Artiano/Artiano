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
	/**
	 * 归一化数据
	 */
	public void normalize(){
		double min = min();
		double max = max();
		for (int i=0; i<vector.size(); i++){
			double r = 2 * (get(i)- min)/(max - min) - 1;
			vector.set(i, r);
		}
	}
	/**
	 * 求取平均值
	 * @return
	 */
	public double mean(){
		double avg = 0;
		for (int i=0; i<this.vector.size(); i++)
			avg += get(i);
		avg /= this.vector.size();
		return avg;
	}
	/**
	 * 求取最大值
	 * @return
	 */
	public double max(){
		double m = get(0);
		for (int i=1; i<this.vector.size(); i++)
			if (m < get(i))
				m = get(i);
		return m;
	}
	/**
	 * 求取最小值
	 * @return
	 */
	public double min(){
		double m = get(0);
		for (int i=1; i<this.vector.size(); i++)
			if (m > get(i))
				m = get(i);
		return m;
	}
	/**
	 * 标准差
	 * @return 
	 */
	public double standardDeviation(){
		double stdDev = 0.;
		double mean = this.mean();
		for (int i=0; i<this.vector.size(); i++){
			double t = get(i) - mean;
			stdDev += t*t;
		}
		stdDev = Math.sqrt(stdDev);
		return stdDev;
	}
	/* (non-Javadoc)
	 * @see artiano.core.structure.Attribute#get(int)
	 */
	@Override
	public Double get(int i) {
		return (Double) this.vector.at(i);
	}
	/* (non-Javadoc)
	 * @see artiano.core.structure.Attribute#toArray()
	 */
	@Override
	public double[] toArray() {
		double[] array = new double[this.vector.size()];
		for (int i=0; i<array.length; i++)
			array[i] = ((Double)this.vector.at(i)).doubleValue();
		return array;
	}
}


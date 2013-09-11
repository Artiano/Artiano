package artiano.core.structure;
/***
 * 
 * @author BreezeDust
 *
 */
public class Domain {
	public double min=0;
	public double max=0;
	/***
	 *  定义域划分，以min=<value<max 的形式
	 * @param min 最小边界，区域包含min
	 * @param max 最大边界,区域部包含max
	 */
	public Domain(double min,double max){
		this.max=max;
		this.min=min;
	}
	/***
	 * 判断是否落在区域内
	 * @param value
	 * @return
	 */
	public int isIn(double value){
		if(value>=min && value<max) return 0;
		if(value<min) return -1;
		if(value>=max) return 1;
		return -2;
	}

}

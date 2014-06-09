/**
 * NumericAttribute.java
 */
package artiano.core.structure;

import java.util.Random;

import artiano.core.operation.OptionsHandler;

/**
 * <p>
 * 表示数值属性
 * </p>
 * 
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-10-30
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class NumericAttribute extends Attribute implements OptionsHandler {
	private static final long serialVersionUID = 5881525711605655997L;
	/** 属性值缺失替换 */
	public static final double MISSING_VALUE_REPLACE = 0.;
	/** 归一化到[-1,1] */
	public static final int NORMALIZE_MINMAX_N11 = 0;
	/** 归一化到[0,1] */
	public static final int NORMALIZE_MINMAX_01 = 1;
	/** 使用z-score归一 */
	public static final int NORMALIZE_ZSCORE = 2;
	/** 归一到指定最大值和最小值之间 */
	public static final int NORMALIZE_MIN_MAX = 3;

	/**
	 * 构造一个数值属性
	 */
	public NumericAttribute() {
		this.type = "Numeric";
	}

	/**
	 * 使用声明的名称构造一个数值属性
	 * 
	 * @param name
	 */
	public NumericAttribute(String name) {
		super(name);
		this.type = "Numeric";
	}

	public NumericAttribute(String name, IncrementVector vector) {
		super(name, vector);
		this.type = "Numeric";
	}

	/**
	 * 归一化数据
	 * 
	 * @param method
	 *            使用的归一化方法
	 */
	public void normalize(int method) {
		if (method == NORMALIZE_MINMAX_N11)
			normalizeByMinMaxN11();
		else if (method == NORMALIZE_MINMAX_01)
			normalizeByMinMax01();
		else if (method == NORMALIZE_ZSCORE)
			normalizeByZScore();
	}

	/**
	 * 使用属性值最大值最小值方法将属性值向量归一化到(-1,1)
	 */
	public void normalizeByMinMaxN11() {
		double min = min();
		double max = max();
		for (int i = 0; i < vector.size(); i++) {
			double r = 2 * (get(i) - min) / (max - min) - 1;
			vector.set(i, r);
		}
	}

	/**
	 * 使用属性值最大值最小值方法将属性值向量归一到(0,1)
	 */
	public void normalizeByMinMax01() {
		double min = min();
		double max = max();
		for (int i = 0; i < vector.size(); i++) {
			double r = (get(i) - min) / (max - min);
			vector.set(i, r);
		}
	}

	/**
	 * 使用属性值最大值最小值方法将属性值向量归一
	 */
	public void normalizeByMinMax() {
		double max = max();
		double min = min();
		double d = max - min;
		for (int i=0; i<vector.size(); i++) {
			double r = (get(i) - min)/d *
					(mNormMax - mNormMin) + mNormMin;
			vector.set(i, r);
		}
	}

	/**
	 * 使用z-score方法将属性值向量归一化
	 */
	public void normalizeByZScore() {
		double mean = mean();
		double stdDev = standardDeviation();
		for (int i = 0; i < vector.size(); i++) {
			double r = (get(i) - mean) / stdDev;
			vector.set(i, r);
		}
	}

	/**
	 * 求取平均值
	 * 
	 * @return
	 */
	public double mean() {
		double avg = 0;
		for (int i = 0; i < this.vector.size(); i++)
			if (!get(i).equals(MISSING_VALUE))
				avg += get(i);
		avg /= countNoneMissing();
		return avg;
	}

	/**
	 * 求取最大值
	 * 
	 * @return
	 */
	public double max() {
		double m = get(0);
		for (int i = 1; i < this.vector.size(); i++)
			if (m < get(i))
				m = get(i);
		return m;
	}

	/**
	 * 求取最小值
	 * 
	 * @return
	 */
	public double min() {
		double m = get(0);
		for (int i = 1; i < this.vector.size(); i++)
			if (m > get(i))
				m = get(i);
		return m;
	}

	/**
	 * 计算向量的方差
	 * 
	 * @return 当前向量的方差
	 */
	public double variance() {
		double var = 0;
		double mean = this.mean();
		for (int i = 0; i < size(); i++) {
			if (!isMissing(i)) {
				double t = get(i) - mean;
				var += t * t;
			}
		}
		var /= countNoneMissing() - 1;
		return var;
	}

	/**
	 * 标准差
	 * 
	 * @return
	 */
	public double standardDeviation() {
		return Math.sqrt(variance());
	}

	/**
	 * 转换为符号属性
	 * 
	 * @return
	 */
	public NominalAttribute toNominal() {
		NominalAttribute att = new NominalAttribute(this.getName());
		for (int i = 0; i < this.vector.size(); i++) {
			if (!isMissing(i))
				att.addNominal(this.vector.at(i));
			att.vector.push(this.vector.at(i));
		}
		return att;
	}

	@Override
	public void replaceMissing() {
		for (int i = 0; i < vector.size(); i++)
			if (isMissing(i))
				vector.set(i, MISSING_VALUE_REPLACE);
	}

	@Override
	public Double get(int i) {
		return (Double) this.vector.at(i);
	}
	
	public double[] toArray(boolean ingnoreMissing) {
		double[] d = new double[countNoneMissing()];
		int c = 0;
		for (int i = 0; i < size(); i++) {
			if (!isMissing(i)) {
				d[c] = get(i);
				c++;
			}
		}
		return d;
	}
	
	@Override
	public double[] toArray() {
		double[] d = new double[size()];
		for (int i = 0; i < d.length; i++) {
			d[i] = get(i);
		}
		return d;
	}

	public class NumericConverter extends AttributeConverter {

		public NumericConverter() {
		}

		@Override
		public String descriptionOfOptions() {
			return "<no options needed>";
		}

		@Override
		public Options supportedOptions() {
			return null;
		}

		@Override
		public boolean applyOptions(Options options) {
			return true;
		}

		@Override
		public Attribute[] convert(AttributeConvertion convertion) {
			if (!isConvertionSupported(convertion))
				return null;
			return new Attribute[] { toNominal() };
		}

		@Override
		public AttributeConvertion[] supportedConvertion() {
			AttributeConvertion[] convertions = new AttributeConvertion[1];
			convertions[0] = new AttributeConvertion(NominalAttribute.class,
					"convert to Nominal");
			return convertions;
		}

	}

	@Override
	public AttributeConverter getConverter() {
		return new NumericConverter();
	}

	private int mNormalizeMethod;
	private double mNormMax, mNormMin;
	
	private static final String KEY_METHOD = "method";
	private static final String KEY_MIN = "min";
	private static final String KEY_MAX = "max";

	private Options options = null;
	@Override
	public String descriptionOfOptions() {
		return "设置属性值归一方法，以便将属性值进行归一化处理。";
	}

	@Override
	public Options supportedOptions() {
		if (null != options)
			return options;
		options = new Options();
		// method
		Option option = new Option("Normalize-method", "进行归一化所使用的方法类型。其中，值["
				+ NORMALIZE_ZSCORE + "]使用最大z-score方法进行归一化，值["
				+ NORMALIZE_MIN_MAX + "]使用最大-最小值方法将属性值归一化到指定区间", new Integer[] {
				NORMALIZE_MIN_MAX, NORMALIZE_ZSCORE }, NORMALIZE_ZSCORE, true);
		options.put(KEY_METHOD, option);
		// min & max
		option = new Option(KEY_MIN, "当使用最大值最小值方法归一时使用的最小值", Double.class, 0, false);
		options.put(KEY_MIN, option);
		option = new Option(KEY_MAX, "当使用最大值最小值方法归一时使用的最大值", Double.class, 1, false);
		options.put(KEY_MAX, option);
		return options;
	}

	@Override
	public boolean applyOptions(Options options) {
		if (options == null)
			return false;
		mNormalizeMethod = (int) options.get(KEY_METHOD).value();
		mNormMax = (double) options.get(KEY_MAX).value();
		mNormMin = (double) options.get(KEY_MIN).value();
		return true;
	}
	
	public void normalize() {
		if (NORMALIZE_MIN_MAX == mNormalizeMethod)
			normalizeByMinMax();
		else if (NORMALIZE_ZSCORE == mNormalizeMethod)
			normalizeByZScore();
	}
	
	public static void main(String[] args) {
		NumericAttribute att = new NumericAttribute();
		Random r = new Random();
		for (int i = 0; i < 10; i++)
			att.getVector().push(r.nextInt(10));
		System.out.println("after push:");
		att.getVector().print();
		att.getVector().push(MISSING_VALUE);
		System.out.println("after push ?: ");
		att.getVector().print();
		System.out.println("mean: " + att.mean());
		System.out.println("max: " + att.max());
		System.out.println("min: " + att.min());
		System.out.println("variance: " + att.variance());
		// standard deviation
		System.out.println("standard deviation: " + att.standardDeviation());
	}

}

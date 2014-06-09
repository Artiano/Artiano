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
	private ArrayList<Object> nominals = new ArrayList<Object>();

	public NominalAttribute() {
		this.type = "Nominal";
	}

	/**
	 * 两个符号属性是否在同一个取值范围
	 * @param att 待测试属性
	 * @return
	 */
	public boolean inARange(NominalAttribute att) {
		refreshNominals();
		att.refreshNominals();
		for (int i = 0; i < nominals.size(); i++) {
			if (!att.nominals.contains(nominals.get(i)))
				return false;
		}
		return true;
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
	 * 获取属性中的符号取值的迭代器
	 * 
	 * @return
	 */
	public Iterator<Object> nominalsIterator() {
		refreshNominals();
		return this.nominals.iterator();
	}

	/**
	 * 获取属性向量中符号取值的数组
	 * 
	 * @return
	 */
	public Object[] nominalsArray() {
		refreshNominals();
		return this.nominals.toArray();
	}

	/**
	 * 获取向量中符号取值的列表
	 * 
	 * @return
	 */
	public ArrayList<Object> nominals() {
		refreshNominals();
		return new ArrayList<Object>(nominals);
	}

	/**
	 * 根据向量中的元素刷新符号取值（可选操作）
	 */
	public void refreshNominals() {
		for (int i = 0; i < vector.size(); i++)
			addNominal(vector.at(i));
	}

	/**
	 * 获取符号取值为nominal的个数
	 * 
	 * @param nominal
	 * @return
	 */
	public int countOfNominal(Object nominal) {
		int count = 0;
		for (int i = 0; i < vector.size(); i++) {
			if (nominal.equals(get(i)))
				count++;
		}
		return count;
	}
	
	public Object max() {
		Object[] n = nominalsArray();
		Object x = null;
		int max = -1;
		for (int i = 0; i < n.length; i++) {
			int c = countOfNominal(n[i]);
			if (max < countOfNominal(n[i])) {
				max = c;
				x = n[i];
			}
		}
		return x;
	}

	/**
	 * 使用指定字符串替换缺失值
	 * 
	 * @param missing
	 *            替换的值
	 */
	public void replaceMissing(String missing) {
		for (int i = 0; i < vector.size(); i++)
			if (isMissing(i))
				vector.set(i, missing);
	}

	/**
	 * 替换缺失值
	 */
	public void replaceMissing() {
		replaceMissing("?");
	}

	/**
	 * 将符号属性转换为数值属性
	 * 
	 * @param nominalToNumericMap
	 *            符号取值与数值取值之间的映射关系
	 * @return
	 */
	public NumericAttribute toNumeric(Map<Object, Double> nominalToNumericMap) {
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
		return toBinary(null);
	}
	
	public Attribute[] toBinary(HashMap<Object, Matrix> classMap) {
		int size = nominals.size();
		Attribute[] binaryAttributes = new NumericAttribute[size];
		// add binary attribute
		for (int i=0; i<size; i++) {
			Object n = nominals.get(i);
			NumericAttribute att = new NumericAttribute(this.name + "=" 
					+ n.toString());
			binaryAttributes[i] = att;
			if (null != classMap) {
				double[] d = new double[size];
				for (int j = 0; j < d.length; j++) {
					if (i == j)
						d[j] = 1.;
					else
						d[j] = 0.;
				}
				Matrix mat = new Matrix(1, nominals.size(), d);
				classMap.put(n, mat);
			}
		}
		// push elements (convert nominal to binary)
		for (int i = 0; i < this.vector.size(); i++) {
			if (isMissing(i)) {
				for (int j = 0; j < binaryAttributes.length; j++)
					binaryAttributes[j].getVector().push(0.);
				continue;
			}
			Object x = vector.at(i);
			int idx = nominals.indexOf(x);
			for (int j = 0; j < binaryAttributes.length; j++)
				binaryAttributes[j].getVector().push(0.);
			binaryAttributes[idx].getVector().set(i, 1.);
		}
		return binaryAttributes;
	}
	
	@Override
	public Object get(int i) {
		return this.vector.at(i);
	}
	
	public Object[] toArray(boolean ingnoreMissing) {
		Object[] array = new Object[countNoneMissing()];
		int c = 0;
		for (int i = 0; i < array.length; i++) {
			if (!isMissing(i)) {
				array[c] = get(i);
				c++;
			}
		}
		return array;
	}

	public class NominalConverter extends AttributeConverter {

		private HashMap<Object, Double> mNumNomMap;

		private static final String KEY_BEGINING = "begining";
		private static final String KEY_PATCH = "patch";

		private Options options = null;
		NominalConverter() {
		}

		@Override
		public String descriptionOfOptions() {
			StringBuilder builder = new StringBuilder();
			builder.append("设置符号属性和数值属性间的映射关系，以便将符号映射为数值。");
			return builder.toString();
		}

		@Override
		public Options supportedOptions() {
			if (null != options)
				return options;
			options = new Options();
			// begining value map to numeric
			Option option = new Option(KEY_BEGINING, "转换为数值属性开始值映射(default=0)",
					Double.class, 0, false);
			options.put(KEY_BEGINING, option);
			// patch value map to numeric
			option = new Option(KEY_PATCH, "转换为数值属性的间隔值映射(default=1)",
					Double.class, 1, false);
			options.put(KEY_PATCH, option);
			// for test
			option = new Option("test", "for test",
					new Boolean[] { true, false }, false, false);
			options.put("test", option);
			return options;
		}

		@Override
		public boolean applyOptions(Options options) {
			if (null == options)
				return false;
			// to numeric
			Option option = options.get(KEY_BEGINING);
			double mStart = (double) option.value();
			option = options.get(KEY_PATCH);
			double mPatch = (double) option.value();
			// map nominal to numeric
			mNumNomMap = new HashMap<>();
			double s = mStart;
			for (int i = 0; i < nominals().size(); i++) {
				mNumNomMap.put(nominals.get(i), s + i * mPatch);
			}
			return true;
		}

		@Override
		public AttributeConvertion[] supportedConvertion() {
			AttributeConvertion[] convertions = new AttributeConvertion[2];
			convertions[0] = new AttributeConvertion(NumericAttribute.class,
					"convert to Binary");
			convertions[0].setFlag(0);
			convertions[1] = new AttributeConvertion(NumericAttribute.class,
					"convert to Numeric");
			convertions[1].setFlag(1);
			return convertions;
		}

		@Override
		public Attribute[] convert(AttributeConvertion convertion) {
			if (!isConvertionSupported(convertion))
				return null;
			Attribute[] attr;
			if (0 == (int) convertion.getFlag(0))
				attr = toBinary();
			else
				attr = new Attribute[] { toNumeric(mNumNomMap) };
			return attr;
		}
	}

	@Override
	public AttributeConverter getConverter() {
		return new NominalConverter();
	}

	public static void main(String[] args) {
		NominalAttribute att = new NominalAttribute("week");
		att.addNominal("sunday");
		att.addNominal("monday");
		att.addNominal("tuesday");

		// get value range
		Iterator<?> value = att.nominalsIterator();
		System.out.println("value range:");
		while (value.hasNext())
			System.out.println(value.next() + " ");
		System.out.println();

		// random generate
		Random r = new Random();
		Object[] strings = att.nominalsArray();
		for (int i = 0; i < 10; i++) {
			int idx = r.nextInt(3);
			att.getVector().push(strings[idx]);
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
		// convert
		AttributeConverter converter = att.getConverter();
		AttributeConvertion convertion = converter.supportedConvertion()[0];
		Attribute[] atts = converter.convert(convertion);
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

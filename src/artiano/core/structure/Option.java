/**
 * Option.java
 */
package artiano.core.structure;

import java.util.HashMap;

/**
 * <p>
 * </p>
 * 
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2014-5-29
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class Option {

	public static final int TYPE_ASSIGNED = 1;
	public static final int VALUE_RANGE_ASSINGED = 2;
	public static final int OPTIONS_WITH_VALUE_ASSINGED = 4;
	public static final int NOTHING_ASSINGED = 3;
	
	private HashMap<Object, Object> mTags = new HashMap<>();
	/**
	 * 参数名
	 */
	private String mName;
	/**
	 * 参数描述
	 */
	private String mDescription;
	/**
	 * 参数是否是必须的
	 */
	private boolean mRequried;
	/**
	 * 指定参数类型，这种情况在参数值不可数的情况下使用，仅支持
	 * {@code Byte, Short, Integer, Long, Float, Double, Character}
	 */
	private Class<?> mType;
	/**
	 * 参数取值范围，在参数值可枚举的情况下使用
	 */
	private Object[] mValueRange;
	/**
	 * 参数值
	 */
	private Object mValue;
	/**
	 * 参数默认值
	 */
	private Object mDefValue;
	/**
	 * 当取值为某个值时的可选参数，如果设置了可选参数，表示参数是互斥的
	 */
	private HashMap<Object, Options> mOptionsWithValue;

	/**
	 * 指定参数类型构造一个参数
	 * 
	 * @param name
	 * @param desc
	 * @param type
	 *            指定参数类型
	 * @param defValue
	 * @param isRequried
	 */
	public Option(String name, String desc, Class<?> type, Object defValue,
			boolean isRequried) {
		init(name, desc, defValue, isRequried);
		mType = type;
	}

	/**
	 * 指定参数取值范围构造一个参数
	 * 
	 * @param name
	 * @param desc
	 * @param valueRange
	 *            指定参数取值范围
	 * @param defValue
	 * @param isRequried
	 */
	public Option(String name, String desc, Object[] valueRange,
			Object defValue, boolean isRequried) {
		init(name, desc, defValue, isRequried);
		mValueRange = valueRange;
	}

	private void init(String name, String desc, Object defValue,
			boolean isRequried) {
		mName = name;
		mDescription = desc;
		mRequried = isRequried;
		mDefValue = defValue;
	}

	/**
	 * 设置一个值对应的参数
	 * 
	 * @param value
	 * @param options
	 * @return
	 */
	public boolean putOptionsWithValue(Object value, Options options) {
		if (mOptionsWithValue == null)
			mOptionsWithValue = new HashMap<>();
		if (!isValueInRange(value))
			return false;
		mOptionsWithValue.put(value, options);
		return true;
	}

	/**
	 * 获取指定值的参数
	 * 
	 * @param value
	 * @return
	 */
	public Options getOptionsWithValue(Object value) {
		return mOptionsWithValue.get(value);
	}

	public interface OnOptionTraversalCallback {
		public void onTraversal(Option parent, Object parentValue, Option option);
		
		public void onTraversalComplete();
	}

	public static void traversalOption(Option option,
			OnOptionTraversalCallback callback) {
		if (null == callback) return;
		traversalOption(null, null, option, callback);
		callback.onTraversalComplete();
	}
	
	public static void traversalOption(Option parent, Object value, 
			Option option, OnOptionTraversalCallback callback) {
		callback.onTraversal(parent, value, option);
		if (OPTIONS_WITH_VALUE_ASSINGED != option.whatAssigned())
			return;
		for (int i = 0; i < option.mValueRange.length; i++) {
			Object v = option.mValueRange[i];
			Options options = option.getOptionsWithValue(v);
			if (null != options) {
				Object[] keys = options.getKeys();
				for (int j = 0; j < options.size(); j++)
					traversalOption(option, v, options.get(keys[j]), callback);
			}
		}
	}

	/**
	 * 参数值是否在值域内
	 * 
	 * @param value
	 * @return
	 */
	public boolean isValueInRange(Object value) {
		for (int i = 0; i < mValueRange.length; i++) {
			if (value.equals(mValueRange[i]))
				return true;
		}
		return false;
	}

	/**
	 * 哪种种参数被设置
	 * 
	 * @return
	 */
	public int whatAssigned() {
		if (null != mType) {
			return TYPE_ASSIGNED;
		}
		if (null != mValueRange && null != mOptionsWithValue) {
			return OPTIONS_WITH_VALUE_ASSINGED;
		}
		if (null != mValueRange) {
			return VALUE_RANGE_ASSINGED;
		}
		return NOTHING_ASSINGED;
	}

	/**
	 * 获取参数名
	 * 
	 * @return
	 */
	public String getName() {
		return mName;
	}
	
	public Object getTag(Object key) {
		return mTags.get(key);
	}
	
	public void setTag(Object key, Object tag) {
		mTags.put(key, tag);
	}

	/**
	 * 获取参数描述
	 * 
	 * @return
	 */
	public String getDescription() {
		return mDescription;
	}

	/**
	 * 获取参数类型
	 * 
	 * @return
	 */
	public Class<?> getType() {
		return mType;
	}

	/**
	 * 获取值域
	 * 
	 * @return
	 */
	public Object[] getValueRange() {
		return mValueRange;
	}

	/**
	 * 获取默认值
	 * 
	 * @return
	 */
	public Object defaultValue() {
		return mDefValue;
	}

	private boolean convertValue2SpecifiedType(String value) {
		if (mType.equals(Byte.class)) {
			try {
				Byte v = Byte.valueOf(value);
				mValue = v;
			} catch (Exception e) {
				return false;
			}
		} else if (mType.equals(Short.class)) {
			try {
				Short v = Short.valueOf(value);
				mValue = v;
			} catch (Exception e) {
				return false;
			}
		} else if (mType.equals(Integer.class)) {
			try {
				Integer v = Integer.valueOf(value);
				mValue = v;
			} catch (Exception e) {
				return false;
			}
		} else if (mType.equals(Long.class)) {
			try {
				Long v = Long.valueOf(value);
				mValue = v;
			} catch (Exception e) {
				return false;
			}
		} else if (mType.equals(Float.class)) {
			try {
				Float v = Float.valueOf(value);
				mValue = v;
			} catch (Exception e) {
				return false;
			}
		} else if (mType.equals(Double.class)) {
			try {
				Double v = Double.valueOf(value);
				mValue = v;
			} catch (Exception e) {
				return false;
			}
		} else if (mType.equals(Character.class)) {
			if (value.length() > 1)
				return false;
			char c = value.charAt(0);
			try {
				Character v = Character.valueOf(c);
				mValue = v;
			} catch (Exception e) {
				return false;
			}
		} else if (mType.equals(String.class)) {
			mValue = value;
		} else
			return false;
		return true;
	}

	/**
	 * 设置参数值
	 * 
	 * @param value
	 * @return
	 */
	public boolean setValue(Object value) {
		int assigned = whatAssigned();
		if (TYPE_ASSIGNED == assigned) {
			if (!(value instanceof String))
				return false;
			return convertValue2SpecifiedType((String) value);
		}
		if (VALUE_RANGE_ASSINGED == assigned || OPTIONS_WITH_VALUE_ASSINGED == assigned) {
			for (int i = 0; i < mValueRange.length; i++) {
				if (value.equals(mValueRange[i])) {
					mValue = value;
					return true;
				}
			}
			return false;
		}
		return false;
	}

	/**
	 * 获取参数值
	 * 
	 * @return
	 */
	public Object value() {
		if (null == mValue)
			return mDefValue;
		return mValue;
	}

	/**
	 * 是否必须
	 * 
	 * @return
	 */
	public boolean isRequired() {
		return mRequried;
	}
	
	private static void buildString(Option parent, Object value, StringBuilder builder, Option option) {
		if (null != parent) {
			builder.append("\t<while ");
			builder.append(parent.getName()+"=");
			builder.append(value);
			builder.append("> ");
		}
		builder.append(option.getName());
		builder.append(" : ");
		if (option.isRequired())
			builder.append("(REQURIED) ");
		int w = option.whatAssigned();
		if (TYPE_ASSIGNED == w) {
			builder.append("[type:");
			builder.append(option.mType.getName());
			builder.append("]");
		} else if (VALUE_RANGE_ASSINGED == w || OPTIONS_WITH_VALUE_ASSINGED == w) {
			builder.append("[supported values:");
			for (int i = 0; i < option.mValueRange.length; i++) {
				builder.append(option.mValueRange[i]);
				if (i != option.mValueRange.length - 1)
					builder.append(",");
			}
			builder.append("]");
		}
		builder.append(option.getDescription());
		if (null != option.defaultValue())
			builder.append("(default=" + option.defaultValue() + ")");
		builder.append("\n");
	}
	
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		traversalOption(this, new OnOptionTraversalCallback() {
			
			@Override
			public void onTraversal(Option parent, Object value, Option option) {
				buildString(parent, value, builder, option);
			}
			
			@Override
			public void onTraversalComplete() {
				
			}
		});
		return builder.toString();
	}

	public static void main(String[] args) {
		Option option = new Option("A", "option A", new Integer[] { 1, 2, 3 }, 2, false);
		Options options = new Options();
		options.put("a", new Option("a", "option a", Boolean.class, false, false));
		options.put("b", new Option("b", "option b", Integer.class, 1, false));
		options.put("c", new Option("c", "option c", Boolean.class, false, false));
		option.putOptionsWithValue(1, options);
		Option.traversalOption(option, new OnOptionTraversalCallback() {

			@Override
			public void onTraversal(Option parent, Object value, Option option) {
				String str = "";
				if (parent != null)
					str += parent.getName()+"="+value+" ";
				str += option.getName();
				System.out.println(str);
			}
			
			@Override
			public void onTraversalComplete() {
				
			}
			
		});
		System.out.println(option.toString());
	}

}

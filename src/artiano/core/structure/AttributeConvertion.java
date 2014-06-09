/**
 * AttributeConvertion.java
 */
package artiano.core.structure;

/**
 * <p></p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2014-5-29
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class AttributeConvertion {
	
	private Class<?> mType;
	
	private String mDescription;
	
	private Object mFlag;
	
	public AttributeConvertion(Class<?> type, String desc) {
		mType = type;
		mDescription = desc;
	}
	
	public void setFlag(Object flag) {
		mFlag = flag;
	}
	
	public Object getFlag(int defValue) {
		if (null == mFlag)
			return defValue;
		return mFlag;
	}
	
	public Class<?> getType() {
		return mType;
	}
	
	public String getDescription() {
		return mDescription;
	}
	
	public boolean isSupported(Class<?> attr) {
		return mType.equals(attr);
	}
	
	@Override
	public String toString() {
		String str = ""+mType.getSimpleName()+":"+mDescription;
		return str;
	}
	
}

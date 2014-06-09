/**
 * Options.java
 */
package artiano.core.structure;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p></p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2014-5-29
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class Options extends HashMap<String, Option> {
	
	private static final long serialVersionUID = 6837265900442299726L;
	
	public Object[] getKeys() {
		ArrayList<String> keys = new ArrayList<>(keySet());
		return keys.toArray();
	}
	
	@Override
	public String toString() {
		ArrayList<String> keys = new ArrayList<>(keySet());
		StringBuilder builder = new StringBuilder();
		builder.append("options:[");
		for (int i=0; i<keys.size(); i++) {
			Option option = get(keys.get(i));
			builder.append(option.getName());
			builder.append("=");
			if (option.value() != null) {
				builder.append(option.value());
			} else if (option.defaultValue() != null) {
				builder.append(option.defaultValue());
			} else {
				builder.append("<none>");
			}
			if (i == keys.size()-1)
				builder.append("]");
			else
				builder.append(",");
		}
		return builder.toString();
	}
	
}

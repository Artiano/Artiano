/**
 * Associator.java
 */
package artiano.ml.association;

import artiano.core.structure.Table;
import artiano.ml.association.structure.AssociationModel;

/**
 * <p></p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2014-6-4
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public abstract class Associator {
	
	public abstract AssociationModel getPattern(Table records);
	
}

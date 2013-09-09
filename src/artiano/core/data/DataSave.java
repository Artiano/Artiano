package artiano.core.data;

import artiano.core.structure.Matrix;
/***
 * 
 * @author BreezeDust
 * 
 *
 */
public interface DataSave{
	/***
	 * 
	 * @param mx
	 * @param fileName
	 * @param fieldNames
	 * @return
	 */
	boolean save(Matrix mx,String fileName,String[] fieldNames);
	/***
	 * 
	 * @param fileName
	 * @return Matrix
	 */
	Matrix get(String fileName);

}

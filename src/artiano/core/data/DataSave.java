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
	 * @param mx 矩阵
	 * @param fileName 保存的路径和文件名
	 * @param fieldNames 字段描述
	 * @return
	 */
	boolean save(Matrix mx,String fileName,String[] fieldNames);
	/***
	 * 
	 * @param fileName 保存的路径和文件名
	 * @return Matrix 
	 */
	Matrix load(String fileName);

}

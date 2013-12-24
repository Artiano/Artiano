/**
 * Validator.java
 */
package artiano.ml.classifier;

/**
 * <p>
 * </p>
 * 
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-12-22
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class Validator {
	
	public class ValidateResult {
		/** 全部实例数 */
		private int totalInstances = 0;
		/** 全部测试集 */
		private int totalTest = 0;
		/** 分类正确实例数 */
		private int incorrectNum = 0;
		/** 未分类正确数 */
		private int correctNum = 0;
		/** 混合矩阵 */
		private int[][] confusionMatrix = null;
		
	}
}

package artiano.core.data;

import artiano.core.structure.Matrix;

public class test {
	public static void main(String[] args){
		double[] a={
				1,2,3,7,8,
				4,5,6,9,0,
				1,2,3,4,5,
				0,0,0,0,0
		};
		//把结果保存为Matrix
		Matrix A=new Matrix(4,5,a);
		DataSave saveBLL=DataSaveFactory.createDataSave();//在这里隔离数据层
		//定义每列的含义
		String[] names={"a","b","c","d","e"};
		//持久化保存
		saveBLL.save(A, "a.txt",names);
		
		//获取持久化数据
		Matrix b=saveBLL.load("a.txt");
		b.print();
	}

}

/**
 * Table.java
 */
package artiano.core.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <p>基本数据结构表，由一系列列属性组成。</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-10-28
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class Table {	
	/** 存放属性的向量列表 */
	private List<Attribute> attributes = new ArrayList<Attribute>();
	/** 表的行数 */
	private int rows = 0;
	/** 行索引，用于快速访问以及重采样共享数据 */
	private IncrementIndex index = new IncrementIndex();
	/**	表的容量（当前最大行数） */
	private int capacity = IncrementVector.DEFAULT_CAPACITY;
	
	/**
	 * 构造一个表
	 */
	public Table(){	}
	/**
	 * 使用声明的属性列表构造一个表
	 * @param attributes
	 */
	public Table(List<Attribute> attributes){
		this.attributes = attributes;
		int minSize = 0;
		int minCapacity = 0;
		//find minimal size of the attributes as the rows of the table
		for (int i=0; i<columns(); i++){
			if (minSize>attributes.get(i).getVector().size())
				minSize = attributes.get(i).getVector().size();
			if (minCapacity>attributes.get(i).getVector().capacity())
				minCapacity = attributes.get(i).getVector().capacity();
		}
		this.rows = minSize;
		this.capacity = minCapacity;
		//add referenced
		//initialize row index (full reference)
		for (int i=0; i<rows; i++)
			index.push(i);
	}
	/**
	 * 获取索引
	 * @return
	 */
	public IncrementIndex getIndex(){
		return this.index;
	}
	/**
	 * 获取表的容量
	 * @return
	 */
	public int capacity(){
		return this.capacity;
	}
	/**
	 * 获取表的行数
	 * @return 表的行数
	 */
	public int rows(){
		return rows;
	}
	/**
	 * 获取表的列数（属性个数）
	 * @return 表的列数
	 */
	public int columns(){
		return attributes.size();
	}
	/**
	 * 在表中增加一个属性
	 * @param attribute 声明的属性
	 */
	public void addAttribute(Attribute attribute){
		attributes.add(attribute);
		//update rows & capacity & indices
		//if the first time add
		if (rows == 0){
			rows = attribute.getVector().size();
			for (int i=0; i<rows; i++)
				index.push(i);
			return;
		}
		if (rows > attribute.getVector().size())
			this.rows = attribute.getVector().size();
		if (capacity > attribute.getVector().capacity())
			this.capacity = attribute.getVector().capacity();
	}
	/**
	 * 在表后附加一系列属性
	 * @param attributes
	 */
	public void addAttributes(Attribute[] attributes){
		for (int i=0; i<attributes.length; i++){
			this.attributes.add(attributes[i]);
		}
	}
	/**
	 * 从表中移除属性
	 * @param i 声明的下标
	 * @return 被移除的属性
	 */
	public Attribute removeAttribute(int i){
		return attributes.remove(i);
	}
	/**
	 * 获取表在下标i处的属性
	 * @param i 声明的下标
	 */
	public Attribute attribute(int i){
		return attributes.get(i);
	}
	/**
	 * 创建一个行
	 * @return 新创建的行
	 */
	public TableRow createRow(){
		TableRow row = new TableRow();
		return row;
	}
	/**
	 * 在表后附加一个表
	 * @param table 待附加的表
	 */
	public void append(Table table){
		for (int i=0; i<table.columns(); i++)
			attributes.get(i).getVector().append(table.attributes.get(i).getVector());
		//update index
		for (int i=0; i<table.rows; i++)
			index.push(rows+i);
		//update rows
		rows += table.rows;
	}
	/**
	 * 在表后附加一行
	 * @param row 待附加的行
	 */
	public void push(TableRow row){
		for (int i=0; i<columns(); i++)
			attributes.get(i).getVector().push(row.at(i));
		index.push(rows++);
	}
	/**
	 * 在表后附加一行
	 * @param objects 待附加的行
	 */
	public void push(Object...objects){
		for (int i=0; i<columns(); i++)
			attributes.get(i).getVector().push(objects[i]);
		index.push(rows++);
	}
	/**
	 * 获取表在下标i，j处的值
	 * @param i 行下标
	 * @param j 列下标
	 * @return 表在i，j处的值
	 */
	public Object at(int i, int j){
		return attributes.get(j).getVector().at(index.at(i));
	}
	/**
	 * 将表按列分割成多个表
	 * @return
	 */
	public Table[] split(Range[] ranges){
		//check valid
		for (int i=0; i<ranges.length; i++){
			if (ranges[i].end() > columns())
				throw new IllegalArgumentException("range out of bound.");
			for (int j=i+1; j<ranges.length; j++){
				if (ranges[i].isCross(ranges[j]))
					throw new IllegalArgumentException("range is crossover");
			}
		}
		//split
		Table[] t = new Table[ranges.length];
		for (int i=0; i<t.length; i++){
			t[i] = new Table();
			t[i].rows = rows;
			t[i].index = this.index;
			for (int j=ranges[i].begin(); j<ranges[i].end(); j++)
				t[i].addAttribute(attributes.get(j));
		}
		return t;
	}
	/**
	 * 随机（均匀分布）重采样表数据
	 * <br><b><i>NOTICE:</i></b> 重采样后形成的表和原来的表共享属性向量的数据，也就是说，
	 * 重采样后的表并不会开辟新的存储空间。
	 * @param percent 重采样数据的百分比
	 * @return
	 */
	public Table[] resample(double percent){
		//create a new table, share the attribute values
		Table t[] = new Table[2];
		t[0] = new Table();
		t[1] = new Table();
		t[0].attributes = this.attributes;
		t[1].attributes = this.attributes;
		//new indices-table & new rows to set
		int newRows = (int)((double)rows*percent);
		newRows = newRows==0 ? 1 : newRows;
		int newRows2 = rows - newRows;
		//choose the smaller to randomize
		int count = Math.min(newRows, newRows2);
		IncrementIndex newIndex = new IncrementIndex(count);
		//random select
		Random r = new Random(System.currentTimeMillis());
		IncrementIndex index_t = this.index.copy();
		for (int i=0; i<count;){
			int idx = r.nextInt(rows);
			if (index_t.at(idx) != IncrementIndex.NOT_INDEX && !newIndex.contains(index.at(idx))){
				newIndex.push(index.at(idx));
				//not an index
				index_t.set(idx, IncrementIndex.NOT_INDEX);
				i++;
			}
		}
		//another indices-table
		IncrementIndex newIndex2 = new IncrementIndex(rows-count);
		for (int i=0; i<rows; i++){
			if (index_t.at(i) != IncrementIndex.NOT_INDEX)
				newIndex2.push(index.at(i));
		}
		t[0].rows = newRows;
		t[1].rows = newRows2;
		//reset the indices
		if (newRows > count){
			//set t & t1
			t[0].index = newIndex2;
			t[1].index = newIndex;
		} else {
			//set t & t1;
			t[0].index = newIndex;
			t[1].index = newIndex2;
		}
		return t;
	}
	/**
	 * 将表转换为矩阵
	 * @return
	 */
	public Matrix toMatrix(){
		//check valid
		for (int i=0; i<attributes.size(); i++)
			if (!(attributes.get(i) instanceof NumericAttribute))
				throw new UnsupportedOperationException("only numeric attribute supported where convert " +
						"table to matrix.");
		//convert to matrix
		Matrix matrix = new Matrix(rows, columns());
		for (int i = 0; i<rows; i++)
			for (int j=0; j<columns(); j++)
				matrix.set(i, j, (double)at(i, j));
		return matrix;
	}
	/**
	 * 获取表在下标i处的行
	 * @param i 声明的下标
	 * @return
	 */
	public TableRow row(int i){
		TableRow tableRow = createRow();
		for (int j=0; j<columns(); j++)
			tableRow.set(j, at(i,j));
		return tableRow;
	}
	/**
	 * 辅助方法，将表中所有元素打印到控制台
	 */
	public void print(){
		//print header
		for (int i=0; i<columns(); i++)
			System.out.print(attributes.get(i).getType()+"\t\t");
		System.out.println();
		for (int i=0; i<columns(); i++)
			System.out.print("<"+attributes.get(i).getName()+">\t\t");
		System.out.println();
		for (int i=0; i<rows; i++){
			for (int j=0; j<columns(); j++)
				System.out.print(at(i, j)+"\t\t");
			System.out.println();
		}
	}
	/**
	 * <p>表的一行，作为临时对象使用</p>
	 * @author Nano.Michael
	 * @version 1.0.0
	 * @date 2013-10-30
	 * @author (latest modification by Nano.Michael)
	 * @since 1.0.0
	 */
	public class TableRow{
		/**	表的一行 */
		Object[] row = new Object[columns()];
		/** 只能在Table中构造行 */
		TableRow(){	}
		/**
		 * 获取行的大小
		 * @return
		 */
		public int size(){
			return columns();
		}
		/**
		 * 获取行在下标i处的值
		 * @param i 声明的下标
		 * @return 行在下标i处的值
		 */
		public Object at(int i){
			return row[i];
		}
		/**
		 * 设置行在下标i处的值
		 * @param i 声明的下标
		 * @param value 待设置的值
		 */
		public void set(int i, Object value){
			row[i] = value;
		}
		/**
		 * 设置一行的值
		 * @param objects 待设置的值
		 */
		public void set(Object... objects){
			for (int i=0; i<columns(); i++)
				row[i] = objects[i];
		}
		/**
		 * 辅助方法，将行打印到控制台
		 */
		public void print(){
			for (int i=0; i<columns(); i++)
				System.out.print(at(i)+" ");
			System.out.println();
		}
	}
	
	public static void main(String[] args){
		Table table = new Table();
		//add attribute
		table.addAttribute(new NumericAttribute("Length"));
		table.addAttribute(new NumericAttribute("Width"));
		System.out.println("add attributes---------------");
		System.out.println(table.attribute(0).getName()+" "+ table.attribute(1).getName());
		//capacity & size & columns
		System.out.println("capacity:"+table.capacity());
		System.out.println("size:"+table.rows());
		System.out.println("columns:"+table.columns());
		//push
		table.push(10,10);
		table.push(20,20);
		System.out.println("after push 2 rows--------------- rows="+table.rows());
		table.print();
		//row
		System.out.println("row at 1:");
		TableRow row = table.row(1);
		row.print();
		//append
		Table table2 = new Table();
		table2.addAttribute(new NumericAttribute("Length"));
		table2.addAttribute(new NumericAttribute("Weight"));
		table2.push(100,100);
		table2.push(200,200);
		System.out.println("table2:"+table2.rows());
		table2.print();
		table.append(table2);
		System.out.println("after append------------------");
		table.print();
		//remove attribute
		Attribute attribute = table.removeAttribute(0);
		System.out.println("remove attribute at 0:");
		table.print();
		
		//resample
		table.addAttribute(attribute);
		Random r = new Random();
		for (int i=0; i<100; i++){
			table.push(r.nextInt(200), r.nextInt(400));
		}
		System.out.println("random generated attributes--------------");
		System.out.println("indices:");
		table.getIndex().print();
		System.out.println("elements:");
		table.print();
		//resample (10%)
		Table[] t = table.resample(0.1);
		System.out.println("after resampled (10%)--------------------");
		System.out.println("indices (sorted):");
		t[0].getIndex().sort();
		t[0].getIndex().print();
		System.out.println("elements:");
		t[0].print();
		System.out.println("remainded-----------------------");
		System.out.println("indices:");
		t[1].getIndex().print();
		System.out.println("rows="+table.rows());
		t[1].print();
		//split
		Table[] tables = table.split(new Range[]{new Range(0, 1)});
		System.out.println("after split:");
		tables[0].print();
		//to matrix
		Matrix x = table.toMatrix();
		System.out.println("to matrix:");
		x.print();
	}

}





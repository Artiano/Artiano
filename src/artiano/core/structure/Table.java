/**
 * Table.java
 */
package artiano.core.structure;

import java.io.Serializable;
import java.util.*;

/**
 * <p>
 * 基本数据结构表，由一系列列属性组成。
 * </p>
 * 
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-10-28
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class Table implements Serializable, Iterable<Table.TableRow> {
	private static final long serialVersionUID = -375665745092267557L;

	/** 表名 */
	private String name = "";
	/** 存放属性的向量列表 */
	private List<Attribute> attributes = new ArrayList<Attribute>();
	/** 表的行数 */
	private int rows = 0;
	/** 行索引，用于快速访问以及重采样共享数据 */
	private IncrementIndex index = new IncrementIndex();
	/** 类属性 */
	private Attribute classAttribute = null;

	/**
	 * <p>
	 * 表的一行，作为临时对象使用
	 * </p>
	 * 
	 * @author Nano.Michael
	 * @version 1.0.0
	 * @date 2013-10-30
	 * @author (latest modification by Nano.Michael)
	 * @since 1.0.0
	 */
	public class TableRow {
		/** 表的一行 */
		Object[] row = new Object[columns()];
		Table mOwner;

		/** 只能在Table中构造行 */
		public TableRow() {
		}
		
		public void setOwner(Table owner) {
			mOwner = owner;
		}

		public Table getOwner() {
			return mOwner;
		}
		
		/**
		 * 获取行的大小
		 * 
		 * @return
		 */
		public int size() {
			return columns();
		}

		/**
		 * 得到当前行的类属性值
		 * 
		 * @return 当前行的类属性值
		 */
		public Object classValue() {
			if (!hasClass())
				throw new UnsupportedOperationException("class not set!");
			return at(classIndex());
		}

		/**
		 * 获取行在下标i处的值
		 * 
		 * @param i
		 *            声明的下标
		 * @return 行在下标i处的值
		 */
		public Object at(int i) {
			return row[i];
		}

		/**
		 * 行中是否缺失数据
		 * 
		 * @return
		 */
		public boolean hasMissing() {
			for (int i = 0; i < columns(); i++)
				if (at(i).equals(Attribute.MISSING_VALUE))
					return true;
			return false;
		}

		/**
		 * 行中第i个元素是否缺失
		 * 
		 * @param i
		 * @return
		 */
		public boolean isMissing(int i) {
			return at(i).equals(Attribute.MISSING_VALUE);
		}

		/**
		 * 设置行在下标i处的值
		 * 
		 * @param i
		 *            声明的下标
		 * @param value
		 *            待设置的值
		 */
		public void set(int i, Object value) {
			row[i] = value;
		}

		/**
		 * 设置一行的值
		 * 
		 * @param objects
		 *            待设置的值
		 */
		public void set(Object... objects) {
			for (int i = 0; i < columns(); i++)
				row[i] = objects[i];
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof TableRow))
				return false;
			TableRow row = (TableRow) obj;
			for (int i = 0; i < size(); i++) {
				if (!row.at(i).equals(at(i)))
					return false;
			}
			return true;
		}

		/**
		 * 辅助方法，将行打印到控制台
		 */
		public void print() {
			for (int i = 0; i < columns(); i++)
				System.out.print(at(i) + " ");
			System.out.println();
		}
	}

	/**
	 * 两个实例之间的距离
	 * 
	 * @param i
	 *            实例i
	 * @param j
	 *            实例j
	 * @return
	 */
	public double distanceOf(int i, int j) {
		TableRow rowI = row(i);
		TableRow rowJ = row(j);
		return distanceOf(rowI, rowJ);
	}
	
	public static double distanceOf(TableRow r1, TableRow r2) {
		double d = 0, a = 0, b = 0;
		TableRow rowI = r1;
		TableRow rowJ = r2;
		Table owner = r1.getOwner();
		for (int k = 0; k < rowI.size(); k++) {
			if (!rowI.isMissing(k) && !rowJ.isMissing(k)) {
				b += 1;
				Attribute att = owner.attribute(k);
				if (att instanceof NumericAttribute) {
					d = ((double) rowI.at(k) - (double) rowJ.at(k));
					d = Math.abs(d);
					NumericAttribute nAtt = (NumericAttribute) att;
					d /= nAtt.max() - nAtt.min();
				} else {
					if (rowI.at(k).equals(rowJ.at(k)))
						d = 1;
					else
						d = 0;
				}
				a += d;
			}
		}
		return 1.d - (a / b);
	}

	public class TableIterator implements Iterator<TableRow> {
		/** 计数器 */
		private int counter = 0;
		/** 待迭代的表 */
		private Table t = null;

		/**
		 * 构造一个表迭代器
		 * 
		 * @param table
		 *            待迭代的表
		 */
		public TableIterator(Table table) {
			t = table;
		}

		@Override
		public boolean hasNext() {
			return counter < t.rows();
		}

		@Override
		public TableRow next() {
			return t.row(counter++);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * 构造一个空表
	 */
	public Table() {
	}

	/**
	 * 使用指定名称构造一个表
	 * 
	 * @param name
	 *            指定名称
	 */
	public Table(String name) {
		this.name = name;
	}

	/**
	 * 使用声明的属性列表构造一个表
	 * 
	 * @param attributes
	 */
	public Table(List<Attribute> attributes, String name) {
		this.name = name;
		this.attributes = attributes;
		int minSize = 0;
		// find minimal size of the attributes as the rows of the table
		for (int i = 0; i < columns(); i++) {
			if (minSize > attributes.get(i).getVector().size())
				minSize = attributes.get(i).getVector().size();
		}
		this.rows = minSize;
		// initialize row index (full reference)
		for (int i = 0; i < rows; i++)
			index.push(i);
	}

	/**
	 * 使用矩阵构造一个表(只适用于数据为数值型的情况)
	 * 
	 * @param data
	 */
	public Table(Matrix data) {
		int rows = data.rows();
		int columns = data.columns();
		attributes = new ArrayList<Attribute>();
		for (int i = 0; i < columns; i++) {
			attributes.add(new NumericAttribute());
		}

		for (int j = 0; j < columns; j++) {
			IncrementVector vector = new IncrementVector(rows);
			for (int i = 0; i < rows; i++) {
				vector.push(data.at(i, j));
			}
			Attribute attr = new NumericAttribute(String.valueOf(j), vector);
			attributes.set(j, attr);
		}
		this.rows = rows;
		this.name = "@Matrix";
		// initialize row index (full reference)
		for (int i = 0; i < rows; i++)
			index.push(i);
		// trim
		trim();
	}
	
	public void trim() {
		for (int i = 0; i < columns(); i++)
			attribute(i).getVector().trim();
	}
	
	/**
	 * 判断与指定表{@code t}是否兼容。所谓兼容，指的是两个表的属性向量的类型依次是否相同
	 * 
	 * @param t
	 *            指定表
	 * @return 如果兼容则返回{@code true}
	 */
	public boolean compatible(Table t) {
		if (columns() != t.columns())
			return false;
		for (int i = 0; i < columns(); i++)
			if (!t.attribute(i).getClass().equals(attribute(i).getClass()))
				return false;
		return true;
	}

	/**
	 * 设置表名
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取表名
	 * 
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 获取索引
	 * 
	 * @return
	 */
	public IncrementIndex indices() {
		return this.index;
	}
	
	public int rowRelativeOriginal(int i) {
		return index.at(i);
	}

	/**
	 * 获取表的行数
	 * 
	 * @return 表的行数
	 */
	public int rows() {
		return rows;
	}

	/**
	 * 获取表的列数（属性个数）
	 * 
	 * @return 表的列数
	 */
	public int columns() {
		return attributes.size();
	}

	/**
	 * 判断表是否为空
	 * 
	 * @return 若为空返回{@code true}
	 */
	public boolean isEmpty() {
		return rows == 0;
	}
	
	/**
	 * 在表中增加一个属性
	 * 
	 * @param attribute
	 *            声明的属性
	 */
	public void addAttribute(Attribute attribute) {
		attributes.add(attribute);
		// update rows & indices
		// if the first time add
		if (rows == 0) {
			rows = attribute.getVector().size();
			for (int i = 0; i < rows; i++)
				index.push(i);
			return;
		}
		if (rows > attribute.getVector().size())
			this.rows = attribute.getVector().size();
	}

	/**
	 * 在表后附加一系列属性
	 * 
	 * @param attributes
	 */
	public void addAttributes(Attribute[] attributes) {
		for (int i = 0; i < attributes.length; i++) {
			this.attributes.add(attributes[i]);
		}
		// find minimal size of the attributes as the rows of the table
		int minSize = Integer.MAX_VALUE;
		for (int i = 0; i < columns(); i++) {
			if (minSize > this.attributes.get(i).getVector().size())
				minSize = this.attributes.get(i).getVector().size();
		}
		// if the first time add
		if (rows == 0) {
			rows = minSize;
			for (int i = 0; i < rows; i++)
				index.push(i);
			return;
		}
		if (rows > minSize)
			this.rows = minSize;
	}
	
	public void addAttributes(Collection<Attribute> attrs) {
		for (Iterator<Attribute> iterator = attrs.iterator(); iterator.hasNext();) {
			Attribute attribute = (Attribute) iterator.next();
			addAttribute(attribute);
		}
	}

	/**
	 * 从表中移除属性
	 * 
	 * @param j
	 *            声明的下标
	 * @return 被移除的属性
	 */
	public Attribute removeAttribute(int j) {
		Attribute att = attributes.remove(j);
		if (columns() == 0)
			rows = 0;
		return att;
	}

	/**
	 * 移除属性名称为{@code name}的属性
	 * 
	 * @param name
	 *            指定属性名称
	 * @return 被移除的属性
	 */
	public Attribute removeAttribute(String name) {
		for (int i = 0; i < columns(); i++) {
			Attribute att = attribute(i);
			if (att.getName().equals(name)) {
				removeAttribute(i);
				return att;
			}

		}
		return null;
	}

	/**
	 * 移除多个属性
	 * 
	 * @param names
	 *            待移除的属性名称
	 * @return
	 */
	public ArrayList<Attribute> removeAttributes(String[] names) {
		ArrayList<Attribute> attrs = new ArrayList<>();
		for (int i = 0; i < names.length; i++) {
			Attribute att = removeAttribute(names[i]);
			if (null != att)
				attrs.add(att);
		}
		return attrs;
	}

	/**
	 * 移除多个属性
	 * 
	 * @param idxs
	 *            待移除的属性下标
	 * @return
	 */
	public ArrayList<Attribute> removeAttributes(int[] idxs) {
		ArrayList<Attribute> attrs = new ArrayList<>();
		Arrays.sort(idxs);
		for (int i = 0; i < idxs.length; i++) {
			Attribute att = removeAttribute(idxs[i]);
			if (null != att)
				attrs.add(att);
			for (int j = 0; j < idxs.length; j++)
				// decrease
				idxs[j]--;
		}
		return attrs;
	}

	/**
	 * 获取表在下标i处的属性
	 * 
	 * @param i
	 *            声明的下标
	 * @return 指定属性
	 */
	public Attribute attribute(int i) {
		return attributes.get(i);
	}

	/**
	 * 依据属性名称获取表属性<br>
	 * <b><i>NOTICE:</b></i><br>
	 * <ul>
	 * <li>如果表中存在多个名称为"name"的属性，则返回第一个名称为"name"的属性</li>
	 * <li>如果表中不存在名称为"name"的属性，则返回{@code null}</li>
	 * </ul>
	 * 
	 * @param name
	 *            属性名称
	 * @return 指定属性
	 */
	public Attribute attribute(String name) {
		for (Iterator<Attribute> it = attributesIterator(); it.hasNext();) {
			Attribute att = it.next();
			if (att.getName().equals(name))
				return att;
		}
		return null;
	}

	/**
	 * 得到属性{@code att}在表中的下标<br>
	 * <b><i>NOTICE:</b></i>
	 * <ul>
	 * <li>若表中存在指定属性，则返回此属性在表中的下标</li>
	 * <li>若表中不存在指定属性，则返回{@code -1}</li>
	 * </ul>
	 * 
	 * @param att
	 *            指定属性
	 * @return 属性在表中的下标
	 */
	public int indexOf(Attribute att) {
		return attributes.indexOf(att);
	}

	/**
	 * 得到名称为{@code name}的属性在表中的下标<br>
	 * <b><i>NOTICE:</b></i>
	 * <ul>
	 * <li>若表中存在指定属性，则返回此属性在表中的下标</li>
	 * <li>若表中不存在指定属性，则返回{@code -1}</li>
	 * </ul>
	 * 
	 * @param name
	 *            指定属性名称
	 * @return 指定属性在表中的下标
	 */
	public int indexOf(String name) {
		for (int i = 0; i < attributes.size(); i++)
			if (attributes.get(i).getName().equals(name))
				return i;
		return -1;
	}
	
	public int indexOfRow(TableRow row) {
		for (int i = 0; i < rows(); i++) {
			if (row.equals(row(i)))
				return i;
		}
		return -1;
	}

	/**
	 * 判断表是否设置了类属性<br>
	 * 下列两种情况之一出现都将被视为没有设置：
	 * <ul>
	 * <li>类属性{@code classAttribute}为{@code null}</li>
	 * <li>类属性虽不为{@code null}，但在表的属性列表中不存在{@code classAttribute}的
	 * 属性（有可能在设置了类属性之后执行了移除{@link #removeAttribute(int)}的操作）</li>
	 * </ul>
	 * 
	 * @return 若没有设置返回{@code false}
	 */
	public boolean hasClass() {
		return attributes.contains(classAttribute);
	}

	/**
	 * 判断指定属性{@code att}是否是类属性<br>
	 * 当且仅当属性<code>att==classAttribute</code>为{@code true}时（而非
	 * <code>att.equals(classAttribute)</code>为{@code true}）， 才判定属性{@code att}
	 * 为类属性
	 * 
	 * @param att
	 *            指定属性
	 * @return 若相等则返回{@code true}
	 */
	public boolean isClassAttribute(Attribute att) {
		return att == classAttribute;
	}

	/**
	 * 获取类属性下标<br>
	 * <b><i>NOTICE:</b></i> 如果没有设置，将返回-1
	 * 
	 * @return 类属性的下标
	 */
	public int classIndex() {
		return attributes.indexOf(classAttribute);
	}

	/**
	 * 根据属性在表中的下标设置类属性
	 * 
	 * @param idx
	 *            指定下标
	 * @see #setClassAttribute(String)
	 */
	public void setClassAttribute(int idx) {
		if (idx < 0 || idx >= columns())
			throw new IllegalArgumentException("index out of range.");
		this.classAttribute = attribute(idx);
	}

	/**
	 * 根据属性名称设置类属性<br>
	 * <b><i>NOTICE:</b></i> 此方法不保证能设置成功，也就是说，如果表中不存在名称为 {@code name}
	 * 的属性，那么方法将不做任何处理返回，类属性依旧保持上一次的设置
	 * 
	 * @param name
	 *            指定属性的名称
	 * @see #setClassAttribute(int)
	 */
	public void setClassAttribute(String name) {
		for (Attribute att : attributes) {
			if (att.getName().equals(name))
				classAttribute = att;
		}
	}

	/**
	 * 得到类属性<br>
	 * <b><i>NOTICE:</b></i> 如果没有设置类属性，将返回{@code null}
	 * 
	 * @return 返回指定的类属性
	 */
	public Attribute classAttribute() {
		if (!hasClass())
			return null;
		return classAttribute;
	}

	/**
	 * 创建表中属性的迭代器
	 * 
	 * @return
	 */
	public Iterator<Attribute> attributesIterator() {
		return attributes.iterator();
	}

	/**
	 * 获取所有属性
	 * 
	 * @return
	 */
	public ArrayList<Attribute> attributes() {
		return new ArrayList<>(attributes);
	}

	/**
	 * 创建一个行
	 * 
	 * @return 新创建的行
	 */
	public TableRow createRow() {
		TableRow row = new TableRow();
		row.setOwner(this);
		return row;
	}

	/**
	 * 在表后附加一个表
	 * 
	 * @param table
	 *            待附加的表
	 */
	public void append(Table table) {
		if (!compatible(table))
			throw new IllegalArgumentException(
					"Table append, attribute type not compatiable.");
		for (int i = 0; i < table.columns(); i++)
			attributes.get(i).getVector()
					.append(table.attributes.get(i).getVector());
		// update index
		for (int i = 0; i < table.rows; i++)
			index.push(rows + i);
		// update rows
		rows += table.rows;
	}

	/**
	 * 在表后附加一行
	 * 
	 * @param row
	 *            待附加的行
	 */
	public void push(TableRow row) {
		for (int i = 0; i < columns(); i++)
			attributes.get(i).getVector().push(row.at(i));
		index.push(rows++);
	}

	/**
	 * 在表后附加一行
	 * 
	 * @param objects
	 *            待附加的行
	 */
	public void push(Object... objects) {
		for (int i = 0; i < columns(); i++)
			attributes.get(i).getVector().push(objects[i]);
		index.push(rows++);
	}

	/**
	 * 获取表在下标i，j处的值
	 * 
	 * @param i
	 *            行下标
	 * @param j
	 *            列下标
	 * @return 表在i，j处的值
	 */
	public Object at(int i, int j) {
		return attributes.get(j).getVector().at(index.at(i));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<TableRow> iterator() {
		return new TableIterator(this);
	}

	/**
	 * 将表按列分割成多个表
	 * 
	 * @return
	 */
	public Table[] split(Range[] ranges) {
		// check valid
		for (int i = 0; i < ranges.length; i++) {
			if (ranges[i].end() > columns())
				throw new IllegalArgumentException("range out of bound.");
			for (int j = i + 1; j < ranges.length; j++) {
				if (ranges[i].isCross(ranges[j]))
					throw new IllegalArgumentException("range is crossover");
			}
		}
		// split
		Table[] t = new Table[ranges.length];
		for (int i = 0; i < t.length; i++) {
			t[i] = new Table();
			t[i].rows = rows;
			t[i].index = this.index;
			for (int j = ranges[i].begin(); j < ranges[i].end(); j++)
				t[i].addAttribute(attributes.get(j));
		}
		return t;
	}

	/**
	 * 将表按列分割 <br>
	 * <b><i>NOTICE:</i></b>分割后并不会分配新的存储空间给分割后的表，只是简单的将索引拷贝给新的表， 且不会改变原来的表
	 * 
	 * @param indicesToSplit
	 *            待分割的列索引
	 * @return
	 */
	public Table[] split(int[][] indicesToSplit) {
		Table[] tables = new Table[indicesToSplit.length];
		for (int i = 0; i < tables.length; i++) {
			tables[i] = new Table();
			tables[i].rows = rows();
			tables[i].index = this.index;
			for (int j = 0; j < indicesToSplit[i].length; j++)
				tables[i].addAttribute(attribute(indicesToSplit[i][j]));
		}
		return tables;
	}

	/**
	 * 按照类属性将表中实例聚集<br>
	 * e.g. 设有表 {@code table} ，其类属性值在表中的顺序为：<code>
	 * 1,2,3,2,1,3</code> 在对表执行方法 {@code orderByClass()} 后类属性值（以及对应实例）在表中的顺序为：
	 * <code>
	 * 1,1,2,2,3,3 </code><br>
	 * <b><i>NOTICE:</b></i> 在进行聚集时，实际上是对索引表进行排序，并不真正对实例排序
	 * 
	 * @throws UnsupportedOperationException
	 *             如果没有设置类属性将抛出此异常
	 */
	public void orderByClass() {
		if (!hasClass())
			throw new UnsupportedOperationException("class is not set!");
		// make distinct
		Object[] distinct = classAttribute.distinct();
		// position of the distinct class value
		int[] positions = new int[distinct.length];
		// new indices
		IncrementIndex newIndex = new IncrementIndex(rows());
		for (int i = 0; i < rows(); i++) {
			
		}
	}

	/**
	 * 将表中实例顺序打乱，使得各个实例无序排列
	 */
	public void randomize() {
		this.index.randomize();
	}

	public Table[] resample(double percent) {
		// new
		Table t[] = new Table[] { new Table(), new Table() };
		// new indices-table & new rows to set
		int newRows = (int) ((double) rows * percent);
		newRows = newRows == 0 ? 1 : newRows;
		int newRows2 = rows - newRows;
		// choose the smaller to randomize
		int count = Math.min(newRows, newRows2);
		IncrementIndex newIndex = new IncrementIndex(count);
		// random select
		Random r = new Random(System.currentTimeMillis());
		IncrementIndex index_t = this.index.copy();
		for (int i = 0; i < count;) {
			int idx = r.nextInt(rows);
			if (index_t.at(idx) != IncrementIndex.NOT_INDEX
					&& !newIndex.contains(index.at(idx))) {
				newIndex.push(index.at(idx));
				// not an index
				index_t.set(idx, IncrementIndex.NOT_INDEX);
				i++;
			}
		}
		// another indices-table
		IncrementIndex newIndex2 = new IncrementIndex(rows - count);
		for (int i = 0; i < rows; i++) {
			if (index_t.at(i) != IncrementIndex.NOT_INDEX)
				newIndex2.push(index.at(i));
		}
		t[0].rows = newRows;
		t[1].rows = newRows2;
		t[0].name = getName();
		t[1].name = getName();
		// attributes
		t[0].attributes = new ArrayList<>();
		t[1].attributes = new ArrayList<>();
		for (int i = 0; i < columns(); i++) {
			try {
				Attribute att = attribute(i).getClass().newInstance();
				att.setName(attribute(i).getName());
				t[0].attributes.add(att);
				att = attribute(i).getClass().newInstance();
				att.setName(attribute(i).getName());
				t[1].attributes.add(att);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}
		}
		// copy elements
		t[0].index = IncrementIndex.increment(0, 1, newRows);
		t[1].index = IncrementIndex.increment(0, 1, newRows2);
		for (int i = 0; i < newRows; i++) {
			for (int j = 0; j < columns(); j++) {
				t[0].attribute(j).push(at(newIndex.at(i), j));
			}
		}
		for (int i = 0; i < newRows2; i++) {
			for (int j = 0; j < columns(); j++) {
				t[1].attribute(j).push(at(newIndex2.at(i), j));
			}
		}
		if (-1 != classIndex()) {
			t[0].setClassAttribute(classIndex());
			t[1].setClassAttribute(classIndex());
		}
		// swap
		if (newRows > count) {
			// set t & t1
			List<Attribute> attrs = t[0].attributes;
			t[0].attributes = t[1].attributes;
			t[1].attributes = attrs;
		}
		// trim
		t[0].trim();
		t[1].trim();
		return t;
	}

	/**
	 * 表中是否有缺失值
	 * 
	 * @return 如果有缺失值则返回{@code true}
	 */
	public boolean hasMissing() {
		for (int i = 0; i < rows(); i++)
			if (row(i).hasMissing())
				return true;
		return false;
	}

	/**
	 * 禁用有缺失值的行
	 * <p>
	 * <b><i>NOTICE:</b></i>与方法{@link #deleteMissing()}不同，此方法禁用有缺失值的行，
	 * 而不是删除。因此，在新形成的表中，表的索引将不再引用缺失值的行。
	 * </p>
	 * 
	 * @return 禁用缺失值后的表
	 */
	public Table disableMissing() {
		Table t = new Table();
		t.attributes = this.attributes;
		t.name = this.name;
		// new index
		t.index = new IncrementIndex(this.index.size());
		for (int i = 0; i < rows(); i++) {
			if (!row(i).hasMissing())
				t.index.push(this.index.at(i));
		}
		t.rows = t.index.size();
		return t;
	}

	/**
	 * 删除表中缺失的数据，并重置表<br>
	 * <b><i>NOTICE:</b></i> 在删除缺失值后，表索引将被重置
	 */
	public void deleteMissing() {
		for (int i = 0; i < rows(); i++) {
			TableRow row = row(i);
			if (row.hasMissing()) {
				for (int j = 0; j < columns(); j++)
					attribute(j).getVector().remove(i);
				i--;
				rows--;
			}
		}
		this.index = IncrementIndex.increment(0, 1, rows());
	}

	/**
	 * 获取非缺失行数
	 * 
	 * @return
	 */
	public int noneMissingRows() {
		int count = Integer.MAX_VALUE;
		for (int i = 0; i < columns(); i++)
			if (count > attribute(i).countNoneMissing())
				count = attribute(i).countNoneMissing();
		return count;
	}

	/**
	 * 将表转换为矩阵
	 * 
	 * @return
	 */
	public Matrix toMatrix() {
		return toMatrix((int[])null);
	}

	/**
	 * 将表转换为矩阵
	 * @param ingnores 在表中被忽略的属性下标
	 * @return
	 */
	public Matrix toMatrix(int[] ingnores) {
		int[] ing = ingnores;
		// check valid
		for (int i = 0; i < attributes.size(); i++)
			if (!(attributes.get(i) instanceof NumericAttribute)
					&& !isIn(i, ing))
				throw new UnsupportedOperationException(
						"only numeric attribute supported where convert "
								+ "table to matrix.");
		// convert to matrix
		int x = ing == null ? 0 : ing.length;
		Matrix matrix = new Matrix(rows, columns() - x);
		int c = 0;
		for (int i = 0; i < rows; i++) {
			c = 0;
			for (int j = 0; j < columns(); j++) {
				if (!isIn(j, ing)) {
					matrix.set(i, c, (double) at(i, j));
					c++;
				}
			}
		}
		return matrix;
	}

	/**
	 * 将表转换为矩阵
	 * 
	 * @param ingnores
	 *            忽略的属性
	 * @return
	 */
	public Matrix toMatrix(Attribute[] ingnores) {
		int[] ing;
		if (null == ingnores)
			ing = new int[0];
		else
			ing = new int[ingnores.length];
		for (int i = 0; i < ing.length; i++)
			ing[i] = indexOf(ingnores[i]);
		return toMatrix(ing);
	}

	private boolean isIn(int x, int[] y) {
		if (null == y) return false;
		for (int i = 0; i < y.length; i++)
			if (x == y[i])
				return true;
		return false;
	}

	/**
	 * 将表转换为二维数组
	 * 
	 * @return
	 */
	public Object[][] to2DArray() {
		Object[][] objects = new Object[rows][columns()];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns(); j++) {
				objects[i][j] = attributes.get(j).get(i);
				if (objects[i][j].equals(Attribute.MISSING_VALUE))
					objects[i][j] = "?";
			}
		}
		return objects;
	}

	/**
	 * 获取表在下标i处的行
	 * 
	 * @param i
	 *            声明的下标
	 * @return
	 */
	public TableRow row(int i) {
		TableRow tableRow = createRow();
		for (int j = 0; j < columns(); j++)
			tableRow.set(j, at(i, j));
		return tableRow;
	}
	
	public Table cloneWithHeader() {
		Table table = new Table();
		table.setName(getName());
		for (int i = 0; i < columns(); i++) {
			try {
				Attribute att = attribute(i).getClass().newInstance();
				att.setName(attribute(i).getName());
				table.attributes.add(att);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}
		}
		return table;
	}
	
	public Table clone() {
		Table table = cloneWithHeader();
		for (int i = 0; i < rows(); i++)
			table.push(row(i));
		table.trim();
		return table;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Table))
			return false;
		Table t = (Table) obj;
		for (int i = 0; i < rows(); i++) {
			if (!row(i).equals(t.row(i)))
				return false;
		}
		return true;
	}
	
	/**
	 * 辅助方法，将表中所有元素打印到控制台
	 */
	public void print() {
		// print header
		System.out.println("Name: " + getName());
		System.out.print("No.\t\t");
		for (int i = 0; i < columns(); i++)
			System.out.print(attributes.get(i).getType() + "\t\t");
		System.out.println();
		System.out.print("\t\t");
		for (int i = 0; i < columns(); i++)
			System.out.print("<" + attributes.get(i).getName() + ">\t\t");
		System.out.println();
		int r = 1;
		for (int i = 0; i < rows; i++) {
			System.out.print((r++) + "\t\t");
			for (int j = 0; j < columns(); j++) {
				if (at(i, j).equals(Attribute.MISSING_VALUE)) {
					System.out.print("?" + "\t\t");
					continue;
				}
				System.out.print(at(i, j) + "\t\t");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
		Table table = new Table();
		// add attribute
		table.addAttribute(new NumericAttribute("Length"));
		table.addAttribute(new NumericAttribute("Width"));
		System.out.println("add attributes---------------");
		System.out.println(table.attribute(0).getName() + " "
				+ table.attribute(1).getName());
		// capacity & size & columns
		System.out.println("size:" + table.rows());
		System.out.println("columns:" + table.columns());
		// push
		table.push(10, 10);
		table.push(20, 20);
		System.out.println("after push 2 rows--------------- rows="
				+ table.rows());
		table.print();
		// row
		System.out.println("row at 1:");
		TableRow row = table.row(1);
		row.print();
		// append
		Table table2 = new Table();
		table2.addAttribute(new NumericAttribute("Length"));
		table2.addAttribute(new NumericAttribute("Weight"));
		table2.push(100, 100);
		table2.push(200, 200);
		System.out.println("table2:" + table2.rows());
		table2.print();
		table.append(table2);
		System.out.println("after append------------------");
		table.print();
		// remove attribute
		Attribute attribute = table.removeAttribute(0);
		System.out.println("remove attribute at 0:");
		table.print();

		// resample
		table.addAttribute(attribute);
		Random r = new Random();
		for (int i = 0; i < 10; i++) {
			table.push(r.nextInt(200), r.nextInt(400));
		}
		System.out.println("random generated attributes--------------");
		System.out.println("indices:");
		table.indices().print();
		System.out.println("elements:");
		table.print();
		// resample (10%)
		Table[] t = table.resample(0.1);
		System.out.println("after resampled (10%)--------------------");
		System.out.println("indices (sorted):");
		t[0].indices().sort();
		t[0].indices().print();
		System.out.println("elements:");
		t[0].print();
		System.out.println("remainded-----------------------");
		System.out.println("indices:");
		t[1].indices().print();
		System.out.println("rows=" + table.rows());
		t[1].print();
		// split
		Table[] tables = table.split(new Range[] { new Range(0, 1) });
		System.out.println("after split:");
		tables[0].print();
		// to matrix
		Matrix x = table.toMatrix();
		System.out.println("to matrix:");
		x.print();
		// add
		Attribute[] att = new Attribute[2];
		att[0] = table.removeAttribute(0);
		att[1] = table.removeAttribute(0);
		table.addAttributes(att);
		System.out.println("remove & add: ");
		table.print();
		// push missing
		table.push(Attribute.MISSING_VALUE, Attribute.MISSING_VALUE);
		System.out.println("after push ?--------------");
		table.print();
		System.out.println("after disable missing------------");
		Table t3 = table.disableMissing();
		t3.print();
		System.out.println("after delete missing-------------");
		table.deleteMissing();
		table.print();

		// set class
		System.out.println("test class---------------------");
		System.out
				.println("class is set (should be false):" + table.hasClass());
		table.setClassAttribute("Length");
		System.out.println("class is set (should be true):" + table.hasClass());
		System.out.println("class attribute name (should be 'Length'):"
				+ table.classAttribute().getName());
		System.out.println("class attribute index (should be '1'):"
				+ table.classIndex());
		System.out.println("remove attribute 'Length'----------");
		//table.removeAttribute("Length");
		System.out
				.println("class is set (should be false):" + table.hasClass());
		// remove multi
		System.out.println("test remove------------------------");
		table.removeAttributes(new int[] { 0, 1 });
		System.out.println("remove 2 columns:");
		table.print();
	}

}

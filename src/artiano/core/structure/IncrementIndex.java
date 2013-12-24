/**
 * IncrementIndex.java
 */
package artiano.core.structure;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

/**
 * <p>
 * 一个非常小巧的类，用来表示索引表
 * </p>
 * 
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-11-1
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class IncrementIndex implements Serializable {
	private static final long serialVersionUID = 7693583697434755230L;

	// default capacity
	public static final int DEFAULT_CAPACITY = 10;
	// not index
	public static final int NOT_INDEX = -1;
	// increment factor
	private int incrementFacor = 2;
	// size
	private int size = 0;
	// integer array to store indices
	private int[] index;

	/**
	 * 以默认容量构造一个索引
	 */
	public IncrementIndex() {
		index = new int[DEFAULT_CAPACITY];
	}

	/**
	 * 以指定容量构造一个索引
	 * 
	 * @param capacity
	 *            指定容量
	 */
	public IncrementIndex(int capacity) {
		index = new int[capacity];
	}

	/**
	 * 创建一个自增索引<br>
	 * <b><i>NOTICE:</b></i> 方法将创建一个从 {@code start} 开始（包含 {@code start} ） ，以
	 * {@code patch} 为间隔，到 {@code end}结束的（但不包含 {@code end}） 自增索引
	 * 
	 * @param start
	 *            开始位置（包含）
	 * @param patch
	 *            自增间隔
	 * @param end
	 *            结束位置（不包含）
	 * @return 自增索引
	 */
	public static IncrementIndex increment(int start, int patch, int end) {
		if (start >= end)
			throw new IllegalArgumentException("End must greater than start!");
		int len = (end - start) / patch;
		IncrementIndex ini = new IncrementIndex(len);
		for (int i = start; i < end; i += patch)
			ini.push(i);
		return ini;
	}

	/**
	 * 拷贝一份索引
	 * 
	 * @return
	 */
	public IncrementIndex copy() {
		IncrementIndex inn = new IncrementIndex(this.size);
		inn.size = this.size;
		System.arraycopy(this.index, 0, inn.index, 0, size);
		return inn;
	}

	/**
	 * 获取索引容量
	 * 
	 * @return
	 */
	public int capacity() {
		return index.length;
	}

	/**
	 * 获取索引大小
	 * 
	 * @return
	 */
	public int size() {
		return size;
	}

	/**
	 * 获取索引值在索引表中的下标
	 * 
	 * @param value
	 *            待搜索的索引值
	 * @return 索引值在索引表中的下标（不在索引表中返回-1）
	 */
	public int indexOf(int value) {
		for (int i = 0; i < size; i++) {
			if (index[i] == value)
				return i;
		}
		return -1;
	}

	/**
	 * 设置在索引在下标i处的值
	 * 
	 * @param i
	 *            下标
	 * @param value
	 *            待设置的值
	 */
	public void set(int i, int value) {
		this.index[i] = value;
	}

	/**
	 * 获取在i处的索引值
	 * 
	 * @param i
	 *            下标
	 * @return
	 */
	public int at(int i) {
		if (i >= size)
			throw new IllegalArgumentException("index at, range out of bounds.");
		return index[i];
	}

	/**
	 * 添加一个索引值
	 * 
	 * @param i
	 *            待添加的值
	 */
	public void push(int i) {
		if (size == index.length) {
			int[] newIndex = new int[incrementFacor * (index.length + 1)];
			System.arraycopy(index, 0, newIndex, 0, size);
			index = newIndex;
		}
		index[size] = i;
		size++;
	}

	/**
	 * 在索引{@code i}处插入一个索引值<br>
	 * <b><i>NOTICE:</b></i> 此方法在插入之前不会判断索引表中是否已经存在待插入的值，
	 * 在某些需要重复实例的应用中，该方法是有效的。
	 * @param i 待插入位置
	 * @param value 待插入值
	 */
	public void insert(int i, int value) {
		if (capacity() < size() + 1) {
			System.arraycopy(index, i, index, i + 1, size - i);
			index[i] = value;
		} else {
			int[] d = new int[incrementFacor * (index.length + 1)];
			System.arraycopy(index, 0, d, 0, i);
			d[i] = value;
			System.arraycopy(index, i, index, i + 1, size - i);
			this.index = d;
		}
		size++;
	}
	
	/**
	 * 移除尾部索引值
	 */
	public void pop() {
		pop(1);
	}

	/**
	 * 从尾部移除一段索引
	 * 
	 * @param length
	 *            需移除的长度
	 */
	public void pop(int length) {
		this.size -= length;
	}

	/**
	 * 清除所有索引
	 */
	public void clear() {
		this.size = 0;
	}

	/**
	 * 判断索引是否存在
	 * 
	 * @param idx
	 *            待判断的索引值
	 * @return
	 */
	public boolean contains(int idx) {
		for (int i = 0; i < size; i++) {
			if (index[i] == idx)
				return true;
		}
		return false;
	}

	/**
	 * 交换向量在i、j处的值
	 * 
	 * @param i
	 * @param j
	 */
	public void swap(int i, int j) {
		int t = index[i];
		index[i] = index[j];
		index[j] = t;
	}

	/**
	 * 对索引进行无序化排列
	 */
	public void randomize() {
		Random r = new Random(System.currentTimeMillis());
		for (int i = size() - 1; i >= 0; i--) 
			swap(i, r.nextInt(i+1));
	}

	/**
	 * 对索引排序（升序）
	 */
	public void sort() {
		Arrays.sort(index, 0, size);
	}

	/**
	 * 将索引压缩以适应索引的大小（{@code size==capacity}）
	 */
	public void trim() {
		int[] d = new int[size()];
		System.arraycopy(index, 0, d, 0, size());
		this.index = d;
	}

	/**
	 * 辅助方法，打印索引值到控制台
	 */
	public void print() {
		for (int i = 0; i < size; i++)
			System.out.print(at(i) + " ");
		System.out.println();
	}

	public static void main(String[] args) {
		IncrementIndex index = new IncrementIndex();
		Random r = new Random(System.nanoTime());
		for (int i = 1; i < 10;) {
			int x = r.nextInt(20);
			if (!index.contains(x)) {
				index.push(x);
				i++;
			}
		}
		System.out.println("after push 9 elements, size=" + index.size());
		index.print();
		System.out.println("2 is exists:" + index.contains(2));
		System.out.println("17 is exists:" + index.contains(17));
		System.out.println("after sort:");
		index.sort();
		index.print();
		System.out.println("increment index--------");
		IncrementIndex ini = IncrementIndex.increment(0, 2, 10);
		ini.print();
		System.out.println("randomize index---------");
		ini.randomize();
		ini.print();
	}

}

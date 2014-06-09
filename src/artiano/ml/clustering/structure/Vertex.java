package artiano.ml.clustering.structure;

import artiano.core.structure.Table;

public class Vertex {
	private int indexInDataPoints;   //在数据点列表dataPoints中的下标
	private Table data;			 //该顶点的数据
	
	public Vertex(int indexInDataPoints, Table data) {
		super();
		this.indexInDataPoints = indexInDataPoints;
		this.data = data;
	}

	public int getIndexInDataPoints() {
		return indexInDataPoints;
	}

	public void setIndexInDataPoints(int indexInDataPoints) {
		this.indexInDataPoints = indexInDataPoints;
	}

	public Table getData() {
		return data;
	}

	public void setData(Table data) {
		this.data = data;
	}
	
}

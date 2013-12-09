package artiano.ml.clustering.structure;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractGraph<V> implements Graph<V> {
	protected List<V> vertices;		//Store vertices
	protected List<List<Integer>> neighbors;	//Adjacency lists		
	
	/** Construct a graph from edges and vertices stored in arrays */
	protected AbstractGraph(int[][] edges, V[] vertices){
		this.vertices = new ArrayList<V>();
		for(int i=0; i<vertices.length; i++) {
			this.vertices.add(vertices[i]);
		}
		
		createAdjacencyLists(edges, vertices.length);
	}
	
	/** Construct a graph from edges and vertices stored in List */
	protected AbstractGraph(List<Edge> edges, List<V> vertices){
		this.vertices = vertices;
		createAdjacencyLists(edges, vertices.size());
	}
	
	/** Construct a graph for integer vertices 0,1,2... and edge list */
	@SuppressWarnings("unchecked")
	protected AbstractGraph(List<Edge> edges, int numberOfVertices){
		vertices = new ArrayList<V>();	//Create vertices
		for(int i=0; i<numberOfVertices; i++){
			vertices.add((V)(new Integer(i)));	//vertices is {0,1,...}
		}
		
		createAdjacencyLists(edges, numberOfVertices);
	}
	 
	/** Construct a graph from integer vertices 0,1 and edge array */
	@SuppressWarnings("unchecked")
	protected AbstractGraph(int[][] edges, int numberOfVertices){
		vertices = new ArrayList<V>();
		for(int i=0; i<numberOfVertices; i++){
			vertices.add((V)(new Integer(i)));
		}
		
		createAdjacencyLists(edges, numberOfVertices);		
	}
	
	/** Create adjacency lists for each vertex */
 	private void createAdjacencyLists(int[][] edges,
			int numberOfVertices) {
		//Create a linked list 
		neighbors = new ArrayList<List<Integer>>();
		for(int i=0; i<numberOfVertices; i++) {
			neighbors.add(new ArrayList<Integer>());
		}
		
		for(int i=0; i<edges.length; i++) {
			int u = edges[i][0];
			int v = edges[i][1];
			neighbors.get(u).add(v);
		}
	}
	
 	/** Create adjacency lists for each vertex */
 	private void createAdjacencyLists(List<Edge> edges, 
 			int numberOfVertices){
 		//Create a linked list
 		neighbors = new ArrayList<List<Integer>>();
 		for(int i=0; i<numberOfVertices; i++){
 			neighbors.add(new ArrayList<Integer>());
 		}
 		
 		for(Edge edge : edges) {
 			neighbors.get(edge.u).add(edge.v);
 		}
 	}

 	/** Return the number of vertices in the graph */
 	public int getSize(){
 		return vertices.size();
 	}
 	
 	/** Return the vertices in the graph */
 	public List<V> getVertices(){
 		List<V> verticesCopy = new ArrayList<V>();
 		for(int i=0; i<vertices.size(); i++){
 			verticesCopy.add(vertices.get(i));
 		}
 		
 		return verticesCopy;
 	}
 	
 	/** Return the object for the specified index */
 	public V getVertex(int index){
 		return vertices.get(index);
 	}
 	
 	/** Return the index of the specified vertex */
 	public int getIndex(V v){
 		return vertices.indexOf(v);
 	}
 	
 	/** Return the neighbors of vertex with the specified index */
 	public List<Integer> getNeighbors(int index){
 		return neighbors.get(index);
 	}
 	
 	/** Return the degree for a specified vertex */
 	public int getDegree(int v){
 		return neighbors.get(v).size();
 	}
 	
 	/** Return the adjacency matrix */
 	public int[][] getAdjacencyMatrix(){
 		int[][] adjacencyMatrix = new int[getSize()][getSize()];
 		
 		for(int i=0; i<neighbors.size(); i++){
 			for(int j=0; j<neighbors.get(i).size(); j++){
 				int v = neighbors.get(i).get(j);
 				adjacencyMatrix[i][v] = 1;
 			}
 		}
 		
 		return adjacencyMatrix;
 	}

 	/** Print the adjacency matrix */
 	public void printAdjacencyMatrix(){
 		int[][] adjacencyMatrix = getAdjacencyMatrix();
 		for(int i=0; i<adjacencyMatrix.length; i++){
 			for(int j=0; j<adjacencyMatrix.length; j++){
 				System.out.print(adjacencyMatrix[i][j]+" ");
 			}
 			System.out.println();
 		}
 		System.out.println(); 		
 	}

 	/** Print the edges */
 	public void printEdges(){
 		for(int u=0; u<neighbors.size(); u++){
 			System.out.print("Vertex "+u+": ");
 			for(int j=0; j<neighbors.get(u).size(); j++){
 				System.out.print("("+u+", "+neighbors.get(u).get(j)+") ");
 			}
 			System.out.println();
 		}
 	} 	 	
 	
 	/** Get connected parts of the graph */
 	public List<List<Integer>> getConnectedBranches(){
 		int v = 0 ;
 		List<List<Integer>> connectedParts = 
 			new ArrayList<List<Integer>>();
 		int numberOfVerticesFound = 0; 		
 		while(numberOfVerticesFound < getSize()){
 			Tree tree = bfs(v);
 			numberOfVerticesFound += 
 				tree.getNumberOfVerticesFound(); //+= not =
 			int vertexnSearched = 
 				getNumOfUnserachedVertex(tree); 			
 			if(vertexnSearched == -1){
 				connectedParts.add(tree.searchOrders) ;
 				return connectedParts;
 			}else {
 				connectedParts.add(tree.searchOrders) ;
 				v = vertexnSearched;
 			}
 		} 		 		 		
		return connectedParts; 		
 	}
 	
 	private int getNumOfUnserachedVertex(Tree tree){
 		List<Integer> verticesSearched = 
 			tree.searchOrders;
 		int numberOfVertices = getSize();
 		for(int i=0; i<numberOfVertices; i++){
 			if(! verticesSearched.contains(i)){ 
 				return i;		//The graph isn't connected
 			}
 		}
 		
		return -1;		//The graph is connected 		
 	}
 	
 	/** Edge inner class inside the AbstractGraph class */
 	public static class Edge {
 		public int u;	//Starting vertex of the edge
 		public int v;	//Ending vertex of the edge		
 		
 		/** Construct an edge for (u, v) */
 		public Edge(int u, int v) {
			super();
			this.u = u;
			this.v = v;
		}
 	}
 	
 	/** Obtain a DFS tree starting from vertex */
 	public Tree dfs(int v){
 		List<Integer> searchOrders = new ArrayList<Integer>();
 		int[] parent = new int[vertices.size()];
 		for(int i=0; i<parent.length; i++){
 			parent[i] = -1; 			
 		}
 		
 		//Mark visited vertices
 		boolean[] isVisited = new boolean[vertices.size()];
 		
 		//Recursively search
 		dfs(v, parent, searchOrders, isVisited);
 		
 		return new Tree(v, parent, searchOrders);
 	}
 	
 	/** Recursive method for DFS search */
 	private void dfs(int v, int[] parent, List<Integer> searchOrders,
 			boolean[] isVisited){
 		//Store the visited vertex
 		searchOrders.add(v);
 		isVisited[v] = true;	//Vertex v is visited
 		
 		for(int i : neighbors.get(v)){
 			if(! isVisited[i]){
 				parent[i] = v;
 				dfs(i, parent, searchOrders, isVisited);	//Recursive search
 			}
 		}
 	}
 	
 	/** Starting bfs search from vertex */
 	public Tree bfs(int v){
 		List<Integer> searchOrders = new ArrayList<Integer>();
 		int[] parent = new int[vertices.size()];
 		for(int i=0; i<parent.length; i++){
 			parent[i] = -1; 			
 		}
 		
 		LinkedList<Integer> queue = 
 			new LinkedList<Integer>();
 		boolean[] isVisited = new boolean[vertices.size()];
 		queue.offer(v);
 		isVisited[v] = true;	//Mark is visited
 		
 		while(! queue.isEmpty()){
 			int u = queue.poll();	//Dequeue to u
 			searchOrders.add(u);	//u searched
 			
 			for(int w: neighbors.get(u)){
 				if(! isVisited[w]){
 					queue.offer(w);		//Enqueue w
 					parent[w] = u;
 					isVisited[w] = true;	//Mark is visited
 				} 				
 			} 			
 		}
 		
 		return new Tree(v, parent, searchOrders);
 	}
 	
 	/** Tree inner class inside the AbstractGraph class */
 	public class Tree {
 		private int root ;	//The root of the tree
 		private int[] parent;	//Store the parent of each vertex
 		private List<Integer> searchOrders ; 	//Store the search order
		 		
 		/** Construct a tree with root , parent and search order */
 		public Tree(int root, int[] parent, List<Integer> searchOrders) {
			this.root = root;
			this.parent = parent;
			this.searchOrders = searchOrders;
		}
 		
 		/** Return the root of the tree */
 		public int getRoot(){
 			return root;
 		} 
 		
 		/** Return the parent of vertex v */
 		public int getParent(int v){
 			return parent[v];
 		}
 		
 		/** Return an array representing search order */
 		public List<Integer> getSearchOrders(){
 			List<Integer> copyOfSearchOrders = 
 				new  ArrayList<Integer>();
 			for(Integer integer : searchOrders){
 				copyOfSearchOrders.add(integer);
 			}
 			
 			return copyOfSearchOrders;
 		}
 		
 		/** Return the number of vertices found */
 		public int getNumberOfVerticesFound(){
 			return searchOrders.size();
 		}
 		
 		/** Return the path of vertices from a vertex index to the root */
 		public List<V> getPath(int index){
 			ArrayList<V> path = new ArrayList<V>();
 			
 			do {
 				path.add(vertices.get(index));
 				index = parent[index];
 			}while(index != -1);
 			
 			return path;
 		}
 		
 		/** Print a path from the root to vertex */
 		public void printPath(int index){
 			List<V> path = getPath(index);
 			System.out.print("A path from "+vertices.get(root)+" to "+
 					vertices.get(index)+": ");
 			for(int i=path.size()-1; i>=0; i--){
 				System.out.print(path.get(i)+" ");
 			}
 			System.out.println();
 		}
 	
 		public void printTree() {
 			System.out.println("Root is: "+vertices.get(root));
 			System.out.print("Edges:  ");
 			for(int i=0; i<parent.length; i++){
 				if(parent[i] != -1){
 					//Display an edge
 					System.out.print("("+vertices.get(parent[i])+
 							", "+vertices.get(i)+")");
 				}
 			}
 			System.out.println();
 		}
 	}
}

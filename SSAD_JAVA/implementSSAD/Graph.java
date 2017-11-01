package implementSSAD;

public interface Graph<V> {
	
	  /** Return the number of vertices in the graph */
	public int getSize();
	
	  /** Return the vertices in the graph */
	public java.util.List<V> getVertices();
	
	  /** Return the vertices in the graph */
	public V getVertex(int index);
	
	  /** Return the index for the specified vertex object */
	public int getIndex(V vertex);
	
	  /** Return the neighbors of vertex with the specified index */
	public java.util.List<Integer> getNeighbors(int index);
	
	  /** Return the edges of vertex with the specified index */
	public java.util.List<Edge> getEdges(int index);

	  /** Clear the graph */
	public void clear();
	
	  /** Add a vertex to the graph */  
	public boolean addVertex(V vertex);
	
	  /** Add an edge to the graph */  
	public boolean addEdge(int u, int v, int weight);
	
	public boolean addEdge(Edge e);
	
	public void printEdges();
}
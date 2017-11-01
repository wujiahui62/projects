package implementSSAD;

import java.util.ArrayList;
import java.util.List;

public class WeightedGraph<V> implements Graph<V>{
	
	protected List<V> vertices = new ArrayList<>();

	protected List<ArrayList<Edge>> neighbors = new ArrayList<>();
	
	public WeightedGraph(){
	}
	
	public WeightedGraph(ArrayList<Edge> edges, int numberOfVertices){
		List<V> vertices = new ArrayList<>();
		for(int i = 0; i < numberOfVertices; i++)
			vertices.add((V)(new Integer(i)));
		
		createWeightedGraph(vertices, edges);
	}
	
	private void createWeightedGraph(List<V> vertices, ArrayList<Edge> edges){
		this.vertices = vertices;
		
		//create empty arrayList<Edge> for neighbors
		for(int i = 0; i < vertices.size(); i++){
			neighbors.add(new ArrayList<Edge>());
		}
		
		//add edge to each empty list
		for(Edge edge: edges){
			neighbors.get(edge.u).add(edge);
		}
	}
	
	@Override
	public boolean addEdge(int u, int v, int weight){
		return addEdge(new Edge(u, v, weight));
	}
	
	@Override
	public boolean addEdge(Edge e){
		if(e.u < 0 || e.u > getSize() - 1)
			throw new IllegalArgumentException("No such index: " + e.u);
		if(e.v < 0 || e.v > getSize() - 1)
			throw new IllegalArgumentException("No such index: " + e.v);
		if(!neighbors.get(e.u).contains(e)){
			neighbors.get(e.u).add(e);
			return true;
		}
		else
			return false;
	}
	
	public int getWeight(int u, int v) throws Exception{
		for(Edge edge: neighbors.get(u)){
			if(edge.get(v) == v)
				return edge.weight;
		}
	    throw new Exception("Edge does not exit");
	}
	
	public int getWeight(Edge e){
		return e.weight;
	}
	
	@Override
	public int getSize() {
		return vertices.size();
	}

	@Override
	public List<V> getVertices() {
		return vertices;
	}

	@Override
	public V getVertex(int index) {
		return vertices.get(index);
	}

	@Override
	public int getIndex(V vertex) {
		return vertices.indexOf(vertex);
	}

	@Override
	public List<Integer> getNeighbors(int index) {
		List<Integer> neighborsOfu = new ArrayList<>();
		for(Edge edge: neighbors.get(index)){
			neighborsOfu.add(edge.v);
		}
		return neighborsOfu;
	}
	
	@Override
	public void clear() {
		vertices.clear();
		neighbors.clear();
	}

	@Override
	public boolean addVertex(V vertex) {
		if(!vertices.contains(vertex)){
			vertices.add(vertex);
			neighbors.add(new ArrayList<Edge>());
			return true;
		}
		return false;
	}
		
	@Override
	public ArrayList<Edge> getEdges(int index){
		return neighbors.get(index);
	}
	
	@Override
	public void printEdges(){
		for(int u = 0; u < neighbors.size(); u++){
			System.out.print(getVertex(u) + " (" + u + "): ");
			for(Edge e: neighbors.get(u)){
				System.out.println("(" + getVertex(e.u) + ", " + 
			getVertex(e.v) + getWeight(e) + ") ");
			}
		}
	}
	
	public void writeGraph(WeightedGraph<V> graph) throws Exception{
		try(java.io.PrintWriter output = new java.io.PrintWriter("Graphoutput.txt")){
			output.println("Node | Out-neighbors");
			output.println("-------------------------------------------");
			int i = 0;
			for(ArrayList<Edge> neighbor: neighbors){
				if(neighbor.size() > 0){
					output.printf("%3s", neighbor.get(0).u);
					i++;
				}
				else{
					output.printf("%3s", i);
				}
				for(Edge e: neighbor){
					output.printf("%8s", e.v + ": ");
					output.printf("%-1s", e.weight);
				}
				output.println();
			}
			output.println();
		}
	}
}

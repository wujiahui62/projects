package implementSSAD;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

	public class SSAD<V>{
		private int root;
		private int[] parent;
		private List<Integer> T = new ArrayList<>();
		private int[] cost;
		private WeightedGraph<V> graph;
		final int POSITIVE_INFINITY = 2147483647;
		
		public SSAD(){
			
		}
		
		/**Dijkstra's algorithm: to find the shortest path between two index,
		 * find the shortest path from a starting index to all other index;
		 * a tree is returned, the starting index is root, to get the path from
		 * desIndex, search tree from desIndex up till the root
		 */
		//construct a SSAD constructor using Dijkstra's algorithm
		public SSAD(int root, WeightedGraph<V> graph) {
			this.root = root;
			this.graph = graph;
			int size = graph.getSize();
			cost = new int[size];
			for(int i = 0; i < size; i++){
				cost[i] = POSITIVE_INFINITY;
			}
			cost[root] = 0;
			parent = new int[size];
			parent[root] = -1;

			while(T.size() < size){
				int u = -1;
				int currentMinCost = POSITIVE_INFINITY;
				for(int i = 0; i < size; i++){
					if(!T.contains(i) && cost[i] < currentMinCost){
						currentMinCost = cost[i];
						u = i;
					}
				}
				
				if(u == -1)
					break;
				else
					T.add(u);
				
				for(Edge e: graph.neighbors.get(u)){
					if(!T.contains(e.v) && cost[e.v] > cost[u] + e.weight){
						cost[e.v] = cost[u] + e.weight;
						parent[e.v] = u;
					}
				}
			}
		}

		
		public int getRoot(){
			return root;
		}
		
		public int getParent(int v){
			return parent[v];
		}
		
		public List<Integer> getT(){
			return T;
		}
		
		public int getNumberOfVerticesFound(){
			return T.size();
		}

		public int getCost(int v){
			return cost[v];
		}
		
		public List<V> getPath(int index){
			ArrayList<V> path= new ArrayList<>();
			if(getCost(index) == POSITIVE_INFINITY){
				return null;
			}
			else if(index == root){
				return null;
			}
			else{
				do{
					path.add(graph.getVertex(index));
					index = parent[index];
				}while(index != root);
				
				return path;
			}
		}		
		
		public void writeShortestPath(int root, WeightedGraph<V> graph) 
				throws Exception{
			try(FileWriter fw = new FileWriter("Graphoutput.txt", true);
				    BufferedWriter bw = new BufferedWriter(fw);
				    PrintWriter out = new PrintWriter(bw))
				{
				    out.println("Start vertex is: " + root);
				    out.println();
				    out.printf("%12s\n", "Total");
				    out.println("Dest | Weight | Path");
				    out.println("-------------------------------------------");
				    for(int i = 0; i < graph.getSize(); i++){
				    	out.printf("%3s", i);
				    	if(getCost(i) == POSITIVE_INFINITY)
				    		out.printf("%8s", "inf");
				    	else
				    		out.printf("%8s", getCost(i));
				    	if(getPath(i) != null){
				    		for(int j = getPath(i).size() - 1; j >= 0; j--){
				    			out.printf("%6s", getPath(i).get(j));
				    		}
				    	}
				    	out.println();
				    }
				} catch (IOException e) {
					System.out.println("I/O Errors");
				}
		}
	}

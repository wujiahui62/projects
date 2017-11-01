package implementSSAD;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class TestSSAD {

	public static <V> void main(String[] args) throws Exception {
		ArrayList<Edge> edges = new ArrayList<>();
		int numberOfVertices = 0;
		int startVertex = 0;
		File file = new File("Graph.txt");
			Scanner input = new Scanner(file);
			while(input.hasNext()){
				String firstLine = input.nextLine().replaceAll("\\s+", "");
				String[] firstLineWords = firstLine.split(":");
				numberOfVertices = Integer.parseInt(firstLineWords[1]);
				String secondLine = input.nextLine().replaceAll("\\s+", "");
				String[] secondLineWords = secondLine.split(":");
				startVertex = Integer.parseInt(secondLineWords[1]);
				input.nextLine();
				int line = 0;
				for(int i = 0; i < numberOfVertices && 
						line < numberOfVertices * numberOfVertices; i++){
					for(int j = 0; j < numberOfVertices; j++){
						int e = input.nextInt();
						if(e != 0){
							edges.add(new Edge(i, j, e));
						}
					}
					line += numberOfVertices;
				}	
			}
			input.close();
			WeightedGraph<Integer> graph = new WeightedGraph<>(edges, numberOfVertices);
			SSAD tree = new SSAD(startVertex, graph);
			graph.writeGraph(graph);
			tree.writeShortestPath(startVertex, graph);
	}
}
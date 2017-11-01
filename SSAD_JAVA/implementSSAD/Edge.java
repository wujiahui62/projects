package implementSSAD;

public class Edge implements Comparable<Edge>{
	protected int u;
	protected int v;
	protected int weight;
	
	public Edge(int u, int v, int weight){
		this.u = u;
		this.v = v;
		this.weight = weight;
	}
	
	public boolean equals(Edge o){
		return this.u == o.u && this.v == o.v 
				&& this.weight == o.weight;
	}
	
	public int compareTo(Edge o){
		if(weight > o.weight)
			return 1;
		else if(weight == o.weight)
			return 0;
		else
			return -1;
	}

	public int get(int v) {
		return v;
	}
}

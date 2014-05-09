import java.util.*;
public class Vertex implements Comparable<Vertex> {
	//a way to switch between the current sorting method used for compareTo, which is used by PriorityQueue
	//remember each time before we operate on Qi  (i=1,2,...), we must set CURRENT_IDEX to i.
	private static int CURRENT_INDEX;		 
	String name;
	boolean isTerminal;
	boolean isVisited;
	double d1; //for find shortest path: distance to T1 
	double d2; //for find shortest path: distance to T2
	Vertex predecessor1;	//each node v visited by the current iterator, we maintain its	current predecessor, that is, the node v from which the iterator reached v
	Vertex predecessor2;	//each node v visited by the current iterator, we maintain its	current predecessor, that is, the node v from which the iterator reached v
	LinkedList<Edge> edges;	//For Steiner tree problem, degree=in degree+out degree
	public static void setIdx(int idx){
		Vertex.CURRENT_INDEX=idx;
	}
	public boolean isATerminal(){
		return this.isTerminal;
	}
	public void setPredecessor(int idx, Vertex v){
		if (idx==1){
			this.predecessor1=v;
		}else{
			this.predecessor2=v;
		}
	}
	public Vertex getPrecedessor(int idx){
		if (idx==1){
			return this.predecessor1;
		}else{
			return this.predecessor2;
		}
	}
	public void setd(int idx, double d){
		if (idx==1){
			this.d1=d;
		}else{
			this.d2=d;
		}
	}
	public double d(int idx){
		if (idx==1){
			return d1;
		}else{
			return d2;
		}
	}
	public Vertex(String name){
		this.name=name;
		this.isTerminal=false;
		edges=new LinkedList<Edge>();
	}
	//return values of getUnvisitedDegree()
	//0 -- it's a isolated vertex(all edges have been visited) 
	//1 -- it's a leaf(only connected to another one vertex)
	//2 -- it's within a loose path.
	//3 -- it's has more that one loose paths crossed.
	public int getUnvisitedDegree(){		 
		int unvisited=0;
		for (Edge e: this.edges){
			if (!e.isVisited()){
				unvisited ++;
			}
		}
		return unvisited;
	}
	public int getUnvisitedDegreeInTree(Graph T){
		int unvisited=0;
		for (Edge e: this.edges){
			if (T.E.contains(e) && !e.isVisited()){
				unvisited ++;
			}
		}
		return unvisited;
	}
	public void addAdjacency(Edge e){
		edges.add(e);
	}
	public void removeAdjacency(Edge e){
		edges.remove(e);
	}
	public Edge getEdgeTo(Vertex v){
		for (Edge e: this.edges){
			if (e.getSource()==v||e.getDestin()==v){
				return e;
			}
		}
		return null;
	}
	public Edge getTheOnlyUnvisitedEdgeInTree(Graph T){
		for (Edge edge: this.edges){
			if (T.E.contains(edge)&& !edge.isVisited()){
				return edge;
			}
		}
		return null;
	}
	public Edge getAnotherEdgeInLoosePath(Edge e, Graph T){
		for (Edge edge: this.edges){
			if (T.E.contains(edge) && edge != e){
				return edge;
			}
		}
		return null;
	}
	public boolean isTerminal(TreeMap<String, Vertex> VPrime){
		return (VPrime.get(this.name)==this);
	}
	public boolean isSteiner(TreeMap<String, Vertex> VPrime, Graph T){
		return this.isInTree(T)&&!this.isTerminal(VPrime); 
	}
	public boolean isFixed(TreeMap<String, Vertex> VPrime, Graph T){
		if (this.isInTree(T)){	//in Steiner Tree
			if (this.isTerminal(VPrime)){		//is Terminal
				return true;
			}else{
				return this.getDegreeInTree(T)>=3;		//Steiner vertex with degree >=3
			}
		}else{
			return false;
		}
	}
	public Map<String, Vertex> getAdjacentsInTree(Graph T){
		Map<String, Vertex> adj=new TreeMap<String, Vertex>();
		for (Edge e: this.edges){
			if (T.E.contains(e)){
				if (e.src!=this){
					adj.put(e.src.getName(), e.src);
				}else{ 
					adj.put(e.dst.getName(), e.dst);
				}
			}
		}
		return adj;
	}
	public Map<String, Vertex> getAdjacents(){
		Map<String, Vertex> adj=new TreeMap<String, Vertex>();
		for (Edge e: this.edges){
			if (e.src!=this){
				adj.put(e.src.getName(), e.src);
			}else{
				adj.put(e.dst.getName(), e.dst);
			}
		}
		return adj;
	}
	public int getDegree(){
		return edges.size();
	}
	public int getDegreeInTree(Graph T){
		int degree=0;
		for (Edge e: this.edges){
			if (T.E.contains(e)){
				degree ++;
			}
		}
		return degree;
	}
	public boolean isVisited(){
		return this.isVisited;
	}
	public void setVisited(boolean  v){
		this.isVisited=v;
	}
	public boolean equals(Vertex v){		//We suppose: any vertex in a graph has only one Vertex instance.
		return this.name.equals(v.getName());
	}
	public String getName() {
		return this.name;
	}
	public boolean isInTree(Graph T){
		return T.V.values().contains(this);
	}
	public boolean isTerminal(){
		return this.isTerminal;
	}
	public void setTerminal(boolean t){
		this.isTerminal=t;
	}
	@Override
	public int compareTo(Vertex v) {
		if (CURRENT_INDEX==1){
			return Double.compare(this.d1, v.d1);
		}else{
			return Double.compare(this.d2, v.d2);
		}
	}
}

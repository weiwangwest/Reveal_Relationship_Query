package graph;

import input.DatasetLoaderWithJena;

import java.util.*;

import fundamental.Mapper;

public class Vertex implements Comparable<Vertex> {
	static public Mapper vertexMap=new Mapper();
	//a way to switch between the current sorting method used for compareTo, which is used by PriorityQueue
	//remember each time before we operate on Qi  (i=1,2,...), we must set CURRENT_IDEX to i.
	int id;
	boolean isTerminal;
	boolean isVisited;
	private static int CURRENT_INDEX;		 
	double d1; //for find shortest path: distance to T1 
	double d2; //for find shortest path: distance to T2
	Vertex predecessor1;	//each node v visited by the current iterator, we maintain its	current predecessor, that is, the node v from which the iterator reached v
	Vertex predecessor2;	//each node v visited by the current iterator, we maintain its	current predecessor, that is, the node v from which the iterator reached v
	LinkedList<Edge> edges;	//For Steiner tree problem, degree=in degree+out degree
	public static void setIdx(int idx){
		Vertex.CURRENT_INDEX=idx;
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
		this.id=vertexMap.put(name);
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
	public int getUnvisitedDegreeInGraph(Graph g){
		int unvisited=0;
		for (Edge e: this.edges){
			if (g.contains(e) && !e.isVisited()){
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
	//returns any Edge connecting the this vertex and v
	public Edge getAnyEdgeBetween(Vertex v){
		for (Edge e: this.edges){
			if (e.getSource()==v||e.getDestin()==v){
				return e;
			}
		}
		return null;
	}
	public Edge getTheLeastWeightEdgeBetween(Vertex v){
		Edge result=null;
		for (Edge e: this.edges){
			if (e.getSource()==v||e.getDestin()==v){
				if (result==null || Double.compare(e.getWeight(), result.getWeight())<0){
					result = e;
				}
			}
		}
		return result;
	}
	public Edge getAnyUnvisitedEdgeInTree(Graph T){
		for (Edge e: this.edges){
			if (T.E.contains(e)&& !e.isVisited()){
				return e;
			}
		}
		return null;
	}
	public Edge getAnyUnvisitedEdgeInGraph(Graph g){
		for (Edge e: this.edges){
			if (g.contains(e)&& !e.isVisited()){
				return e;
			}
		}
		return null;
	}
	//get any other edge connected to this vertex in T to build a loose path.
	public Edge getAnyOtherEdgeInLoosePath(Edge e, Graph T){
		for (Edge edge: this.edges){
			if (T.E.contains(edge) && edge != e){
				return edge;
			}
		}
		return null;
	}
	public boolean isTerminal(HashMap<Integer, Vertex> VPrime){
		//return (VPrime.get(this.name)==this);
		return VPrime.containsKey(this.getId());
	}
	public boolean isSteiner(HashMap<Integer, Vertex> VPrime, Graph T){
		return this.isContainedBy(T) && !this.isTerminal(VPrime); 
	}
	public boolean isFixed(HashMap<Integer, Vertex> VPrime, Graph T){
		if (this.isContainedBy(T)){	//in Steiner Tree
			return (this.isTerminal(VPrime) || this.getDegreeInGraph(T)>=3);
		}else{
			return false;
		}
	}
	public void print(){
		System.out.println("Vertex: "+this.getNameString());
	}
	@Override
	public String toString(){
		return this.getNameString();
	}
	//return all vertices in T connected to this vertex.
	//otherwise return an empty map;
	public Map<Integer, Vertex> getAdjacentsInTree(Graph g){
		Map<Integer, Vertex> adj=new HashMap<Integer, Vertex>();
		for (Edge e: this.edges){
			if (g.E.contains(e)){
				if (e.src!=this){
					adj.put(e.src.getId(), e.src);
				}else{ 
					adj.put(e.dst.getId(), e.dst);
				}
			}
		}
		return adj;
	}
	//return all vertices connected to this vertex.
	//otherwise return null;
	public Map<Integer, Vertex> getAdjacents(){
		Map<Integer, Vertex> adj=null;
		for (Edge e: this.edges){
			if (adj==null){
				adj=new HashMap<Integer, Vertex>();
			}
			if (e.src!=this){
				adj.put(e.src.getId(), e.src);
			}else{
				adj.put(e.dst.getId(), e.dst);
			}
		}
		return adj;
	}
	public int getDegree(){
		return edges.size();
	}
	public int getDegreeInGraph(Graph g){
		int degree=0;
		for (Edge e: this.edges){
			if (g.contains(e)){
				degree ++;
			}
		}
		return degree;
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
	@Override
	public boolean equals(Object obj){		//We suppose: any vertex in a graph has only one Vertex instance.
		if (!(obj instanceof Vertex)){
			return false;
		}
		if (this==obj){
			return true;
		}
		Vertex that=(Vertex) obj;
		return (this.getId()==that.getId());
	}
	public int getId() {
		return this.id;
	}
	public String getNameString(){
		return vertexMap.getKey(this.id);
	}
	public boolean isContainedBy(Graph T){
		//return T.V.values().contains(this);
		return T.contains(this);
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
	public static void main(String args[]) throws Exception{
		Graph G = DatasetLoaderWithJena.generateGraphFromEntitiesOfNQFile("/data/example.nq");
	    Random rand = new Random();
		for (Vertex v: G.V.values()){		//1: for all v ∈ V do
				v.d1=rand.nextInt((1000-0) + 1) + 0;
				v.d2=rand.nextInt((1000-0) + 1) + 0;
		}	//4: end for
		PriorityQueue <Vertex> Q1=new PriorityQueue <Vertex>();
		PriorityQueue <Vertex> Q2=new PriorityQueue <Vertex>();
		for (Vertex v: G.V.values()){
			Graph.Q(Q1, Q2, 1).add(v);
			Graph.Q(Q1, Q2, 2).add(v);
		}
		while (!(Graph.Q(Q1, Q2, 1)).isEmpty()){
			Vertex v=Graph.Q(Q1,Q2,1).poll();
			System.out.println(v.getNameString() + "		" +v.d1);
		}
		System.out.println("---------------------");
		Vertex.setIdx(2);		
		while (!Graph.Q(Q1, Q2, 2).isEmpty()){
			Vertex v=Graph.Q(Q1, Q2, 2).poll();
			System.out.println(v.getNameString()+ "		" +v.d2);
		}
	}
}

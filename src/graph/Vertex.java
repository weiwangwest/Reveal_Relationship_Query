package graph;

import input.DatasetLoaderWithJena;
import java.util.*;

import fundamental.DBMapper;
import fundamental.FileNameManager;
import fundamental.MyLinkedList;


public class Vertex implements Comparable<Vertex> {
	//static public DBMapper vertexMap=new DBMapper("vertex");
	static public DBMapper vertexMap=new DBMapper("vertex");
	//a way to switch between the current sorting method used for compareTo, which is used by PriorityQueue
	//remember each time before we operate on Qi  (i=1,2,...), we must set CURRENT_IDEX to i.
	private static int CURRENT_INDEX;	
	int id;
	boolean isVisited;
	double d1; //for find shortest path: distance to T1 
	double d2; //for find shortest path: distance to T2
	Vertex predecessor1;	//each node v visited by the current iterator, we maintain its	current predecessor, that is, the node v from which the iterator reached v
	Vertex predecessor2;	//each node v visited by the current iterator, we maintain its	current predecessor, that is, the node v from which the iterator reached v
	MyLinkedList<Edge> edges;	//For Steiner tree problem, degree=in degree+out degree

	public MyLinkedList<Edge> getEdges(){
		return this.edges;
	}
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
		this(vertexMap.put(name));
	}
	public Vertex(Vertex v){	// deep copy from an existing vertex
		this.d1=v.d1;
		this.d2=v.d2;
		this.edges=new MyLinkedList<Edge>();
		for (Edge e: v.edges){
			this.edges.add(new Edge(e)); //TODO: Problem!!! src vertex and dst vertex have different Edge objects
		}
		this.id=v.id;
		this.isVisited=v.isVisited;
		this.predecessor1=v.predecessor1;
		this.predecessor2=v.predecessor2;
	}
	public Vertex(int id) {
		this.id=id;
		edges=new MyLinkedList<Edge>();
	}
	//return values of getUnvisitedDegree()
	//0 -- it's a isolated vertex(all edges have been visited) 
	//1 -- it's a leaf(only connected to another one vertex)
	//2 -- it's within a loose path.
	//3 -- it's has more that one loose paths crossed.
	public int getUnvisitedDegreeInGraph(Graph g){
		int unvisited=0;
		for (Edge e: this.edges){
			if (g.contains(e) && !e.isVisited()){
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
			if (e.getSource()==v.getId()||e.getDestin()==v.getId()){
				return e;
			}
		}
		return null;
	}
	public Edge getDirectedEdgeTo(Vertex dst){
		for (Edge e: this.edges){
			if (e.getDestin()==dst.getId()){
				return e;
			}		
		}
		return null;
	}
	public Edge getDirectedEdgeFrom(Vertex src){
		for (Edge e: this.edges){
			if (e.getSource()==src.getId()){
				return e;
			}		
		}
		return null;
	}
	public Edge getTheLeastWeightEdgeBetween(Vertex v){
		Edge result=null;
		for (Edge e: this.edges){
			if (e.getSource()==v.getId()||e.getDestin()==v.getId()){
				if (result==null || Double.compare(e.getWeight(), result.getWeight())<0){
					result = e;
				}
			}
		}
		return result;
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
	public Edge getAnyOtherEdgeInLoosePath(Edge e, Tree T){
		for (Edge edge: this.edges){
			if (T.contains(edge) && edge != e){
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
	public void print() {
		System.out.println("Vertex: "+this.getNameString());
	}
	@Override
	public String toString(){
		try{
			return String.valueOf(this.id);
		}catch(Exception e){
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			e.printStackTrace();
			return null;
		}
	}
	//return all vertices connected to this vertex.
	//otherwise return null;
	public Map<Integer, Vertex> getAdjacentsInGraph(Graph g){
		Map<Integer, Vertex> adj=null;
		for (Edge e: this.edges){
			if (adj==null){
				adj=new HashMap<Integer, Vertex>();
			}
			if (e.getSource()!=this.getId()){
				adj.put(e.getSource(), g.getVertex(e.getSource()));
			}else{
				adj.put(e.getDestin(), g.getVertex(e.getDestin()));
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
	public boolean isVisited(){
		return this.isVisited;
	}
	public void setVisited(boolean  v){
		this.isVisited=v;
	}
	@Override
	public int hashCode(){
		return this.getId();
	}
	@Override
	public boolean equals(Object obj){		//We suppose: any vertex in a graph has only one Vertex instance.
		if (obj==null){
			return false;
		}
		if (!(obj instanceof Vertex)){
			return false;
		}
		Vertex that=(Vertex) obj;
		return (this.getId()==that.getId());
	}
	public int getId() {
		return this.id;
	}
	public int getNameString() {
		//return vertexMap.getKey(this.id);
		return this.id;
	}
	public boolean isContainedBy(Graph T){
		//return T.V.values().contains(this);
		return T.contains(this);
	}
	public boolean isTerminal(Tree tree){
		return tree.hasTerminal(this);
	}
	public void setTerminal(boolean t, Tree tree){
		if (t==false){
			tree.removeTerminal(this);
		}else{
			tree.addTerminal(this);
		}
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
		Graph G = new Graph(Graph.GRAPH_CAPACITY);
		DatasetLoaderWithJena.addEntitiesFromNqNoExcetionProcessor(G, FileNameManager.pathToDataFiles+"example.nq");
	    Random rand = new Random();
		for (Vertex v: G.vertices()){		//1: for all v ∈ V do
				v.d1=rand.nextInt((1000-0) + 1) + 0;
				v.d2=rand.nextInt((1000-0) + 1) + 0;
		}	//4: end for
		PriorityQueue <Vertex> Q1=new PriorityQueue <Vertex>();
		PriorityQueue <Vertex> Q2=new PriorityQueue <Vertex>();
		for (Vertex v: G.vertices()){
			GraphManager.Q(Q1, Q2, 1).add(v);
			GraphManager.Q(Q1, Q2, 2).add(v);
		}
		while (!(GraphManager.Q(Q1, Q2, 1)).isEmpty()){
			Vertex v=GraphManager.Q(Q1,Q2,1).poll();
			System.out.println(v.getNameString() + "		" +v.d1);
		}
		System.out.println("---------------------");
		Vertex.setIdx(2);		
		while (!GraphManager.Q(Q1, Q2, 2).isEmpty()){
			Vertex v=GraphManager.Q(Q1, Q2, 2).poll();
			System.out.println(v.getNameString()+ "		" +v.d2);
		}
	}
}

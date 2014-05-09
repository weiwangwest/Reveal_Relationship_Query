import java.util.*;
public class Edge {
	//todo: private members, public methods.
	Vertex src;
	Vertex dst;
	String nameOfType;
	double weight;
	boolean isVisited;
	public Vertex getAnotherVertex(Vertex v){
		if (this.src==v){
			return this.dst;
		}else if (this.dst==v){
			return this.src;
		}else{
			return null;
		}
	}
	public Edge getNextEdgeInLoosePath(Vertex v, Graph T){ //get another edge of the vertex in loose path
		Vertex v1=this.getAnotherVertex(v);
		return v1.getAnotherEdgeInLoosePath(this, T);
	}
	public LoosePath findNextLoosePath( TreeMap<String, Vertex> treeVertices){
		LoosePath lp=new LoosePath();
		//find fixed nodes whose tree degree==1
		//find a loose path starting from the node, marks all nodes in the path as in the tree.
		return lp;
	}
	public Edge clone(){
		return new Edge(this);
	}
	public Edge(Edge e){
		this.dst=e.dst;
		this.isVisited=e.isVisited;
		this.nameOfType=e.nameOfType;
		this.src=e.src;
		this.weight=e.weight;
	}
	public Edge(Vertex src, Vertex dst, String type, double w){
		this.src=src;
		this.dst=dst;
		nameOfType=type;
		weight=w;
		isVisited=false;
	}
	public Vertex getSource(){
		return this.src;
	}
	public Vertex getDestin(){
		return this.dst;
	}
	public boolean equals(Edge e){	
		//Suppose: any two vertices must have one edge at most.
		// Any edge has but only one Edge instance.
		return this.src.equals(e.getSource()) && this.dst.equals(e.getDestin());
	}
	public String getName(){
		return nameOfType;
	}
	public boolean isVisited(){
		return this.isVisited;
	}
	public void setVisited(boolean  v){
		this.isVisited=v;
	}
	public double getWeight(){
		return weight;
	}
	public void print(){		
		System.out.println(src.getName()+" -- ("+nameOfType+", "+weight+") --> "+dst.getName());
	}
	public boolean isInTree(Graph T) {
		return T.E.contains(this);
	}
}

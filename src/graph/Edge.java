package graph;

import fundamental.DBMapper;

public class Edge {
	static DBMapper edgeMap=new DBMapper("edge_type");
	//todo: private members, public methods.
	Vertex src;
	Vertex dst;
	int nameOfType;
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
	public Edge getNextEdgeInLoosePath(Vertex v, Graph g){ //get another edge of the vertex in loose path
		Vertex v1=this.getAnotherVertex(v);
		return v1.getAnyOtherEdgeInLoosePath(this, g);
	}
	public Edge clone(){
		return new Edge(this);
	}
	public Edge(Edge e){
		this.src=e.src;
		this.dst=e.dst;
		this.nameOfType=e.nameOfType;
		this.weight=e.weight;
		this.isVisited=e.isVisited;
	}
	public Edge(Vertex src, Vertex dst, String type, double w){
		this.src=src;
		this.dst=dst;
		nameOfType=edgeMap.put(type);
		weight=w;
		isVisited=false;
	}
	public Vertex getSource(){
		return this.src;
	}
	public Vertex getDestin(){
		return this.dst;
	}
	@Override
	public boolean equals(Object obj){
		//Suppose: any two vertices must have at most one edge of  a given name of type at most.
		// Any edge has but only one Edge instance.
		if (!(obj instanceof Edge)){
			return false;
		}
		if (this==obj){
			return true;
		}
		Edge that=(Edge)obj;
		return  that.getSource().getId()==this.getSource().getId() 
				&&that.getDestin().getId()==this.getDestin().getId();
	}
	public int getType(){
		return nameOfType;
	}
	public String getTypeString(){
		return edgeMap.getKey(this.getType());
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
		System.out.println(src.getNameString()+" -- ("+this.getTypeString()+", "+weight+") --> "+dst.getNameString());
	}
	@Override
	public String toString(){
		return new String(src.getNameString()+" -- ("+this.getTypeString()+", "+weight+") --> "+dst.getNameString());
	}
	public boolean isContainedByTree(Graph t){
		return t.E.contains(this);
	}
	public boolean isContainedBy(Graph g) {
		return g.contains(this);
	}
}

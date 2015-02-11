package graph;

import fundamental.DBMapper;

public class Edge {
	private static DBMapper edgeMap; 	//TODO: complement this line with "=new DBMapper("edge_type"); " 
	private int src;
	private int dst;
	private int typeOfEdge;
	private double weight;
	private boolean isVisited;
	public Vertex getAnotherVertex(Vertex v, Graph g){
		if (this.src==v.getId()){
			return g.getVertex(this.dst);
		}else if (this.dst==v.getId()){
			return g.getVertex(this.src);
		}else{
			return null;
		}
	}
	public void setWeight(double weight){
		this.weight=weight;
	}
	public static DBMapper getEdgeMap(){
		return Edge.edgeMap;
	}
	public Edge getNextEdgeInLoosePath(Vertex v, Tree g){ //get another edge of the vertex in loose path
		Vertex v1=this.getAnotherVertex(v, g);
		return v1.getAnyOtherEdgeInLoosePath(this, g);
	}
	public Edge clone(){
		return new Edge(this);
	}
	public Edge(Edge e){
		this(e.src, e.dst, e.typeOfEdge, e.weight);
		this.isVisited=e.isVisited;
	}
	public Edge(Vertex src, Vertex dst, String type, double w){
		this(src, dst, edgeMap.put(type),w);		
	}
	public Edge(Vertex src, Vertex dst, int type, double w){
		this.src=src.getId();
		this.dst=dst.getId();
		typeOfEdge=type;
		weight=w;
		isVisited=false;
	}
	public Edge(int src, int dst, int type , double w){
		this.src=new Vertex(src).getId();
		this.dst=new Vertex(dst).getId();
		typeOfEdge=type;
		weight=w;
		isVisited=false;
	}
	public int getSource(){
		return this.src;
	}
	public int getDestin(){
		return this.dst;
	}
	@Override
	public int hashCode(){
		return this.getSource()+this.getDestin()+this.getType();
	}
	@Override
	public boolean equals(Object obj){
		//Suppose: any two vertices must have at most one edge of  a given name of type at most.
		// Any edge has but only one Edge instance.
		if (obj==null){
			return false;
		}
		if (!(obj instanceof Edge)){
			return false;
		}
		if (this==obj){
			return true;
		}
		Edge that=(Edge)obj;
		return  that.getSource()==this.getSource()
				&&that.getDestin()==this.getDestin()
				&&that.getType()==this.getType();
	}
	public int getType(){
		return typeOfEdge;
	}
	public String getTypeString(){
		//return edgeMap.getKey(this.getType());
		return String.valueOf(this.typeOfEdge);
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
		System.out.println(src +" -- ("+this.getTypeString()+", "+weight+") --> "+dst);
	}
	@Override
	public String toString(){
		return new String(src +" -- ("+this.getTypeString()+", "+weight+") --> "+dst);
	}
	public boolean isContainedBy(Graph g) {
		return g.contains(this);
	}
}	




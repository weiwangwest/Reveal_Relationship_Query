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
	@Override
	public boolean equals(Object obj){
		//Suppose: any two vertices must have at most one edge of  a given name of type at most.
		// Any edge has but only one Edge instance.
		boolean result=true;
		if (obj instanceof Edge){
			Edge e=(Edge)obj;
			if (e.getSource()!=null){
				result = result && this.src.equals(e.getSource()); 
			}
			if (e.getDestin()!=null){
				result = result && this.dst.equals(e.getDestin());
			}
			if (e.getType()!=null){
				result = result && this.nameOfType.equals(e.getType());
			}
		}else{
			result=false;
		}
		return result;
	}
	public String getType(){
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
	public String toString(){
		return new String(src.getName()+" -- ("+nameOfType+", "+weight+") --> "+dst.getName());
	}
	public boolean isContainedBy(Graph g) {
		return g.contains(this);
	}
}

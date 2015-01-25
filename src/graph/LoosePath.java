package graph;

import java.util.*;
public class LoosePath implements Comparable<LoosePath>{
	double weight;
	ArrayList<Edge> edges;
	public LoosePath(ArrayList<Edge>edges){
		this.edges=new ArrayList<Edge>();
		this.weight=0;
		for (Edge e: edges){
			this.edges.add(e);
			this.weight += e.getWeight();
		}
	}
	public void print(){
		System.out.println("--------Loose path-----------");
		for (Edge e: edges){
			e.print();			
		}
	}
	public LoosePath(){
		weight=0;
		edges=new ArrayList<Edge>();
	}
	public void add(Edge edge){	//every 2 neighboring edges must have a common vertex which connecting both of the edges.
		edges.add(edge);		//the new edge is added to the end of the loose path
		weight += edge.getWeight();
	}
	public double getWeight(){
		return this.weight;
	}
	@Override	//implement compareTo(...) in Comparable 
	public int compareTo(LoosePath lp) {
		return Double.compare(lp.getWeight(), this.weight);		//longer paths before shorter ones in priority queue.
	}
	public Vertex getStartVertex(Graph g){
		if (edges.size()==0){
			return null;
		}
		Edge e0=edges.get(0);
		if (edges.size()==1){
			return g.getVertex(e0.getSource());			
		}else{
			Edge e1=edges.get(1);
			if (e0.getDestin()==e1.getSource()||e0.getDestin()==e1.getDestin()){
				return g.getVertex(e0.getSource());
			}else{
				return g.getVertex(e0.getDestin());
			}
		}
	}
	public Vertex getEndVertex(Graph g){
		if (edges.size()==0){
			return null;
		}
		Edge e0=edges.get(edges.size()-1);
		if (edges.size()==1){
			return g.getVertex(e0.getDestin());			
		}else{
			Edge e1=edges.get(edges.size()-2);
			if (e0.getSource()==e1.getSource()||e0.getSource()==e1.getDestin()){
				return g.getVertex(e0.getDestin());
			}else{
				return g.getVertex(e0.getSource());
			}
		}
	}
	/**Returns vertices within a loose path, not including the start and end vertices.
	 * When the loose path consists of a single edge, returns an empty ArrayList<Vertex>.
	 */
	public ArrayList<Vertex> getVerticesWithinPath(Graph g){
		ArrayList<Vertex>	vertices=new ArrayList<Vertex>();
		Edge previous=null;
		for(Edge current: edges){
				if (previous !=null){
					if (current.getSource()==previous.getSource()||current.getSource()==previous.getDestin()){
						vertices.add(g.getVertex(current.getSource()));
					}else{ // (current.dst==previous.src||current.dst==previous.dst)
						vertices.add(g.getVertex(current.getDestin()));
					}
				}
				previous=current;
		}
		return vertices;
	}

	public HashSet <Edge> getEdges(){
		return new HashSet<Edge>(this.edges);
	}
	
	public ArrayList<Vertex> getVertices(Graph g){	//list of vertices in the path, excluding the source and destination vertices.
		ArrayList<Vertex>	vertices=new ArrayList<Vertex>();
		vertices.add(this.getStartVertex(g));
		vertices.addAll(this.getVerticesWithinPath(g));
		vertices.add(this.getEndVertex(g));
		return vertices;
	}
}

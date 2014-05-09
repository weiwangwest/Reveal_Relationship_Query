import java.util.*;
public class LoosePath implements Comparable<LoosePath>{
	double weight;
	ArrayList<Edge> edges;
	public LoosePath(ArrayList<Edge>edges){
		this.edges=new ArrayList<Edge>();
		this.weight=0;
		for (Edge e: edges){
			this.edges.add(e);
			this.weight += e.weight;
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
	public int compareTo(LoosePath lp) {
		return Double.compare(lp.getWeight(), this.weight);		//longer paths before shorter ones in priority queue.
	}
	public Vertex getStartVertex(){
		if (edges.size()==0){
			return null;
		}
		Edge e0=edges.get(0);
		if (edges.size()==1){
			return e0.src;			
		}else{
			Edge e1=edges.get(1);
			if (e0.dst==e1.src||e0.dst==e1.dst){
				return e0.src;
			}else{
				return e0.dst;
			}
		}
	}
	public Vertex getEndVertex(){
		if (edges.size()==0){
			return null;
		}
		Edge e0=edges.get(edges.size()-1);
		if (edges.size()==1){
			return e0.dst;			
		}else{
			Edge e1=edges.get(edges.size()-2);
			if (e0.src==e1.src||e0.src==e1.dst){
				return e0.dst;
			}else{
				return e0.src;
			}
		}
	}
	/**Returns vertices within a loose path, not including the start and end vertices.
	 * When the loose path consists of a single edge, returns an empty ArrayList<Vertex>.
	 */
	public ArrayList<Vertex> getVerticesWithinPath(){
		ArrayList<Vertex>	vertices=new ArrayList<Vertex>();
		Edge previous=null;
		for(Edge current: edges){
				if (previous !=null){
					if (current.src==previous.src||current.src==previous.dst){
						vertices.add(current.src);
					}else{ // (current.dst==previous.src||current.dst==previous.dst)
						vertices.add(current.dst);
					}
				}
				previous=current;
		}
		return vertices;
	}
	public ArrayList<Vertex> getVertices(){	//list of vertices in the path, excluding the source and destination vertices.
		ArrayList<Vertex>	vertices=this.getVertices();
		vertices.add(0, this.getStartVertex());
		vertices.add(this.getEndVertex());
		return vertices;
	}
}

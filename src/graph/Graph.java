package graph;

import fundamental.DBMapper;
import fundamental.MyLinkedList;

import java.util.*;

public class Graph implements Iterable<Edge> {
	public static final int GRAPH_CAPACITY = 32553223;
	public static final int TREE_CAPACITY = 2048;
	public static final int NORMAL_CAPACITY = 100000; // not sure why this size
	// public HashMap<Integer, Vertex> V; //max vertex id =32553223 (data-6.nq)
	private VertexArray V;
	// public ArrayList<Edge> E;
	private int edgeNumber;

	public VertexArray V() {
		return this.V;
	}

	public void clearVertex() {
		this.V.clear();
	}

	public int vertexCapacity() {
		return this.V.capacity();
	}

	public int vertexNumber() {
		return this.V.size();
	}

	public Collection <Vertex> vertices() {
		return this.V.values();
	}

	public Set<Integer> vertexKeySet() {
		return this.V.keySet();
	}

	public Vertex getVertex(int key) {
		return this.V.get(key);
	}

	public HashSet<Vertex> getVertices(Collection<Vertex>verticesSet){
		HashSet<Vertex> result=new HashSet<Vertex>();
		for (Vertex v:verticesSet){
			if (this.contains(v)){
				result.add(this.getVertex(v.getId()));
			}
		}
		return result;
	}
	@SuppressWarnings("hiding")
	private class EdgeIterator<Edge> implements Iterator<Edge> {

		boolean started = false;
		int vId; // vertex id which has edge eId
		int eId; // index of current available edge in edges list of vertex vId
		int vIdNext; // vertex id which has edge eIdNext
		int eIdNext; // index of next available edge in edges list of vertex
						// vIdNext

		public EdgeIterator() {
			this.findNext();
		}

		@Override
		public boolean hasNext() {
			return (this.vId != -1 && this.eId != -1);
		}

		@SuppressWarnings("unchecked")
		@Override
		// use @Override whenever implementing a interface method
		public Edge next() {
			Edge e = null;
			if (vId != -1 && eId != -1) {
				e = (Edge) (Graph.this.V).get(vId).edges.get(eId);
			}
			this.findNext();
			return e;
		}

		/**
		 * Update vId, eId, vIdNext and eIdNext to the next edge position, so
		 * that all variables are prepared for calling hasNext() and next()
		 */
		private void findNext() {
			if (!this.started) { // first run
				this.updateNextPosition(); // get the next position
				this.started = true;
			}
			this.vId = this.vIdNext; // move current position to the next
			this.eId = this.eIdNext;
			this.updateNextPosition(); // renew the next position
		}

		/**
		 * Update vIdNext and eIdNext with corresponding values of the next
		 * available edge if it can be found, otherwise with -1
		 * 
		 * @return true if found, false if not found.
		 */
		private boolean updateNextPosition() {
			if ((!started) || (started && vIdNext != -1 && eIdNext != -1)) {
				if (!started) { // if first time, start from the first vertex,
								// the first edge.
					vIdNext = 0;
					eIdNext = -1;
				}
				eIdNext = availableEdgeId(vIdNext, eIdNext + 1);
				// current vertex has an available edge
				if (eIdNext != -1) {
					return true;
				}
				// current vertex has no available edge
				for (vIdNext = vIdNext + 1; vIdNext <= Graph.this.V.capacity(); vIdNext++) {
					eIdNext = availableEdgeId(vIdNext, 0);
					if (eIdNext != -1) { // found next vertex and next edge
						return true;
					}
				}
			}
			// no next vertex or not found
			vIdNext = -1;
			eIdNext = -1;
			return false;
		}

		/**
		 * Search for the next available edge in the edges list of vertex vId
		 * 
		 * @param vId
		 *            given vertex
		 * @param eId
		 *            possible smallest edge index in the edge list of vertex id
		 * @return the smallest available edge index in the edge list of vertex
		 *         vId if found, -1 if not found.
		 */
		private int availableEdgeId(int vId, int eId) {
			Vertex v = Graph.this.V.get(vId);
			if (v != null && v.edges != null) {
				for (int i = eId; i < v.edges.size(); i++) {
					if (v.edges.get(i).getSource()== vId) {
						return i;
					}
				}
			}
			return -1;
		}

		public void remove() {
			// implement... if supported.
		}
	}

	public class ArtificialSteinerTree {
		Tree steinerTree = null;
		int edgeType;
		private boolean toBeRemoved;

		public ArtificialSteinerTree(HashMap<Integer, Vertex> VPrime) {
			steinerTree = new Tree(TREE_CAPACITY);
			edgeType = Integer.MAX_VALUE; // "aritificialSteinerTreeEdge: "+new java.rmi.dgc.VMID().toString();
			// add vertices & terminals from VPrime
			for (Integer key : VPrime.keySet()) { 
				steinerTree.addVertex(key);
				steinerTree.addTerminal(steinerTree.getVertex(key));
			}
			// create aritificial edges
			int k = 0;
			Integer previousKey = null;
			for (Integer key : steinerTree.vertexKeySet()) {
				if (k != 0) {
					// to do: add only one vertex and one edge into the program space, not two!!!
					Graph.this.addEdge(V.get(key).getId(), V.get(previousKey).getId(),	edgeType, 1000);
					steinerTree.addEdge(new Edge(V.get(key), V.get(previousKey), 	edgeType, 1000));
					Graph.this.edgeNumber++;
				}
				previousKey = key;
				k++;
			}
		}

		public void removeArificialSteinerTree() {
			toBeRemoved = false;
			HashSet <Edge> edgesToBeRemoved=new HashSet<Edge>();
			Iterator<Edge> iter=steinerTree.iterator();
			while (iter.hasNext()){
				edgesToBeRemoved.add(iter.next());
			}
			for (Edge e : edgesToBeRemoved) {
				/*
				 * if (Graph.this.E !=E){
				 * System.out.println("outer class object can't be accessed");
				 * System.exit(0); }
				 */Graph.this.removeEdge(e);
			}
		}

		@Override
		public void finalize() {
			if (toBeRemoved) {
				removeArificialSteinerTree();
			}
		}

		public Tree getTree() {
			return steinerTree;
		}
	}

	public boolean containsAll(MyLinkedList<Edge> ea) {
		boolean result = true;
		for (Edge e : ea) {
			if (!this.containsEdge(e)) {
				result = false;
				break;
			}
		}
		return result;
	}

	public boolean contains(Object o) {
		boolean result = false;
		if (o instanceof Vertex) {
			Vertex v = (Vertex) o;
			result = this.V.containsKey(v.getId());
		}
		if (o instanceof Edge) {
			Edge e = (Edge) o;
			return (this.containsEdge(e));
		}
		return result;
	}
	
	public boolean containsEdge(int src, int dst, int type, double weight){
		Vertex vSrc=this.getVertex(src);
		Vertex vDst=this.getVertex(dst);
		if (vSrc==null || vDst==null){
			return false;
		}
		return (this.containsEdge(new Edge(src, dst, type, weight)));
	}
	
	private boolean containsEdge(Edge e) {
		Vertex src=this.getVertex(e.getSource());
		Vertex dst=this.getVertex(e.getDestin());
		if (src==null || dst==null){
			return false;
		}
		return (src.getEdges().contains(e) 
				|| (dst.getEdges().contains(e)));
	}

	public Graph(Graph g) {
		this.V = (VertexArray) g.V.clone();
		this.removeShadowEdges();	//solve the problem that duplicate edges referenced by src and dst vertices.
		this.edgeNumber = g.edgeNumber;
	}

	public Graph(int capacityOfVertex) { // 32553223
		V = new VertexArray(capacityOfVertex); // sometimes node id starts from 1, instead of 0
		// V=new HashMap<Integer, Vertex>();
		// E=new ArrayList<Edge>();
		this.edgeNumber = 0;
	}

	public Graph(VertexArray v, ArrayList<Edge> e) {
		this.V = v;	//not deep copy
		this.edgeNumber = 0;
		for (Edge edge : e) {
			this.addEdge(edge);
		}
	}

	public Graph(HashMap<Integer, Vertex> vPrime, ArrayList<Edge> ePrime) {
		this.V = new VertexArray(NORMAL_CAPACITY);
		for (Integer i : vPrime.keySet()) {
			V.put(i, vPrime.get(i));	//not deep copy
		}
		this.edgeNumber = 0;
		for (Edge edge : ePrime) {
			this.addEdge(edge);
		}
	}

	public boolean addExistingVertex(Vertex v) {
		if (V.get(v.getId()) == null) {
			V.put(v.getId(), v);
			return true;
		} else {
			return false;
		}
	}
	public boolean addVertex(int i){
		if (V.get(i) == null) {
			this.V.put(i, new Vertex(i));
			return true;
		} else {
			return false;
		}		
	}

	public boolean addEdge(String from, String to, String type, double weight) {
		return addEdge(V.get(Vertex.vertexMap.getValue(from)).getId(),
				V.get(Vertex.vertexMap.getValue(to)).getId(),
				graph.Edge.getEdgeMap().put(type), weight);
	}

	public boolean addEdge(int from, int to, int type, double weight) {
		Vertex src = V.get(from);
		Vertex dst = V.get(to);
		if (src != null && dst != null) {
			addEdge(new Edge(src, dst, type, weight));
			return true;
		} else {
			return false;
		}
	}

	protected void addEdge(Edge e) {
		this.getVertex(e.getSource()).addAdjacency(e); // For Steiner tree problem, degree=in degree+out degree
		this.getVertex(e.getDestin()).addAdjacency(e); // For Steiner tree problem, degree=in degree+out degree
		this.edgeNumber++;
	}

	@Override
	public int hashCode(){
		return this.V.size()+this.edgeNumber;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj==null){
			return false;
		}
		if (! (obj instanceof Graph)) {
			return false;
		}
		Graph g = (Graph) obj;
		boolean result = true;
		result = result && this.V.equals(g.V);
		// when two graphs share a shallow copy, 
		// to delete one edge from one may cause another to have a wrong edgeNumber.
		//result = result && this.edgeNumber == g.edgeNumber; 
		for (Vertex v : g.V.values()) {
			result = result && this.containsAll(v.edges); //TODO: containsAll() has low performance.
		}
		for (Vertex v: this.V.values()){
			result = result && g.containsAll(v.edges);
		}
		return result;
	}

	public void removeEdge(int from, int to, int name) {
		this.removeEdge(new Edge(V.get(from), V.get(to), name, 0));
	}

	public void removeEdge(Edge e) {
		if (e != null) {
			if (!this.getVertex(e.getSource()).getEdges().remove(e)){
				System.out.println("edge not found exception!");
				System.exit(-1);
			}
			if (!this.getVertex(e.getDestin()).getEdges().remove(e)){
				System.out.println("edge not found exception!");
				System.exit(-1);
			}
			this.edgeNumber--;
		}
	}
	private void removeShadowEdges(){
		for (int i=0; i<=V.capacity(); i++){
			Vertex v=V.get(i);
			if (v!=null){
				for (Edge e: v.edges){
					if  (e.getSource()==v.getId()){	//let the Edge in src list replace the Edge in dst list
						V.get(e.getDestin()).edges.remove(e);
						V.get(e.getDestin()).edges.add(e);
					}
				}
			}
		}
	}
	public void checkShadowEdges(){
		if (this.shadowEdges().size()>0){
			throw new ArrayIndexOutOfBoundsException();
		}
	}
	public ArrayList<Edge> shadowEdges(){
		int matchedEdgeNumber=0; 
		int mismatchEdEdgeNumber=0;
		ArrayList<Edge>edges=new ArrayList<Edge>();
		for (int i=0; i<=V.capacity(); i++){
			Vertex v=V.get(i);
			if (v!=null){
				for (Edge e: v.edges){
					int idx=edges.indexOf(e);
					if (idx==-1){		//new edge
						edges.add(e);
						mismatchEdEdgeNumber ++;
					}else{
						if (e==edges.get(idx)){	//edges pair
							edges.remove(idx);
							mismatchEdEdgeNumber --;
							matchedEdgeNumber ++;
						}else{		// equal edges not match each other 
							edges.add(e);
							mismatchEdEdgeNumber ++;
						}
					}
				}
			}
		}
		return edges;
	}
	public void removeVertex(int vId) {
		Vertex v = this.getVertex(vId);
		if (v != null) {
			HashSet <Edge>es=new HashSet<Edge>();		//use HashSet to prevent selfcircle Edge added twice
			for (Edge e : v.edges)
					es.add(e);
			for (Edge e: es)
					this.removeEdge(e);
			this.V.remove(vId); // remove the vertex from the graph's vertices list.
		}
	}

	public Edge getAnyEdgeBetween(String v1Name, String v2Name) {
		return getAnyEdgeBetween(Vertex.vertexMap.getValue(v1Name),
				Vertex.vertexMap.getValue(v2Name));
	}

	public Edge getAnyEdgeBetween(Integer v1Id, Integer v2Id) {
		return V.get(v1Id).getAnyEdgeBetween(V.get(v2Id));
	}

	public double getWeight(){
		double weight=0;
		Iterator<Edge> it=this.iterator();
		while (it.hasNext()){
			weight += it.next().getWeight();
		}
		return weight;
	}
	public Edge getDirectedEdgeBetween(Integer start, Integer end) {
		return V.get(start).getDirectedEdgeTo(V.get(end));
	}

	public void print() { // print each node and its edges
		Iterator<Integer> iterator = V.keySet().iterator();
		System.out.println("\n-----------graph----------------");
		while (iterator.hasNext()) {
			Vertex v = V.get(iterator.next());
			System.out.println("Vertex: " + v.getNameString() + ", degree="
					+ v.getDegree());
			for (Edge e : v.edges) {
				System.out.print("\t\t");
				e.print();
			}
		}
	}

	public void printVerticesStastistics() { // print overall analysis on edges types.
		int maxDegree = -1;
		for (Vertex v : V.values()) {
			int degree = 0;
			for (Edge e : v.edges) {
				if (this.contains(e)) {
					degree++;
				}
			}
			if (degree > maxDegree) {
				maxDegree = degree;
			}
		}
		System.out.println("-------------VerticesStastistics-------------");
		System.out.println("Number of vertices: " + V.size());
		System.out.println("Max degree=" + maxDegree);
	}

	public void printEdgesStastistics() { // print overall analysis on edges.
		double totalWeight = 0;
		Map<String, Integer> edgeTypes = new HashMap<String, Integer>();
		int edgeVisited = 0;
		OUT: for (Vertex v : this.V.values()) { // for all vertices
			for (Edge e : v.edges) { // for all edges of a vertex
				if (this.getVertex(e.getSource()) == v) { // only count edges for the source vertex
					totalWeight += e.getWeight();
					String type = e.getTypeString();
					if (edgeTypes.containsKey(type)) { // existing type, count++
						edgeTypes.put(type, (edgeTypes.get(type) + 1));
					} else { // new type, count=1
						edgeTypes.put(type, 1);
					}
					edgeVisited++;
					if (edgeVisited == this.edgeNumber) {
						break OUT;
					}
				}
			}
		}
		System.out.println("-------------EdgesStastistics-------------");
		System.out.println("Number of edges: " + this.edgeNumber);
		System.out.println("Total weight: " + totalWeight);
		Set<String> keys = edgeTypes.keySet();
		for (String key : keys) {
			System.out.println(key + ": " + edgeTypes.get(key));
		}
	}

	public Graph clone() {
		return new Graph(this);
	}

	public void clearVisited() {
		for (Vertex v : this.V.values()) {
			v.setVisited(false);
			for (Edge e : v.edges) {
				e.setVisited(false);
			}
		}
	}

	public void setEdgeWeight(String from, String to, String name, double weight) { // to
																					// do:
																					// test
																					// this
																					// method.
		setEdgeWeight(from == null ? 0 : Vertex.vertexMap.getValue(from),
				to == null ? 0 : Vertex.vertexMap.getValue(to),
				name == null ? 0 : Edge.getEdgeMap().getValue(name), weight);
	}

	public void setEdgeWeight(int from, int to, int name, double weight) { // to
																			// do:
																			// test
																			// this
																			// method.
		Edge edge = new Edge(from == 0 ? null : V.get(from), to == 0 ? null
				: V.get(to), name, weight);
		boolean found = false;
		Vertex src = this.V.get(from);
		int idx;
		if (src != null && (idx = src.edges.indexOf(edge)) != -1) {
			found = true;
			src.edges.get(idx).setWeight(weight);
		}
		if (!found) {
			System.out.println("edge not found:");
			edge.print();
			System.exit(-1);
		}
	}

	public void addAll(Graph that){
		for (Vertex v : that.V.values()) {
			//this.addExistingVertex(v);
			this.addVertex(v.id);	//TODO: or addExistingVertex()?
		}
		Iterator<Edge> it = that.iterator();
		while (it.hasNext()) {
			this.addEdge(it.next());
		}
	}

	// remove isolated entities
	public void removeIsolatedVertices() {
		Vertex vToBeDelete;
		do {
			vToBeDelete = null;
			for (Vertex v : this.V.values()) {
				if (v.getDegree() == 0) {
					vToBeDelete = v;
					break;
				}
			}
			if (vToBeDelete != null) {
				this.removeVertex(vToBeDelete.getId());
			}
		} while (vToBeDelete != null);
	}

	public int getEdgeNumber() {
		return this.edgeNumber;
	}

	@Override
	public Iterator<Edge> iterator() {
		return new EdgeIterator<Edge>();
	}
	public String toString(){
		StringBuffer result=new StringBuffer();
		Iterator<Integer> iterator=this.vertexKeySet().iterator();
		while (iterator.hasNext()){
			Vertex v=this.getVertex(iterator.next());
			if (v.isContainedBy(this)){				
				result.append("Vertex: "+v.getNameString()+", degree="+v.getDegreeInGraph(this)+"\n");
				for (Edge e: v.edges){
					if (e.isContainedBy(this)){						
						result.append("\t\t");
						result.append(e.toString()+"\n");
					}
				}
			}
		}
		result.append("total weight of edges: "+this.getWeight()+"\n");
		result.append(this.shadowEdges()+"\b");
		return result.toString();
	}
	public HashSet<Edge> edgeSet(){
		HashSet<Edge> edges = new HashSet<Edge>();
		Iterator <Edge>it=this.iterator();
		while(it.hasNext()){
			edges.add(it.next());
		}
		return edges;
	}
	public ArrayList<Edge> edgeArray(){
		ArrayList<Edge> edges = new ArrayList<Edge>();
		Iterator <Edge>it=this.iterator();
		while(it.hasNext()){
			edges.add(it.next());
		}
		return edges;
	}
}

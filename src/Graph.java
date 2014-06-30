import java.util.*;
public class Graph {
	TreeMap<String, Vertex> V;
	 ArrayList<Edge> E;

		public boolean isInTree(Edge e){
			return this.E.contains(e);
		}		
		
	 public Graph(){
		V=new TreeMap<String, Vertex>();
		E=new ArrayList<Edge>();
	}
	public Graph(TreeMap<String, Vertex> v,  ArrayList<Edge> e){	
		this.V=v;
		this.E=e;
	}

	/**decides whether a graph contains a tree with its vertices and edges marked as isInTree();
	 * @return
	 */
/*	public boolean hasATree(){
			int treeVertices=0;			//calculate the number of tree vertices
			Set<String> keys=V.keySet();
			for (String key: keys){
				Vertex v=V.get(key);
				if (v.isInTree()){
					treeVertices++;
					if (v.getDegree()==0){		//isolated vertex!
						return false;
					}
				}
			}
			int treeEdges=0;	//calculate the number of tree edges
			for (Edge e: E){
				if (e.isInTree()){
					treeEdges ++;
				}
			}
			return (treeVertices-1==treeEdges);
	}
*/	
	public boolean isATree(){
		//Every node and edge must be in Tree.
		//every node has at least one tree edge
		//every edge has its dst and src connected to a tree node
		//|V|=|E|+1;
		int treeVertices=0;			//calculate the number of tree vertices
		for (Vertex v: V.values()){
			if (!v.isInTree(this)){
				System.err.println("Vertex not in tree: "+v.getName());
				return false;
			}
			if (v.getDegreeInTree(this)==0 && V.values().size()>1){
				System.err.println("Vertex has no edge connected: "+v.getName());
				return false;	
			}
			treeVertices++;
		}
		int treeEdges=0;	//calculate the number of tree edges
		for (Edge e: E){
			if (!e.isInTree(this)){
				System.err.println("Edge not in tree: ");
				e.print();
				return false;
			}
			if (!e.getSource().isInTree(this)||!e.getDestin().isInTree(this)){
				System.err.println("Edge connects vertex/verticis that can not be found in the tree:");
				e.print();
				return false;
			}
			treeEdges ++;
		}
		return (treeVertices-1==treeEdges);
	}
	public void addVertex(String name){	//create a Vertex instance
		if (V.containsKey(name)){
			//System.err.println("Vertex name already exists!"); 
			//System.exit(-1);
		}else{
			V.put(name, new Vertex(name));
		}
	}	
	public void addEdge(String from, String to, String type, double weight){ //create an Edge instance
		if (!V.containsKey(from)){
			System.err.println("Couldn't find "+from);
			System.exit(-1);
		}
		if (!V.containsKey(to)){
			System.err.println("Couldn't find "+to);
			System.exit(-1);
		}
		Edge e=new Edge(V.get(from), V.get(to), type, weight); 
		V.get(from).addAdjacency(e);				//For Steiner tree problem, degree=in degree+out degree
		V.get(to).addAdjacency(e);				//For Steiner tree problem, degree=in degree+out degree
		if (!E.add(e)){
			System.err.println("Failed to add edge!");
			System.exit(-1);
		}
	}
	public void removeEdge(String from, String to){
		Edge e=new Edge(V.get(from), V.get(to), "", 0);
		V.get(from).edges.remove(e);		//remove edge from source Vertex's edges list
		V.get(to).edges.remove(e);	//remove edge from dest Vertex's edges list
		E.remove(e);		////remove edge from graph's edges list
	}
	public void removeEdgeFromTree(String from, String to, Graph T){	
		Edge e=new Edge(V.get(from), V.get(to), "", 0);
//		T.E.get(this.E.indexOf(e)).setInTree(false);		//don't remove it from the memory record (the original graph), the following stmt has same effect:
		//this.	E.get(this.E.indexOf(e)).setInTree(false);	
		T.E.remove(e);	//only remove from the tree
	}
	public void removeVertexFromTree(String name, Graph T){
		Vertex v=T.V.get(name);
		for (Edge e: v.edges){
			if (e.isInTree(T)){
//				e.setInTree(false);		//mark the edge as not in tree
				T.E.remove(e);		//remove the edge from Tree, please don't care about the other vertex of the edge in the tree.
			}
		}
//		v.setIsInTree(false);	//mark the vertex as not in tree
		T.V.remove(name);	//remove the vertex from Tree
	}
	
	public void removeVertex(String name){
		Vertex v=V.get(name);
		for (Edge e: v.edges){
			if (e.src.equals(v)){		//remove the edge from the other relative vertex's edges list.
				e.dst.edges.remove(e);
			}else{
				e.src.edges.remove(e);
			}
			E.remove(e);		//remove the edge from the graph's edges list.
		}
		V.remove(name);	//remove the vertex from the graph's vertices list.
	}
	public Edge getDirectedEdge(String start, String end){
		return V.get(start).getEdgeTo(V.get(end));
	}	
	public void print(){	//print each node and its edges
		Iterator<String> iterator=V.keySet().iterator();
		while (iterator.hasNext()){
			Vertex v=V.get(iterator.next());
			System.out.println("Vertex: "+v.getName()+", degree="+v.getDegree());
			for (Edge e: v.edges){
					System.out.print("\t\t");
					e.print();
			}
		}
	}
	public void printVerticesStastistics(){	//print overall analysis on edges types.
		int maxDegree=-1;
		for (Vertex v: V.values()){
			int degree=0;
			for (Edge e: v.edges){
					if (E.contains(e)){
						degree ++;
					}
			}
			if (degree>maxDegree){
				maxDegree=degree;
			}
		}
		System.out.println("-------------VerticesStastistics-------------");
		System.out.println("Number of vertices: "+V.size());
		System.out.println("Max degree="+maxDegree);
	}
	public void printEdgesStastistics(){	//print overall analysis on edges.
		double totalWeight=0;
		Map<String, Integer> edgeTypes=new HashMap<String, Integer>();
		for (Edge e: E){
				totalWeight += e.getWeight();
				String type=e.getName();
				if (edgeTypes.containsKey(type)){	//existing type, count++
					edgeTypes.put(type, (edgeTypes.get(type)+1));
				}else{		//new type, count=1
					edgeTypes.put(type, 1);						
				}
		}
		System.out.println("-------------EdgesStastistics-------------");
		System.out.println("Number of edges: "+E.size());
		System.out.println("Total weight: "+totalWeight);
		Set<String> keys=edgeTypes.keySet();
		for (String key: keys){
			System.out.println(key+": "+edgeTypes.get(key));
		}
	}
	public void getTreebyVertex(Graph src, Vertex v,  Graph dst){ // starting from a vertex, traverse the tree in a root first sequence, save its vertices in tree
		if (!v.isInTree(src) || dst.V.values().contains(v)){		// recurse stop condition
			return; 	
		}
		dst.V.put(v.name, v);	//add this vertex
		for (Edge e: v.edges){	//add edges
			if (e.isInTree(src) && !dst.E.contains(e)){
				dst.E.add(e);
			}
		}
		for(Vertex child: v.getAdjacentsInTree(src).values()){	
			getTreebyVertex(src, child, dst);					//recursive call
		}
	}
	public LoosePath findNextLoosePath(Graph T){
		LoosePath lp=null;
		for (String key: T.V.keySet()){
			Vertex v=T.V.get(key);
			if (v.getUnvisitedDegreeInTree(T)==1){ 	 //find the (unvisited fixed node) == (nodes' unvisited degree ==1)
				Edge e=v.getTheOnlyUnvisitedEdgeInTree(T);
				if (lp==null){
					lp=new LoosePath();
				}
				boolean toContinue=true;
				Vertex vNext;
				do{	//find a loose path starting from the fixed node, mark nodes and edges as visited.
					v.setVisited(true);	//be careful: make sure to judge whether the other vertex is fixed before mark e as visited.
					lp.add(e);
					e.setVisited(true);
					vNext=e.getAnotherVertex(v);	//prepare to move v to the next hop
					v.setVisited(true);
					switch (vNext.getDegreeInTree(T)){
						case 1:		//we meet a leaf vertex, just mark it as visited.
							vNext.setVisited(true);
							toContinue=false;
							break;
						case 2:
							v=vNext;		//we are on a single loose path, so just move v to the next hop
							e=v.getAnotherEdgeInLoosePath(e, T); //move e to the next edge;
							toContinue=true;
							break;
						default:	//we meet a fixed vertex, its previous degree >= 3	
							toContinue=false;
							break;
					}
				}while (toContinue);
				break; //when found one loose path, break  and return it right now.
			}
			//if not for current vertex, try another one.
		}
		return lp;
	}
	/**Returns a PriorityQueue of loose paths composing a Tree T containing terminal nodes VPrime.
	 * @param T	 the Steiner Tree
	 * @return PriorityQueue of loose paths
	 */
	public PriorityQueue <LoosePath> LP(Graph T){		
		PriorityQueue <LoosePath>Q= new PriorityQueue <LoosePath>();
		LoosePath lp=this.findNextLoosePath(T);
		while (lp != null){
			Q.add(lp);
			lp=this.findNextLoosePath(T);
		}
		return Q;
	}
	public double getWeight(Graph T){
		double weight=0;
		for (Edge edge: T.E){
			if (edge.isInTree(T)){
				weight += edge.getWeight();
			}
		}
		return weight;
	}
	
	public PriorityQueue <Vertex> Q (PriorityQueue <Vertex>Q1, PriorityQueue <Vertex>Q2, int idx){
		PriorityQueue <Vertex> q= null;
		Vertex.setIdx(idx);
		switch (idx){
			case 1:
				q=Q1;
				break;
			case 2:
				q=Q2;
				break;
			default:
				break;
		}
		return q;
	}
	public Graph G(Graph T1, Graph T2, int idx){
		if (idx==1){
			return T1;
		}else{
			return T2;
		}
	}
	public LoosePath findShortestPath(Graph T1 , Graph T2, LoosePath lp){
		for (Vertex v: this.V.values()){		//1: for all v ∈ V do
			if (T1.V.values().contains(v)){	//2:	 if v ∈ V (T1 ) then d1(v) = 0 else d1 (v) = ∞
				v.d1=0;
			}else{
				v.d1=Double.POSITIVE_INFINITY;
			}
			if(T2.V.values().contains(v)){//3:	 if v ∈ V (T2 ) then d2(v) = 0 else d2 (v) = ∞
				v.d2=0;
			}else{
				v.d2=Double.POSITIVE_INFINITY;
			}
		}	//4: end for
		Vertex.setIdx(1);
		PriorityQueue <Vertex> Q1=new PriorityQueue <Vertex>(T1.V.values()); //5: PriorityQueue Q1 = V (T1 ) //ordered by inc. distance d1
		PriorityQueue<Vertex> DQ1=new PriorityQueue<Vertex>(); //all vertices dequeued from Q1;
		Vertex.setIdx(2);		
		PriorityQueue <Vertex> Q2=new PriorityQueue <Vertex>(T2.V.values()); //6: PriorityQueue Q2 = V (T2 ) //ordered by inc. distance d2
		PriorityQueue<Vertex> DQ2=new PriorityQueue<Vertex>(); //all vertices dequeued from Q2;
		int current =1; //7: current=1
		int other=2; //8: other=2
		Vertex v;
		do {//9: repeat
			if  (fringe(Q(Q1,Q2,other))<fringe(Q(Q1,Q2,current))){//10:	 if degree(Qother )<degree(Qcurrent ) then
				int temp=current;	//11:	 swap(current, other)
				current=other;
				other=temp;
			}	//12:	 end if
			v=Q(Q1,Q2,current).poll();	//13:	 v = Qcurrent .dequeue()
			Q(DQ1,DQ2,current).add(v);	//record dequeued vertex.
			if (Double.compare(v.d(current), lp.getWeight())>=0){	//14:	 if dcurrent (v) ≥ w(lp) then
				break;		//15:	 break
			}	//16:	 end if		
			for (Edge e: v.edges){	//17:	 for all (v, v' ) ∈ E do
				Vertex vPrime=e.getAnotherVertex(v);
				if (Q(DQ1,DQ2,current).contains(vPrime)){//18:	 if v' has been dequeued from Qcurrent then
					continue;	//19:	 continue
				}	//20:	 end if
				if (Double.compare(vPrime.d(current), v.d(current)+e.getWeight())>0){		//21:	 if dcurrent (v') > dcurrent(v) + w(v, v ) then
					vPrime.setd(current, v.d(current)+e.getWeight());		//22:	 dcurrent(v') = dcurrent (v) + w(v, v')
					vPrime.setPredecessor(current, v);	//23:	 v' .predecessorcurrent = v
				}	//24:	 end if
				Q(Q1, Q2, current).add(vPrime);	//25:	 Qcurrent.enqueue(v)
			}	//26:	 end for				
		}while (!(Q1.isEmpty() 
				|| Q2.isEmpty() 
				||G(T1,T2, other).V.values().contains(v)
					));  //27: until Q1 = ∅ ∨ Q2 = ∅ ∨ v ∈ V (Tother)
		//28: return path connecting T1 and T2
		if (G(T1,T2, other).V.values().contains(v)){	//v is inside the other subTree.
			ArrayList<Edge> result=new ArrayList<Edge>(); 
			while (!G(T1,T2, current).V.values().contains(v)){ //v has not reach the current subTree.
				Vertex vPrime=v;
				v=v.getPrecedessor(current);
				result.add(v.getEdgeTo(vPrime));//construct the result
			}
			return new LoosePath(result);
		}else{
			return lp;	//??????
		}
	}
	public int fringe(PriorityQueue <Vertex>Q){
		Vertex v=Q.peek();
		return v.getDegree();	//wrong: to do
	}
	public Graph clone(){
		Graph temp=new Graph();
		temp.E=(ArrayList<Edge>) this.E.clone();
		temp.V=(TreeMap<String, Vertex>) this.V.clone();
		return temp;
	}
	/** this method changes Tree T by replacing loose path lp with another shorter loose path.
	 * @param lp
	 * @param T
	 * @return
	 */
	public Graph replace(LoosePath lp, Graph T){
		//removes the loose path lp from T, which will split T into two subtrees T1 and T2.
		// We don't want to change T!!!
		//so we made a copy of T, call temp
		Graph temp=T.clone();
		ArrayList<Vertex> vertices=lp.getVerticesWithinPath();
		if (vertices.size()==0){		//if there is only one edge connecting T1 and T2, remove that edge.
			Edge e=lp.edges.get(0);
			removeEdgeFromTree(e.src.name, e.dst.name, temp);	//temp has been changed
		}else{	//if there is more than one vertex to be deleted, just delete those vertices (edges will automatically deleted.)
			for (Vertex v: vertices){
				temp.removeVertexFromTree(v.name, temp);	//temp has been changed
			}
		}
		//Now T has been devided into to parts T1 and T2. So T is not a tree any more.
		//find the resulted subtrees of T1 and T2
		Graph T1=new Graph();
		Graph T2=new Graph();
		this.getTreebyVertex(temp, lp.getStartVertex(), T1);
		this.getTreebyVertex(temp, lp.getEndVertex(), T2);
		assert T1.isATree():"T1 is not a tree";
		assert T2.isATree():"T2 is not a tree";
		//T1.printTree(T1);
		//T2.printTree(T2);
		// fint the shortest path in G that connect T1 and T2 into a new tree  of lower weight.
		LoosePath lpNew=this.findShortestPath(T1 , T2, lp);		//to do: test it by comparing with hand work
		temp=createTree(T1, T2, lpNew);	//to do: : test it by comparing with hand work
		return temp;
	}
	private Graph createTree(Graph t1, Graph t2, LoosePath lpNew) {
		Graph T=new Graph();
		T.E.addAll(t1.E);
		T.E.addAll(t2.E);
		for (Edge e: lpNew.edges){
//			e.setInTree(true);
			T.E.add(e);
		}
		T.V.putAll(t1.V);
		T.V.putAll(t2.V);
		for (Vertex v: lpNew.getVerticesWithinPath()){
//			v.setIsInTree(true);
			T.V.put(v.name, v);
		}		
		return T;
	}

	/**improveTree will change the instance referenced by T
	 * which may possibly lead to a new tree with a smaller total weight.
	 * @param T
	 */
	public Graph improveTree(Graph T ){
		//generate loose paths, add each of them to Q
		T.clearVisited();
		PriorityQueue <LoosePath> Q=LP(T); //	1: priorityQueue Q = LP (T ) //ordered by decreasing weight
		int numOfIter=0;
		while (Q.size() >0){	//	2: while Q.notEmpty() do
			LoosePath lp=Q.poll();	// lp = Q.dequeue()
			Graph TPrime = this.replace(lp, T);	// TPrime ← Replace(lp, T ) without changing T!!!
			TPrime.clearVisited();
			//assert TPrime.isATree():"TPrime is not a tree";		//TPrime.isATree()
			//TPrime.printTree(TPrime);		//To do: why is not Tprime A tree again?
			//T.printTree(T);	
			//assert T.isATree():"TPrime is a tree, but T is not a tree!";
			if (getWeight(TPrime ) < getWeight(T )){	// if w(T ) < w(T ) then
				 T = TPrime;	// T = T
				 numOfIter++;
				 System.out.println("--------------Iteration NO: "+numOfIter+"--------------");
				 T.printTree(T);
				 Q = LP (T ); //	 Q = LP (T ) //ordered by decreasing weight
			 }		// end if
		}	//end while
		return T;
	}
	/** get a breath first spanning tree of from V and E
	 * @param V
	 * @param V
	 * @param E
	 * @return Graph T
	 */
	public Graph getBreathFirstSpanningTree(Map<String, Vertex> V, ArrayList<Edge> E){
		LinkedList<Vertex> S=new LinkedList<Vertex>();	//vertices of T
		TreeMap<String, Vertex> VPrime=new TreeMap<String, Vertex>();		//working vertices
		Iterator<String> iterator=V.keySet().iterator();
		String name=iterator.next();
		Vertex vPrime=V.get(name);		//v1 is the root of the spanning tree
		S.add(vPrime); 			//ordered list of vertices of a fix level
//		vPrime.setIsInTree(true);
		VPrime.put(name, vPrime);
		ArrayList<Edge> EPrime=new ArrayList<Edge>();		//no edges in the spanning tree yet
		while (true){
			boolean added=false;
			for (Vertex x: S){		// for each x in S, in order
				for (String y: V.keySet()){
					if (!VPrime.containsKey(y)){		// for each y in V - V’
						Edge e=this.getDirectedEdge(x.getName(), y);
						if (e!=null){	//if (x,y) is an edge then
//							e.setInTree(true);
							EPrime.add(e);	//add edge (x,y) to E’ and vertex y to V’
//							V.get(y).setIsInTree(true);
							VPrime.put(y, V.get(y));
							added=true;
						}
					}
				}
			}
			if (!added ){		// if no edges were added then
				return new Graph(VPrime, EPrime);		//returns T
			}
			//	 S := children of S
			LinkedList<Vertex>temp=new LinkedList<Vertex>();
			for (Vertex v: S){
				temp.addAll(v.getAdjacents().values());
			}
			S=temp;
		}
	}
	
	public Graph getFirstSteinerTree(Map<String, Vertex> VPrime){		
		Graph tree=this.getBreathFirstSpanningTree(this.V, this.E);		//find a tree for the whole vertices of the graph.
		//reduce the tree, so that it only contains the necessary vertices: i.e. all leaf vertices are terminal vertices.
		boolean changed;
		do{
			changed=false;
			Vertex vmem=null;
			for (Vertex v: tree.V.values()){
				if (!VPrime.values().contains(v) && v.getDegreeInTree(tree)==1){ 	//todo: tree or this?
					vmem = v;
					changed=true;
					break;
				}
			}
			if 	(changed){
				this.removeVertexFromTree(vmem.getName(), tree);
			}
		} while (changed);
		return tree;
	}
	
	public Graph getArtificialSteinerTree(TreeMap<String, Vertex>VPrime){
		Graph resultGraph=new Graph();
		resultGraph.V.putAll(VPrime);
		int k=0;
		String previousKey="";
		for (String key: resultGraph.V.keySet()){
			if (k!=0){
				resultGraph.addEdge(this.V.get(key).getName(), this.V.get(previousKey).getName(), "myType", 1000);
			}
			previousKey=key;
			k++;
		}
		return resultGraph;
	}
	public void clearVisited(){
		for (Edge e: this.E){
			e.setVisited(false);
		}
		for (Vertex v: this.V.values()){
			v.setVisited(false);
		}
	}
	public void clearTerminal(){
		for (Vertex v: this.V.values()){
			v.setTerminal(false);
		}
	}
	public void clearAll(){
		this.clearVisited();
		this.clearTerminal();
	}
	public void printTree(Graph T){
		Iterator<String> iterator=V.keySet().iterator();
		while (iterator.hasNext()){
			Vertex v=V.get(iterator.next());
			if (v.isInTree(T)){				
				System.out.println("Vertex: "+v.getName()+", degree="+v.getDegreeInTree(T));
				for (Edge e: v.edges){
					if (e.isInTree(T)){						
						System.out.print("\t\t");
						e.print();
					}
				}
			}
		}
	}
	public void setEdgeWeight(String from, String to, String name, double weight){
		Edge edge=new Edge(from ==null?null:V.get(from), to==null?null:V.get(to), name, weight);
		boolean found=false;
		for (Edge e: this.E){
			if (e.equals(edge)){
				e.weight=weight;
				found = true;
			}
		}
		if (!found){
			System.err.println("edge not found:");
			edge.print();
			System.exit(-1);
		}
	}

}

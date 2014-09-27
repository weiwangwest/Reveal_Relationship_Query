package graph;


import java.util.*;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;

import performance.JenaPerformTestDatanq;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
public class Graph {
	public TreeMap<String, Vertex> V;
	 public ArrayList<Edge> E;

	public Graph getArtificialSteinerTree(TreeMap<String, Vertex> vPrime) {
		return new ArtificialSteinerTree(vPrime).getTree();
	}
	 private class ArtificialSteinerTree {
		 Graph steinerTree=null;
		 String edgeType;
		 private boolean toBeRemoved;
		   public ArtificialSteinerTree(TreeMap<String, Vertex>VPrime){
				//create result tree
			   steinerTree=new Graph();
			   edgeType="aritificialSteinerTreeEdge: "+new java.rmi.dgc.VMID().toString();
			   steinerTree.V.putAll(VPrime);
				int k=0;
				String previousKey="";
				for (String key: steinerTree.V.keySet()){
					if (k!=0){
						//to do: add only one vertex and one edge into the program space, not two!!!
						addEdge(V.get(key).getName(), V.get(previousKey).getName(), edgeType, 1000);
						steinerTree.E.add(new Edge(V.get(key), V.get(previousKey), edgeType, 1000));
					}
					previousKey=key;
					k++;
				}
		   }
			public void removeArificialSteinerTree(){
				toBeRemoved=false;
				for (Edge e: steinerTree.E){
					if (Graph.this.E !=E){
						System.err.println("outer class object can't be accessed");
						System.exit(0);
					}
					Graph.this.removeEdge(e.src.toString(), e.dst.toString(), e.getType());
				}
			}
			@Override
			public void finalize(){
				if (toBeRemoved){
					removeArificialSteinerTree();
				}
			}
			public Graph getTree() {
				return steinerTree;
			}
	 }
	 public boolean contains(Object o){
		 boolean result =false;
		 if (o instanceof Vertex){
			 result=this.V.containsKey(((Vertex)o).getName());
		 }
		 if (o instanceof Edge){
			 result=this.E.contains((Edge) o);
		 }
		 return result;
	 }
		
	 public Graph(){
		V=new TreeMap<String, Vertex>();
		E=new ArrayList<Edge>();
	}
	public Graph(TreeMap<String, Vertex> v,  ArrayList<Edge> e){	
		this.V=v;
		this.E=e;
	}
	//Breadth-First traverse the graph to see whether all vertices are reachable.
	public boolean isConnected(){
		this.clearVisited();
		TreeMap<String, Vertex> vistedVertices=new TreeMap<String, Vertex>();
		LinkedList<Vertex> adjacents=new LinkedList<Vertex>();
		if (this.E.size()>0){	//at least one edge
			adjacents.add(this.E.get(0).src);			
			while (!adjacents.isEmpty()){
				Vertex v=adjacents.poll();
				vistedVertices.put(v.getName(), v); //visit the current vertex
				v.setVisited(true);
				for (Edge e: v.edges){
					if (e.isContainedBy(this)){
						Vertex another=e.getAnotherVertex(v);
						if (!another.isVisited()){
							adjacents.add(another);
						}				
					}
				}
			}
			return vistedVertices.equals(this.V);
		}else{
			return (this.V.size()==1);	//only one vertex, no edge
		}
	}
	public boolean isATree(){
		boolean result=true;
		//|V|=|E|+1;
		if (this.E.size()!=this.V.size()-1){
			result=false;
		}
		//must be connected
		if (!this.isConnected()){
			result=false;
		}
		//build End vertices set from edges, and it should equal V
		if (E.size()>0){
			TreeMap<String, Vertex> vFromEdges=new TreeMap<String, Vertex>();
			for (Edge e: this.E){
				vFromEdges.put(e.src.getName(), e.src);
				vFromEdges.put(e.dst.getName(),e.dst);
			}
			if (!vFromEdges.equals(this.V)){
				result=false;
			}			
		}
		return result;
	}
	public boolean isATreeOld(){
		//Every node and edge must be in Tree.
		//every node has at least one tree edge
		//every edge has its dst and src connected to a tree node
		//|V|=|E|+1;
		int treeVertices=0;			//calculate the number of tree vertices
		for (Vertex v: V.values()){
			if (!v.isContainedBy(this)){
				System.err.println("Vertex not in tree: "+v.getName());
				return false;
			}
			if (v.getDegreeInGraph(this)==0 && V.size()>1){
				System.err.println("Vertex has no edge connected: "+v.getName());
				return false;	
			}
			treeVertices++;
		}
		int treeEdges=0;	//calculate the number of tree edges
		for (Edge e: E){
			if (!e.isContainedBy(this)){
				System.err.println("Edge not in tree: ");
				e.print();
				return false;
			}
			if (!e.getSource().isContainedBy(this)||!e.getDestin().isContainedBy(this)){
				System.err.println("Edge connects vertex/verticis that can not be found in the tree:");
				e.print();
				return false;
			}
			treeEdges ++;
		}
		return (treeVertices-1==treeEdges);
	}
	public boolean addVertex(String name){	//create a Vertex instance
		boolean result=true;
		if (V.containsKey(name)){
			//System.err.println("Vertex name already exists!"); 
			result=false;
		}else{
			V.put(name, new Vertex(name));
		}
		return result;
	}
	public boolean addEdge(String from, String to, String type, double weight){ //create an Edge instance
		boolean result=true;
		if (!V.containsKey(from)){
			System.err.println("addEdge: Couldn't find "+from);
			result=false;
		}
		if (!V.containsKey(to)){
			System.err.println("addEdge: Couldn't find "+to);
			result=false;
		}
		if (from.equals(to)){
			//System.err.println("addEdge: loop edge connecting single vertex: "+to);
			result=false;			
		}
		Edge e=new Edge(V.get(from), V.get(to), type, weight);		
		if (E.contains(e)){
			//System.err.println("addEdge: same type edge already exists: "+from + "--" + to);
			result=false;			
		}
		V.get(from).addAdjacency(e);				//For Steiner tree problem, degree=in degree+out degree
		V.get(to).addAdjacency(e);				//For Steiner tree problem, degree=in degree+out degree
		if (!E.add(e)){
			System.err.println("Failed to add edge!");
			result=false;
		}
		return result;
	}
	public void removeEdge(String from, String to, String name){
		Edge e=new Edge(V.get(from), V.get(to), name, 0);
		V.get(from).edges.remove(e);		//remove edge from source Vertex's edges list
		V.get(to).edges.remove(e);	//remove edge from dest Vertex's edges list
		E.remove(e);		////remove edge from graph's edges list
	}
	public void removeEdgeFromTree(Edge e, Graph T){	
		T.E.remove(e);	//only remove from the tree
	}
	public void removeVertexFromTree(String name, Graph T){
		Vertex v=T.V.get(name);
		for (Edge e: v.edges){
			if (e.isContainedBy(T)){
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
		return V.get(start).getAnyEdgeBetween(V.get(end));
	}	
	public void printTerminals(){
		System.out.println("---------terminals--------");
		for (Vertex v: V.values()){
			if (v.isTerminal){
				v.print();
			}
		}
	}
	public void printFixed(Graph tree){
		System.out.println("---------fixed vertices--------");
		for (Vertex v: V.values()){
			if (v.isTerminal||v.getDegreeInGraph(tree)>=3){
				v.print();
			}
		}
	}
	public void print(){	//print each node and its edges
		Iterator<String> iterator=V.keySet().iterator();
		System.out.println("\n-----------graph----------------");
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
				String type=e.getType();
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
		if (!v.isContainedBy(src) || dst.V.values().contains(v)){		// recurse stop condition
			return; 	
		}
		dst.V.put(v.name, v);	//add this vertex
		for (Edge e: v.edges){	//add edges
			if (e.isContainedBy(src) && !dst.E.contains(e)){
				dst.E.add(e);
			}
		}
		Map<String, Vertex> adjacents=v.getAdjacentsInGraph(src);
		for(Vertex child: adjacents.values()){	
			getTreebyVertex(src, child, dst);					//recursive call
		}
	}
	//produce a randomly connected graph
	// nEdges >= nVertices - 1, a tree
	// nEdges <= nVertices * (nVertices - 1), a wholly connected graph
	public static Graph produceRandomConnectedUndirectedGraph(int nVertices, int nEdges){
		if (!(nEdges >= nVertices - 1 && nEdges <= nVertices * (nVertices - 1)/2)){
			System.err.println("nEdges out of range [nVertices - 1,  nVertices * (nVertices - 1) / 2]");
			return null;
		}
		Graph g=null;
		if (nEdges <= (nVertices * nVertices +nVertices - 2 )/4){ //start from a tree, add edges into it 
			g=produceRandomTree(nVertices);
			for (int i=nVertices - 1; i < nEdges; i++){
				Edge e1, e2;
				Vertex src, dst;
				do{
					src=g.V.get(String.valueOf(JenaPerformTestDatanq.randInt(0, nVertices-1)));
					dst=g.V.get(String.valueOf(JenaPerformTestDatanq.randInt(0, nVertices-1)));
					e1=new Edge(src, dst, "", 1);
					e2=new Edge(dst, src, "", 1);
				}while (g.E.contains(e1) || g.E.contains(e2)||src==dst);
				g.addEdge(e1.getSource().getName(), e1.getDestin().getName(), "", 1);
			}					
		}else{	//start from a whole graph, subtract edges from it
			g=new Graph();
			for (int i=0; i<nVertices; i++){
				g.addVertex(String.valueOf(i));
				for (int j=0; j<i; j++){
					g.addEdge(String.valueOf(i), String.valueOf(j), "", 1);
				}
			}
			while (g.E.size()>nEdges){
				Edge e=null;
				do{
					e=g.E.get(JenaPerformTestDatanq.randInt(0, g.E.size()-1));
				}while (e.src.getDegree()==1 || e.dst.getDegree()==1);
				g.removeEdge(e.src.getName(), e.dst.getName(), e.getType());
			}
		}
		return g;
	}
	public static Graph produceRandomTree(int nodes){
		Graph tree=new Graph();
		for (int i=0; i<nodes; i++){
			tree.addVertex(i+"");
			//randomly select a existing node to become parent of current node
			if (i>0){
				tree.addEdge(i+"", JenaPerformTestDatanq.randInt(0, i-1)+"", "", 1);
			}
		}
		return tree;
	}
	//return terminal nodes for this steiner tree
	public HashSet<Vertex> getTerminalNodes(){
		HashSet<Vertex> terminals=new HashSet<Vertex>();
		for (Vertex v: this.V.values()){
			if (v.isTerminal){
				terminals.add(v);
			}
		}
		return terminals;
	}
	//return fixed nodes for this steiner tree
	public HashSet<Vertex> getFixedNodes(){
		HashSet<Vertex> fixedNodes=new HashSet<Vertex>();
		for (Vertex v: this.V.values()){
			if (v.isTerminal||v.getDegreeInGraph(this)>=3){
				fixedNodes.add(v);
			}
		}
		return fixedNodes;
	}
	public static Graph produceRandomSteinerTree(int nodes, int terminals){
		if (terminals>nodes){
			System.err.println("getRandomSteinerTree: more terminals than tree nodes!");
			System.exit(1);
		}
		//produce a random tree with nodes
		Graph steinerTree=produceRandomTree(nodes);
		//randomly mark terminals
		int nTerminals =0;
		do{
			int guess=JenaPerformTestDatanq.randInt(0, nodes-1);
			if (steinerTree.V.get(new Integer(guess).toString()).isTerminal()){
				continue;
			}else{
				steinerTree.V.get(new Integer(guess).toString()).setTerminal(true);
				nTerminals ++;
			}
		}while (nTerminals<terminals);
		//check non-terminal node whose degree==1, change it into an internal node
		boolean changing;
		do{
			changing=false;
			int id=0;
			for (Vertex v:steinerTree.V.values()){
				id=Integer.parseInt(v.name);
				if (v.getDegreeInGraph(steinerTree)==1 && !v.isTerminal()){	//v is a leaf but not a terminal
					changing = true;
					break;
				}
			}
			if (changing==true){
				//detach v and insert it into an existing path(Edge)
				steinerTree.removeVertex(id+"");	//remove vertex and edge
				Edge e=steinerTree.E.get(JenaPerformTestDatanq.randInt(0, steinerTree.E.size()-1));
				steinerTree.addVertex(id+"");	//add vertex
				steinerTree.addEdge(e.src.getName(), id+"", "", 1);	//link to src
				steinerTree.addEdge(id+"", e.dst.getName(), "", 1);	//link to dst
				steinerTree.removeEdge(e.src.getName(), e.dst.getName(), ""); //remove the formal edge
			}
		}while (changing);
		return steinerTree;
	}
	
	public static LoosePath findNextLoosePathNew(Graph T){
		LoosePath lp=null;
		for (Vertex v: T.V.values()){
			if (v.getUnvisitedDegreeInGraph(T)==1){	//find a leaf in the unvisited subtree
				do{
					Edge e=v.getAnyUnvisitedEdgeInGraph(T);
					if (lp==null){
						lp=new LoosePath();
					}
					lp.add(e);
					e.setVisited(true);
					v=e.getAnotherVertex(v);
				}while(!(v.isTerminal()||v.getDegreeInGraph(T)>=3));
				break; 
			}
		}
		return lp;
	}
	public static LoosePath findNextLoosePath(Graph T){
		LoosePath lp=null;
		for (String key: T.V.keySet()){
			Vertex v=T.V.get(key);
			if (v.getUnvisitedDegreeInGraph(T)==1){ 	 //find the (unvisited fixed node) == (nodes' unvisited degree ==1)
				Edge e=v.getAnyUnvisitedEdgeInGraph(T);
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
					if (v.isTerminal()||v.getDegreeInGraph(T)>=3){
						toContinue=false;
					}else{						
						switch (vNext.getDegreeInGraph(T)){
						case 1:		//we meet a leaf vertex, just mark it as visited.
							vNext.setVisited(true);
							toContinue=false;
							break;
						case 2:
							v=vNext;		//we are on a single loose path, so just move v to the next hop
							e=v.getAnyOtherEdgeInLoosePath(e, T); //move e to the next edge;
							toContinue=true;
							break;
						default:	//we meet a fixed vertex, its previous degree >= 3	
							toContinue=false;
							break;
						}
					}
				}while (toContinue);
				break; //when found one loose path, break  and return it right now.
			}//if not for current vertex, try another one.
		}
		return lp;
	}
	/**Returns a PriorityQueue of loose paths composing a Tree T containing terminal nodes VPrime.
	 * @param T	 the Steiner Tree
	 * @return PriorityQueue of loose paths
	 */
	public static PriorityQueue <LoosePath> getLoosePaths(Graph T){		
		//T.print();
		//T.printTerminals();
		PriorityQueue <LoosePath>Q= new PriorityQueue <LoosePath>();
		LoosePath lp=findNextLoosePathNew(T);
		while (lp != null){
			Q.add(lp);
			lp=findNextLoosePathNew(T);
		}
		return Q;
	}
	public double getWeight(Graph T){
		double weight=0;
		for (Edge edge: T.E){
			if (edge.isContainedBy(T)){
				weight += edge.getWeight();
			}
		}
		return weight;
	}
	
	public static PriorityQueue <Vertex> Q (PriorityQueue <Vertex>Q1, PriorityQueue <Vertex>Q2, int idx){
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
	public Graph T(Graph T1, Graph T2, int idx){
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
			if  (Q(Q1,Q2,other).peek().getDegree()<Q(Q1,Q2,current).peek().getDegree()){//10:	 if degree(Qother )<degree(Qcurrent ) then
				int temp=current;	//11:	 swap(current, other)
				current=other;
				other=temp;
			}	//12:	 end if
			v=Q(Q1,Q2,current).poll();	//13:	 v = Qcurrent .dequeue()
			Q(DQ1,DQ2,current).add(v);	//record dequeued vertex.
			if (Double.compare(v.d(current), lp.getWeight())>=0){	//14:	 if dcurrent (v) ≥ w(lp) then
				break;		//15:	 break
			}	//16:	 end if	
			if (T(T1,T2,other).contains(v)){
				break;
			}
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
				Q(Q1, Q2, current).add(Q(Q1,Q2,current).poll()); //make sure the queue is in good order.
			}	//26:	 end for				
		}while (!(Q(Q1, Q2, 1).isEmpty() 
				|| Q(Q1, Q2, 2).isEmpty() 
				|| T(T1,T2, other).V.values().contains(v)
					));  //27: until Q1 = ∅ ∨ Q2 = ∅ ∨ v ∈ V (Tother)
		//28: return path connecting T1 and T2
		if (T(T1,T2, other).V.values().contains(v)){	//v is inside the other subTree.
			ArrayList<Edge> result=new ArrayList<Edge>(); 
			while (!T(T1,T2, current).V.values().contains(v)){ //v has not reach the current subTree.
				Vertex vPrime=v;
				v=v.getPrecedessor(current);
				result.add(v.getTheLeastWeightEdgeBetween(vPrime));//construct the result
			}
			return new LoosePath(result);
		}else{
			return lp;	//??????
		}
	}
	@SuppressWarnings("unchecked")
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
		Graph tempTree=T.clone();
		ArrayList<Vertex> vertices=lp.getVerticesWithinPath();
		if (vertices.size()==0){		//if there is only one edge connecting T1 and T2, remove that edge.
			Edge e=lp.edges.get(0);
			tempTree.removeEdgeFromTree(e, tempTree);	//temp has been changed
		}else{	//if there is more than one vertex to be deleted, just delete those vertices (edges will automatically deleted.)
			for (Vertex v: vertices){
				tempTree.removeVertexFromTree(v.name, tempTree);	//temp has been changed
			}
		}
		//Now temp has been divided into to parts T1 and T2. So temp is not a tree any more.
		//find the resulted subtrees of T1 and T2 from temp
		Graph T1=new Graph();
		//tempTree.printTree(tempTree);
		this.getTreebyVertex(tempTree, lp.getStartVertex(), T1);
		//T1.printTree(T1);
		assert Graph.isATree(T1):"T1 is not a tree";
		Graph T2=new Graph();
		//tempTree.printTree(tempTree);
		this.getTreebyVertex(tempTree, lp.getEndVertex(), T2);
		//T2.printTree(T2);
		assert Graph.isATree(T2):"T2 is not a tree";
		// find the shortest path in G that connect T1 and T2 into a new tree  of lower weight.
		LoosePath lpNew=this.findShortestPath(T1 , T2, lp);		//to do: test it by comparing with hand work
		// if (lpNew==lp) then a new loosepath could not be found.
		tempTree=createTree(T1, T2, lpNew);	//to do: : test it by comparing with hand work
		return tempTree;
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
		T.clearVisited(); //don't use clearAll(), otherwise terminal marks will be cleared. 
		PriorityQueue <LoosePath> Q=getLoosePaths(T); //	1: priorityQueue Q = LP (T ) //ordered by decreasing weight
		while (Q.size() >0){	//	2: while Q.notEmpty() do
			//System.out.println("----T and the longest loose path-------");
			//T.printTree(T);
			LoosePath lp=Q.poll();	// lp = Q.dequeue()
			//lp.print();
			//System.out.println("----during building tprime-------");
			Graph TPrime = this.replace(lp, T);	// TPrime ← Replace(lp, T ) without changing T!!!
			assert T.isATree():"T is not a tree!";
			assert TPrime.isATree():"TPrime is not a tree";		//TPrime.isATree()
			TPrime.clearVisited();
			//System.out.println("----Tprime-------");
			//TPrime.printTree(TPrime);		//To do: why is not Tprime A tree again?
			if (getWeight(TPrime) < getWeight(T)){	// if w(T ) < w(T ) then
				 T = TPrime;	// T = T
				// System.out.println("-----T is replaced by another lighter weighted tree: replacement No. "+ numOfIter++);
				 Q = getLoosePaths (T); //	 Q = LP (T ) //ordered by decreasing weight
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
				if (!VPrime.values().contains(v) && v.getDegreeInGraph(tree)==1){ 	//todo: tree or this?
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
		System.out.println("-------tree--------");
		Iterator<String> iterator=V.keySet().iterator();
		while (iterator.hasNext()){
			Vertex v=V.get(iterator.next());
			if (v.isContainedBy(T)){
				System.out.println("Vertex: "+v.getName()+", degree="+v.getDegreeInGraph(T));
				for (Edge e: v.edges){
					if (e.isContainedBy(T)){						
						System.out.print("\t\t");
						e.print();
					}
				}
			}
		}
		System.out.println("total weight of edges: "+this.getWeight(T));
	}

	public String printTreeToString(Graph T){
		StringBuffer result=new StringBuffer();
		Iterator<String> iterator=V.keySet().iterator();
		while (iterator.hasNext()){
			Vertex v=V.get(iterator.next());
			if (v.isContainedBy(T)){				
				result.append("Vertex: "+v.getName()+", degree="+v.getDegreeInGraph(T)+"\n");
				for (Edge e: v.edges){
					if (e.isContainedBy(T)){						
						result.append("\t\t");
						result.append(e.toString()+"\n");
					}
				}
			}
		}
		result.append("total weight of edges: "+this.getWeight(T)+"\n");
		return result.toString();
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
	// load dataset into a graph from a nq file
	public static Graph loadDatasetFromNQFile(String fileName){
		Graph G = new Graph();
		Dataset dataset = RDFDataMgr.loadDataset(	fileName, RDFLanguages.NQUADS);
		Iterator<String> it = dataset.listNames();
		while (it.hasNext()) {
			Model tim = dataset.getNamedModel(it.next());

			// add Vertices from the dataset file
			ResIterator r = tim.listSubjects();
			while (r.hasNext()) {
				G.addVertex(r.next().toString());
			}
			NodeIterator n = tim.listObjects();
			while (n.hasNext()) {
				G.addVertex(n.next().toString());
			}

			// add edges from the dataset file
			StmtIterator s = tim.listStatements();
			while (s.hasNext()) {
				Statement stmt = s.next();
				G.addEdge(stmt.getSubject().toString(), stmt.getObject().toString(), stmt.getPredicate().toString(), 1);
			}
		}
		return G;
	}
	// load dataset into a graph from a nq file, except for 
	public static Graph loadDatasetEntitiesFromNQFile(String fileName) throws Exception{
		Graph G = new Graph();
		Dataset dataset = RDFDataMgr.loadDataset(fileName, RDFLanguages.NQUADS);
		Iterator<String> it = dataset.listNames();			
		while (it.hasNext()) {
			Model tim = dataset.getNamedModel(it.next());
			
			// add Vertices from the subjects entities
			ResIterator r = tim.listSubjects();			
			while (r.hasNext()) {
				Resource rsc=r.next();	//add entities only into the Graph
				if (JenaPerformTestDatanq.isEntity(rsc)){
					G.addVertex(rsc.toString());
				}
			}
			// add Vertices from the  objects entities
			NodeIterator n = tim.listObjects();
			while (n.hasNext()) {
				RDFNode rdfnd=n.next();
				if (JenaPerformTestDatanq.isEntity(rdfnd)){
					G.addVertex(rdfnd.toString());						
				}
			}
			// add edges (connecting two entities) from statements,  get statistics
			StmtIterator s = tim.listStatements();
			while (s.hasNext()) {
				Statement stmt = s.next();
				if (JenaPerformTestDatanq.isEntity(stmt.getSubject())&&JenaPerformTestDatanq.isEntity(stmt.getObject())){
					G.addEdge(stmt.getSubject().toString(), stmt.getObject().toString(), stmt.getPredicate().toString(), 1);
				}
			}
			//remove isolated entities
			Vertex vToBeDelete;
			do{
				vToBeDelete=null;
				for (Vertex v: G.V.values()){
					if (v.getDegree()==0){
						vToBeDelete=v;
						break;
					}
				}
				if (vToBeDelete!=null){
					G.removeVertex(vToBeDelete.getName());
				}				
			}while (vToBeDelete!=null);
		} //while each model
		return G;
	}
	public static boolean isATree(Graph graph) {
		return graph.isATree();
	}

	// find the best steiner tree
	public Graph findBestSteinerTree(String [] Terminals){
		// clear all tags.
		this.clearAll(); 
		// store terminal nodes in VPrime.
		//System.out.println("store terminal nodes in VPrime.");
		TreeMap<String, Vertex> VPrime = new TreeMap<String, Vertex>();
		for (String terminal: Terminals){
			VPrime.put(terminal, this.V.get(terminal));
			this.V.get(terminal).setTerminal(true);	//setTerminal
		}
		// make aritificial steiner tree.
		//System.out.println("make aritificial steiner tree.");
		Graph T = (new ArtificialSteinerTree(VPrime)).getTree();
		if (!Graph.isATree(T)){
			System.err.println("artificialSteinerTree is not a tree!");
			T.print();
			System.exit(1);
		}
		// improve the steiner tree.
		//System.out.println("improve the steiner tree.");
		//T.printTree(T);
		T = this.improveTree(T);
		//T.printTree(T);
		return T;
	}

}
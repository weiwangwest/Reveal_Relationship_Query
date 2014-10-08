package graph;

import java.util.*;
public class SimpleTest {
	public static void main(String[] args) {
		System.out.println("\n\n**********1. G: the Original Graph***************");
		Graph G=new Graph();
		G.addVertex(new Vertex("entity"));
		G.addVertex(new Vertex("person"));
		G.addVertex(new Vertex("scientist"));
		G.addVertex(new Vertex("organization unit"));
		G.addVertex(new Vertex("physicist"));
		G.addVertex(new Vertex("politician"));
		G.addVertex(new Vertex("actor"));
		G.addVertex(new Vertex("state"));
		G.addVertex(new Vertex("Max Planck"));
		G.addVertex(new Vertex("Arnold Schwarzenegger"));
		G.addVertex(new Vertex("Germany"));
		G.addVertex(new Vertex("Angela Merkel"));
		G.addEdge("person", "entity", "subClassOf", 0.99);
		G.addEdge("organization unit", "entity", "subClassOf", 0.99);
		G.addEdge("scientist", "person", "subClassOf", 0.99);
		G.addEdge("physicist", "scientist", "subClassOf", 0.99);
		G.addEdge("politician", "person", "subClassOf", 0.99);
		G.addEdge("actor", "person", "subClassOf", 0.99);
		G.addEdge("state", "organization unit", "subClassOf", 0.99);
		G.addEdge("Germany", "state", "type", 0.95);
		G.addEdge("Max Planck", "physicist", "type", 0.95);
		G.addEdge("Angela Merkel", "physicist", "type", 0.95);
		G.addEdge("Angela Merkel", "politician", "type", 0.95);
		G.addEdge("Arnold Schwarzenegger", "politician", "type", 0.95);
		G.addEdge("Arnold Schwarzenegger", "actor", "type", 0.95);		
		G.addEdge("Angela Merkel", "Germany", "chancellorOf", 0.96);		
		G.print();
		G.printVerticesStastisticsTree();
		G.printEdgesStastistics();
		System.out.println("--------test: is it a tree?----------\n"+Graph.isATree(G));

		System.out.println("\n\n**********2. The Breath First Spanning Tree***************");
		Graph g=G.getBreathFirstSpanningTree(G.V, G.E);
		g.printTree(g);
		G.printVerticesStastisticsTree();
		g.printEdgesStastistics();
		System.out.println("--------test: is it a tree?----------\n"+Graph.isATree(g));
		
		System.out.println("\n\n********3. T: The original steiner tree,	 VPrime: the set of terminal nodes***************");
		G.clearAll();		//clear all tags.
		HashMap<String, Vertex> VPrime=new HashMap<String, Vertex>(); //store terminal nodes in VPrime.
		VPrime.put("Max Planck", G.V.get(Vertex.vertexMap.getValue("Max Planck")));
		VPrime.put("Arnold Schwarzenegger", G.V.get(Vertex.vertexMap.getValue("Arnold Schwarzenegger")));
		VPrime.put("Germany", G.V.get(Vertex.vertexMap.getValue("Germany")));
		Graph T=G.getFirstSteinerTree(VPrime); //find Steiner tree.
		T.printTree(T);
		T.printVerticesStastisticsTree();
		T.printEdgesStastistics();
		System.out.println("--------test: is it a tree?----------\n"+Graph.isATree(T));

		System.out.println("\n\n********4. T: the manually built steiner tree***************");
		G.clearAll();		//clear all tags.
		T.V.clear();
		T.E.clear();

		//set terminals
		G.V.get(Vertex.vertexMap.getValue("Max Planck")).setTerminal(true);
		G.V.get(Vertex.vertexMap.getValue("Arnold Schwarzenegger")).setTerminal(true);
		G.V.get(Vertex.vertexMap.getValue("Germany")).setTerminal(true);
		
		//manually build the original steiner tree.
		for (String str: "entity,person,scientist,organization unit,physicist,politician,state,Max Planck,Arnold Schwarzenegger,Germany".split(",")){
			int id=Vertex.vertexMap.getValue(str);
			T.V.put(id, G.V.get(id));			
		}
		T.E.add(G.getDirectedEdge("person", "entity"));
		T.E.add(G.getDirectedEdge("organization unit", "entity"));
		T.E.add(G.getDirectedEdge("scientist", "person"));
		T.E.add(G.getDirectedEdge("physicist", "scientist"));
		T.E.add(G.getDirectedEdge("politician", "person"));
		T.E.add(G.getDirectedEdge("state", "organization unit"));
		T.E.add(G.getDirectedEdge("Germany", "state"));
		T.E.add(G.getDirectedEdge("Max Planck", "physicist"));
		T.E.add(G.getDirectedEdge("Arnold Schwarzenegger", "politician"));
		T.printTree(T);
		T.printVerticesStastisticsTree();
		T.printEdgesStastistics();
		System.out.println("--------test: is it a tree?----------\n"+Graph.isATree(T));

		System.out.println("\n\n********5. The BEST steiner tree***************");
		T=G.improveTree(T);	//Of course T has been changed during improveTree(T)
		System.out.println("\n\n*******************The Final tree*******************");
		T.printTree(T);
		T.printVerticesStastisticsTree();
		T.printEdgesStastistics();
		System.out.println("--------test: is it a tree?----------\n"+Graph.isATree(T));
	}
}
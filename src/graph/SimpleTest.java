package graph;

import java.util.*;
public class SimpleTest {
	public static void main(String[] args) {
		System.out.println("\n\n**********1. G: the Original Graph***************");
		Graph G=new Graph();
		G.addVertex("entity");
		G.addVertex("person");
		G.addVertex("scientist");
		G.addVertex("organization unit");
		G.addVertex("physicist");
		G.addVertex("politician");
		G.addVertex("actor");
		G.addVertex("state");
		G.addVertex("Max Planck");
		G.addVertex("Arnold Schwarzenegger");
		G.addVertex("Germany");
		G.addVertex("Angela Merkel");
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
		TreeMap<String, Vertex> VPrime=new TreeMap<String, Vertex>(); //store terminal nodes in VPrime.
		VPrime.put("Max Planck", G.V.get("Max Planck"));
		VPrime.put("Arnold Schwarzenegger", G.V.get("Arnold Schwarzenegger"));
		VPrime.put("Germany", G.V.get("Germany"));
		Graph T=G.getFirstSteinerTree(VPrime); //find Steiner tree.
		T.printTree(T);
		T.printVerticesStastisticsTree();
		T.printEdgesStastistics();
		System.out.println("--------test: is it a tree?----------\n"+Graph.isATree(T));

		System.out.println("\n\n********4. T: the manually built steiner tree***************");
		G.clearAll();		//clear all tags.
		T.V.clear();
		T.E.clear();
			//manually build the original steiner tree.
		T.V.put("entity", G.V.get("entity"));
//		G.V.get("entity").setIsInTree(true);
		T.V.put("person", G.V.get("person"));
//		G.V.get("person").setIsInTree(true);
		T.V.put("scientist", G.V.get("scientist"));
//		G.V.get("scientist").setIsInTree(true);
		T.V.put("organization unit", G.V.get("organization unit"));
//		G.V.get("organization unit").setIsInTree(true);
		T.V.put("physicist", G.V.get("physicist"));
//		G.V.get("physicist").setIsInTree(true);
		T.V.put("politician", G.V.get("politician"));
//		G.V.get("politician").setIsInTree(true);
		T.V.put("state", G.V.get("state"));
//		G.V.get("state").setIsInTree(true);
		T.V.put("Max Planck", G.V.get("Max Planck"));
//		G.V.get("Max Planck").setIsInTree(true);
		T.V.put("Arnold Schwarzenegger", G.V.get("Arnold Schwarzenegger"));
//		G.V.get("Arnold Schwarzenegger").setIsInTree(true);
		T.V.put("Germany", G.V.get("Germany"));
//		G.V.get("Germany").setIsInTree(true);
		T.E.add(G.getDirectedEdge("person", "entity"));
//		G.getDirectedEdge("person", "entity").setInTree(true);
		T.E.add(G.getDirectedEdge("organization unit", "entity"));
//		G.getDirectedEdge("organization unit", "entity").setInTree(true);
		T.E.add(G.getDirectedEdge("scientist", "person"));
//		G.getDirectedEdge("scientist", "person").setInTree(true);
		T.E.add(G.getDirectedEdge("physicist", "scientist"));
//		G.getDirectedEdge("physicist", "scientist").setInTree(true);
		T.E.add(G.getDirectedEdge("politician", "person"));
//		G.getDirectedEdge("politician", "person").setInTree(true);
		T.E.add(G.getDirectedEdge("state", "organization unit"));
//		G.getDirectedEdge("state", "organization unit").setInTree(true);
		T.E.add(G.getDirectedEdge("Germany", "state"));
//		G.getDirectedEdge("Germany", "state").setInTree(true);
		T.E.add(G.getDirectedEdge("Max Planck", "physicist"));
//		G.getDirectedEdge("Max Planck", "physicist").setInTree(true);
		T.E.add(G.getDirectedEdge("Arnold Schwarzenegger", "politician"));
//		G.getDirectedEdge("Arnold Schwarzenegger", "politician").setInTree(true);
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
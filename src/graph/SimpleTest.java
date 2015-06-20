package graph;

import java.util.*;
public class SimpleTest {
	public static void main(String[] args) {
		System.out.println("\n\n**********1. G: the Original Graph***************");
		Graph G=new Graph(Graph.GRAPH_VERTICES);
		G.addExistingVertex(new Vertex("entity"));
		G.addExistingVertex(new Vertex("person"));
		G.addExistingVertex(new Vertex("scientist"));
		G.addExistingVertex(new Vertex("organization unit"));
		G.addExistingVertex(new Vertex("physicist"));
		G.addExistingVertex(new Vertex("politician"));
		G.addExistingVertex(new Vertex("actor"));
		G.addExistingVertex(new Vertex("state"));
		G.addExistingVertex(new Vertex("Max Planck"));
		G.addExistingVertex(new Vertex("Arnold Schwarzenegger"));
		G.addExistingVertex(new Vertex("Germany"));
		G.addExistingVertex(new Vertex("Angela Merkel"));
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
		G.printVerticesStastistics();
		G.printEdgesStastistics();
		System.out.println("--------test: is it a tree?----------\n"+GraphManager.isATree(G));

		System.out.println("\n\n**********2. The Breath First Spanning Tree***************");
		Tree g=GraphManager.getBreathFirstSpanningTree(G);
		g.print();
		G.printVerticesStastistics();
		g.printEdgesStastistics();
		System.out.println("--------test: is it a tree?----------\n"+GraphManager.isATree(g));
		
		System.out.println("\n\n********3. T: The original steiner tree,	 VPrime: the set of terminal nodes***************");
		G.clearVisited();		//clear all tags.
		HashMap<String, Vertex> VPrime=new HashMap<String, Vertex>(); //store terminal nodes in VPrime.
		VPrime.put("Max Planck", G.getVertex(Vertex.vertexMap.getValue("Max Planck")));
		VPrime.put("Arnold Schwarzenegger", G.getVertex(Vertex.vertexMap.getValue("Arnold Schwarzenegger")));
		VPrime.put("Germany", G.getVertex(Vertex.vertexMap.getValue("Germany")));
		Tree T=GraphManager.getFirstSteinerTree(G, VPrime); //find Steiner tree.
		T.print();
		T.printVerticesStastistics();
		T.printEdgesStastistics();
		System.out.println("--------test: is it a tree?----------\n"+GraphManager.isATree(T));

		System.out.println("\n\n********4. T: the manually built steiner tree***************");
		G.clearVisited();		//clear all tags.
		T.clearVertex();

		//set terminals
		G.getVertex(Vertex.vertexMap.getValue("Max Planck")).setTerminal(true, T);
		G.getVertex(Vertex.vertexMap.getValue("Arnold Schwarzenegger")).setTerminal(true, T);
		G.getVertex(Vertex.vertexMap.getValue("Germany")).setTerminal(true, T);
		
		//manually build the original steiner tree.
		for (String str: "entity,person,scientist,organization unit,physicist,politician,state,Max Planck,Arnold Schwarzenegger,Germany".split(",")){
			int id=Vertex.vertexMap.getValue(str);
			T.addExistingVertex (G.getVertex(id));			
		}
		T.addEdge(G.getAnyEdgeBetween("person", "entity"));
		T.addEdge(G.getAnyEdgeBetween("organization unit", "entity"));
		T.addEdge(G.getAnyEdgeBetween("scientist", "person"));
		T.addEdge(G.getAnyEdgeBetween("physicist", "scientist"));
		T.addEdge(G.getAnyEdgeBetween("politician", "person"));
		T.addEdge(G.getAnyEdgeBetween("state", "organization unit"));
		T.addEdge(G.getAnyEdgeBetween("Germany", "state"));
		T.addEdge(G.getAnyEdgeBetween("Max Planck", "physicist"));
		T.addEdge(G.getAnyEdgeBetween("Arnold Schwarzenegger", "politician"));
		T.print();
		T.printVerticesStastistics();
		T.printEdgesStastistics();
		System.out.println("--------test: is it a tree?----------\n"+GraphManager.isATree(T));

		System.out.println("\n\n********5. The BEST steiner tree***************");
		T=GraphManager.improveTree(G, T);	//Of course T has been changed during improveTree(T)
		System.out.println("\n\n*******************The Final tree*******************");
		T.print();
		T.printVerticesStastistics();
		T.printEdgesStastistics();
		System.out.println("--------test: is it a tree?----------\n"+GraphManager.isATree(T));
	}
}
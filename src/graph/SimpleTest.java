package graph;

import java.util.*;
public class SimpleTest {
	public static void main(String[] args) {
		System.out.println("\n\n**********1. G: the Original Graph***************");
		Graph G=new Graph(Graph.GRAPH_CAPACITY);
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
		G.printVerticesStastisticsGraph();
		G.printEdgesStastistics();
		System.out.println("--------test: is it a tree?----------\n"+GraphManager.isATree(G));

		System.out.println("\n\n**********2. The Breath First Spanning Tree***************");
		Tree g=GraphManager.getBreathFirstSpanningTree(G);
		g.print();
		G.printVerticesStastisticsGraph();
		g.printEdgesStastistics();
		System.out.println("--------test: is it a tree?----------\n"+GraphManager.isATree(g));
		
		System.out.println("\n\n********3. T: The original steiner tree,	 VPrime: the set of terminal nodes***************");
		G.clearAll();		//clear all tags.
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
		G.clearAll();		//clear all tags.
		T.clearVertex();
		T.E.clear();

		//set terminals
		G.getVertex(Vertex.vertexMap.getValue("Max Planck")).setTerminal(true, T);
		G.getVertex(Vertex.vertexMap.getValue("Arnold Schwarzenegger")).setTerminal(true, T);
		G.getVertex(Vertex.vertexMap.getValue("Germany")).setTerminal(true, T);
		
		//manually build the original steiner tree.
		for (String str: "entity,person,scientist,organization unit,physicist,politician,state,Max Planck,Arnold Schwarzenegger,Germany".split(",")){
			int id=Vertex.vertexMap.getValue(str);
			T.addVertex (G.getVertex(id));			
		}
		T.E.add(G.getAnyEdgeBetween("person", "entity"));
		T.E.add(G.getAnyEdgeBetween("organization unit", "entity"));
		T.E.add(G.getAnyEdgeBetween("scientist", "person"));
		T.E.add(G.getAnyEdgeBetween("physicist", "scientist"));
		T.E.add(G.getAnyEdgeBetween("politician", "person"));
		T.E.add(G.getAnyEdgeBetween("state", "organization unit"));
		T.E.add(G.getAnyEdgeBetween("Germany", "state"));
		T.E.add(G.getAnyEdgeBetween("Max Planck", "physicist"));
		T.E.add(G.getAnyEdgeBetween("Arnold Schwarzenegger", "politician"));
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
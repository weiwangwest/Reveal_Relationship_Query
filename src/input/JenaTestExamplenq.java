package input;
import fundamental.FileNameManager;
import graph.Graph;
import graph.GraphManager;
import graph.Tree;
import graph.Vertex;

import java.util.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.query.Dataset;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;

public class JenaTestExamplenq {
	public static void main(String[] args) {
		Graph G = new Graph(Graph.GRAPH_CAPACITY);
		System.out.println("\n\n**********1. G: the Original Graph***************");
		Dataset dataset = RDFDataMgr.loadDataset(
				FileNameManager.pathToDataFiles+"example.nq",
				RDFLanguages.NQUADS);
		Iterator<String> it = dataset.listNames();
		while (it.hasNext()) {
			Model tim = dataset.getNamedModel(it.next());

			// add Vertices
			ResIterator r = tim.listSubjects();
			while (r.hasNext()) {
				G.addExistingVertex(new Vertex(r.next().toString()));
			}
			NodeIterator n = tim.listObjects();
			while (n.hasNext()) {
				G.addExistingVertex(new Vertex(n.next().toString()));
			}

			// add edges
			StmtIterator s = tim.listStatements();
			while (s.hasNext()) {
				Statement stmt = s.next();
				G.addEdge(stmt.getSubject().toString(), stmt.getObject().toString(), stmt.getPredicate().toString(), 1);
			}
		}
		// Manually preset graph parameters
		G.setEdgeWeight(null, "http://example.org/bob/foaf.rdf", "http://www.w3. org/2000/01/rdf-schema#seeAlso", 0.6);
		G.setEdgeWeight(null, "http://example.org/bob/", "http://xmlns.com/foaf/0.1/homepage", 0.6);
		G.setEdgeWeight("http://example.org/bob/foaf.rdf#me", "http://xmlns.com/foaf/0.1/Person", null, 0.8);

		G.print();
		G.printVerticesStastistics();
		G.printEdgesStastistics();
		System.out.println("--------test: is it a tree?----------\n"+ GraphManager.isATree(G));

		System.out.println("\n\n**********2. The Breath First Spanning Tree***************");
		Tree g = GraphManager.getBreathFirstSpanningTree(G);
		g.print();
		G.printVerticesStastistics();
		g.printEdgesStastistics();
		System.out.println("--------test: is it a tree?----------\n"+ GraphManager.isATree(g));

		System.out.println("\n\n********3. T: The original steiner tree,	 VPrime: the set of terminal nodes***************");
		G.clearVisited(); // clear all tags.
		
		// store terminal(required)  nodes in VPrime.
		HashMap<String, Vertex> VPrime = new HashMap<String, Vertex>(); 
		VPrime.put("http://example.org/bob/foaf.rdf", G.getVertex(Vertex.vertexMap.getValue("http://example.org/bob/foaf.rdf")));
		VPrime.put("http://example.org/bob/", G.getVertex(Vertex.vertexMap.getValue("http://example.org/bob/")));
		VPrime.put("http://xmlns.com/foaf/0.1/Person", G.getVertex(Vertex.vertexMap.getValue("http://xmlns.com/foaf/0.1/Person")));
		
		// find first Steiner tree.
		Tree T = GraphManager.getFirstSteinerTree(G, VPrime); 
		T.print();
		T.printVerticesStastistics();
		T.printEdgesStastistics();
		System.out.println("--------test: is it a tree?----------\n"+ GraphManager.isATree(T));

		// improve the Steiner tree
		System.out.println("\n\n********5. The BEST steiner tree***************");
		T = GraphManager.improveTree(G, T); // Of course T has been changed during improveTree(T)
		
		System.out.println("\n\n*******************The Final tree*******************");
		T.print();
		T.printVerticesStastistics();
		T.printEdgesStastistics();
		System.out.println("--------test: is it a tree?----------\n"+ GraphManager.isATree(T));
	}
}
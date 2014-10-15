package input;
import graph.Graph;
import graph.Vertex;

import java.util.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.query.Dataset;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;

public class JenaTestExamplenq {
	public static void main(String[] args) {
		Graph G = new Graph();
		System.out.println("\n\n**********1. G: the Original Graph***************");
		Dataset dataset = RDFDataMgr.loadDataset(
				DatasetLoaderWithJena.pathToDataFiles+"example.nq",
				RDFLanguages.NQUADS);
		Iterator<String> it = dataset.listNames();
		while (it.hasNext()) {
			Model tim = dataset.getNamedModel(it.next());

			// add Vertices
			ResIterator r = tim.listSubjects();
			while (r.hasNext()) {
				G.addVertex(new Vertex(r.next().toString()));
			}
			NodeIterator n = tim.listObjects();
			while (n.hasNext()) {
				G.addVertex(new Vertex(n.next().toString()));
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
		G.printVerticesStastisticsGraph();
		G.printEdgesStastistics();
		System.out.println("--------test: is it a tree?----------\n"+ Graph.isATree(G));

		System.out.println("\n\n**********2. The Breath First Spanning Tree***************");
		Graph g = G.getBreathFirstSpanningTree(G.V, G.E);
		g.printTree(g);
		G.printVerticesStastisticsGraph();
		g.printEdgesStastistics();
		System.out.println("--------test: is it a tree?----------\n"+ Graph.isATree(g));

		System.out.println("\n\n********3. T: The original steiner tree,	 VPrime: the set of terminal nodes***************");
		G.clearAll(); // clear all tags.
		
		// store terminal(required)  nodes in VPrime.
		HashMap<String, Vertex> VPrime = new HashMap<String, Vertex>(); 
		VPrime.put("http://example.org/bob/foaf.rdf", G.V.get("http://example.org/bob/foaf.rdf"));
		VPrime.put("http://example.org/bob/", G.V.get("http://example.org/bob/"));
		VPrime.put("http://xmlns.com/foaf/0.1/Person", G.V.get("http://xmlns.com/foaf/0.1/Person"));
		
		// find first Steiner tree.
		Graph T = G.getFirstSteinerTree(VPrime); 
		T.printTree(T);
		T.printVerticesStastisticsTree();
		T.printEdgesStastistics();
		System.out.println("--------test: is it a tree?----------\n"+ Graph.isATree(T));

		// improve the Steiner tree
		System.out.println("\n\n********5. The BEST steiner tree***************");
		T = G.improveTree(T); // Of course T has been changed during improveTree(T)
		
		System.out.println("\n\n*******************The Final tree*******************");
		T.printTree(T);
		T.printVerticesStastisticsTree();
		T.printEdgesStastistics();
		System.out.println("--------test: is it a tree?----------\n"+ Graph.isATree(T));
	}
}
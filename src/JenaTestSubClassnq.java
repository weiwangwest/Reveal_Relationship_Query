import java.util.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.query.Dataset;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;

public class JenaTestSubClassnq {
	public static void mainOriginal(String[] args) {
		Timer.start(null);

		Timer.tick("1. G: the Original Graph");
		Graph G = new Graph();
		Dataset dataset = RDFDataMgr.loadDataset(
						"file:///home//wang//myDocuments//UniKoblenz//STAR//subclass.nq",
						RDFLanguages.NQUADS);
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
		// Manually preset graph parameters
		//G.setEdgeWeight(null, "http://example.org/bob/foaf.rdf", "http://www.w3. org/2000/01/rdf-schema#seeAlso", 0.6);
		//G.setEdgeWeight(null, "http://example.org/bob/", "http://xmlns.com/foaf/0.1/homepage", 0.6);
		//G.setEdgeWeight("http://example.org/bob/foaf.rdf#me", "http://xmlns.com/foaf/0.1/Person", null, 0.8);

		//G.print();
		//G.printVerticesStastistics();
		//G.printEdgesStastistics();
		//Timer.tick("--------test: is it a tree?----------");
		//Timer.tick(String.valueOf(G.isATree()));
/*
		Timer.tick("\n\n**********2. The Breath First Spanning Tree***************");
		Graph g = G.getBreathFirstSpanningTree(G.V, G.E);
		//g.printTree(g);
		//G.printVerticesStastistics();
		//g.printEdgesStastistics();
		System.out.println("--------test: is it a tree?----------\n"+ g.isATree());
*/
		Timer.tick("3. T: The original steiner tree,	 VPrime: the set of terminal nodes");
		// clear all tags.
		G.clearAll(); 
		// store terminal nodes in VPrime.
		TreeMap<String, Vertex> VPrime = new TreeMap<String, Vertex>(); 
		VPrime.put("http://rdf.data-vocabulary.org/#Organization", G.V.get("http://rdf.data-vocabulary.org/#Organization"));
		VPrime.put("http://aims.fao.org/aos/geopolitical.owl#territory", G.V.get("http://aims.fao.org/aos/geopolitical.owl#territory"));
//		<http://aims.fao.org/aos/geopolitical.owl#territory> <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://aims.fao.org/aos/geopolitical.owl#area> <http://aims.fao.org/aos/geopolitical.owl> .
//		<http://aims.fao.org/aos/geopolitical.owl#territory> <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://aims.fao.org/aos/geopolitical.owl#area> <http://aims.fao.org/geopolitical.owl> .
//		<http://aims.fao.org/aos/geopolitical.owl#area> <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://www.w3.org/2002/07/owl#Thing> <http://aims.fao.org/aos/geopolitical.owl> .
//		<http://aims.fao.org/aos/geopolitical.owl#area> <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://www.w3.org/2002/07/owl#Thing> <http://aims.fao.org/geopolitical.owl> .
		VPrime.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#Resource", G.V.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#Resource"));

		// find Steiner tree.
		Graph T = G.getArtificialSteinerTree(VPrime); 
		T.printTree(T);
		T.printVerticesStastistics();
		T.printEdgesStastistics();
		Timer.tick("--------test: is it a tree?----------\n"+ Graph.isATree(T));
		
		// Of course T has been changed during improveTree(T)
		Timer.tick("5. The BEST steiner tree");
		T = G.improveTree(T); 
	
		Timer.tick("The Final tree");
		T.printTree(T);
		T.printVerticesStastistics();
		T.printEdgesStastistics();
		Timer.tick("--------test: is it a tree?----------\n"+ Graph.isATree(T));
		
		Timer.stop("");
	}
	public static void main(String[] args) {
		Timer.start(null);

		Timer.tick("1. G: the Original Graph");
		Graph G = Graph.loadDatasetFromNQFile("file:///home//wang//myDocuments//UniKoblenz//STAR//subclass.nq");

		Timer.tick("2. T: The original steiner tree,	 VPrime: the set of terminal nodes");
		Graph T = G.findBestSteinerTree(new String[] {"http://rdf.data-vocabulary.org/#Organization",
				"http://aims.fao.org/aos/geopolitical.owl#territory",
				"http://www.w3.org/1999/02/22-rdf-syntax-ns#Resource"
				});
		Timer.tick("3. The Final tree");
		T.printTree(T);
		T.printVerticesStastistics();
		T.printEdgesStastistics();
		Timer.tick("--------test: is it a tree?----------\n"+ Graph.isATree(T));		
		Timer.stop("");
	}
}
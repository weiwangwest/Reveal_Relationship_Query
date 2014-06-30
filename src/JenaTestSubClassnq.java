import java.util.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.query.Dataset;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;

public class JenaTestSubClassnq {
	public static void main(String[] args) {
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
		Timer.tick("\n\n********3. T: The original steiner tree,	 VPrime: the set of terminal nodes***************");
		G.clearAll(); // clear all tags.
		TreeMap<String, Vertex> VPrime = new TreeMap<String, Vertex>(); // store terminal nodes in VPrime.
		VPrime.put("http://rdf.data-vocabulary.org/#Organization", G.V.get("http://rdf.data-vocabulary.org/#Organization"));
		VPrime.put("http://aims.fao.org/aos/geopolitical.owl#territory", G.V.get("http://aims.fao.org/aos/geopolitical.owl#territory"));
//		<http://aims.fao.org/aos/geopolitical.owl#territory> <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://aims.fao.org/aos/geopolitical.owl#area> <http://aims.fao.org/aos/geopolitical.owl> .
//		<http://aims.fao.org/aos/geopolitical.owl#territory> <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://aims.fao.org/aos/geopolitical.owl#area> <http://aims.fao.org/geopolitical.owl> .
//		<http://aims.fao.org/aos/geopolitical.owl#area> <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://www.w3.org/2002/07/owl#Thing> <http://aims.fao.org/aos/geopolitical.owl> .
//		<http://aims.fao.org/aos/geopolitical.owl#area> <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://www.w3.org/2002/07/owl#Thing> <http://aims.fao.org/geopolitical.owl> .
		VPrime.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#Resource", G.V.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#Resource"));

		Graph T = G.getArtificialSteinerTree(VPrime); // find Steiner tree.
		T.printTree(T);
		T.printVerticesStastistics();
		T.printEdgesStastistics();
		System.out.println("--------test: is it a tree?----------\n"+ T.isATree());
		

		Timer.tick("\n\n********5. The BEST steiner tree***************");
		T = G.improveTree(T); // Of course T has been changed during improveTree(T)
	
		System.out.println("\n\n*******************The Final tree*******************");
		T.printTree(T);
		T.printVerticesStastistics();
		T.printEdgesStastistics();
		System.out.println("--------test: is it a tree?----------\n"+ T.isATree());
		Timer.stop("");
	}

}
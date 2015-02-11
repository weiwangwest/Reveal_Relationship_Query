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

import output.Timer;


public class JenaTestSubClassnq {
	public static void mainOriginal(String[] args) {
		Timer.start(null);

		Timer.tick("1. G: the Original Graph");
		Graph G = new Graph(Graph.GRAPH_CAPACITY);
		Dataset dataset = RDFDataMgr.loadDataset(
				FileNameManager.pathToDataFiles+"subclass.nq",
				RDFLanguages.NQUADS);
		Iterator<String> it = dataset.listNames();
		while (it.hasNext()) {
			Model tim = dataset.getNamedModel(it.next());

			// add Vertices from the dataset file
			ResIterator r = tim.listSubjects();
			while (r.hasNext()) {
				G.addExistingVertex(new Vertex(r.next().toString()));
			}
			NodeIterator n = tim.listObjects();
			while (n.hasNext()) {
				G.addExistingVertex(new Vertex(n.next().toString()));
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
		G.clearVisited(); 
		// store terminal nodes in VPrime.
		HashMap<Integer, Vertex> VPrime = new HashMap<Integer, Vertex>(); 
		int id;
		VPrime.put(id=Vertex.vertexMap.getValue("http://rdf.data-vocabulary.org/#Organization"), G.getVertex(id));
		VPrime.put(id=Vertex.vertexMap.getValue("http://aims.fao.org/aos/geopolitical.owl#territory"), G.getVertex(id));
//		<http://aims.fao.org/aos/geopolitical.owl#territory> <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://aims.fao.org/aos/geopolitical.owl#area> <http://aims.fao.org/aos/geopolitical.owl> .
//		<http://aims.fao.org/aos/geopolitical.owl#territory> <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://aims.fao.org/aos/geopolitical.owl#area> <http://aims.fao.org/geopolitical.owl> .
//		<http://aims.fao.org/aos/geopolitical.owl#area> <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://www.w3.org/2002/07/owl#Thing> <http://aims.fao.org/aos/geopolitical.owl> .
//		<http://aims.fao.org/aos/geopolitical.owl#area> <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://www.w3.org/2002/07/owl#Thing> <http://aims.fao.org/geopolitical.owl> .
		VPrime.put(id=Vertex.vertexMap.getValue("http://www.w3.org/1999/02/22-rdf-syntax-ns#Resource"), G.getVertex(id));

		// find Steiner tree.
		Tree T = GraphManager.getArtificialSteinerTree(G, VPrime); 
		T.print();
		T.printVerticesStastistics();
		T.printEdgesStastistics();
		Timer.tick("--------test: is it a tree?----------\n"+ GraphManager.isATree(T));
		
		// Of course T has been changed during improveTree(T)
		Timer.tick("5. The BEST steiner tree");
		T = GraphManager.improveTree(G, T); 
	
		Timer.tick("The Final tree");
		T.print();
		T.printVerticesStastistics();
		T.printEdgesStastistics();
		Timer.tick("--------test: is it a tree?----------\n"+ GraphManager.isATree(T));
		
		Timer.stop("");
	}
	public static void main(String[] args) throws Exception {
		Timer.start(null);

		Timer.tick("1. G: the Original Graph");
		Graph G = new Graph(Graph.GRAPH_CAPACITY);
		DatasetLoaderWithJena.addEntitiesFromNqNoExcetionProcessor(
				G, FileNameManager.pathToDataFiles+"subclass.nq");

		Timer.tick("2. T: The original steiner tree,	 VPrime: the set of terminal nodes");
		Tree T = GraphManager.findBestSteinerTree(G, new String[] {"http://rdf.data-vocabulary.org/#Organization",
				"http://aims.fao.org/aos/geopolitical.owl#territory",
				"http://www.w3.org/1999/02/22-rdf-syntax-ns#Resource"
				});
		Timer.tick("3. The Final tree");
		T.print();
		T.printVerticesStastistics();
		T.printEdgesStastistics();
		Timer.tick("--------test: is it a tree?----------\n"+ GraphManager.isATree(T));		
		Timer.stop("");
	}
}
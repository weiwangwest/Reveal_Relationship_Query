package input;

import graph.Edge;
import graph.Graph;
import graph.NewGraph;
import graph.Vertex;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;


import com.hp.hpl.jena.rdf.model.*;

public class DatasetLoaderFromMapReplacedFile {

	public static void loadDataFromMapReplacedGzipNqFile(Graph g, String gzipFileName) throws Exception{
		GzipNqFileReader in=new GzipNqFileReader(gzipFileName);
		while (in.hasNext()){
			TripleParser tp=new TripleParser(in.next());
			int from=Integer.valueOf(tp.getSubject());
			int type=Integer.valueOf(tp.getPredicate());
			int to=Integer.valueOf(tp.getObject());
			if (!g.contains(from)){
				g.addExistingVertex(new Vertex(from));
			}
			if (!g.contains(to)){
				g.addExistingVertex(new Vertex(to));
			}
			g.addEdge(from, to, type, 1.0);
		}
		in.close();
	}
	public static void loadNewGraphFromMapReplacedGzipNqFile(NewGraph g, String gzipFileName) throws Exception{
		GzipNqFileReader in=new GzipNqFileReader(gzipFileName);
		while (in.hasNext()){
			TripleParser tp=new TripleParser(in.next());
			int from=Integer.valueOf(tp.getSubject());
			int type=Integer.valueOf(tp.getPredicate());
			int to=Integer.valueOf(tp.getObject());
			if (!g.containsV(from)){
				g.addExistingVertex(from);
			}
			if (!g.containsV(to)){
				g.addExistingVertex(to);
			}
			g.addEdge(from, to, type, 1);
		}
		in.close();
	}
	public static int loadEdgesFromMapReplacedGzipNqFile(Edge[]edges, int i, String gzipFileName) throws Exception{
		GzipNqFileReader in=new GzipNqFileReader(gzipFileName);
		while (in.hasNext()){
			TripleParser tp=new TripleParser(in.next());
			int from=Integer.valueOf(tp.getSubject());
			int type=Integer.valueOf(tp.getPredicate());
			int to=Integer.valueOf(tp.getObject());
			edges[i++]=new Edge(from, to, type, 1.0);
		}
		in.close();
		return i;
	}
}
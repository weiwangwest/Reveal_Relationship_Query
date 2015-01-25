package input;

import graph.Graph;
import graph.Vertex;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.zip.GZIPInputStream;

import com.hp.hpl.jena.rdf.model.*;

public class DatasetLoaderFromMapReplacedFile {
	public static String pathToDataFiles="/data/";
	public static HashMap<String, Integer> Entities;  // a list of entities, when load Entities into a graph, Entities==Vertex.VertexMap
	public static long Numberoftriples;
	public static HashSet<String>DistinctRdfsSubclassOfStmtsSet;	//column 1	
	public static HashSet<String>DistinctRdfClassTypeExplicitClassDefinitionSet;	//column2
	public static HashSet<String>DistinctRdfClassTypeAsObjectTypeSet;		//column 3
	public static HashSet<String> OverallDistinctRDFclassesSet;			//column4

	public static boolean isBlankNode(String name){
		//the stmt follows doesn't work, because Jena gives each blank node a universal Hexadecimal number
		//return name.startsWith("_:");

		//test whether name contains 32 hexadecimal digits		
		if (name.length()!=32){
			return false;
		}
		try {
			new BigInteger(name, 16);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}	
	 //Total number of distinct RDF class types (total and used as object in rdf:type statements)
	//	either "rdf:type" or "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
	public static boolean isRdfClassType(String str){
		return (str.startsWith("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")||str.startsWith("rdf:type"));		
	}
	public static boolean isEntity(RDFNode node) throws Exception{
			return node.toString().startsWith("http")&&!(isBlankNode(node.toString())||node.isLiteral()||isRdfClassType(node.toString()));
	}
	public static void loadDataFromMapReplacedGzipNqFile(Graph g, String gzipFileName) throws Exception{
		GzipNqFileReader in=new GzipNqFileReader(gzipFileName);
		while (in.hasNext()){
			TripleParser tp=new TripleParser(in.next());
			int from=Integer.valueOf(tp.getSubject());
			int type=Integer.valueOf(tp.getPredicate());
			int to=Integer.valueOf(tp.getObject());
			if (!g.contains(from)){
				g.addVertex(new Vertex(from));
			}
			if (!g.contains(to)){
				g.addVertex(new Vertex(to));
			}
			g.addEdge(from, to, type, 1.0);
		}
	}

}
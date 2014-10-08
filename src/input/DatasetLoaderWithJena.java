package input;
import graph.Graph;
import graph.Vertex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.*;
import java.util.zip.GZIPInputStream;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.query.Dataset;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;

import output.Timer;
import output.WikiTable;

public class DatasetLoaderWithJena {
	public static String pathToDataFiles="/data/";
	public static HashSet <String> Entities; // a list of entities
	static long Numberoftriples;
	static HashSet<String>DistinctRdfsSubclassOfStmtsSet;	//column 1	
	static HashSet<String>DistinctRdfClassTypeExplicitClassDefinitionSet;	//column2
	static HashSet<String>DistinctRdfClassTypeAsObjectTypeSet;		//column 3
	static HashSet<String> OverallDistinctRDFclassesSet;			//column4

	public static void resetAllValues(){
		Entities=new HashSet<String>(); // a list of entities
		Numberoftriples=0;
		DistinctRdfsSubclassOfStmtsSet=new HashSet<String>();
		DistinctRdfClassTypeExplicitClassDefinitionSet=new HashSet<String>();
		DistinctRdfClassTypeAsObjectTypeSet=new HashSet<String>();
		OverallDistinctRDFclassesSet=new HashSet<String>();
	}
	/**
	 * Returns a pseudo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 *
	 * @param min Minimum value
	 * @param max Maximum value.  Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public static int randInt(int min, int max) {

	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	//randomly select number of Entities from the entities set.
	public static String [] selectEntities(int numberOfEntities){
		Object[] entitiesArray=Entities.toArray();
		HashSet <String> resultSet=new HashSet<String>();
		while(resultSet.size()<numberOfEntities){
			resultSet.add(entitiesArray[randInt(0, entitiesArray.length-1)].toString());
		}
		String [] results=new String[numberOfEntities];
		int i=0;
		for (String entityStr: resultSet){
			results [i++]=entityStr;
		}
		return results;
	}
	
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


	public static void getStatisticByTriple(Statement stmt) throws Exception{
		String subject=stmt.getSubject().toString();
		String object=stmt.getObject().toString();
		String predicate=stmt.getPredicate().toString();
		String statement=stmt.toString();
		
		//Generate a list of entities (subject or object URIs -- so no blank nodes, no literals, no RDF class types)
		if (isEntity(stmt.getSubject())){
			System.out.println(stmt.getSubject());
			Entities.add(new Vertex(stmt.getSubject().toString()).toString());
		}
		if (isEntity(stmt.getObject())){
			Entities.add(new Vertex(stmt.getObject().toString()).toString());
		}
		
		 //Total number of triples / NQuads in data set
		Numberoftriples++;

		//1. 关于Distinct Rdfs Subclass Stmts: Total number of distinct rdfs:subclass statements in data set
			//A rdfs:subClassOf B, then 
			//A rdf:type rdfs:Class
			//B rdf:type rdfs:Class			
			if (predicate.equals("http://www.w3.org/2000/01/rdf-schema#subClassOf")){
				OverallDistinctRDFclassesSet.add(subject);
				OverallDistinctRDFclassesSet.add(object);
				DistinctRdfsSubclassOfStmtsSet.add(statement);
			}

			//2. 关于Distinct Rdf Class Type: explicit class definition
			//D rdf:type rdfs:Class
			if (predicate.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type") 
					&& object.equals("http://www.w3.org/2000/01/rdf-schema#Class")){
				DistinctRdfClassTypeExplicitClassDefinitionSet.add(subject);
				OverallDistinctRDFclassesSet.add(subject);
			}

			//3. Distinct Rdf Class Type as Object: type statments: 
			//Total number of distinct RDF class types used as object in rdf:type statements
			//X rdf:type C, THEN
			//C rdf:type rdfs:Class
			if (predicate.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
				DistinctRdfClassTypeAsObjectTypeSet.add(object);
				OverallDistinctRDFclassesSet.add(object);
			}
			
			//4. Overall distinct RDF classes (new column)
			//Overall distinct RDF classes:
			//A+B+C+D distinct		
	}
  //generate entitiesList files and overviewOfDataSetFiles
	public static  void getEntitiesList_OverviewOfDataSet() throws Exception{
		Timer.tick("initialize data structure");
		//generate the table 2
		//System.out.println("<div id=\"Table 2. Test overview\"></div>");
		WikiTable table2=new WikiTable("Table 2. Test overview",		//title 
				new String[] {"testResults", "datasets", "triples/NQuads","entities", "distinctRdfsSubclassStmts", "distinctRdfClassTyps", "distinctRdfClassTypsAsObj", "OverallDistinctRdfClasses"}	//heads 
				);		

		//append the dataset report to lines of table 2.
		for (int  i=2; i<=3; i++){	//todo: i<=6
			Timer.tick("overview of data-0_"+i+".nq");			
			resetAllValues(); 
			Dataset dataset = RDFDataMgr.loadDataset(pathToDataFiles+"data-0_"+i+".nq", RDFLanguages.NQUADS);  //load data into Dataset 
			Iterator<String> it = dataset.listNames();
			while (it.hasNext()) {
				Model tim = dataset.getNamedModel(it.next());
				// add edges from statements,  get statistics
				StmtIterator s = tim.listStatements();
				while (s.hasNext()) {
					Statement stmt = s.next();
					getStatisticByTriple(stmt);
				}
			} //while each model 
			// write statistics into Wiki table as a new line of data
			table2.appendLine(
						new Object [] {	//dataLine
				            "(see [[#3|Table "+(i+1)+"]])", "{0.."+i+"}", 
				            String.valueOf(Numberoftriples), 
				            String.valueOf(Entities.size()), 
				            String.valueOf(DistinctRdfsSubclassOfStmtsSet.size()), 
				            String.valueOf(DistinctRdfClassTypeExplicitClassDefinitionSet.size()), 
				            String.valueOf(DistinctRdfClassTypeAsObjectTypeSet.size()),
				            String.valueOf(OverallDistinctRDFclassesSet.size()),
				        }
					);
			//write entities into file "entitiesList0_i"
			PrintStream entityList=new PrintStream("entitiesList"+"0_"+i);
			int lineNo=0;
			for (String entity: Entities){
				if (lineNo==0){
					entityList.print(entity); //prevent additional empty line at the end.					
				}else{
					entityList.print("\n"+entity); //prevent additional empty line at the end.
				}
				lineNo++;
			}
			entityList.close();			
		} 	// for each aggregated dataset

		//print the Wiki table to file and console
		PrintStream overviewDataFile=new PrintStream("overviewOfDataSetFiles");
		table2.print(overviewDataFile);
		overviewDataFile.close();
		table2.print(System.out);
		Timer.stop("");		
	}
	public static void main(String args[]) throws Exception{
		getEntitiesList_OverviewOfDataSet();
	}
	public void unGunzipFile(String compressedFile, String decompressedFile) {
		byte[] buffer = new byte[1024];
		try {
			FileInputStream fileIn = new FileInputStream(compressedFile);
			GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);
			FileOutputStream fileOutputStream = new FileOutputStream(decompressedFile);
			int bytes_read;
			while ((bytes_read = gZIPInputStream.read(buffer)) > 0) {
				fileOutputStream.write(buffer, 0, bytes_read);
			}
			gZIPInputStream.close();
			fileOutputStream.close();
			System.out.println("The file was decompressed successfully!");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	public static Graph generateGraphFromInputStream(InputStream inputStream, String fileName) throws IOException {
		Graph G=new Graph();
		BufferedReader bigFileReader = new BufferedReader(new InputStreamReader(inputStream));
		PrintStream partFileWriter=null;		
		String line;
		int fileId=0;
		do{	//divide file into small 2500 lines temp.nq
			line = bigFileReader.readLine();
			if (line!=null && !line.endsWith("> .")){
				System.err.println(line);
				assert(false);
			}
			if (line!=null){
				fileId ++;
				partFileWriter=new PrintStream(fileName+".part");
				for (int i=1; i<2500 && line!=null; i++){ 
					if (i==1) {
						partFileWriter.print(line);
					}else{
						partFileWriter.print("\n"+line);
					}
					line = bigFileReader.readLine(); 
					if (line!=null && !line.endsWith("> .")){
						System.err.println(line);
						assert(false);
					}
				}
				if (line!=null){
					partFileWriter.print("\n"+line);
				}
				partFileWriter.close();
				try{
					Graph gPart=generateGraphFromEntitiesOfNQFile(fileName+".part"); //load dataset from nq.part
					G.addAll(gPart);	//merge the dataset into G, catch outOfMemory exception		
				}catch(Exception e){
					new File(fileName+".part").renameTo(new File(fileName+".part."+fileId+".toadd"));
					PrintStream err=new PrintStream(fileName+".part."+fileId+".stacktrace");
					err.println(e.getMessage());
					e.printStackTrace(err);
					err.close();
				}
			}
		}while (line!=null);
		bigFileReader.close();
		return G;
	}
	public static Graph generateGraphFromEntitiesOfGzipBigNQFile(String gzipFileName) throws Exception{
		return generateGraphFromInputStream(new GZIPInputStream(new FileInputStream(gzipFileName)), gzipFileName);
	}	
	// load big dataset into a graph from a nq file
	public static Graph generateGraphFromEntitiesOfBigNQFile(String fileName) throws Exception{
		return  generateGraphFromInputStream(new FileInputStream(fileName), fileName);
	}
	// load entities and relationships into a graph from a dataset nq file 
	public static Graph generateGraphFromEntitiesOfNQFile(String fileName) throws Exception{
		Graph G = new Graph();
		Dataset dataset = RDFDataMgr.loadDataset(fileName, RDFLanguages.NQUADS);
		Iterator<String> it = dataset.listNames();			
		while (it.hasNext()) {
			Model tim = dataset.getNamedModel(it.next());
			
			// add subjects entities into Vertices
			ResIterator r = tim.listSubjects();			
			while (r.hasNext()) {
				Resource rsc=r.next();	//add entities only into the Graph
				if (isEntity(rsc)){
					G.addVertex(new Vertex(rsc.toString()));
				}
			}
			// add objects entities into Vertices
			NodeIterator n = tim.listObjects();
			while (n.hasNext()) {
				RDFNode rdfnd=n.next();
				if (isEntity(rdfnd)){
					G.addVertex(new Vertex(rdfnd.toString()));						
				}
			}
			// add  statements connecting two entities into edges
			StmtIterator s = tim.listStatements();
			while (s.hasNext()) {
				Statement stmt = s.next();
				if (isEntity(stmt.getSubject())&&isEntity(stmt.getObject())){
					G.addEdge(stmt.getSubject().toString(), stmt.getObject().toString(), stmt.getPredicate().toString(), 1);
				}
			}
			G.removeIsolatedVertices();
		} //while each model
		return G;
	}

	// load dataset into a graph from a nq file
	public static Graph generateGraphFromStmtsOfNQFile(String fileName){
		Graph G = new Graph();
		Dataset dataset = RDFDataMgr.loadDataset(fileName, RDFLanguages.NQUADS);
		Iterator<String> it = dataset.listNames();
		while (it.hasNext()) {
			Model tim = dataset.getNamedModel(it.next());
	
			// add Vertices from the dataset file
			ResIterator r = tim.listSubjects();
			while (r.hasNext()) {
				G.addVertex(new Vertex(r.next().toString()));
			}
			NodeIterator n = tim.listObjects();
			while (n.hasNext()) {
				G.addVertex(new Vertex(n.next().toString()));
			}
	
			// add edges from the dataset file
			StmtIterator s = tim.listStatements();
			while (s.hasNext()) {
				Statement stmt = s.next();
				G.addEdge(stmt.getSubject().toString(), stmt.getObject().toString(), stmt.getPredicate().toString(), 1);
			}
			G.removeIsolatedVertices();
		}
		return G;
	}	
}
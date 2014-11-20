package input;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.zip.GZIPInputStream;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.query.Dataset;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;

import fundamental.DBMapper;
import graph.Graph;
import graph.Vertex;

public class DatasetLoaderFromMapReplacedFile {
	public static String pathToDataFiles="/data/";
	public static DBMapper Entities;  // a list of entities, when load Entities into a graph, Entities==Vertex.VertexMap
	private static boolean isInTrans=false;	//transaction status
	public static long Numberoftriples;
	private static long NumberoftriplesTemp;		//temporary storage used by transaction
	public static HashSet<String>DistinctRdfsSubclassOfStmtsSet;	//column 1	
	public static HashSet<String>DistinctRdfClassTypeExplicitClassDefinitionSet;	//column2
	public static HashSet<String>DistinctRdfClassTypeAsObjectTypeSet;		//column 3
	public static HashSet<String> OverallDistinctRDFclassesSet;			//column4
	public static void resetAllValues(boolean IsEntitiesBoundToGraph){
		if (IsEntitiesBoundToGraph){
			Entities=Vertex.vertexMap;
		}else{
			Entities=new DBMapper("entity"); // a list of entities  TODO: table entity doesn't exist!
		}
		Numberoftriples=0;
		DistinctRdfsSubclassOfStmtsSet=new HashSet<String>();
		DistinctRdfClassTypeExplicitClassDefinitionSet=new HashSet<String>();
		DistinctRdfClassTypeAsObjectTypeSet=new HashSet<String>();
		OverallDistinctRDFclassesSet=new HashSet<String>();
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

	public static void startTrans(){		
		isInTrans=true;
		NumberoftriplesTemp=0;
	}
	public static void commitTrans(){
		Numberoftriples+=NumberoftriplesTemp;
		isInTrans=false;		
	}
	public static void rollBackTrans(){
		NumberoftriplesTemp=0;
		isInTrans=false;
	}
	public static void doStatisticByTriple(Statement stmt) throws Exception{
		String subject=stmt.getSubject().toString();
		String object=stmt.getObject().toString();
		String predicate=stmt.getPredicate().toString();
		String statement=stmt.toString();
		
		//Generate a list of entities (subject or object URIs -- so no blank nodes, no literals, no RDF class types)
		if (isEntity(stmt.getSubject())){
			System.out.println(stmt.getSubject());
			Entities.put(stmt.getSubject().toString());
		}
		if (isEntity(stmt.getObject())){
			Entities.put(stmt.getObject().toString());
		}
		
		 //Total number of triples / NQuads in data set
		if (isInTrans){
			NumberoftriplesTemp++;
		}else{
			Numberoftriples++;
		}

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
	public static void addEntitiesFromInputStreamExceptionHandler(Graph G, InputStream inputStream, String fileName) throws IOException {
		final int lineCapacityOfPartFile=2500; 
		BufferedReader bigFileReader = new BufferedReader(new InputStreamReader(inputStream));
		int lineIdNqFile=0;
		(new File(fileName+".toadd")).delete();
		(new File(fileName+".stacktrace")).delete();
		for (String line = bigFileReader.readLine(); line!=null; line = bigFileReader.readLine()){
				//divide file into smaller .part files consist of linesOfPartFile lines.
				PrintStream partFileWriter=new PrintStream(fileName+".part");
				int linesPartFile=0;
				for (int i=1; i<lineCapacityOfPartFile && line!=null; i++){ 
					lineIdNqFile ++;
					linesPartFile++;
					if (i==1) {
						partFileWriter.print(line);
					}else{
						partFileWriter.print("\n"+line);
					}
					line = bigFileReader.readLine(); 
//					if (line!=null && !line.endsWith("> .")){
//						System.out.println(line);
//						assert(false);
//					}
				}
				if (line!=null){
					lineIdNqFile ++;
					linesPartFile++;
					partFileWriter.print("\n"+line);
				}
				partFileWriter.close();

				// load dataset from nq.part
				try{
					addEntitiesFromNqNoExcetionProcessor(G, fileName+".part");  //merge the dataset into G, catch outOfMemory exception		
				}catch(Exception e){
					//	for each line of fileName.part {
					//  	write current line into fileName.part.part
					//		load dataset from fileName.part.part, and append to G
					//		if error happens{
					//			APPEND current line of fileName.part into fileName.toadd
					//			APPEND current error into fileName.part.stacktrace
					//		}
					//	}
					BufferedReader partFileReader = new BufferedReader(new FileReader(fileName+".part"));	
					int currentLineIdPartFile=0;
					for (String StrlineOfPartFile = partFileReader.readLine(); StrlineOfPartFile !=null; StrlineOfPartFile = partFileReader.readLine()){
						currentLineIdPartFile++;
						PrintWriter writer=new PrintWriter(new BufferedWriter(new FileWriter(fileName+".part.part")));
						writer.println(StrlineOfPartFile);
						writer.close();
						try{
							addEntitiesFromNqNoExcetionProcessor(G, fileName+".part.part"); //add the dataset into G, catch outOfMemory exception								
						}catch(Exception e1){
							writer=new PrintWriter(new BufferedWriter(new FileWriter(fileName+".toadd")));
							writer.println((lineIdNqFile-linesPartFile+currentLineIdPartFile)+": "+StrlineOfPartFile);
							writer.close();
							writer=new PrintWriter(new BufferedWriter(new FileWriter(fileName+".stacktrace")));
							writer.println((lineIdNqFile-linesPartFile+currentLineIdPartFile)+": ");
							writer.println(e1.getMessage());
							StringWriter stackTraceSW= new StringWriter();
							e1.printStackTrace(new PrintWriter(stackTraceSW));
							writer.println(stackTraceSW.toString());
							writer.close();
						}
					}
					partFileReader.close();
				}
		}
		bigFileReader.close();
	}
	public static void addEntitiesFromBigGzipNq(Graph G, String gzipFileName) throws Exception{
		addEntitiesFromInputStreamExceptionHandler(G, new GZIPInputStream(new FileInputStream(gzipFileName)), gzipFileName);
	}
	// load big dataset into a graph from a nq file
	public static void addEntitiesFromBigNq(Graph G, String fileName) throws Exception{
		addEntitiesFromInputStreamExceptionHandler(G, new FileInputStream(fileName), fileName);
	}
	// load entities and relationships into existing graph from a dataset nq file 
	// get statistic by triples of all statements
	public static void addEntitiesFromNqNoExcetionProcessor(Graph G, String fileName) throws Exception {
		try{
			startTrans();
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
					doStatisticByTriple(stmt); 
					if (isEntity(stmt.getSubject())&&isEntity(stmt.getObject())){
						G.addEdge(stmt.getSubject().toString(), stmt.getObject().toString(), stmt.getPredicate().toString(), 1);
					}
				}
				// even without isolated vertices, we still can not make sure all vertices are connected.
				//G.removeIsolatedVertices(); 
			} //while each model			
			commitTrans();
		}catch(Exception e){
			rollBackTrans();
			throw e;
		}
	}

	// load dataset into a graph from a nq file
	public static void addAllFromNqNoExcetionProcessor(Graph G, String fileName) throws Exception{
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
				doStatisticByTriple(stmt);				
				G.addEdge(stmt.getSubject().toString(), stmt.getObject().toString(), stmt.getPredicate().toString(), 1);
			}
			// even without isolated vertices, we still can not make sure all vertices are connected.
			//G.removeIsolatedVertices();
		}
	}
}
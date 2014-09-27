package performance;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.*;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.query.Dataset;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;

import output.Timer;
import output.WikiTable;

public class JenaPerformTestDatanq {
	public static String pathToDataFiles="";
	static HashSet <String> Entities; // a list of entities
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
			Entities.add(stmt.getSubject().toString());
		}
		if (isEntity(stmt.getObject())){
			Entities.add(stmt.getObject().toString());
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
		//JenaPerformTestDatanq.pathToDataFiles = "/home/wang/myDocuments/UniKoblenz/STAR/temp/";
		JenaPerformTestDatanq.pathToDataFiles = "/home/wang//myDocuments/UniKoblenz/STAR/data/";
		getEntitiesList_OverviewOfDataSet();
	}
}
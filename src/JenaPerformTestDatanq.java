import java.io.PrintStream;
import java.math.BigInteger;
import java.util.*;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.query.Dataset;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;

import output.format.WikiTable;

public class JenaPerformTestDatanq {
	static HashSet <String> entities; // a list of entities
	static long numberoftriples;
	static long numberofdistinctrdfsSubclassstatement;
	static long numberofdistinctRDFclasstypesTotal;
	static long numberofdistinctRDFclasstypesUsedAsObject;
	static String pathToDataFiles="";
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
		Object[] entitiesArray=entities.toArray();
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
		//Generate a list of entities (subject or object URIs -- so no blank nodes, no literals, no RDF class types)
		if (isEntity(stmt.getSubject())){
			entities.add(stmt.getSubject().toString());
		}
		if (isEntity(stmt.getObject())){
			entities.add(stmt.getObject().toString());
		}
		
		 //Total number of triples / NQuads in data set
		numberoftriples++;
		
		//Total number of distinct rdfs:subClassOf statements in data set
		if (stmt.getPredicate().toString().contains("rdfs:subClassOf")||stmt.getPredicate().toString().contains("http://www.w3.org/2000/01/rdf-schema#subClassOf")){
			numberofdistinctrdfsSubclassstatement++;
		}
		
		//	rdfs:subClassOf
		// http://www.w3.org/2000/01/rdf-schema#subClassOf
		
		if (isRdfClassType(stmt.getSubject().toString())){
			
			numberofdistinctRDFclasstypesTotal++;
		}
		if (isRdfClassType(stmt.getObject().toString())){
			numberofdistinctRDFclasstypesTotal++;
			numberofdistinctRDFclasstypesUsedAsObject++;
		}
	}
	//generate entitiesList files and overviewOfDataSetFiles
	public static  void getEntitiesList_OverviewOfDataSet() throws Exception{
		Timer.tick("initialize data structure");
		//generate the table 2
		//System.out.println("<div id=\"Table 2. Test overview\"></div>");
		WikiTable table2=new WikiTable("Table 2. Test overview",		//title 
				new String[] {"testResults", "datasets", "entities","triples/NQuads", "distinctRdfsSubclassStmts", "distnctRdfClassTypsTotal", "distnctRdfClassTypsObj"}	//heads 
				);		

		//append the dataset report to lines of table 2.
		for (int  i=2; i<=6; i++){	//todo: i<=6
			Timer.tick("overview of data-0_"+i+".nq");
			
			entities=new HashSet<String>(); // a list of entities
			numberoftriples=0;
			numberofdistinctrdfsSubclassstatement=0;
			numberofdistinctRDFclasstypesTotal=0;
			numberofdistinctRDFclasstypesUsedAsObject=0;
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
				            "(see [[#3|Table "+(i+1)+"]])", "{0.."+i+"}", String.valueOf(entities.size()), String.valueOf(numberoftriples), String.valueOf(numberofdistinctrdfsSubclassstatement), String.valueOf(numberofdistinctRDFclasstypesTotal), String.valueOf(numberofdistinctRDFclasstypesUsedAsObject)
				        }
					);
			//write entities into file "entitiesList0_i"
			PrintStream entityList=new PrintStream("entitiesList"+"0_"+i);
			int lineNo=0;
			for (String entity: entities){
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
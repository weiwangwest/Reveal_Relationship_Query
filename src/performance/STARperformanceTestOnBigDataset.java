package performance;
import fundamental.Randomizer;
import graph.Graph;

import input.DatasetLoaderWithJena;

import java.io.*;

import output.Timer;
import output.WikiTable;

public class  STARperformanceTestOnBigDataset {
	
	public static void main(String[] args) throws Exception {		
		Timer.start(null);		
		Graph G=new Graph();
		// entities list
		DatasetLoaderWithJena.resetAllValues(true);	//DatasetLoaderWithJena.Entities=Vertex.vertexMap;	

		//generate the table 2
		//System.out.println("<div id=\"Table 2. Test overview\"></div>");
		WikiTable table2=new WikiTable("Table 2. Test overview",		//title 
				new String[] {
				"testResults", 
				"datasets", 
				"triples/NQuads",
				"entities", 
				"distinctRdfsSubclassStmts", 
				"distinctRdfClassTyps", 
				"distinctRdfClassTypsAsObj", 
				"OverallDistinctRdfClasses"}	//heads 
				);		
		
		for (int  idOfDataFile=0; idOfDataFile<=6; idOfDataFile++){	//todo: i<=6			
			Timer.tick("data-"+idOfDataFile+".nq.gz");
			try{
				 DatasetLoaderWithJena.addEntitiesFromBigGzipNq(G, DatasetLoaderWithJena.pathToDataFiles+"data-"+idOfDataFile+".nq.gz");
			 	//append the current dataset report to lines of table 2.
				table2.appendLine(
						new Object [] {	//dataLine
				            "(see [[#3|Table "+(idOfDataFile+1)+"]])", 
				            "{0.."+idOfDataFile+"}", 
				            String.valueOf(DatasetLoaderWithJena.Numberoftriples), 
				            String.valueOf(DatasetLoaderWithJena.Entities.size()), 
				            String.valueOf(DatasetLoaderWithJena.DistinctRdfsSubclassOfStmtsSet.size()), 
				            String.valueOf(DatasetLoaderWithJena.DistinctRdfClassTypeExplicitClassDefinitionSet.size()), 
				            String.valueOf(DatasetLoaderWithJena.DistinctRdfClassTypeAsObjectTypeSet.size()),
				            String.valueOf(DatasetLoaderWithJena.OverallDistinctRDFclassesSet.size()),
				        }
					);
				//append the Wiki table to file and console				
				PrintWriter overviewFile=new PrintWriter(new BufferedWriter(new FileWriter("overviewOfDataSetFiles", true)));
				table2.print(overviewFile);
				overviewFile.close();
				table2.print(new PrintWriter(System.out));

				 if (idOfDataFile<2){	//skip query on data-0, 1
					 continue;
				 }
				//write entities into file "entitiesList0_i"
				PrintWriter entitiesFile=new PrintWriter(new BufferedWriter(new FileWriter("entitiesList"+"0_"+idOfDataFile)));
				long size=DatasetLoaderWithJena.Entities.size();
				if (size>Integer.MAX_VALUE){
					PrintWriter writer=new PrintWriter(new BufferedWriter(new FileWriter("data-"+idOfDataFile+".bigPerform.error", true)));
					writer.println("size out of range of integer: " + size);
					writer.close();
				}
				for (int value=0; value<size; value++){
					if (value==0){
						entitiesFile.print(DatasetLoaderWithJena.Entities.getKey(value)); //prevent additional empty line at the end.					
					}else{
						entitiesFile.print("\n"+DatasetLoaderWithJena.Entities.getKey(value)); //prevent additional empty line at the end.
					}
				}
				entitiesFile.close();

				 //do different types of queries
				for (int currentQueryType = 0; currentQueryType < 3; currentQueryType++) {
					QUERYID:
					for (int queryId = 0; queryId < 3; queryId++) {
/*						// if the queryRunTime file exists, skip current loop
						if (new File("queryRunTime"+i+"."+(currentQueryType+1)+"."+(queryId+1)).exists()){
							continue;
						}
*/						// generate entities for current query
						String[] requiredVertices = Randomizer.getRandomSetOfEntitiesString(currentQueryType + 2); // for qtyp1 2 URIs, qtyp2 3 URIs, qtyp3 4 URIs
						double runTimeOfCurrentQueryId = 0;
						long runTimes[]=new long[11]; //the last element is saved for average time.
						String queryRecordTxt="";
						for (int queryRun = 0; queryRun < 10; queryRun++) {
							//run query once
							String lineRequiredVertices="";
							for (String vertexStr: requiredVertices){
								lineRequiredVertices +=" " + vertexStr;
							}
							queryRecordTxt = lineRequiredVertices;
							Timer.tick(lineRequiredVertices);
							Graph T=G.findBestSteinerTree(requiredVertices);
	/*						if (T.getWeight(T) >= 1000){	//if there is at least one isolated vertex in the tree.
								queryId --;		//select another set of entities.
								continue QUERYID ;
							}
	*/
							runTimes[queryRun]=Timer.tick(null);
							queryRecordTxt += "\n"+T.printTreeToString(T)+"\n";
							runTimeOfCurrentQueryId +=runTimes[queryRun];
						} // for queryRun
						
						//store queryRecord into a file
						PrintWriter queryEntitiesFile=new PrintWriter(new BufferedWriter(new FileWriter("queryRecord"+idOfDataFile+"."+(currentQueryType+1)+"."+(queryId+1))));
						queryEntitiesFile.println(queryRecordTxt);
						queryEntitiesFile.close();
	
						runTimeOfCurrentQueryId /= 10; //average time of each run
						//store 10 and the average runtime into a file each.
						PrintWriter queryTimeFile=new PrintWriter(new BufferedWriter(new FileWriter("queryRunTime"+idOfDataFile+"."+(currentQueryType+1)+"."+(queryId+1))));
						queryTimeFile.println(requiredVertices);
						for (int queryRun=0; queryRun<10; queryRun++){
							queryTimeFile.println(runTimes[queryRun]);
						}
						queryTimeFile.println(runTimeOfCurrentQueryId);
						queryTimeFile.close();
					} // for queryID
				} // for currentQueryType
			}catch(Exception e){
				PrintWriter errFile=new PrintWriter(new BufferedWriter(new FileWriter("data-"+idOfDataFile+".bigPerform.error")));
				errFile.println(e.getMessage());
				e.printStackTrace(errFile);
				errFile.close();
			}
		} 	// for each aggregated dataset

		//print the Wiki table to console
		Timer.stop("");
	}
}
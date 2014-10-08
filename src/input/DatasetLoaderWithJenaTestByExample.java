package input;
import graph.Graph;
import graph.Vertex;


import java.io.*;
import java.util.*;

import output.Timer;


public class DatasetLoaderWithJenaTestByExample {
	public static void main(String[] args) throws Exception {
		Timer.start(null);
		// append the dataset report to lines of table 2.
		Timer.tick("example.nq");
		Graph G = DatasetLoaderWithJena.generateGraphFromEntitiesOfNQFile(DatasetLoaderWithJena.pathToDataFiles	+ "example.nq");
		DatasetLoaderWithJena.Entities = new HashSet<String>(); // a list of entities
		for (Vertex v: G.V.values()){
			DatasetLoaderWithJena.Entities.add(v.getNameString());					
		}
		//do performance test 3 types(queries with 2, 3, 4 entities respectively), 3 queries/type, 10 runs/query
		for (int currentQueryType = 0; currentQueryType < 3; currentQueryType++) {
			for (int queryId = 0; queryId < 3; queryId++) {				
				// if the queryRunTime file exists, skip current loop
				if (new File("queryRunTime"+"."+(currentQueryType+1)+"."+(queryId +1)).exists()){
					continue; 
				}
				// generate entities for current query
				String[] requiredVertices = DatasetLoaderWithJena.selectEntities(currentQueryType + 2); // for qtyp1 2 URIs, qtyp2 3 URIs, qtyp3 4URIs
				String lineRequiredVertices = "";
				boolean firstTime=true;
				for (String vertexStr : requiredVertices) {
					if (!firstTime){
						lineRequiredVertices += " " ;
					}else{
						firstTime=false;
					}
					lineRequiredVertices += vertexStr;
				}
				System.out.println(lineRequiredVertices);
				// run query for 10 times to get query time
				String queryRecordTxt =lineRequiredVertices;
				double runTimeOfCurrentQueryId = 0;
				long runTimes[] = new long[11]; // the last element is saved for average time.				
				for (int queryRun = 0; queryRun < 10; queryRun++) {		
					Timer.tick(String.valueOf(queryRun));
					Graph T = G.findBestSteinerTree(requiredVertices);					
					runTimes[queryRun] = Timer.tick(null);
					queryRecordTxt +=  "\n"+String.valueOf(queryRun)+"\n" + T.printTreeToString(T);
					runTimeOfCurrentQueryId += runTimes[queryRun];
				} 
				// store queryRecord into a file
				PrintStream queryRecord = new PrintStream("queryRecord" + "."	+ (currentQueryType + 1) + "." + (queryId + 1));
				queryRecord.print(queryRecordTxt);
				queryRecord.close();
				// store 10 and the average runtime into a file.
				PrintStream queryTimeOut = new PrintStream("queryRunTime" + "."+ (currentQueryType + 1) + "." + (queryId + 1));
				for (int queryRun = 0; queryRun < 10; queryRun++) {
					queryTimeOut.println(runTimes[queryRun]);
				}
				runTimeOfCurrentQueryId /= 10; // average time of each run
				queryTimeOut.println(runTimeOfCurrentQueryId);
				queryTimeOut.close();
			} // for queryID
		} // for currentQueryType
		// print the Wiki table to console
		Timer.stop("");
	}
}
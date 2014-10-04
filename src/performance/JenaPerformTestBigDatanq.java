package performance;
import graph.Graph;

import java.io.File;
import java.io.PrintStream;
import java.util.*;
import output.Timer;

public class JenaPerformTestBigDatanq {
	
	public static void main(String[] args) throws Exception {		
	
		Timer.start(null);		
		//append the dataset report to lines of table 2.
		Graph G=new Graph();
		for (int  i=0; i<=6; i++){	//todo: i<=6
			Timer.tick("data-"+i+".nq.gz");
			try{
				 G.addAll(JenaPerformTestDatanq.generateGraphFromEntitiesOfGzipBigNQFile(JenaPerformTestDatanq.pathToDataFiles+"data-"+i+".nq.gz"));
				 if (i<2){	//skip query on data-0, 1
					 continue;
				 }
				JenaPerformTestDatanq.Entities=new HashSet<String>();	// a list of entities
				JenaPerformTestDatanq.Entities.addAll(G.V.keySet()); 
				for (int currentQueryType = 0; currentQueryType < 3; currentQueryType++) {
					QUERYID:
					for (int queryId = 0; queryId < 3; queryId++) {
/*						// if the queryRunTime file exists, skip current loop
						if (new File("queryRunTime"+i+"."+(currentQueryType+1)+"."+(queryId+1)).exists()){
							continue;
						}
*/						// generate entities for current query
						String[] requiredVertices = JenaPerformTestDatanq.selectEntities(currentQueryType + 2); // for qtyp1 2 URIs, qtyp2 3 URIs, qtyp3 4 URIs
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
						PrintStream queryRecord=new PrintStream("queryRecord"+i+"."+(currentQueryType+1)+"."+(queryId+1));
						queryRecord.println(queryRecordTxt);
						queryRecord.close();
	
						runTimeOfCurrentQueryId /= 10; //average time of each run
						//store 10 and the average runtime into a file each.
						PrintStream queryTimeOut=new PrintStream("queryRunTime"+i+"."+(currentQueryType+1)+"."+(queryId+1));
						queryTimeOut.println(requiredVertices);
						for (int queryRun=0; queryRun<10; queryRun++){
							queryTimeOut.println(runTimes[queryRun]);
						}
						queryTimeOut.println(runTimeOfCurrentQueryId);
						queryTimeOut.close();
					} // for queryID
				} // for currentQueryType
			}catch(Exception e){
				PrintStream err=new PrintStream("data-"+i+".bigPerform.error");
				err.println(e.getMessage());
				e.printStackTrace(err);
				err.close();
			}
			} 	// for each aggregated dataset

		//print the Wiki table to console
		Timer.stop("");
	}
}
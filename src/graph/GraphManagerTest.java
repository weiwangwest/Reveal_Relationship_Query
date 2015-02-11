package graph;

import static org.junit.Assert.*;

import fundamental.DBMapper;
import fundamental.FileNameManager;
import fundamental.Randomizer;
import input.DatasetLoaderWithJena;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;

public class GraphManagerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		new DBMapper("vertex").clear();
		new DBMapper("edge_type").clear();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		new DBMapper("vertex").clear();
		new DBMapper("edge_type").clear();
	}

	@Test
	public void testProduceRandomTree() {
		for (int i = 1; i <= 10; i++) {
			for (int run = 0; run < 100; run++) {
				Graph tree = GraphManager.produceRandomTree(i);
				assertTrue(tree.shadowEdges().size()==0);				
				boolean b=GraphManager.isATree(tree);
				if (b==false){
					assertTrue("not a tree", GraphManager.isATree(tree));					
				}
			}
		}
	}
	@Test
	public void testProduceRandomConnectedUndirectedGraph(){
		for (int nVertices = 1; nVertices <= 5; nVertices++) {
			for (int nEdges = nVertices -1 ; nEdges<= nVertices *(nVertices -1)/2; nEdges ++){ 
				for (int run = 0; run < 100; run++) {
					Graph g = GraphManager.produceRandomConnectedUndirectedGraph(nVertices, nEdges);
					assertTrue("not a connected graph", GraphManager.isConnected(g));
				}				
			}
		}
	}
	@Test
	public void testProduceRandomSteinerTree() {
		for (int nodes = 3; nodes < 10; nodes++) { // to do: nodes to be changed.
			for (int terminals = 2; terminals <= nodes; terminals++) {
				for (int run = 0; run < 100; run++) {
					Tree tree = GraphManager.produceRandomSteinerTree(nodes, terminals);
					assertTrue(tree.shadowEdges().size()==0);					
					boolean t=GraphManager.isATree(tree);
					if (t==false){
						assertTrue("not a tree", t=GraphManager.isATree(tree));
					}
				}
			}
		}
	}

	@Test
	public void testFindBestSteinerTree() {
		  for (int nVertices = 1; nVertices < 5; nVertices++) {
			for (int nEdges = nVertices -1; nEdges<= nVertices *(nVertices -1)/2; nEdges ++){
				for (int nTerminals=2; nTerminals <= 4 && nTerminals<=nVertices; nTerminals++){
/*		  for (int nVertices = 5; nVertices < 10; nVertices++) { //to do : nVertices < 10
				for (int nEdges = 4; nEdges<= nVertices *(nVertices -1)/2; nEdges ++){
					for (int nTerminals=4; nTerminals <= 4 && nTerminals<=nVertices; nTerminals++){
*/
					for (int run = 0; run < 100; run++) {
						System.out.println("nVertices"+nVertices+"nEdges:"+nEdges+"nTerminals:"+nTerminals+"run:"+run);
				
						Graph g = GraphManager.produceRandomConnectedUndirectedGraph(nVertices, nEdges);
						HashSet <Integer> requiredVerticesSet=new HashSet<Integer>();
						while (requiredVerticesSet.size()<nTerminals){
							requiredVerticesSet.add(Randomizer.randInt(1, nVertices));
						}
						int[] requiredVertices=new int[nTerminals];
						int i=0;
						for (Integer vId: requiredVerticesSet){
							//requiredVertices[i++]=Vertex.vertexMap.getKey(vId);
							requiredVertices[i++]=vId;
						}
						
/*							Graph g = new Graph();
							g.addVertex("0");
							g.addVertex("1");
							g.addVertex("2");
							g.addVertex("3");
							g.addVertex("4");
							g.addEdge("1", "0", "", 1);
							g.addEdge("2", "0", "", 1);
							g.addEdge("3", "1", "", 1);
							g.addEdge("4", "2", "", 1);
							String[] requiredVertices=new String[nTerminals];
							requiredVertices[0]="3";
							requiredVertices[1]="2";
							requiredVertices[2]="1";
							requiredVertices[3]="4";							
						System.out.println("\n******graph******");
							g.print();		//to do: to be deleted
							System.out.print("\n****Terminals: [");
						for (String str:requiredVertices){
							System.out.print(str+" "); //to do: to be deleted
						}
						System.out.println("]");
*/
						Tree T = GraphManager.findBestSteinerTree(g, requiredVertices);
						//System.out.println("\n******tree******");
						//g.printTree(T);	//to do: to be deleted
						//check result
						for (int vertexId: requiredVertices){
							assertTrue(" could not be found in the tree." ,T.contains(vertexId));
						}
						assertTrue("The result is not a tree!", GraphManager.isATree(T));
						// since isolated vertex has been removed from the graph when it's created, following case should be considered as a unit test failure.
						// if there is at least one isolated vertex in the tree.
						//assertTrue("failed to find a tree,  about to select another set of entities", );
						if (T.getWeight() >= 500){
							fail("failed to find a tree,  about to select another set of entities");
						}
					}							
				}		
			}
		}
	}
	@Test
	public void testFindBestSteinerTreeForSpecificExample1() throws Exception {
					for (int run = 0; run < 1000; run++) {
						Graph g=new Graph(Graph.TREE_CAPACITY);
						DatasetLoaderWithJena.resetAllValues(true);
						DatasetLoaderWithJena.addEntitiesFromNqNoExcetionProcessor(g, FileNameManager.pathToDataFiles	+ "example.nq");
						String[] requiredVertices=new String[3];						  
						requiredVertices[2]="http://example.org/bob/";
						requiredVertices[1]="http://xmlns.com/foaf/0.1/Person";
						requiredVertices[0]="http://example.org/alice/foaf.rdf#me";
						//System.out.println("run:"+run);
						Tree T = GraphManager.findBestSteinerTree(g, requiredVertices);
						for (String vertexStr: requiredVertices){
							assertTrue(" could not be found in the tree." ,T.getVertex(Vertex.vertexMap.getValue(vertexStr))!=null);
						}
						assertTrue("The result is not a tree!", GraphManager.isATree(T));
						if (Double.compare(T.getWeight(), 500) > 0){
							fail("failed to find a tree,  about to select another set of entities");
						}
						System.out.println("\n"+String.valueOf(run)+"\n" + T.toString());
					}							
				}		
	public static void main(String[] args) throws Exception {                    
	       JUnitCore.main(
	         "graph.GraphManagerTest"); 
	}

}

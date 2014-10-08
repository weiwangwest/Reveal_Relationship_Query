package graph;

import static org.junit.Assert.*;

import input.DatasetLoaderWithJena;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class GraphTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testProduceRandomTree() {
		for (int i = 1; i < 100; i++) {
			for (int run = 0; run < 100; run++) {
				Graph tree = Graph.produceRandomTree(i);
				assertTrue("not a tree", Graph.isATree(tree));
			}
		}
	}
	@Test
	public void testProduceRandomConnectedUndirectedGraph(){
		for (int nVertices = 1; nVertices < 5; nVertices++) {
			for (int nEdges = nVertices -1; nEdges<= nVertices *(nVertices -1)/2; nEdges ++){
				for (int run = 0; run < 100; run++) {
					Graph g = Graph.produceRandomConnectedUndirectedGraph(nVertices, nEdges);
					assertTrue("not a connected graph", g.isConnected());
				}				
			}
		}
	}
	@Test
	public void testProduceRandomSteinerTree() {
		for (int nodes = 3; nodes < 10; nodes++) { // to do: nodes to be changed.
			for (int terminals = 2; terminals <= nodes; terminals++) {
				for (int run = 0; run < 100; run++) {
					Graph tree = Graph.produceRandomSteinerTree(nodes, terminals);
					assertTrue("not a tree", Graph.isATree(tree));
				}
			}
		}
	}

	@Test
	public void testGetLoosePaths() {
		for (int nodes = 3; nodes < 10; nodes++) {
			for (int terminals = 3; terminals <= nodes; terminals++) {
				for (int i = 0; i < 100; i++) {
					Graph tree = Graph.produceRandomSteinerTree(nodes,
							terminals);
					PriorityQueue<LoosePath> lps = Graph.getLoosePaths(tree);
					HashSet<Edge> edgeSet = new HashSet<Edge>();
					for (LoosePath lp : lps) {
						ArrayList<Vertex> vtcs = lp.getVertices();
						Vertex v1 = vtcs.get(0);
						Vertex v2 = vtcs.get(vtcs.size() - 1);
						// both end nodes must be fixed nodes
						Vertex start = lp.getStartVertex();
						Vertex end = lp.getEndVertex();
						assertTrue("endpoint not match",
								(v1 == start || v1 == end)
										&& (v2 == start || v2 == end));
						assertTrue("start point is not a fixed node",
								start.isTerminal
										|| start.getDegreeInGraph(tree) >= 3);
						assertTrue("end point is not a fixed node",
								end.isTerminal
										|| end.getDegreeInGraph(tree) >= 3);
						// no fixed node within the path
						vtcs = lp.getVerticesWithinPath();
						for (Vertex v : vtcs) {
							assertTrue("fixed node within a loosepath",
									!v.isTerminal
											&& v.getDegreeInGraph(tree) < 3);
						}
						edgeSet.addAll(lp.getEdges());
					}
					// check {edges in the Steiner tree} =={edges in the lps }
					HashSet<Edge> treeEdges = new HashSet<Edge>(tree.E);
					assertTrue("steiner tree edges not contain ",
							edgeSet.equals(treeEdges));
				}
			}
		}
	}

	@Test
	public void testFindBestSteinerTree() {
		  for (int nVertices = 1; nVertices < 10; nVertices++) {
			for (int nEdges = nVertices -1; nEdges<= nVertices *(nVertices -1)/2; nEdges ++){
				for (int nTerminals=2; nTerminals <= 4 && nTerminals<=nVertices; nTerminals++){
/*		  for (int nVertices = 5; nVertices < 10; nVertices++) { //to do : nVertices < 10
				for (int nEdges = 4; nEdges<= nVertices *(nVertices -1)/2; nEdges ++){
					for (int nTerminals=4; nTerminals <= 4 && nTerminals<=nVertices; nTerminals++){
*/
					for (int run = 0; run < 100; run++) {
						System.out.println("nVertices"+nVertices+"nEdges:"+nEdges+"nTerminals:"+nTerminals+"run:"+run);
				
						Graph g = Graph.produceRandomConnectedUndirectedGraph(nVertices, nEdges);
						HashSet <Integer> requiredVerticesSet=new HashSet<Integer>();
						while (requiredVerticesSet.size()<nTerminals){
							requiredVerticesSet.add(DatasetLoaderWithJena.randInt(0, nVertices-1));
						}
						String[] requiredVertices=new String[nTerminals];
						int i=0;
						for (Integer vId: requiredVerticesSet){
							requiredVertices[i++]=Vertex.vertexMap.getKey(vId);							
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
						Graph T = g.findBestSteinerTree(requiredVertices);
						//System.out.println("\n******tree******");
						//g.printTree(T);	//to do: to be deleted
						//check result
						for (String vertexStr: requiredVertices){
							assertTrue(" could not be found in the tree." ,T.V.containsKey(Vertex.vertexMap.getValue(vertexStr)));
						}
						assertTrue("The result is not a tree!", Graph.isATree(T));
						// since isolated vertex has been removed from the graph when it's created, following case should be considered as a unit test failure.
						// if there is at least one isolated vertex in the tree.
						//assertTrue("failed to find a tree,  about to select another set of entities", );
						if (T.getWeightTree(T) >= 500){
							fail("failed to find a tree,  about to select another set of entities");
						}
					}							
				}		
			}
		}
	}
	@Test
	public void testFindBestSteinerTreeForSpecificExample1() throws Exception {
					for (int run = 0; run < 10000; run++) {
						Graph g = DatasetLoaderWithJena.generateGraphFromEntitiesOfNQFile(DatasetLoaderWithJena.pathToDataFiles	+ "example.nq");
						String[] requiredVertices=new String[3];						  
						requiredVertices[2]="http://example.org/bob/";
						requiredVertices[1]="http://xmlns.com/foaf/0.1/Person";
						requiredVertices[0]="http://example.org/alice/foaf.rdf#me";
						//System.out.println("run:"+run);				
						Graph T = g.findBestSteinerTree(requiredVertices);
						for (String vertexStr: requiredVertices){
							assertTrue(" could not be found in the tree." ,T.V.containsKey(Vertex.vertexMap.getValue(vertexStr)));
						}
						assertTrue("The result is not a tree!", Graph.isATree(T));
						if (Double.compare(T.getWeightTree(T), 500) > 0){
							fail("failed to find a tree,  about to select another set of entities");
						}
						System.out.println("\n"+String.valueOf(run)+"\n" + T.printTreeToString(T));
					}							
				}		
}

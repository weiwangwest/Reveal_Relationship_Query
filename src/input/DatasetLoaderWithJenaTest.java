package input;

import static org.junit.Assert.*;

import java.io.File;

import fundamental.DBMapper;
import graph.Graph;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;

public class DatasetLoaderWithJenaTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DatasetLoaderWithJena.resetAllValues(true);	//DatasetLoaderWithJena.Entities=Vertex.vertexMap;	
		new DBMapper("vertex").clear();
		new DBMapper("edge").clear();
		new DBMapper("edge_type").clear();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		new DBMapper("vertex").clear();
		new DBMapper("edge").clear();
		new DBMapper("edge_type").clear();
	}

	@Test //passed when singlely run on reveal VM 
	public void addEntitiesFromBigGzipNqCorrectnessTest() throws Exception {
		new DBMapper("vertex").clear();
		new DBMapper("edge").clear();
		new DBMapper("edge_type").clear();
		//append the dataset report to lines of table 2.
		Graph g1 = new Graph(Graph.GRAPH_CAPACITY);
		DatasetLoaderWithJena.resetAllValues(true);
		DatasetLoaderWithJena.addEntitiesFromBigGzipNq(g1, DatasetLoaderWithJena.pathToDataFiles+"data-0.nq.gz");
		DatasetLoaderWithJena.addEntitiesFromBigGzipNq(g1, DatasetLoaderWithJena.pathToDataFiles+"data-1.nq.gz");
		DatasetLoaderWithJena.addEntitiesFromBigGzipNq(g1, DatasetLoaderWithJena.pathToDataFiles+"data-2.nq.gz");
		Graph g2= new Graph(Graph.GRAPH_CAPACITY);
		DatasetLoaderWithJena.addEntitiesFromNqNoExcetionProcessor(g2, DatasetLoaderWithJena.pathToDataFiles+"data-0_2.nq");
		assertTrue("", g1.equals(g2));
	}	

	@Test
	public void addEntitiesFromBigNqCorrectnessTest() throws Exception {
		DatasetLoaderWithJena.resetAllValues(true);
		new DBMapper("vertex").clear();
		new DBMapper("edge").clear();
		new DBMapper("edge_type").clear();
		//append the dataset report to lines of table 2.
		for (int  i=1; i<=2; i++){	//todo: i<=6
			Graph g1 = new Graph(Graph.GRAPH_CAPACITY);
			DatasetLoaderWithJena.addEntitiesFromNqNoExcetionProcessor(g1, DatasetLoaderWithJena.pathToDataFiles+"data-0_"+i+".nq");
			//Graph g2 = JenaPerformTestDatanq.generateGraphFromEntitiesOfNQFile(JenaPerformTestDatanq.pathToDataFiles+"data-0_"+i+".nq");
			Graph g2= new Graph(Graph.GRAPH_CAPACITY);
			DatasetLoaderWithJena.addEntitiesFromBigNq(g2, DatasetLoaderWithJena.pathToDataFiles+"data-0_"+i+".nq");
			assertTrue("", g1.equals(g2));
		} 	// for each aggregated dataset
	}
/*
	@Test
	public void generateGraphFromEntitiesOfBigNQFileMemoryTest() throws Exception {
		for (int i=5; i<=6; i++){
			DatasetLoaderWithJena.addGraphAndStatisticsFromEntitiesOfGzipBigNQFile(new Graph(), DatasetLoaderWithJena.pathToDataFiles+"data-"+i+".nq.gz");
			assertTrue(true);
		}
	}
*/
	@Test
	public void addEntitiesFromInputStreamExceptionHandlerTest() throws Exception {
		new DBMapper("vertex").clear();
		new DBMapper("edge").clear();
		new DBMapper("edge_type").clear();
		DatasetLoaderWithJena.resetAllValues(true);
		DatasetLoaderWithJena.addEntitiesFromBigNq(new Graph(Graph.GRAPH_CAPACITY), DatasetLoaderWithJena.pathToDataFiles+"example.nq.wrong");
	}

public static void main(String[] args) throws Exception {                  
			JUnitCore.main("input.DatasetLoaderWithJenaTest"); 
	}
}

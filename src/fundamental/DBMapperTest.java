package fundamental;

import static org.junit.Assert.*;

import graph.Edge;
import graph.Graph;
import graph.Vertex;
import input.DatasetLoaderWithJena;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DBMapperTest {
	private static DBMapper vertexMapper=new DBMapper("vertex");
	@BeforeClass
	public static void setUpBeforeClass() {
		vertexMapper.clear();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		vertexMapper.clear();
	}

	@Test
	public void testDBMapper() {
		DBMapper vertexMapper, edgeMapper;
		assertNotNull(vertexMapper=new DBMapper("vertex"));
		assertNotNull(edgeMapper=new DBMapper("edge_type"));
	}

	@Test
	public void testContainsKey() {
		DBMapper vertexMapper;
		vertexMapper=new DBMapper("vertex");
		vertexMapper.clear();
		assertFalse(vertexMapper.containsKey("1"));
		for (int i=1; i<1000; i++){
			vertexMapper.put(String.valueOf(i));
			assertTrue(vertexMapper.containsKey(String.valueOf(i)));
			assertFalse(vertexMapper.containsKey("0"));
			assertFalse(vertexMapper.containsKey(String.valueOf(i+1)));
		}
		vertexMapper.clear();
		assertFalse(vertexMapper.containsKey("1"));
	}

	@Test
	public void testGetKey() {
		DBMapper vertexMapper;
		vertexMapper=new DBMapper("vertex");
		vertexMapper.clear();
		for (int i=1; i<1000; i++){
			assertEquals(vertexMapper.getKey(i), null);
			vertexMapper.put(String.valueOf(i));
			assertEquals(vertexMapper.getKey(i), String.valueOf(i));
		}
		vertexMapper.clear();
	}
	@Test
	public void testGetValue() {
		DBMapper vertexMapper;
		vertexMapper=new DBMapper("vertex");
		vertexMapper.clear();
		for (int i=1; i<1000; i++){
			assertEquals(vertexMapper.getValue(String.valueOf(i)), -1);
			vertexMapper.put(String.valueOf(i));
			assertEquals(vertexMapper.getValue(String.valueOf(i)), i);
		}
		vertexMapper.clear();
	}
	@Test
	public void testSize(){
		DBMapper vertexMapper;
		vertexMapper=new DBMapper("vertex");
		vertexMapper.clear();
		assertEquals(vertexMapper.size(), 0);		
		for (int i=1; i<1000; i++){
			vertexMapper.put(String.valueOf(i));
			assertEquals(vertexMapper.size(), i);
		}
		vertexMapper.clear();
		assertEquals(vertexMapper.size(), 0);		
	}
	@Test
	public void uriTest() throws Exception{
		 Graph g=new Graph(Graph.GRAPH_VERTICES);
		 DatasetLoaderWithJena.resetAllValues(true);
		 DatasetLoaderWithJena.addEntitiesFromBigGzipNq(g, FileNameManager.pathToDataFiles+"data-2.nq.gz");
		 HashSet <String> predicates=new HashSet<String>();	// a list of edges
		 DBMapper map=new DBMapper("vertex");
		 map.clear();
		 ArrayList<String>missedStrings=new ArrayList<String>();
		 for (Vertex v: g.vertices()){
			 for (Edge e: v.getEdges()){
					predicates.add(e.getTypeString());	//add edge type into HashSet
					if (map.getValue(e.getTypeString())==-1){
							map.put(e.getTypeString());
					}
				}			 
		 }
		System.out.println("number of edges ="+g.getEdgeNumber());
		System.out.println("types of edges in predicate="+predicates.size());
		System.out.println("types of edges in mapper="+map.size());
		System.out.println(missedStrings);
		System.out.println(missedStrings.size());
		//assertEquals(map.size(), predicates.size());
		Iterator <String>it=predicates.iterator();
		while (it.hasNext())
		{
			assertTrue(map.containsKey(it.next()));			
		}
		for (int i=1; i<=map.size(); i++){		// id starts from 1, not 0.
			String key=map.getKey(i);
			System.out.println("checking for: ''" +key+"'");
			assertTrue(predicates.contains(key));		
		}
	}
}

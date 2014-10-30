package fundamental;

import static org.junit.Assert.*;

import graph.Edge;
import graph.Graph;
import performance.*;

import input.DatasetLoaderWithJena;

import java.util.HashSet;
import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class CompressedHashBiMapperTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void numeralCharTest() {
		CompressedHashBiMapper testMap=new CompressedHashBiMapper();
		for (int i=0; i<10000; i++){
			assertEquals(testMap.put(String.valueOf(i)), i);
		}
		for (int i=0; i<10000; i++){
			assertTrue(testMap.containsKey(String.valueOf(i)));
		}
		
		assertTrue(testMap.size()==10000);
		for (int i=0; i<10000; i++){
			assertEquals(testMap.getKey(i), String.valueOf(i));		
		}
	}
	@Test
	public void uriTest() throws Exception {
		 Graph g=new Graph();
		 DatasetLoaderWithJena.resetAllValues(true);
		 DatasetLoaderWithJena.addEntitiesFromBigGzipNq(g, DatasetLoaderWithJena.pathToDataFiles+"data-2.nq.gz");
		 HashSet <String> predicates=new HashSet<String>();	// a list of edges
		 CompressedHashBiMapper map=new CompressedHashBiMapper();
		for (Edge e: g.E){
			predicates.add(e.getTypeString());	//add edge type into HashSet
			if (map.getValue(e.getTypeString())==-1){
				map.put(e.getTypeString());	//add edge type into map 
			}
		}
		System.out.println("number of edges ="+g.E.size());
		System.out.println("types of edges in predicate="+predicates.size());
		System.out.println("types of edges in mapper="+map.size());
		assertEquals(map.size(), predicates.size());
		Iterator <String>it=predicates.iterator();
		while (it.hasNext())
		{
			assertTrue(map.containsKey(it.next()));			
		}
		for (int i=0; i<map.size(); i++){
			assertTrue(predicates.contains(map.getKey(i)));		
		}
	}

}

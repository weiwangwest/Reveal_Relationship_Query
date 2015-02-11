package graph;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Iterator;

import junit.framework.Assert;

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
	public void testContains() {
		// new graph g
		Graph g=new Graph(3);
		assertEquals(g.vertexCapacity(), 3);
		assertEquals(g.vertexNumber(), 0);
		Vertex v1=new Vertex(1);
		Vertex v2=new Vertex(2);
		Vertex v3=new Vertex(3);
		assertFalse(g.contains(new Vertex(1)));
		assertFalse(g.containsEdge(2,2,3,4.0));

		//add vertex
		g.addExistingVertex(v1);
		g.addExistingVertex(v2);
		g.addExistingVertex(v3);
		assertTrue(g.contains(new Vertex(1)));
		assertTrue(g.contains(v2));
		assertTrue(g.contains(v3));
	
		// add edge 
		g.addEdge(1, 2, 3, 4.0);
		assertTrue(g.containsEdge(1,2,3,4.0));
		assertFalse(g.containsEdge(2, 1, 3, 4.0));		
		
		// new h from g
		Graph h=new Graph(g);
		assertTrue(h.contains(new Vertex(1)));
		assertTrue(h.contains(v2));
		assertTrue(h.contains(v3));
		assertTrue(h.containsEdge(1,2,3,4.0));
		assertFalse(h.containsEdge(2, 1, 3, 4.0));		
		assertEquals(h.vertexCapacity(),g.vertexCapacity());
		assertEquals(h.vertexNumber(),g.vertexNumber());
		assertEquals(h.vertexKeySet(),g.vertexKeySet());
		assertEquals(h.vertices(),g.vertices());
		assertEquals(h.getEdgeNumber(),g.getEdgeNumber());
		assertEquals(h,g);
		
		//change h by removing vertex 3
		h.removeVertex(3);
		assertTrue(h.contains(new Vertex(1)));
		assertTrue(h.contains(new Vertex(2)));
		assertFalse(h.contains(v3));
		assertTrue(h.containsEdge(1,2,3,4.0));
		assertFalse(h.containsEdge(2, 1, 3, 4.0));		
		assertEquals(h.vertexCapacity(),g.vertexCapacity());
		assertThat (h.vertexNumber(), is(not(equalTo(g.vertexNumber()))));
		assertThat(h.vertexKeySet(), is(not(equalTo(g.vertexKeySet()))));
		assertThat(h.vertices(), is(not(equalTo(g.vertices()))));
		assertEquals(h.getEdgeNumber(),g.getEdgeNumber());
		assertThat(h, is(not(equalTo(g))));		
		
		//change g by removing vertex 3
		g.removeVertex(3);
		assertEquals(g, h);
		assertThat(h.vertices(), is((equalTo(g.vertices()))));
		
		//removing vertex 1 from g and h, removing vertex 1-->2 from g
		assertNotNull(g.getAnyEdgeBetween(1, 2));
		assertNotNull(g.getAnyEdgeBetween(2, 1));
		assertNotNull(g.getDirectedEdgeBetween(1, 2));
		assertNull(g.getDirectedEdgeBetween(2, 1));
		g.removeEdge(1, 2, 3);
		assertFalse(h.equals(g));		
		h.removeVertex(1);
		g.removeVertex(1);
		assertTrue(g.equals(h));
	}


	@Test
	public void testAddAll() {
		Graph g=new Graph(Graph.TREE_CAPACITY);
		g.addExistingVertex(new Vertex(1));
		g.addExistingVertex(new Vertex(2));
		g.addExistingVertex(new Vertex(3));
		g.addExistingVertex(new Vertex(4));
		g.addEdge(1, 1, 1, 0.1);		// selfcircle edge, whose src==dst
		assertEquals(g.getEdgeNumber(), 1);
		assertNotNull(g.getAnyEdgeBetween(1, 1));
		g.addEdge(2, 3, 1,0.2);
		assertEquals(g.getEdgeNumber(), 2);
		g.addEdge(3,4, 1, 0.2);
		assertEquals(g.getEdgeNumber(), 3);
		assertFalse(GraphManager.isATree(g));
		g.removeVertex(1);
		assertEquals(g.getEdgeNumber(), 2);		
		assertTrue(GraphManager.isATree(g));		
		Graph h=new Graph(Graph.TREE_CAPACITY);
		h.addAll(g);
		assertTrue(g.equals(g));
		assertTrue(g.equals(h));
	}

	@Test
	public void testIterator() {		
		//empty graph
		Graph g=new Graph(Graph.TREE_CAPACITY);
		assertEquals(g.vertexCapacity(), Graph.TREE_CAPACITY);
		assertEquals(g.vertexNumber(), 0);
		assertEquals(g.getEdgeNumber(),0);		
		Iterator<Edge>it=g.iterator();
		assertFalse(it.hasNext());
		assertNull(it.next());
		
		//add 2 vertices
		g.addExistingVertex(new Vertex(1));
		g.addExistingVertex(new Vertex(2));
		assertEquals(g.vertexNumber(), 2);
		assertFalse(g.addExistingVertex(new Vertex(2)));
		assertEquals(g.vertexNumber(), 2);
		it=g.iterator();
		assertFalse(it.hasNext());
		assertNull(it.next());		
		assertEquals(g.getEdgeNumber(),0);
		
		//add 1 edge
		g.addEdge(1, 2, 0, 1.0);
		assertEquals(g.getEdgeNumber(),1);
		it=g.iterator();
		assertTrue(it.hasNext());
		Assert.assertNotNull(it.next());			
		assertFalse(it.hasNext());
		assertNull(it.next());
	}
}

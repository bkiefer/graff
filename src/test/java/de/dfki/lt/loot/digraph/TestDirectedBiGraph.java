package de.dfki.lt.loot.digraph;

import static de.dfki.lt.loot.digraph.io.GraphPrinterFactory.printGraph;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

/**
 * {@link TestDirectedBiGraph} is a test class for {@link DirectedGraph}.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id$
 */
public class TestDirectedBiGraph {

  DirectedBiGraph<String> graph;
  VertexPropertyMap<String> names;
  static String[] vertexNames = {"A", "B", "C"};
  int[] vertexIds;
  Edge<String>[] edges;

  @Before
  public void makeGraph() {
    // create empty graph
    graph = new DirectedBiGraph<>();

    // vertices have names
    names = new VertexListPropertyMap<>(graph);

    vertexIds= new int[vertexNames.length];
    // add some vertices
    int i = 0;
    for (String vName : vertexNames) {
      vertexIds[i] = graph.newVertex();
      names.put(vertexIds[i], vName);
      ++i;
    }

    // add some edges
    int[][] testEdges = { { 0, 1 }, { 0, 2 }, { 2, 0 } };
    edges = new Edge[testEdges.length];
    i = 0;
    for (int[] testEdge : testEdges) {
      edges[i] = graph.newEdge(
          (vertexNames[testEdge[0]] + vertexNames[testEdge[1]]).toLowerCase(),
          vertexIds[testEdge[0]], vertexIds[testEdge[1]]);
      ++i;
    }
  }

  @Test
  public void testNewVertex() {
    DirectedBiGraph<String> dg = new DirectedBiGraph<String>();
    assertEquals(0, dg.getNumberOfVertices());
    int v = dg.newVertex();
    assertEquals(1, dg.getNumberOfVertices());
    assertTrue(dg.isVertex(v));
  }

  @Test
  public void testNewEdge() {
    DirectedBiGraph<String> dg = new DirectedBiGraph<>();
    assertEquals(0, dg.getNumberOfVertices());
    int from = dg.newVertex();
    int to = dg.newVertex();
    assertEquals(2, dg.getNumberOfVertices());
    assertTrue(dg.isVertex(from));
    assertTrue(dg.isVertex(to));
    assertFalse(dg.hasInEdges(from));
    assertFalse(dg.hasInEdges(to));
    assertFalse(dg.hasOutEdges(from));
    assertFalse(dg.hasOutEdges(to));
    Edge<String> e = dg.newEdge(from + "->" + to, from, to);
    assertFalse(dg.hasInEdges(from));
    assertTrue(dg.hasInEdges(to));
    assertTrue(dg.hasOutEdges(from));
    assertFalse(dg.hasOutEdges(to));    assertEquals(from, e.getSource());
    assertEquals(to, e.getTarget());
    assertEquals(e, dg.getOutEdges(from).iterator().next());
    assertEquals(e, dg.getInEdges(to).iterator().next());
    assertFalse(dg.getInEdges(from).iterator().hasNext());
    assertFalse(dg.getOutEdges(to).iterator().hasNext());
    assertEquals(from + "->" + to, e.getInfo());
  }

  @Test
  public void testHasEdge() {
    DirectedBiGraph<String> dg = new DirectedBiGraph<>();
    assertEquals(0, dg.getNumberOfVertices());
    int from = dg.newVertex();
    int to = dg.newVertex();
    Edge<String> e = dg.newEdge(from + "->" + to, from, to);
    assertFalse(dg.hasEdge(to, from));
    assertTrue(dg.hasEdge(from, to));
  }


  @Test
  public void testRemoveEdge() {
    DirectedBiGraph<String> dg = new DirectedBiGraph<>();
    int from = dg.newVertex();
    int to = dg.newVertex();
    Edge<String> e = dg.newEdge(from + "->" + to, from, to);
    dg.removeEdge(e);
    assertFalse(dg.getOutEdges(from).iterator().hasNext());
    assertFalse(dg.getInEdges(to).iterator().hasNext());
    assertTrue(dg.isVertex(from));
    assertTrue(dg.isVertex(to));
  }

  @Test
  public void testRemoveVertex() {
    DirectedBiGraph<String> dg = new DirectedBiGraph<>();
    int wech = dg.newVertex();
    int from = dg.newVertex();
    int to = dg.newVertex();
    Edge<String> e1 = dg.newEdge(from + "->" + to, from, to);
    Edge<String> e2 = dg.newEdge(from + "->" + wech, from, wech);
    Edge<String> e3 = dg.newEdge(wech + "->" + to, wech, to);
    dg.removeVertex(wech);
    Iterator<Edge<String>> out = dg.getOutEdges(from).iterator();
    Edge<String> testOut = out.next();
    assertEquals(e1, testOut);
    assertFalse(out.hasNext());
    assertEquals(from, testOut.getSource());
    Iterator<Edge<String>> in = dg.getOutEdges(from).iterator();
    Edge<String> testIn = in.next();
    assertEquals(e1, testIn);
    assertFalse(in.hasNext());
    assertEquals(from, testIn.getSource());
  }


  /**
   * Tests graph creation and manipulation methods.
   */
  @Test
  public void testGraph1() {
    // check result graph
    //System.out.println(graph.toString(names));
    assertEquals(
      String.format("Vertex A%n == out ===== %n"+
          "A-ab->B%n" +
          "A-ac->C%n" +
          " == in  ===== %n" +
          "C-ca->A%n" +
          "Vertex B%n" +
          " == out ===== %n" +
          " == in  ===== %n" +
          "A-ab->B%n" +
          "Vertex C%n" +
          " == out ===== %n" +
          "C-ca->A%n" +
          " == in  ===== %n" +
          "A-ac->C%n"),
      graph.toString(names));
  }

  @Test
  public void testGraph2() {
    // change edge
    graph.changeEndVertex(edges[0], vertexIds[2]);
    // check result graph
    //System.out.println("changed 'ab' edge to vertex 'C':");
    //System.out.println(graph.toString(names));
    assertEquals(
      String.format("Vertex A%n" +
          " == out ===== %n" +
          "A-ab->C%n" +
          "A-ac->C%n" +
          " == in  ===== %n" +
          "C-ca->A%n" +
          "Vertex B%n" +
          " == out ===== %n" +
          " == in  ===== %n" +
          "Vertex C%n" +
          " == out ===== %n" +
          "C-ca->A%n" +
          " == in  ===== %n" +
          "A-ac->C%n" +
          "A-ab->C%n"),
      graph.toString(names));
  }

  @Test
  public void testGraph3() {
    graph.changeEndVertex(edges[0], vertexIds[2]);
    // remove a vertex
    graph.removeVertex(vertexIds[1]);
    // check result graph
    //System.out.println("graph with vertex 'B' removed:");
    //System.out.println(graph.toString(names));
    assertEquals(
      String.format("Vertex A%n" +
          " == out ===== %n" +
          "A-ab->C%n" +
          "A-ac->C%n" +
          " == in  ===== %n" +
          "C-ca->A%n" +
          "Vertex C%n" +
          " == out ===== %n" +
          "C-ca->A%n" +
          " == in  ===== %n" +
          "A-ac->C%n" +
          "A-ab->C%n"),
      graph.toString(names));
  }


  @Test
  public void testCompact0() throws IOException {
    // read in graph
    DirectedBiGraph<String> graph = new DirectedBiGraph<String>();
    Utils.readGraph(graph, new StringReader(Utils.exampleBfsGraph));
    printGraph(graph, "XXXinit");
    graph.removeVertex(3);
    graph.removeVertex(5);
    printGraph(graph, "XXXplain");
    int v = graph.getNumberOfActiveVertices();
    int e = TestDirectedGraph.countEdges(graph);
    graph.compact();
    printGraph(graph, "XXXcompact");
    assertEquals(v, graph.getNumberOfActiveVertices());
    assertEquals(e, TestDirectedGraph.countEdges(graph));
    assertEquals(graph.getNumberOfActiveVertices(),
        graph.getNumberOfVertices());
  }


  @Test
  public void testCompact1() throws IOException {
    // read in graph
    DirectedBiGraph<String> graph = new DirectedBiGraph<String>();
    Utils.readGraph(graph, new StringReader(Utils.exampleBfsGraph));
    //graph.printGraph("XXXinit");
    graph.removeVertex(0);
    graph.removeVertex(7);
    //graph.printGraph("XXXplain");
    int v = graph.getNumberOfActiveVertices();
    int e = TestDirectedGraph.countEdges(graph);
    graph.compact();
    //graph.printGraph("XXXcompact");
    assertEquals(v, graph.getNumberOfActiveVertices());
    assertEquals(e, TestDirectedGraph.countEdges(graph));
    assertEquals(graph.getNumberOfActiveVertices(),
        graph.getNumberOfVertices());
  }

}

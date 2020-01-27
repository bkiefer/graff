package de.dfki.lt.loot.digraph;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;


/**
 * {@link TestDirectedGraph} is a test class for {@link DiGraph}.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id$
 */
public class TestDirectedGraph {

  public static <T> int countEdges(Graph<T> graph) {
    final int[] result = { 0 };
    graph.dfs(new GraphVisitorAdapter<T>() {
      @Override
      @SuppressWarnings("unused")
      public void discoverVertex(int v, Graph<T> g) {
        for (Edge<T> e : g.getOutEdges(v)) ++result[0];
      }
    });
    return result[0];
  }


  DiGraph<String> graph;
  VertexPropertyMap<String> names;
  static String[] vertexNames = {"A", "B", "C"};
  int[] vertexIds;
  Edge<String>[] edges;

  @Before
  public void makeGraph() {
    // create empty graph
    graph = new DiGraph<>();

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
    DiGraph dg = new DiGraph();
    assertEquals(0, dg.getNumberOfVertices());
    int v = dg.newVertex();
    assertEquals(1, dg.getNumberOfVertices());
    assertTrue(dg.isVertex(v));
  }

  @Test
  public void testNewEdge() {
    DiGraph<String> dg = new DiGraph<>();
    assertEquals(0, dg.getNumberOfVertices());
    int from = dg.newVertex();
    int to = dg.newVertex();
    assertEquals(2, dg.getNumberOfVertices());
    assertTrue(dg.isVertex(from));
    assertTrue(dg.isVertex(to));
    Edge<String> e = dg.newEdge(from + "->" + to, from, to);
    assertEquals(from, e.getSource());
    assertEquals(to, e.getTarget());
    assertEquals(e, dg.getOutEdges(from).iterator().next());
    assertFalse(dg.getOutEdges(to).iterator().hasNext());
    assertEquals(from + "->" + to, e.getInfo());
  }

  @Test
  public void testRemoveEdge() {
    DiGraph<String> dg = new DiGraph<>();
    int from = dg.newVertex();
    int to = dg.newVertex();
    Edge<String> e = dg.newEdge(from + "->" + to, from, to);
    dg.removeEdge(e);
    assertFalse(dg.getOutEdges(from).iterator().hasNext());
    assertTrue(dg.isVertex(from));
    assertTrue(dg.isVertex(to));
  }

  @Test
  public void testRemoveVertex() {
    DiGraph<String> dg = new DiGraph<>();
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
          "Vertex B%n" +
          " == out ===== %n" +
          "Vertex C%n" +
          " == out ===== %n" +
          "C-ca->A%n"),
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
          "Vertex B%n" +
          " == out ===== %n" +
          "Vertex C%n" +
          " == out ===== %n" +
          "C-ca->A%n"),
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
          "Vertex C%n" +
          " == out ===== %n" +
          "C-ca->A%n"),
      graph.toString(names));
  }

}

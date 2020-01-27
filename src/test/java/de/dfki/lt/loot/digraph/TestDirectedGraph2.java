package de.dfki.lt.loot.digraph;

import static de.dfki.lt.loot.digraph.io.SimpleGraphReader.*;
import static de.dfki.lt.loot.digraph.io.GraphPrinterFactory.printGraph;
import static de.dfki.lt.loot.digraph.Utils.*;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

/**
 * <code>DirectedGraphTest</code> is a test class for {@link DiGraph}.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: DirectedGraphTest.java,v 1.1 2005/11/15 10:46:27 steffen Exp $
 */
public class TestDirectedGraph2 {
  // default visibility for examples to be able to use them in other modules
  // in this package



  @Test
  public void testCompact0() throws IOException {
    // read in graph
    DiGraph<String> graph = new DiGraph<String>();
    readGraph(new StringReader( exampleBfsGraph), graph);
    if (print) printGraph(graph, "XXXinit");
    graph.removeVertex(3);
    graph.removeVertex(5);
    if (print) printGraph(graph, "XXXplain");
    int v = graph.getNumberOfActiveVertices();
    int e = TestDirectedGraph.countEdges(graph);
    graph.compact();
    if (print) printGraph(graph, "XXXcompact");
    assertEquals(v, graph.getNumberOfActiveVertices());
    assertEquals(e, TestDirectedGraph.countEdges(graph));
    assertEquals(graph.getNumberOfActiveVertices(),
        graph.getNumberOfVertices());
  }


  @Test
  public void testCompact1() throws IOException {
    // read in graph
    DiGraph<String> graph = new DiGraph<String>();
    readGraph(new StringReader(exampleBfsGraph), graph);
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


  @Test
  public void testCompactStable0() throws IOException {
    // read in graph
    DiGraph<String> graph = new DiGraph<String>();
    readGraph(new StringReader(exampleBfsGraph), graph);
    if (print) printGraph(graph, "XXXinit");
    graph.removeVertex(3);
    graph.removeVertex(5);
    if (print) printGraph(graph, "XXXplain");
    int v = graph.getNumberOfActiveVertices();
    int e = TestDirectedGraph.countEdges(graph);
    graph.compactStable();
    if (print) printGraph(graph, "XXXcompact");
    assertEquals(v, graph.getNumberOfActiveVertices());
    assertEquals(e, TestDirectedGraph.countEdges(graph));
    assertEquals(graph.getNumberOfActiveVertices(),
        graph.getNumberOfVertices());
  }


  @Test
  public void testCompactStable1() throws IOException {
    // read in graph
    DiGraph<String> graph = new DiGraph<String>();
    readGraph(new StringReader(exampleBfsGraph), graph);
    //graph.printGraph("XXXinit");
    graph.removeVertex(0);
    graph.removeVertex(7);
    //graph.printGraph("XXXplain");
    int v = graph.getNumberOfActiveVertices();
    int e = TestDirectedGraph.countEdges(graph);
    graph.compactStable();
    //graph.printGraph("XXXcompact");
    assertEquals(v, graph.getNumberOfActiveVertices());
    assertEquals(e, TestDirectedGraph.countEdges(graph));
    assertEquals(graph.getNumberOfActiveVertices(),
        graph.getNumberOfVertices());
  }
}
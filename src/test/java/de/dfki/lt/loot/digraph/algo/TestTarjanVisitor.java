package de.dfki.lt.loot.digraph.algo;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.BeforeClass;
import org.junit.Test;

import de.dfki.lt.loot.digraph.DiGraph;
import de.dfki.lt.loot.digraph.TestDirectedGraph;
import de.dfki.lt.loot.digraph.VertexPropertyMap;

/**
 * {@link TestTarjanVisitor} is a test class for {@link TarjanVisitor}.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id$
 */
public class TestTarjanVisitor {


  /**
   * Contains the graph on which the tests run.
   */
  private static DiGraph<String> graph;


  /**
   * Initializes the graph.
   *
   * @throws IOException
   *           if there is an error when reading the graph
   */
  @BeforeClass
  public static void oneTimeSetUp()
      throws IOException {

    String graphString =
        "s --> w z\n"
          + "z --> w y\n"
          + "v --> w s\n"
          + "w --> x q\n"
          + "t --> u v\n"
          + "u --> t v\n"
          + "x --> z\n"
          + "q --> x\n"
          + "y --> x\n";

    graph = new DiGraph<String>();
    TestDirectedGraph.readGraph(graph, new StringReader(graphString));
  }


  /**
   * Tests the {@link TarjanVisitor}.
   */
  @Test
  public void test() {

    // use DFS with Tarjan SCC visitor
    TarjanVisitor<String> visitor = new TarjanVisitor<String>();
    graph.dfs(visitor);

    // print resulting SCCs
    @SuppressWarnings("unchecked")
    VertexPropertyMap<String> names =
        (VertexPropertyMap<String>)graph.getPropertyMap("names");
    int counter = 0;
    /*
    for (List<Integer> comp : visitor.getSCCs()) {
      System.out.format("Component %d: ", ++counter);
      for (Integer v : comp) {
        System.out.format("%s ", names.get(v));
      }
      System.out.println();
    }
    */
    // check results
    assertEquals(4, visitor.getSCCs().size());
    assertEquals(
      new HashSet<Integer>(Arrays.asList(new Integer[] {6, 3, 2, 5, 1})),
      new HashSet<Integer>(visitor.getSCCs().get(0)));
    assertEquals(
      Arrays.asList(new Integer[] {0}), visitor.getSCCs().get(1));
    assertEquals(
      Arrays.asList(new Integer[] {4}), visitor.getSCCs().get(2));
    assertEquals(
      new HashSet<Integer>(Arrays.asList(new Integer[] {8, 7})),
      new HashSet<Integer>(visitor.getSCCs().get(3)));
  }
}

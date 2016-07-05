package de.dfki.lt.loot.digraph;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import org.junit.BeforeClass;
import org.junit.Test;


/**
 * {@link TestTopoOrderVisitor} is a test class for {@link TopoOrderVisitor}.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id$
 */
public class TestTopoOrderVisitor {

  /**
   * Contains the graph on which the tests run.
   */
  private static DirectedGraph<String> graph;


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
        "shirt --> tie jacket belt\n"
          + "tie --> jacket\n"
          + "belt --> jacket\n"
          + "watch --> \n"
          + "undershorts --> pants shoes\n"
          + "pants --> belt shoes\n"
          + "socks --> shoes\n";

    graph = new DirectedGraph<String>();
    TestDirectedGraph.readGraph(graph, new StringReader(graphString));
  }


  /**
   * Tests the {@link TopoOrderVisitor}.
   */
  @Test
  public void test() {

    //System.out.println(graph);
    // calculate topological order
    TopoOrderVisitor<String> topoVisitor = new TopoOrderVisitor<>();
    graph.dfs(topoVisitor);
    @SuppressWarnings("unchecked")
    VertexPropertyMap<String> names =
        (VertexPropertyMap<String>)graph.getPropertyMap("names");
    Iterator<Integer> topoIt = topoVisitor.getSortedVertices().iterator();
    StringBuilder result = new StringBuilder();
    while (topoIt.hasNext()) {
      int v = topoIt.next();
      //System.out.println(names.get(v));
      result.append(String.format("%s ", names.get(v)));
    }
    assertEquals(
      "socks undershorts pants shoes watch shirt belt tie jacket",
      result.toString().trim());
  }
}

package de.dfki.lt.loot.digraph.algo;

import static de.dfki.lt.loot.digraph.Utils.*;
import static de.dfki.lt.loot.digraph.io.SimpleGraphReader.*;
import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import de.dfki.lt.loot.digraph.DiGraph;
import de.dfki.lt.loot.digraph.VertexPropertyMap;

/**
 * {@link TestTarjanVisitor} is a test class for {@link TarjanVisitor}.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id$
 */
public class TestTarjanVisitor {

  /**
   * Tests the {@link TarjanVisitor}.
   * @throws IOException
   */
  @Test
  public void test() throws IOException {
    DiGraph<String> graph= new DiGraph<String>();
    readGraph(new StringReader(exampleGraphCyclic), graph);

    // use DFS with Tarjan SCC visitor
    TarjanVisitor<String> visitor = new TarjanVisitor<String>();
    graph.dfs(visitor);

    // print resulting SCCs
    /*
    @SuppressWarnings("unchecked")
    VertexPropertyMap<String> names =
        (VertexPropertyMap<String>)graph.getPropertyMap("names");
    int counter = 0;
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

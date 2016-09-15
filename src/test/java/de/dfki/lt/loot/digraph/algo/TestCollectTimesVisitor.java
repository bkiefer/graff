package de.dfki.lt.loot.digraph.algo;

import de.dfki.lt.loot.digraph.DiGraph;
import de.dfki.lt.loot.digraph.TestDirectedGraph2;
import de.dfki.lt.loot.digraph.VertexPropertyMap;
import de.dfki.lt.loot.digraph.algo.CollectTimesVisitor;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.assertEquals;


/**
 * {@link TestCollectTimesVisitor} is a test class for
 * {@link CollectTimesVisitor}.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id$
 */
public class TestCollectTimesVisitor {

  @Test
  public void testDFS() throws IOException {
    // read in graph
    DiGraph<String> graph = new DiGraph<String>();
    TestDirectedGraph2.readGraph(graph,
        new StringReader(TestDirectedGraph2.exampleGraph));
    //System.out.println(graph.toString(graph.getPropertyMap("names")));

    // use DFS
    CollectTimesVisitor<String> visitor = new CollectTimesVisitor<String>();
    graph.dfs(visitor);
    @SuppressWarnings("unchecked")
    VertexPropertyMap<String> names =
        (VertexPropertyMap<String>) graph.getPropertyMap("names");
    int[] disc = {  1, 2, 3, 6, 9, 11, 12, 13, 17 };
    int[] finish = { 8, 5, 4, 7, 10, 16, 15, 14, 18 };
    for (int i = 0; i < disc.length; ++i) {
      assertEquals("Discovery " + i, disc[i],
          visitor.getDiscoveryMap().get(i).intValue());
      assertEquals("Finish " + i, finish[i],
          visitor.getFinishMap().get(i).intValue());
    }
    /*
    for (DirectedGraph<String>.VertexIterator it = graph.vertices();
        it.hasNext();) {
      int v = it.next();
      System.out.println(//graph.toString(v, names)
          v + "discovered: "
          + visitor.getDiscoveryMap().get(v)
          + " finished: "
          + visitor.getFinishMap().get(v));
      System.out.println();
    }
    */
  }

  @Test
  public void testBFS() throws IOException {
    // read in graph
    DiGraph<String> graph = new DiGraph<String>();
    TestDirectedGraph2.readGraph(graph,
        new StringReader(TestDirectedGraph2.exampleBfsGraph));

    // use BFS
    BfsTimesVisitor<String> bfsVisitor = new BfsTimesVisitor<String>();
    graph.bfs(bfsVisitor);

    @SuppressWarnings("unchecked")
    VertexPropertyMap<String> names =
       (VertexPropertyMap<String>) graph.getPropertyMap("names");

    int[] dist = { 0, 1, 1, 2, 2, 2, 3, 3 };
    for (int i = 0; i < dist.length; ++i) {
      assertEquals("Distance " + i, dist[i],
          bfsVisitor.getDiscoveryMap().get(i).intValue());
    }
    /*
    for (DirectedGraph<String>.VertexIterator it = graph.vertices();
        it.hasNext();) {
      int v = it.next();
      System.out.println(names.get(v) + " distance: "
                         + bfsVisitor.getDiscoveryMap().get(v));
    }
    */
  }

}

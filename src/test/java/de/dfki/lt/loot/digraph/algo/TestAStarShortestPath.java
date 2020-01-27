package de.dfki.lt.loot.digraph.algo;

import static de.dfki.lt.loot.digraph.Utils.*;
import static de.dfki.lt.loot.digraph.io.GraphPrinterFactory.*;
import static de.dfki.lt.loot.digraph.io.SimpleGraphReader.readNodePositionGraph;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.function.Function;

import org.junit.Test;

import de.dfki.lt.loot.digraph.*;
import de.dfki.lt.loot.digraph.io.SimpleGraphReader.Vector;
import de.dfki.lt.loot.digraph.weighted.DoubleMonoid;

/**
 * <code>DirectedGraphTest</code> is a test class for
 * {@link DiGraph}.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: DirectedGraphTest.java,v 1.1 2005/11/15 10:46:27 steffen Exp $
 */
public class TestAStarShortestPath {

  private class RestEstimate implements Function<Integer, Double> {
    private Vector goalPos;
    private VertexPropertyMap<Vector> nodePos;

    @SuppressWarnings("unchecked")
    public RestEstimate(Graph<?> g, int endVertex) {
      nodePos = (VertexPropertyMap<Vector>)g.getPropertyMap("nodePos");
      goalPos = nodePos.get(endVertex);
    }

    @Override
    public Double apply(Integer t) {
      return goalPos.euclidDist(nodePos.get(t));
    }
  }

  private class EuclidWeight implements Function<Edge<Double>, Double> {
    private Graph<Double> graph;
    private VertexPropertyMap<Vector> nodePos;

    public EuclidWeight(Graph<Double> g) {
      graph = g;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Double apply(Edge<Double> edge) {
      // lazy caching
      if (edge.getInfo() == null) {
        if (nodePos == null)
          nodePos = (VertexPropertyMap<Vector>)graph.getPropertyMap("nodePos");
        Vector spos = nodePos.get(edge.getSource());
        Vector epos = nodePos.get(edge.getTarget());
        edge.setInfo(spos.euclidDist(epos));
      }
      return edge.getInfo();
    }

  }

  private void testAStar(Graph<Double> graph, boolean zeroEst,
      String from, String to, String[] res)
      throws IOException {

    // read in graph
    readNodePositionGraph(new StringReader(exampleGraphNodePositions), graph);

    @SuppressWarnings("unchecked")
    VertexPropertyMap<String> names =
      (VertexPropertyMap<String>) graph.getPropertyMap("names");
    int start = names.findVertices(from, (a,b) -> a.equals(b)).get(0);
    int end = names.findVertices(to, (a,b) -> a.equals(b)).get(0);
    //System.out.println(graph);

    // use DFS with special visitor
    AStarShortestPath<Double, Double> algorithm =
      new AStarShortestPath<>();

    List<Edge<Double>> result =
      algorithm.shortestPath(graph, start, end, new DoubleMonoid(),
          new EuclidWeight(graph),
          zeroEst ? (vertex) -> 0.0 : new RestEstimate(graph, end));

    if (print) printGraph(graph, "aStar"
        + ((graph instanceof UndirectedGraph) ? "Undir" : "Dir")
        + (zeroEst ? "Zero" : "Euclid")
        + "Graph");
    //System.out.print(names.get(0));
    int curr = start;
    int i = 0;
    for (Edge<Double> edge : result) {
      /*System.out.print("--" +
                       edge.getInfo() + "-->" +
                       names.get(edge.getTarget()));*/
      assertEquals(res[i++], names.get(curr = edge.getTargetForSource(curr)));
    }
    //System.out.println();
  }

  /**
   * Directed Graph, Euclidean Distance estimate.
   */
  @Test
  public void testAStarDirEuclid()
      throws IOException {
    Graph<Double> g = new DiGraph<Double>();
    String[] res = { "z", "y", "x", "u" };
    testAStar(g, false, "s", "u", res);
  }

  /**
   * With a zero estimate, this must function like Dijkstra (it's the same!)
   */
  @Test
  public void testAStarDirZero()
      throws IOException {
    Graph<Double> g = new DiGraph<Double>();
    String[] res = { "z", "y", "x", "u" };
    testAStar(g, true, "s", "u", res);
  }

  /**
   * Undirected Graph, Euclidean Distance estimate: this produces a non-optimal
   * solution since the estimate is not admissible! The euclidean distance
   * to the endpoint may be misleading since not all directions are possible!
   */
  @Test
  public void testAStarUndirEuclid()
      throws IOException {
    Graph<Double> g = new UndirectedGraph<Double>();
    String[] res = { "v", "u" };
    testAStar(g, false, "s", "u", res);
  }

  /** Undirected Graph
   * With a zero estimate, this must function like Dijkstra (it's the same!)
   */
  @Test
  public void testAStarUndirZero()
      throws IOException {
    Graph<Double> g = new UndirectedGraph<Double>();
    String[] res = { "v", "u" };
    testAStar(g, true, "s", "u", res);
  }


}
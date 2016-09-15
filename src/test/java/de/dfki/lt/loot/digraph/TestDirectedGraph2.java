package de.dfki.lt.loot.digraph;

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
 * <code>DirectedGraphTest</code> is a test class for {@link DiGraph}.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: DirectedGraphTest.java,v 1.1 2005/11/15 10:46:27 steffen Exp $
 */
public class TestDirectedGraph2 {
  // default visibility for examples to be able to use them in other modules
  // in this package

  /** Use this example to test your traversal methods, it's the last example
   *  on the first set of slides.
   */
  public static final String exampleGraph =
    "shirt --> tie jacket belt\n" +
    "tie --> jacket\n" +
    "belt --> jacket\n" +
    "watch --> \n" +
    "undershorts --> pants shoes\n" +
    "pants --> belt shoes\n" +
    "socks --> shoes\n";

  /** Use this example to test your traversal methods, it's the last example
   *  on the first set of slides.
   */
  public static final String exampleBfsGraph =
    "s --> r w\n" +
    "r --> s v\n" +
    "v --> r\n" +
    "w --> s t x\n" +
    "t --> w u x\n" +
    "u --> t y\n" +
    "x --> w t y\n" +
    "y --> x u\n";


  /**
   * <code>EqualsPredicate</code> is an inner class that defines a functional
   * object used to check if two instances of the argument type are equal.
   */
  static class EqualsPredicate<ArgumentType>
    implements BinaryPredicate<ArgumentType> {

    /**
     * This compares the given objects of type ArgumentTyp and returns
     * <code>true</code> if they are compatible.
     *
     * @param arg1 a <code>ArgumentType</code> with the first argument
     * @param arg2 a <code>ArgumentType</code> with the second argument
     * @return a <code>boolean</code> indicating if the arguments are compatible
     */
    public boolean compare(ArgumentType arg1, ArgumentType arg2) {

      return arg1.equals(arg2);
    }
  }

  /**
   * <code>ComparableComparator</code> is an inner class that defines a functional
   * object used to compare two instances of the argument type using their
   * internal comparison method
   */
  static class ComparableComparator<ArgumentType extends Comparable<ArgumentType>>
    implements Comparator<ArgumentType> {

    /**
     * This compares the given objects of type ArgumentTyp and returns
     * <code>true</code> if they are compatible.
     *
     * @param arg1 a <code>ArgumentType</code> with the first argument
     * @param arg2 a <code>ArgumentType</code> with the second argument
     * @return a <code>boolean</code> indicating if the arguments are compatible
     */
    public int compare(ArgumentType arg1, ArgumentType arg2) {
      return arg1.compareTo(arg2);
    }
  }




  /** create a graph from a readable specification.
   *  This method has default visibility to be able to use it in other test
   *  modules.
   */
  public static void readGraph(DiGraph<String> result, Reader in)
      throws IOException {
    BufferedReader bin = new BufferedReader(in);

    EqualsPredicate<String> eqPred = new EqualsPredicate<String>();

    VertexPropertyMap<String> names = new VertexListPropertyMap<String>(result);
    result.register("names", names);

    String nextLine = null;
    while((nextLine = bin.readLine()) != null) {
      String[] fromTo = nextLine.split("\\s*-->\\s*");
      if (fromTo.length < 1)
        continue;

      List<Integer> nodes = names.findVertices(fromTo[0], eqPred);
      int from = -1;
      if (nodes.isEmpty()) {
        from = result.newVertex();
        names.put(from, fromTo[0]);
      } else {
        from = nodes.get(0);
      }

      if (fromTo.length == 2) {
        String[] token = fromTo[1].split("\\s+");

        for(String name : token) {
          nodes = names.findVertices(name, eqPred);
          int to = -1;
          if (nodes.isEmpty()) {
            to = result.newVertex();
            names.put(to, name);
          } else {
            to = nodes.get(0);
          }
          result.newEdge(fromTo[0] + " --> " + name, from, to);
        }
      }
    }
  }


  @Test
  public void testCompact0() throws IOException {
    // read in graph
    DiGraph<String> graph = new DiGraph<String>();
    readGraph(graph, new StringReader( exampleBfsGraph));
    graph.printGraph("XXXinit");
    graph.removeVertex(3);
    graph.removeVertex(5);
    graph.printGraph("XXXplain");
    int v = graph.getNumberOfActiveVertices();
    int e = TestDirectedGraph.countEdges(graph);
    graph.compact();
    graph.printGraph("XXXcompact");
    assertEquals(v, graph.getNumberOfActiveVertices());
    assertEquals(e, TestDirectedGraph.countEdges(graph));
    assertEquals(graph.getNumberOfActiveVertices(),
        graph.getNumberOfVertices());
  }


  @Test
  public void testCompact1() throws IOException {
    // read in graph
    DiGraph<String> graph = new DiGraph<String>();
    readGraph(graph, new StringReader(exampleBfsGraph));
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
    readGraph(graph, new StringReader( exampleBfsGraph));
    graph.printGraph("XXXinit");
    graph.removeVertex(3);
    graph.removeVertex(5);
    graph.printGraph("XXXplain");
    int v = graph.getNumberOfActiveVertices();
    int e = TestDirectedGraph.countEdges(graph);
    graph.compactStable();
    graph.printGraph("XXXcompact");
    assertEquals(v, graph.getNumberOfActiveVertices());
    assertEquals(e, TestDirectedGraph.countEdges(graph));
    assertEquals(graph.getNumberOfActiveVertices(),
        graph.getNumberOfVertices());
  }


  @Test
  public void testCompactStable1() throws IOException {
    // read in graph
    DiGraph<String> graph = new DiGraph<String>();
    readGraph(graph, new StringReader(exampleBfsGraph));
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
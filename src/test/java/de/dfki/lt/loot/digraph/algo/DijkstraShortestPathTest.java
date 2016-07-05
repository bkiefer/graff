package de.dfki.lt.loot.digraph.algo;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import de.dfki.lt.loot.digraph.BinaryPredicate;
import de.dfki.lt.loot.digraph.DirectedGraph;
import de.dfki.lt.loot.digraph.Edge;
import de.dfki.lt.loot.digraph.VertexListPropertyMap;
import de.dfki.lt.loot.digraph.VertexPropertyMap;
import de.dfki.lt.loot.digraph.weighted.EdgeWeight;
import de.dfki.lt.loot.digraph.weighted.IntMonoid;
import de.dfki.lt.loot.digraph.weighted.WeightInfo;


class NameWeight implements WeightInfo<Integer> {
  String _name;
  int _weight;

  NameWeight(String name, int weight) {
    _name = name;
    _weight = weight;
  }

  public Integer getWeight() { return _weight; }

  @Override
  public String toString() {
    return Integer.toString(_weight); //_name;
  }
}

/**
 * <code>DirectedGraphTest</code> is a test class for
 * {@link DirectedGraph}.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: DirectedGraphTest.java,v 1.1 2005/11/15 10:46:27 steffen Exp $
 */
public class DijkstraShortestPathTest {

  /** Use this example to test your traversal methods, it's the last example
   *  on the first set of slides.
   */
  private static final String exampleGraph =
    "s --> w(2) z(4)\n" +
    "z --> w(5) y(9)\n" +
    "v --> w(1) s(6)\n" +
    "w --> x(6) q(3)\n" +
    "t --> u(1) v(9) s(8)\n" +
    "u --> t(5) v(7)\n" +
    "x --> z(8) u(7)\n" +
    "q --> x(2)\n" +
    "y --> x(5)\n";

  /**
   * This is the main method. It requires no argument
   *
   * @param args an array of <code>String</code>s with the arguments;
   * not used here
   */
  @Test
  public void testDijkstra() throws IOException {

    DirectedGraph<NameWeight> graph;

    // read in graph
    graph = readGraph(new StringReader(exampleGraph));

    //System.out.println(graph);
    //graph.printGraph("testGraph");

    // use DFS with special visitor
    DijkstraShortestPath<NameWeight, Integer> algorithm =
      new DijkstraShortestPath<NameWeight, Integer>();

    List<Edge<NameWeight>> result =
      algorithm.shortestPath(graph, 0, graph.getNumberOfVertices() - 1,
          new IntMonoid(), new EdgeWeight<NameWeight, Integer>());

    @SuppressWarnings("unchecked")
    VertexPropertyMap<String> names =
      (VertexPropertyMap<String>) graph.getPropertyMap("names");

    String[] res = { "w", "q", "x", "u" };
    //System.out.print(names.get(0));
    int i = 0;
    for (Edge<NameWeight> edge : result) {
      /*System.out.print("--" +
                       edge.getInfo() + "-->" +
                       names.get(edge.getTarget()));*/
      assertEquals(names.get(edge.getTarget()), res[i++]);
    }
    //System.out.println();
  }


  /**
   * <code>EqualPredicate</code> is an inner class that defines a functional
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
   * create a graph from a readable specification (including edge weights)
   */
  static DirectedGraph<NameWeight> readGraph(Reader in)
    throws IOException {
    BufferedReader bin = new BufferedReader(in);

    DirectedGraph<NameWeight> result = new DirectedGraph<NameWeight>();
    EqualsPredicate<String> eqPred = new EqualsPredicate<String>();

    VertexPropertyMap<String> names = new VertexListPropertyMap<String>(result);
    result.register("names", names);

    Pattern nw = Pattern.compile("([^(]*)\\(([0-9]*)\\)");

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

        for(String nameWeight : token) {
          Matcher nameAndWeight = nw.matcher(nameWeight);
          if (! nameAndWeight.matches()) continue;

          String name = nameAndWeight.group(1);
          int weight = Integer.parseInt(nameAndWeight.group(2));
          nodes = names.findVertices(name, eqPred);
          int to = -1;
          if (nodes.isEmpty()) {
            to = result.newVertex();
            names.put(to, name);
          } else {
            to = nodes.get(0);
          }

          result.newEdge(
            new NameWeight(fromTo[0] + "-" + weight + " -> " + name, weight)
            , from, to);
        }
      }
    }
    return result;
  }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.lt.loot.digraph;

import static de.dfki.lt.loot.util.Predicates.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;


/** Utilities for tests, including test graphs in string form.
 *
 * @author kiefer
 */
public class Utils {

  public static boolean print = false;

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

  /** An example graph with cycles in it, e.g., for tests of scc reduction.
   */
  public static final String exampleGraphCyclic =
    "s --> w z\n" +
    "z --> w y\n" +
    "v --> w s\n" +
    "w --> x q\n" +
    "t --> u v\n" +
    "u --> t v\n" +
    "x --> z\n" +
    "q --> x\n" +
    "y --> x\n";

  /** An acyclic test graph.
   */
  public static final String exampleGraphAcyclic =
    "s --> w z\n" +
    "z --> y\n" +
    "v --> w s\n" +
    "w --> x q\n" +
    "t --> u v\n" +
    "u --> v\n" +
    "x --> z\n" +
    "q --> x\n";

  /** Use this example to test shortest path methods, it's the last example
   *  on the first set of slides.
   */
  public static final String exampleGraphWeightedEdges =
    "s --> w (2) z (4)\n" +
    "z --> w(5) y(9)\n" +
    "v --> w(1) s(6)\n" +
    "w --> x(6) q(3)\n" +
    "t --> u(1) v(9) s(8)\n" +
    "u --> t(5) v(7)\n" +
    "x --> z(8) u(7)\n" +
    "q --> x(2)\n" +
    "y --> x(5)\n";

  /** Use this example to test shortest path methods, it's the last example
   *  on the first set of slides.
   */
  public static final String exampleUndirGraphWeightedEdges =
    "s --> w(2) z(4)\n" +
    "z --> w(5) y(9)\n" +
    "v --> w(1) s(6)\n" +
    "w --> x(6) q(3)\n" +
    "t --> u(1) v(9) s(1)\n" +
    "u --> v(7)\n" +
    "x --> z(8) u(7)\n" +
    "q --> x(2)\n" +
    "y --> x(5)\n";

  /** Use this example to test A* search methods, it's the last example
   *  on the first set of slides.
   */
  public static final String exampleGraphNodePositions =
    "s (1.3,6) --> w z\n" +
    "z (1,4) --> w y\n" +
    "v (4,4) --> w s\n" +
    "w (3,2) --> x q\n" +
    "t (5,6) --> u v s\n" +
    "u (6,4) --> v\n" +
    "x (0,0) --> z u\n" +
    "q (4.5,0) --> x\n" +
    "y (0,2) --> x\n";

  public DiGraph<String> graphCyclic;
  public DiGraph<String> graphAcyclic;


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

  /** create a new graph from a readable specification.
   *  This method has default visibility to be able to use it in other test
   *  modules.
   */
  public static DiGraph<String> readGraph(Reader in) throws IOException {
    DiGraph<String> result = new DiGraph<String>();
    readGraph(result, in);
    return result;
  }

  /** create a new graph from a readable specification.
   *  This method has default visibility to be able to use it in other test
   *  modules.
   */
  public static DiGraph<String> readGraph(String in) throws IOException {
      DiGraph<String> result = new DiGraph<String>();
      readGraph(result, new StringReader(in));
      return result;
    }
}

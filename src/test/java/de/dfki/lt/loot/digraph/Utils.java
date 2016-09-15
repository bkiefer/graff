/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.lt.loot.digraph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Comparator;
import java.util.List;


/**
 *
 * @author kiefer
 */
public class Utils {

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

  public DiGraph<String> graphCyclic;
  public DiGraph<String> graphAcyclic;

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

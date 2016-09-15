package de.dfki.lt.loot.digraph.algo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.dfki.lt.loot.digraph.CyclicGraphException;
import de.dfki.lt.loot.digraph.DiGraph;
import de.dfki.lt.loot.digraph.TestDirectedGraph2;
import de.dfki.lt.loot.digraph.Edge;
import de.dfki.lt.loot.digraph.VertexListPropertyMap;
import de.dfki.lt.loot.digraph.VertexPropertyMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransitiveClosureTest {
  /** Use this example to test your traversal methods, it's the last example
   *  on the first set of slides.
   */
  static final String exampleGraphCyclic =
    "s --> w z\n" +
    "z --> w y\n" +
    "v --> w s\n" +
    "w --> x q\n" +
    "t --> u v\n" +
    "u --> t v\n" +
    "x --> z\n" +
    "q --> x\n" +
    "y --> x\n";

  /** Use this example to test your traversal methods, it's the last example
   *  on the first set of slides.
   */
  static final String exampleGraphACyclic =
    "s --> w z\n" +
    "z --> y\n" +
    "v --> w s\n" +
    "w --> x q\n" +
    "t --> u v\n" +
    "u --> v\n" +
    "x --> z\n" +
    "q --> x\n";

  DiGraph<String> graphCyclic;
  DiGraph<String> graphAcyclic;

  @Before
  public void setUp() {
    // read in graph
    try {
      graphCyclic = new DiGraph<String>();
      TestDirectedGraph2.readGraph(graphCyclic,
          new StringReader(exampleGraphCyclic));
      graphAcyclic =  new DiGraph<String>();
      TestDirectedGraph2.readGraph(graphAcyclic,
          new StringReader(exampleGraphACyclic));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test(expected=CyclicGraphException.class)
  public void testCycleTest0() throws CyclicGraphException {
    graphCyclic.topoSort(0);
  }

  @Test
  public void testCycleTest1() throws CyclicGraphException {
    assertEquals(graphAcyclic.getNumberOfVertices(),
        graphAcyclic.topoSort(7).size());
  }

  @Test
  public void testReduction() {
    //graphCyclic.printGraph("input");
    DiGraph<String> red = TransitiveClosure.acyclicReduction(graphCyclic);
    VertexPropertyMap<String> redNames = new VertexListPropertyMap<String>(red);
    @SuppressWarnings("unchecked")
    VertexPropertyMap<List<Integer>> componentsMap =
        (VertexPropertyMap<List<Integer>>) red.getPropertyMap("originalSCCs");
    for (int v = 0; v < red.getNumberOfVertices(); ++v) {
      StringBuilder sb = new StringBuilder();
      sb.append('{'); boolean first = true;
      for (int origV : componentsMap.get(v)) {
        if (! first) sb.append(',');
        else first = false;
        sb.append(Integer.toString(origV));
      }
      sb.append('}');
      redNames.put(v, sb.toString());
    }
    red.register("names", redNames);
    //red.printGraph("red.dot");
  }

  /*
  @Test
  public void testDestructiveReduction() {
    graphCyclic.printGraph("input");
    TarjanVisitor<String> visitor = new TarjanVisitor<String>();
    graphCyclic.dfs(visitor);
    // now do a destructive acyclic reduction of the graph
    List<List<Integer>> components = visitor.getSCCs();
    TransitiveClosure.destructiveAcyclicReduction(graphCyclic, components);
    VertexPropertyMap<String> names =
        (VertexPropertyMap<String>) graphCyclic.getPropertyMap("names");

    for (List<Integer> component : components) {
      StringBuilder sb = new StringBuilder();
      sb.append('{'); boolean first = true;
      for (int v: component) {
        if (! first) sb.append(',');
        else first = false;
        sb.append(Integer.toString(v));
      }
      sb.append('}');
      String name = sb.toString();
      for (int v: component) {
        names.put(v, name + names.get(v));
      }
    }
    graphCyclic.printGraph("d_red.dot");
  }
  */

  @Test
  public void testAcyclicReduction() {
    //graphAcyclic.printGraph("input_acyclic");
    DiGraph<String> red =
        TransitiveClosure.acyclicReduction(graphAcyclic);
    assertEquals(graphAcyclic.getNumberOfVertices(),
        red.getNumberOfVertices());
    VertexPropertyMap<String> redNames = new VertexListPropertyMap<String>(red);
    @SuppressWarnings("unchecked")
    VertexPropertyMap<List<Integer>> componentsMap =
        (VertexPropertyMap<List<Integer>>) red.getPropertyMap("originalSCCs");
    for (int v = 0; v < red.getNumberOfVertices(); ++v) {
      StringBuilder sb = new StringBuilder();
      sb.append('{'); boolean first = true;
      for (int origV : componentsMap.get(v)) {
        if (! first) sb.append(',');
        else first = false;
        sb.append(Integer.toString(origV));
      }
      sb.append('}');
      redNames.put(v, sb.toString());
    }
    red.register("names", redNames);
    //red.printGraph("a_red.dot");
  }

  @Test
  public void testAcyclicClosure() {
    int[][] acyclicResult = {
        { 1, 2, 3, 5, 6 },
        { 2, 3, 5, 6 },
        { 3 },
        { },
        { 0, 1, 2, 3, 5, 6 },
        { 2, 3 },
        { 2, 3, 5 },
        { 0, 1, 2, 3, 4, 5, 6, 8 },
        { 0, 1, 2, 3, 4, 5, 6 }
    };
    VertexPropertyMap<Set<Integer>> closure =
        TransitiveClosure.acyclicClosure(graphAcyclic);
    for (int v = 0; v < graphAcyclic.getNumberOfVertices(); ++v) {
      Set<Integer> toTest = new HashSet<Integer>();
      for(int i : acyclicResult[v]) toTest.add(i);
      assertEquals(toTest, closure.get(v));
    }
  }

  @Test
  public void testClosure() {
    int[][] results = {
      { 1, 2, 3, 5, 6 },
      { 1, 2, 3, 5, 6 },
      { 1, 2, 3, 5, 6 },
      { 1, 2, 3, 5, 6 },
      { 0, 1, 2, 3, 5, 6 },
      { 1, 2, 3, 5, 6 },
      { 1, 2, 3, 5, 6 },
      { 0, 1, 2, 3, 4, 5, 6, 7, 8 },
      { 0, 1, 2, 3, 4, 5, 6, 7, 8 }
    };

    VertexPropertyMap<Set<Integer>> closure =
        TransitiveClosure.transitiveClosure(graphCyclic, false);
    for(int v = 0; v < graphCyclic.getNumberOfVertices(); ++v) {
      Set<Integer> toTest = new HashSet<Integer>();
      for(int i : results[v]) toTest.add(i);
      assertEquals("v"+v, toTest, closure.get(v));
    }
    // graphCyclic.printGraph("out.dot");
  }

  @Test
  public void testReflexiveClosure() {
    int[][] results = {
      { 0, 1, 2, 3, 5, 6 },
      { 1, 2, 3, 5, 6 },
      { 1, 2, 3, 5, 6 },
      { 1, 2, 3, 5, 6 },
      { 0, 1, 2, 3, 4, 5, 6 },
      { 1, 2, 3, 5, 6 },
      { 1, 2, 3, 5, 6 },
      { 0, 1, 2, 3, 4, 5, 6, 7, 8 },
      { 0, 1, 2, 3, 4, 5, 6, 7, 8 }
    };
    VertexPropertyMap<Set<Integer>> closure =
        TransitiveClosure.transitiveClosure(graphCyclic, true);
    for(int v = 0; v < graphCyclic.getNumberOfVertices(); ++v) {
      Set<Integer> toTest = new HashSet<Integer>();
      for(int i : results[v]) toTest.add(i);
      assertEquals("v"+v, toTest, closure.get(v));
    }
    // graphCyclic.printGraph("out.dot");
  }

  public String asString(Set<Integer> s) {
    StringBuilder sb = new StringBuilder();
    sb.append('{');
    for (Integer i : s) sb.append(i).append(' ');
    sb.append('}');
    return sb.toString();
  }

  @Test
  public void testWarshallClosure() {
    int[][] results = {
      { 1, 2, 3, 5, 6 },
      { 1, 2, 3, 5, 6 },
      { 1, 2, 3, 5, 6 },
      { 1, 2, 3, 5, 6 },
      { 0, 1, 2, 3, 5, 6 },
      { 1, 2, 3, 5, 6 },
      { 1, 2, 3, 5, 6 },
      { 0, 1, 2, 3, 4, 5, 6, 7, 8 },
      { 0, 1, 2, 3, 4, 5, 6, 7, 8 }
    };
    //  closure.asMatrix()
    //  .***.**..
    //  .***.**..
    //  .***.**..
    //  .***.**..
    //  ****.**..
    //  .***.**..
    //  .***.**..
    //  *********
    //  *********

    TransitiveClosure.warshallTransitiveClosure(graphCyclic);
    // graphCyclic.printGraph("wout.dot");
    for(int v = 0; v < graphCyclic.getNumberOfVertices(); ++v) {
      Set<Integer> toTest = new HashSet<Integer>();
      for(int i : results[v]) toTest.add(i);
      for(Edge<String> e : graphCyclic.getOutEdges(v)) {
        assertTrue("V"+v+" "+e.getTarget(), toTest.contains(e.getTarget()));
        toTest.remove(e.getTarget());
      }
      assertTrue("V"+v+" "+asString(toTest), toTest.isEmpty());
    }
  }
}
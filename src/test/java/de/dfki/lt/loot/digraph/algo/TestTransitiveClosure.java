package de.dfki.lt.loot.digraph.algo;

import static de.dfki.lt.loot.digraph.io.SimpleGraphReader.*;
import static de.dfki.lt.loot.digraph.Utils.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.dfki.lt.loot.digraph.CyclicGraphException;
import de.dfki.lt.loot.digraph.DiGraph;
import de.dfki.lt.loot.digraph.Edge;
import de.dfki.lt.loot.digraph.VertexPropertyMap;

public class TestTransitiveClosure {
  /** Use this example to test your traversal methods, it's the last example
   *  on the first set of slides.
   */

  public DiGraph<String> graphAcyclic = new DiGraph<>();
  public DiGraph<String> graphCyclic = new DiGraph<>();

  @Before
  public void setUp() {
    // read in graph
    try {
      readGraph(new StringReader(exampleGraphCyclic), graphCyclic);
      readGraph(new StringReader(exampleGraphAcyclic), graphAcyclic);
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
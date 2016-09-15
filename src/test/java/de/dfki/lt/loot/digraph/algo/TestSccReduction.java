/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.lt.loot.digraph.algo;

import static de.dfki.lt.loot.digraph.Utils.*;
import static org.junit.Assert.*;

import de.dfki.lt.loot.digraph.DiGraph;
import de.dfki.lt.loot.digraph.VertexListPropertyMap;
import de.dfki.lt.loot.digraph.VertexPropertyMap;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

/**
 *
 * @author kiefer
 */
public class TestSccReduction {

  @Test
  public void testSCCReduction() throws IOException {
    DiGraph<String> graphCyclic = readGraph(exampleGraphCyclic);
    //graphCyclic.printGraph("input_cyclic");
    DiGraph<String> red = SccReduction.acyclicSccReduction(graphCyclic);
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
    // TODO: check that the structure of the graph is correct.
    assertEquals(4, red.getNumberOfActiveVertices());
  }

  @Test
  public void testAcyclicReduction() throws IOException {
    DiGraph<String> graphAcyclic = readGraph(exampleGraphAcyclic);
    // graphAcyclic.printGraph("input_acyclic");
    DiGraph<String> red = SccReduction.acyclicSccReduction(graphAcyclic);
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
    // red.printGraph("a_red.dot");
  }
}

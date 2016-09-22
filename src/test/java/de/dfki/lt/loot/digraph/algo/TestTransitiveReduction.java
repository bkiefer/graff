package de.dfki.lt.loot.digraph.algo;

import static de.dfki.lt.loot.digraph.Utils.*;
import static org.junit.Assert.*;

import org.junit.Test;

import de.dfki.lt.loot.digraph.DiGraph;
import de.dfki.lt.loot.digraph.Edge;
import java.io.IOException;

public class TestTransitiveReduction {

  @Test
  public void testReduction() throws IOException {
    DiGraph<String> red =  readGraph(exampleGraphAcyclic);
    //red.printGraph("input_transred0");
    TransitiveReduction.transitiveReduction(red);
    int noEdges = 0;
    for (int v : red) {
      for (Edge<String> e : red.getOutEdges(v)) ++noEdges;
    }
    //red.printGraph("transred.dot");
    assertEquals(8, noEdges);
  }

  @Test
  public void testReduction2() throws IOException {
    DiGraph<String> red =  readGraph(exampleGraph);
    // red.printGraph("input_transred1");
    TransitiveReduction.transitiveReduction(red);
    int noEdges = 0;
    for (int v :red) {
      for (Edge<String> e : red.getOutEdges(v)) ++noEdges;
    }
    // red.printGraph("transred.dot");
    assertEquals(8, noEdges);
  }

}
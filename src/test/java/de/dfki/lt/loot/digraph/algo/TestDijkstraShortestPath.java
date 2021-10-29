/*
 * Copyright 2019-2022 Jörg Steffen, Bernd Kiefer
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * ​https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package de.dfki.lt.loot.digraph.algo;

import static de.dfki.lt.loot.digraph.Utils.*;
import static de.dfki.lt.loot.digraph.io.SimpleGraphReader.*;
import static de.dfki.lt.loot.digraph.io.GraphPrinterFactory.*;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.junit.Test;

import de.dfki.lt.loot.digraph.*;
import de.dfki.lt.loot.digraph.io.SimpleDotPrinter;
import de.dfki.lt.loot.digraph.weighted.IntMonoid;

/**
 * <code>DirectedGraphTest</code> is a test class for
 * {@link DiGraph}.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: DirectedGraphTest.java,v 1.1 2005/11/15 10:46:27 steffen Exp $
 */
public class TestDijkstraShortestPath {

  /**
   * This is the main method. It requires no argument
   *
   * @param args an array of <code>String</code>s with the arguments;
   * not used here
   */
  public void testDijkstra(Graph<Integer> graph, String in, String[] res) throws IOException {
    // read in graph
    readEdgeWeightGraph(new StringReader(in), graph);

    //System.out.println(graph);
    //if (print) printGraph(graph, "testGraph");

    // use DFS with special visitor
    DijkstraShortestPath<Integer, Integer> algorithm =
      new DijkstraShortestPath<Integer, Integer>();

    List<Edge<Integer>> result =
      algorithm.shortestPath(graph, 0, graph.getNumberOfVertices() - 1,
          new IntMonoid(), (edge) -> edge.getInfo());

    @SuppressWarnings("unchecked")
    VertexPropertyMap<String> names =
      (VertexPropertyMap<String>) graph.getPropertyMap("names");

    //System.out.print(names.get(0));
    int i = 0;
    int curr = 0;
    for (Edge<Integer> edge : result) {
      
      /*System.out.print("--" +
                       edge.getInfo() + "-->" +
                       names.get(edge.getTarget()));*/
      assertEquals(res[i++], names.get(curr = edge.getTargetForSource(curr)));
    }
    if (print) {
      SimpleDotPrinter<Integer> pr = new SimpleDotPrinter<Integer>();
      for (Edge<Integer> edge : result) {
        pr.highlightEdge(edge);
        pr.highlightNode(edge.getSource());
        pr.highlightNode(edge.getTarget());
        printGraph(graph, "testGraph", pr);
      }
    }
    //System.out.println();
  }

  /**
   * This is the main method. It requires no argument
   *
   * @param args an array of <code>String</code>s with the arguments;
   * not used here
   */
  @Test
  public void testDijkstra() throws IOException {
    String[] res = { "w", "q", "x", "u" };
    testDijkstra(new DiGraph<Integer>(), exampleGraphWeightedEdges, res);
  }

  /**
   * Use an undirected graph this time
   */
  @Test
  public void testDijkstraUndirected() throws IOException {
    String[] res = { "t", "u" };
    testDijkstra(new UndirectedGraph<Integer>(), exampleUndirGraphWeightedEdges, res);
  }
}
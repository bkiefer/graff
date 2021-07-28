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

import de.dfki.lt.loot.digraph.CyclicGraphException;
import de.dfki.lt.loot.digraph.DiGraph;
import de.dfki.lt.loot.digraph.VertexPropertyMap;
import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.BeforeClass;
import org.junit.Test;


/**
 * {@link TestTopoOrderVisitor} is a test class for {@link TopoOrderVisitor}.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id$
 */
public class TestTopoOrderVisitor {

  /**
   * Contains the graph on which the tests run.
   */
  private static DiGraph<String> graph;

  /**
   * Initializes the graph.
   *
   * @throws IOException
   *           if there is an error when reading the graph
   */
  @BeforeClass
  public static void oneTimeSetUp()
      throws IOException {
    graph = new DiGraph<String>();
    readGraph(new StringReader(exampleGraph), graph);
  }


  /**
   * Tests the {@link TopoOrderVisitor} via graph.toposort()
   * @throws CyclicGraphException
   */
  @Test
  public void test() throws CyclicGraphException {
    // calculate topological order
    @SuppressWarnings("unchecked")
    VertexPropertyMap<String> names =
        (VertexPropertyMap<String>)graph.getPropertyMap("names");
    StringBuilder result = new StringBuilder();
    for (int v : graph.topoSort()) {
      result.append(String.format("%s ", names.get(v)));
    }
    assertEquals(
      "socks undershorts pants shoes watch shirt belt tie jacket",
      result.toString().trim());
  }

  /**
   * Tests the {@link TopoOrderVisitor}.
   * @throws CyclicGraphException
   */
  @Test
  public void testInverse() throws CyclicGraphException {
	// calculate inverse topological order
    @SuppressWarnings("unchecked")
    VertexPropertyMap<String> names =
        (VertexPropertyMap<String>)graph.getPropertyMap("names");
    StringBuilder result = new StringBuilder();
    for (int v : graph.topoSortInverse()) {
      result.append(String.format("%s ", names.get(v)));
    }
    assertEquals(
      "jacket tie belt shirt watch shoes pants undershorts socks",
      result.toString().trim());
  }
}

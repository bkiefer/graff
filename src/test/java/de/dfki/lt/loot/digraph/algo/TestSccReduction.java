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

import static de.dfki.lt.loot.digraph.io.SimpleGraphReader.*;
import static de.dfki.lt.loot.digraph.Utils.*;
import static org.junit.Assert.*;

import de.dfki.lt.loot.digraph.DiGraph;
import de.dfki.lt.loot.digraph.VertexListPropertyMap;
import de.dfki.lt.loot.digraph.VertexPropertyMap;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.junit.Test;

/**
 *
 * @author kiefer
 */
public class TestSccReduction {

  @Test
  public void testSCCReduction() throws IOException {
    DiGraph<String> graphCyclic = new DiGraph<>();
    readGraph(new StringReader(exampleGraphCyclic), graphCyclic);
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
    DiGraph<String> graphAcyclic = new DiGraph<>();
    readGraph(new StringReader(exampleGraphAcyclic), graphAcyclic);
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

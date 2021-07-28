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

import org.junit.Test;

import de.dfki.lt.loot.digraph.DiGraph;
import de.dfki.lt.loot.digraph.Edge;
import java.io.IOException;
import java.io.StringReader;

public class TestTransitiveReduction {

  @Test
  public void testReduction() throws IOException {
    DiGraph<String> red = new DiGraph<>();
    readGraph(new StringReader(exampleGraphAcyclic), red);
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
    DiGraph<String> red = new DiGraph<>();
    readGraph(new StringReader(exampleGraph), red);
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
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

import de.dfki.lt.loot.digraph.Graph;
import de.dfki.lt.loot.digraph.Edge;
import de.dfki.lt.loot.digraph.GraphVisitor;
import de.dfki.lt.loot.digraph.VertexListPropertyMap;
import de.dfki.lt.loot.digraph.VertexPropertyMap;

public class CollectTimesVisitor<EdgeInfo> implements GraphVisitor<EdgeInfo> {
  private VertexPropertyMap<Integer> discovery, finish;

  private int time;

  public CollectTimesVisitor() {
    time = 1;
  }

  public void startVertex(int v, Graph<EdgeInfo> g) {
    if (discovery == null) {
      discovery = new VertexListPropertyMap<Integer>(g);
      finish = new VertexListPropertyMap<Integer>(g);
    }
  }

  public void discoverVertex(int v, Graph<EdgeInfo> g) {
    discovery.put(v, time);
    ++time;
  }

  public void finishVertex(int v, Graph<EdgeInfo> g) {
    finish.put(v, time);
    ++time;
  }

  public void treeEdge(Edge<EdgeInfo> e, Graph<EdgeInfo> g) {}

  public void nonTreeEdge(Edge<EdgeInfo> e, Graph<EdgeInfo> g) {}

  VertexPropertyMap<Integer> getDiscoveryMap() {
    return discovery;
  }

  VertexPropertyMap<Integer> getFinishMap() {
    return finish;
  }

}

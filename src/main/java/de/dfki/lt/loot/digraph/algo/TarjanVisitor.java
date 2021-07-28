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

import java.util.*;

import de.dfki.lt.loot.digraph.Graph;
import de.dfki.lt.loot.digraph.Edge;
import de.dfki.lt.loot.digraph.GraphVisitorAdapter;
import de.dfki.lt.loot.digraph.VertexListPropertyMap;
import de.dfki.lt.loot.digraph.VertexPropertyMap;

public class TarjanVisitor<EdgeInfo>
  extends GraphVisitorAdapter<EdgeInfo> {

  private VertexPropertyMap<Integer> _discovery;

  private VertexPropertyMap<Integer> _low;

  private Stack<Integer> _vertexStack;

  private List<List<Integer>> _components;

  private int time;

  public List<List<Integer>> getSCCs() {
    return _components;
  }

  @Override
  public void startVertex(int v, Graph<EdgeInfo> g) {
    // do the initialization
    if (_components == null) {
      _discovery = new VertexListPropertyMap<Integer>(g);
      _low = new VertexListPropertyMap<Integer>(g);
      _vertexStack = new Stack<Integer>();
      _components = new ArrayList<List<Integer>>();
      time = 0;
    }
  }

  @Override
  public void discoverVertex(int v, Graph<EdgeInfo> g) {
    ++time;
    // i also use _low to detect if a vertex is still in the queue, see later
    _low.put(v, time);
    _discovery.put(v, time);
    _vertexStack.push(v);
    /*
    System.out.println("disc: " + g.getPropertyMap("names").get(v)
                       + " " + _low.get(v));
                       */
  }

  @Override
  public void finishVertex(int v, Graph<EdgeInfo> g) {
    // do the computation of the lowest reachable vertex now
    /*
    System.out.println("fin: " + g.getPropertyMap("names").get(v)
                       + " " + _low.get(v));
                       */
    int low = _low.get(v);
    for(Edge<EdgeInfo> outEdge : g.getOutEdges(v)) {
      Integer targetLow = _low.get(outEdge.getTarget());
      if (targetLow != null) {
        low = Math.min(low, targetLow);
      }
    }
    if (low == _discovery.get(v)) {
      List<Integer> component = new ArrayList<Integer>();
      int nextVertex = -1;
      do {
        nextVertex = _vertexStack.pop();
        component.add(nextVertex);
        _low.remove(nextVertex);
      } while (nextVertex != v);
      _components.add(component);
    } else {
      _low.put(v, low);
    }
  }
}
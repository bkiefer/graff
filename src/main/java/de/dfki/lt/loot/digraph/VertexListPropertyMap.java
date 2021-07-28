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

package de.dfki.lt.loot.digraph;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;


public class VertexListPropertyMap<ValueType>
  implements VertexPropertyMap<ValueType> {

  private Graph<?> _graph;

  private ArrayList<ValueType> _map;

  public VertexListPropertyMap(Graph<?> graph) {
    _map = new ArrayList<ValueType>();
    _graph = graph;
  }

  public void clear() {
    _map.clear();
  }

  public void put(int vertex, ValueType value) {
    assert (_graph.isVertex(vertex));
    while (_map.size() <= vertex) {
      _map.add(null);
    }
    _map.set(vertex, value);
  }

  public ValueType get(int vertex) {
    /** During compaction of the graph, the assertion does not hold. */
    // assert (_graph.isVertex(vertex));
    if (_map.size() <= vertex || vertex < 0) return null;
    return _map.get(vertex);
  }

  public void remove(int vertex) {
    if (_map.size() > vertex) {
      _map.set(vertex, null);
    }
  }

  public void removeRange(int from, int to) {
    if (_map.size() > from) {
      assert(_map.size() <= to);
      for (int i = Math.min(to, _map.size()) - 1; i >= from; --i) {
        _map.remove(i);
      }
    }
  }

  public List<Integer>
    findVertices(ValueType val, BiPredicate<ValueType, ValueType> pred) {

    List<Integer> result = new ArrayList<Integer>(_map.size());
    for(int i = 0; i < _map.size(); ++i) {
      if (_graph.isVertex(i) && pred.test(_map.get(i), val)) {
        result.add(i);
      }
    }
    return result;
  }
}
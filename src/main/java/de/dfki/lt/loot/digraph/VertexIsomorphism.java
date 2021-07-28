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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;


public class VertexIsomorphism<ValueType>
  implements VertexPropertyMap<ValueType> {

  private Graph<?> _graph;

  private ArrayList<ValueType> _map;

  private Map<ValueType, Integer> _inverseMap;

  public VertexIsomorphism(Graph<?> graph,
    Map<ValueType, Integer> invMap) {
    _map = new ArrayList<ValueType>();
    _inverseMap = invMap;
    _graph = graph;
  }

  public VertexIsomorphism(Graph<?> graph) {
    this(graph, new HashMap<ValueType, Integer>());
  }

  public void clear() {
    _map.clear();
    _inverseMap.clear();
  }

  public void put(int vertex, ValueType value) {
    assert (_graph.isVertex(vertex));
    while (_map.size() <= vertex) {
      _map.add(null);
    }
    _map.set(vertex, value);
    _inverseMap.put(value, vertex);
  }

  public ValueType get(int vertex) {
    assert (_graph.isVertex(vertex));
    if (_map.size() <= vertex) return null;
    return _map.get(vertex);
  }

  public void remove(int vertex) {
    if (_map.size() > vertex) {
      _inverseMap.remove(_map.get(vertex));
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

  /** Return the vertex associated with this value */
  public int getVertex(ValueType val) {
    if (_inverseMap.containsKey(val))
      return _inverseMap.get(val);
    return -1;
  }
}
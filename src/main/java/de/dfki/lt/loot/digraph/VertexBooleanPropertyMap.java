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
import java.util.BitSet;
import java.util.List;
import java.util.function.BiPredicate;


public class VertexBooleanPropertyMap implements VertexPropertyMap<Boolean> {

  private Graph<?> _graph;

  private BitSet _map;

  public VertexBooleanPropertyMap(Graph<?> g) {
    _map = new BitSet();
    _graph = g;
  }

  public void clear() {
    _map.clear();
  }

  public void put(int vertex, Boolean value) {
    assert (_graph.isVertex(vertex));
    _map.set(vertex, value);
  }

  public Boolean get(int vertex) {
    assert (_graph.isVertex(vertex));
    return _map.get(vertex);
  }

  public void set(int vertex) {
    assert (_graph.isVertex(vertex));
    _map.set(vertex);
  }

  public void clear(int vertex) {
    assert (_graph.isVertex(vertex));
    _map.clear(vertex);
  }

  public void remove(int vertex) {
    if (_map.size() > vertex) {
      _map.clear(vertex);
    }
  }

  public void removeRange(int from, int to) {
    if (_map.size() > from) {
      assert(_map.size() <= to);
      _map.set(from, Math.min(to, _map.size()), false);
    }
  }

  public List<Integer>
    findVertices(Boolean val, BiPredicate<Boolean, Boolean> pred) {

    List<Integer> result = new ArrayList<Integer>(_map.size());
    for(int i = 0; i < _map.size(); ++i) {
      if (_graph.isVertex(i) && pred.test(_map.get(i), val)) {
        result.add(i);
      }
    }
    return result;
  }
}
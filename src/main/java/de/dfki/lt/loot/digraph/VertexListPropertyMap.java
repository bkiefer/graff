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
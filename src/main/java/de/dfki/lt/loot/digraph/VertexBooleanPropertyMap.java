package de.dfki.lt.loot.digraph;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;


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
    findVertices(Boolean val, BinaryPredicate<Boolean> pred) {

    List<Integer> result = new ArrayList<Integer>(_map.size());
    for(int i = 0; i < _map.size(); ++i) {
      if (_graph.isVertex(i) && pred.compare(_map.get(i), val)) {
        result.add(i);
      }
    }
    return result;
  }
}
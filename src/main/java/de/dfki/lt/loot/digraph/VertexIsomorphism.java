package de.dfki.lt.loot.digraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VertexIsomorphism<ValueType>
  implements VertexPropertyMap<ValueType> {

  private DirectedGraph<?> _graph;

  private ArrayList<ValueType> _map;

  private Map<ValueType, Integer> _inverseMap;

  public VertexIsomorphism(DirectedGraph<?> graph,
    Map<ValueType, Integer> invMap) {
    _map = new ArrayList<ValueType>();
    _inverseMap = invMap;
    _graph = graph;
  }

  public VertexIsomorphism(DirectedGraph<?> graph) {
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
    findVertices(ValueType val, BinaryPredicate<ValueType> pred) {

    List<Integer> result = new ArrayList<Integer>(_map.size());
    for(int i = 0; i < _map.size(); ++i) {
      if (_graph.isVertex(i) && pred.compare(_map.get(i), val)) {
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
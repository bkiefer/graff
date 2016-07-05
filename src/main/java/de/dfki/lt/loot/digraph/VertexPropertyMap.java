package de.dfki.lt.loot.digraph;

import java.util.List;


public interface VertexPropertyMap<ValueType> {

  public void clear();

  public void put(int vertex, ValueType value);

  public ValueType get(int vertex);

  public void remove(int vertex);

  public void removeRange(int from, int to);

  public List<Integer>
    findVertices(ValueType val, BinaryPredicate<ValueType> pred);
}
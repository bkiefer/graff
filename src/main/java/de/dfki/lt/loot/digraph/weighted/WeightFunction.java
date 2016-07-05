package de.dfki.lt.loot.digraph.weighted;

import de.dfki.lt.loot.digraph.Edge;

public interface WeightFunction<EdgeInfo, T> {
  public T get(Edge<EdgeInfo> e);
}

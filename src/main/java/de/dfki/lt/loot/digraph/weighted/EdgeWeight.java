package de.dfki.lt.loot.digraph.weighted;

import de.dfki.lt.loot.digraph.Edge;

public class EdgeWeight <EdgeInfo extends WeightInfo<T>, T>
implements WeightFunction<EdgeInfo, T> {
  public T get(Edge<EdgeInfo> e) {
    return e.getInfo().getWeight();
  }

}

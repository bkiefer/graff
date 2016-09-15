package de.dfki.lt.loot.digraph;

/** A DirectedGraph interface containing all methods currently needed for
 *  abstract algorithms.
 */
public interface AbstractBiGraph<EdgeInfo> extends Graph<EdgeInfo> {

  /** return the edges ending in this vertex, may not return null */
  public abstract Iterable<Edge<EdgeInfo>> getInEdges(int vertex);

}
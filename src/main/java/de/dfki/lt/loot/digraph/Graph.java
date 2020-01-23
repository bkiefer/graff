package de.dfki.lt.loot.digraph;

import java.util.BitSet;
import java.util.Comparator;

/** A DirectedGraph interface containing all methods currently needed for
 *  abstract algorithms.
 *
 *  EI is short for EdgeInfo, the type that is attached to every edge.
 */
public interface Graph<EI> extends Iterable<Integer> {

  /** This creates a new vertex. It either takes an available deleted vertex
   * or creates a completely new one.
   *
   * @return an empty vertex
   */
  public abstract int newVertex();

  /** is the given vertex a vertex of this graph?
   *
   * @return <code>true</code> if the vertex is deleted, <code>false</code>
   *         otherwise
   */
  public abstract boolean isVertex(int vertex);

  /** Return the number of vertices in this graph (including deleted vertices)
   */
  public abstract int getNumberOfVertices();

  /** return the edges emerging from vertex, may not return null */
  public abstract Iterable<Edge<EI>> getOutEdges(int vertex);

  /** return true if the node has at least one outgoing edge */
  public abstract boolean hasOutEdges(int vertex);

  /** return the edges ending in this vertex, may not return null */
  // public abstract Iterable<Edge<EdgeInfo>> getInEdges(int vertex);

  /** This removes the given vertex from this directed graph.
   *
   * @param vertex the vertex to remove from the graph
   */
  public abstract void removeVertex(int vertex);

  /** Remove a vertex without removing the edges pointing to it
   */
  public abstract void removeVertexLazy(int vertex);

  /** Remove all edges pointing to a deleted vertex */
  public abstract void cleanupEdges();


  public abstract Edge<EI> newEdge(EI info, int from, int to);

  /**
   * This returns the first edges emerging from vertex where the edge info is
   * compatible to the given edge info according to the given predicate.
   *
   * @param anEdgeInfo the <code>EdgeInfo</code> for which to find matching
   * edges
   * @param binPred a {@link BinaryPredicate} used to compare the edge infos
   * @return an {@link Edge}s that matches or <code>null</code>
   */
  public abstract Edge<EI> findEdge(int sourceVertex, EI info,
      Comparator<EI> comp);

  /**
   * This returns all edges emerging from vertex where the edge info is
   * compatible to the given edge info according to the given predicate.
   *
   * @param anEdgeInfo the <code>EdgeInfo</code> for which to find matching
   * edges
   * @param binPred a {@link BinaryPredicate} used to compare the edge infos
   * @return a <code>List</code> of {@link Edge}s with matching edges
   */
  public abstract Iterable<Edge<EI>> findEdges(int sourceVertex,
      EI info, Comparator<EI> comp);

  /** This changes the to vertex of this edge to the given vertex.
   *
   * @param anEndVertex the new end vertex
   */
  public abstract void changeEndVertex(Edge<EI> edge, int newTarget);

  /** Return the lazy converse graph of this graph, i.e., one where all edges
   * are put into the target outEdges instead of the source outEdges.
   */
  public abstract Graph<EI> converseLazy();

  /** register a property map with this graph, so that it can be retrieved
   *  by name and will be synchronized regarding delete and compact operations
   */
  public abstract void register(String name, VertexPropertyMap<?> map);

  /** return the registered property map with the given name, if it exists */
  public abstract VertexPropertyMap<?> getPropertyMap(String name);

  /** register a property map with this graph, so that it can be retrieved
   *  by name and will be synchronized regarding delete and compact operations
   */
  public abstract void register(String name, BitSet map);

  /** return the registered property map with the given name, if it exists */
  public abstract BitSet getBooleanPropertyMap(String name);
}
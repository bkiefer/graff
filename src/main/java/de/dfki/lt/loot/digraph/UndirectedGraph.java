package de.dfki.lt.loot.digraph;

import java.util.BitSet;
import java.util.Comparator;
import java.util.Iterator;

import de.dfki.lt.loot.util.DelegateIterator;

public class UndirectedGraph<EI> implements Graph<EI> {
  DirectedBiGraph<EI> _impl;

  public UndirectedGraph() {
    _impl = new DirectedBiGraph<>();
  }

  /** This creates a new vertex. It either takes an available deleted vertex
   * or creates a completely new one.
   *
   * @return an empty vertex
   */
  public int newVertex() { return _impl.newVertex(); }

  /** is the given vertex a vertex of this graph?
   *
   * @return <code>true</code> if the vertex is deleted, <code>false</code>
   *         otherwise
   */
  public boolean isVertex(int vertex) { return _impl.isVertex(vertex); }

  /** Return the number of vertices in this graph (including deleted vertices)
   */
  public int getNumberOfVertices() { return _impl.getNumberOfVertices(); }

  /** return the edges emerging from vertex, may not return null */
  public Iterable<Edge<EI>> getOutEdges(int vertex) {
    return new DelegateIterator<Edge<EI>>(_impl.getOutEdges(vertex),
        _impl.getInEdges(vertex));
  }

  /** return true if the node has at least one outgoing edge */
  public boolean hasOutEdges(int vertex) {
    return _impl.hasOutEdges(vertex) || _impl.hasInEdges(vertex);
  }

  /** This removes the given vertex from this directed graph.
   *
   * @param vertex the vertex to remove from the graph
   */
  public void removeVertex(int vertex) {
    _impl.removeVertex(vertex);
  }

  /** Remove a vertex without removing the edges pointing to it
   */
  public void removeVertexLazy(int vertex) {
    _impl.removeVertexLazy(vertex);
  };

  public Edge<EI> newEdge(EI info, int from, int to) {
    return _impl.newEdge(info, from, to);
  }

  /**
   * This returns the first edges emerging from vertex where the edge info is
   * compatible to the given edge info according to the given predicate.
   *
   * @param anEdgeInfo the <code>EdgeInfo</code> for which to find matching
   * edges
   * @param binPred a {@link BinaryPredicate} used to compare the edge infos
   * @return an {@link Edge}s that matches or <code>null</code>
   */
  public Edge<EI> findEdge(int sourceVertex, EI info,
      Comparator<EI> comp) {
    Edge<EI> res = _impl.findEdge(sourceVertex, info, comp);
    if (res != null) return res;
    for (Edge<EI> edge : _impl.getInEdges(sourceVertex)) {
      if (comp.compare(edge.getInfo(), info) == 0) {
        return edge;
      }
    }
    return null;
  }

  /**
   * This returns all edges emerging from vertex where the edge info is
   * compatible to the given edge info according to the given predicate.
   *
   * @param anEdgeInfo the <code>EdgeInfo</code> for which to find matching
   * edges
   * @param binPred a {@link BinaryPredicate} used to compare the edge infos
   * @return a <code>List</code> of {@link Edge}s with matching edges
   */
  public Iterable<Edge<EI>> findEdges(int sourceVertex,
      EI info, Comparator<EI> comp) {
    throw new UnsupportedOperationException();
  }

  /** This changes the to vertex of this edge to the given vertex.
   *
   * @param anEndVertex the new end vertex
   */
  public void changeEndVertex(Edge<EI> edge, int newTarget) {
    throw new UnsupportedOperationException();
  }

  /** Return the lazy converse graph of this graph, i.e., one where all edges
   * are put into the target outEdges instead of the source outEdges.
   */
  public Graph<EI> converseLazy() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Iterator<Integer> iterator() {
    return _impl.iterator();
  }

  @Override
  public void cleanupEdges() {
    // a no-op, see DirectedBiGraph
  }

  /** register a property map with this graph, so that it can be retrieved
   *  by name and will be synchronized regarding delete and compact operations
   */
  public void register(String name, VertexPropertyMap<?> map){
    _impl.register(name, map);
  }

  /** return the registered property map with the given name, if it exists */
  public VertexPropertyMap<?> getPropertyMap(String name){
    return _impl.getPropertyMap(name);
  }

  /** register a property map with this graph, so that it can be retrieved
   *  by name and will be synchronized regarding delete and compact operations
   */
  public void register(String name, BitSet map){
    _impl.register(name, map);
  }

  /** return the registered property map with the given name, if it exists */
  public BitSet getBooleanPropertyMap(String name){
    return _impl.getBooleanPropertyMap(name);
  }
}

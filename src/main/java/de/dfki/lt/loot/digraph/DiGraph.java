package de.dfki.lt.loot.digraph;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

import de.dfki.lt.loot.util.FilteredIterator;


/**
 * <code>DirectedGraph</code> represents a directed graph. Vertices and edges
 * are managed in lists. This class only stores outgoing edges. If efficient
 * access to the list of incoming edges is required, either a DirectedBiGraph
 * should be used, or the inverse graph should be computed.
 *
 * @author Bernd Kiefer, DFKI
 * @author Joerg Steffen, DFKI
 * @version $Id$
 */
public class DiGraph<EI> implements Graph<EI> {

 /** For each vertex, this contains the outgoing and incoming edges of this
   *  graph.
   */
  protected ArrayList<EdgeContainer<EI>> _outEdges;

  /** Check internally if an edge is added twice */
  protected boolean _checkMultiEdges;

  /** A list of ints representing deleted vertices */
  protected BitSet _deletedVertices;

  /** All VertexPropertyMaps registered with this DirectedGraph */
  protected HashMap<String, VertexPropertyMap<?>> _vertexPropertyMaps;

  protected HashMap<String, BitSet> _vertexBooleanPropertyMaps;

  /* bullshit
  public class EdgeIterator<E> implements Iterator<Edge<E>> {
    private Iterator<Edge<E>> _it;
    private EdgeContainer<E> _symm;
    private Edge<E> _lastEdge;

    EdgeIterator(Iterator<Edge<E>> it, EdgeContainer<E> symm) {
      _it = it;
      _symm = symm;
    }

    @Override
    public boolean hasNext() { return _it.hasNext(); }

    @Override
    public Edge<E> next() { return (_lastEdge = _it.next()); }

    @Override
    public void remove() {
      _it.remove();
      if (_lastEdge != null) _symm.removeEdge(_lastEdge);
    }
  }
  */

  /*
  public class ImmutableIterator<E> implements Iterator<E> {
    private Iterator<E> _it;
    ImmutableIterator(Iterator<E> it) { _it = it; }

    @Override
    public boolean hasNext() { return _it.hasNext(); }

    @Override
    public E next() { return _it.next(); }

    @Override
    public void remove() {
      throw new UnsupportedOperationException(
          "This iterator does not allow removal of elements");
    }
  }
  */

  private class ArrayListEdgeContainer<EI>
  extends ArrayList<Edge<EI>> implements EdgeContainer<EI> {
    private static final long serialVersionUID = 2922994063452493448L;

    public boolean removeEdge(Edge<EI> e) {
      return remove(e);
    }

    public Edge<EI> findTarget(int to) {
      for (Edge<EI> e: this)
        if (e.getTarget() == to) return e;
      return null;
    }

  }

  /**/
  private class SetEdgeContainer<EI>
  extends HashMap<Integer, Edge<EI>> implements EdgeContainer<EI> {
    private static final long serialVersionUID = 2922994063452493449L;

    @Override
    public Iterator<Edge<EI>> iterator() {
      return values().iterator();
    }

    @Override
    public boolean add(Edge<EI> e) {
      put(e.getTarget(), e);
      return false;
    }

    public boolean removeEdge(Edge<EI> e) {
      Iterator<Edge<EI>> it = values().iterator();
      while(it.hasNext()) {
        if (it.next() == e) {
          it.remove();
          return true;
        }
      }
      return false;
    }


    //@Override
    //public Iterator<Edge<EI>> getEdgeIterator(EdgeContainer<EI> edgeContainer) {
    //  return new EdgeIterator<EI>(values().iterator(), edgeContainer);
    //}

    public Edge<EI> findTarget(int to) {
      return get(to);
    }
  }

  public void forceSimpleGraph(boolean what) {
    _checkMultiEdges = what;
  }

  protected EdgeContainer<EI> newEdgeList() {
    return _checkMultiEdges ? new SetEdgeContainer<EI>() :
        new ArrayListEdgeContainer<EI>();
  }

  protected EdgeContainer<EI>
    getEdgeList(ArrayList<EdgeContainer<EI>> edges, int vertex) {
    EdgeContainer<EI> edgeList = edges.get(vertex);
    if (edgeList == null) {
      edgeList = newEdgeList();
      edges.set(vertex, edgeList);
    }
    return edgeList;
  }

  /** Set the start vertex of edge and add it to the out edges of the new source.
   *  WARNING: the edge list of the old source is not changed. In most cases,
   *  doing this would result in a ConcurrentModificationException.
   *  The caller of this method must take care of removing the edge of the
   *  old edge list.
   *
   *  For secure changing of source node, @see changeStartVertex
   */
  protected void setFrom(Edge<EI> edge, int from) {
    edge.setSource(from);
    getEdgeList(_outEdges, from).add(edge);
  }

  /** Set the end vertex of edge and add it to the in edges of the new target.
   *  WARNING: the edge list of the old source is not changed. In most cases,
   *  doing this would result in a ConcurrentModificationException.
   *  The caller of this method must take care of removing the edge of the
   *  old edge list.
   *
   *  For secure changing of target node, @see changeEndVertex
   */
  protected void setTo(Edge<EI> edge, int to) {
    edge.setTarget(to);
  }

  public boolean hasEdge(int from, int to) {
     EdgeContainer<EI> out = _outEdges.get(from);
     if (out == null)
       return false;
     return out.findTarget(to) != null;
  }

  /** This creates a new instance of <code>DirectedGraph</code>.
   */
  public DiGraph() {
    _outEdges = new ArrayList<EdgeContainer<EI>>();
    _deletedVertices = new BitSet();
    _vertexPropertyMaps = new HashMap<String, VertexPropertyMap<?>>();
    _vertexBooleanPropertyMaps = new HashMap<String, BitSet>();
  }

  /** Create a new graph with n vertices and no edges */
  public DiGraph(int n) {
    this();
    for (int i = 0; i < n; ++i) {
      newVertex();
    }
  }

  /** Return the number of vertices in this graph (including deleted vertices)
   */
  public int getNumberOfVertices() {
    return _outEdges.size();
  }

  /** Return the number of non-deleted vertices in this graph, mostly for
   *  statistical purposes.
   */
  public int getNumberOfActiveVertices() {
    return getNumberOfVertices() - _deletedVertices.cardinality();
  }

  /** This returns an iterator over all the edges of this graph.
   *
   * @return an <code>Iterator<Edge<VertexInfo,EdgeInfo>></code> over the edges
   *
  public Iterator<Edge<EdgeInfo>> getEdges() {
    return new EdgeIterator();
  }
  */

  /** is the given vertex a deleted vertex of this graph?
   * @precondition vertex must be smaller than nextFreeVertex
   * @return <code>true</code> if the vertex is deleted, <code>false</code>
   *         otherwise.
   */
  public boolean isDeletedVertex(int vertex) {
    return _deletedVertices.get(vertex);
  }

  /** is the given vertex a vertex of this graph?
   *
   * @return <code>true</code> if the vertex is deleted, <code>false</code>
   *         otherwise
   */
  public boolean isVertex(int vertex) {
    return vertex < _outEdges.size() && ! isDeletedVertex(vertex) ;
  }

  private class VertexIterator implements Iterator<Integer> {
    private int _current;

    VertexIterator() {
      _current = 0;
      while (isDeletedVertex(_current)) {
        ++_current;
      }
    }

    /** for an iteration over the vertices, are you done? */
    public boolean hasNext() {
      return _current < _outEdges.size();
    }

    /** return the next valid vertex */
    public Integer next() {
      int result = _current;
      ++_current;
      while (hasNext() && isDeletedVertex(_current)) {
        ++_current;
      }
      return result;
    }

    public void remove() {
      throw new UnsupportedOperationException("use removeVertex");
    }
  }

  /** This allows to iterate over all vertices of this graph */
  public Iterator<Integer> iterator() {
    return new VertexIterator();
  }


  /** This creates a new vertex. It either takes an available deleted vertex
   * or creates a completely new one.
   *
   * @return an empty vertex
   */
  public int newVertex() {
    if (_deletedVertices.isEmpty()) {
      _outEdges.add(null);
      return _outEdges.size() - 1;
    }
    int vertex = _deletedVertices.nextSetBit(0);
    _deletedVertices.clear(vertex);
    return vertex;
  }

  /** return the edges emerging from vertex */
  public Iterable<Edge<EI>> getOutEdges(int vertex) {
    EdgeContainer<EI> result = _outEdges.get(vertex);
    if (result == null)
      return Collections.emptyList();
    // return Collections.unmodifiableCollection(result);
    return result; // not nice, but needed for minimization
  }

  /** Return true if the node has at least one outgoing edge. */
  public boolean hasOutEdges(int vertex) {
    EdgeContainer<EI> result = _outEdges.get(vertex);
    if (result == null) return false;
    return ! result.isEmpty();
  }


  /**
   * This creates a new edge with the given edge info and returns it. The edge
   * is part of this directed graph.
   *
   * The implementation of the edge containers (_inEdges and _outEdges)
   * determines if the graph may have multiple edges with the same source and
   * target (and info ...)
   *
   * @param edgeInfo  a <code>EdgeInfo</code> with the info assigned to the
   *                  newly cerated edge
   * @param from      the start vertex of the edge, which must be a valid
   *                  vertex of the graph
   * @param to        the end vertex of the edge, which must be a valid
   *                  vertex of the graph
   * @return a newly created {@link Edge}
   */
  public Edge<EI> newEdge(EI edgeInfo, int from, int to) {
    assert(isVertex(from) && isVertex(to));
    Edge<EI> edge = new Edge<EI>(edgeInfo, from, to);
    setFrom(edge, from);
    setTo(edge, to);
    return edge;
  }

  /** remove the given Edge */
  public void removeEdge(Edge<EI> edge) {
    _outEdges.get(edge.getSource()).removeEdge(edge);
  }

  /** remove the given Edges */
  public void removeEdges(List<Edge<EI>> edges) {
    for (Edge<EI> edge : edges) {
      removeEdge(edge);
    }
  }

  /** This changes the to vertex of this edge to the given vertex.
   *
   * @param anEndVertex the new end vertex
   */
  public void changeEndVertex(Edge<EI> edge, int anEndVertex) {
    // set new end vertex
    setTo(edge, anEndVertex);
  }


  /** This changes the from vertex of this edge to the given vertex.
   *
   * @param aStartVertex the new end vertex
   */
  public void changeStartVertex(Edge<EI> edge, int aStartVertex) {
    // remove edge from old start vertex's outgoing edge list
    _outEdges.get(edge.getSource()).removeEdge(edge);

    // set new start vertex
    setFrom(edge, aStartVertex);
  }


  /**
   * This returns the first edge emerging from vertex where the edge info is
   * compatible to the given edge info according to the given predicate.
   *
   * @param anEdgeInfo the <code>EdgeInfo</code> for which to find matching
   * edges
   * @param binPred a {@link BinaryPredicate} used to compare the edge infos
   * @return an {@link Edge}s that matches or <code>null</code>
   */
  public Edge<EI> findEdge(
      int vertex,
      EI anEdgeInfo,
      Comparator<EI> comp) {

    for (Edge<EI> edge : getOutEdges(vertex)) {
      if (comp.compare(edge.getInfo(), anEdgeInfo) == 0) {
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
  public Iterable<Edge<EI>> findEdges(
      int vertex,
      EI anEdgeInfo,
      Comparator<EI> comp) {
    return new FilteredIterator<Edge<EI>, EI>(getOutEdges(vertex), anEdgeInfo,
        comp, (Function<Edge<EI>, EI>) e -> e.getInfo());
  }

  /**
   * This returns all edges from this directed graph where the edge info is
   * compatible to the given edge info according to the given predicate.
   *
   * @param anEdgeInfo the <code>EdgeInfo</code> for which to find matching
   * edges
   * @param binPred a {@link BinaryPredicate} used to compare the edge infos
   * @return a <code>List</code> of {@link Edge}s with matching edges
   */
  public List<Edge<EI>> findEdges(
      EI anEdgeInfo,
      Comparator<EI> comp) {
    LinkedList<Edge<EI>> result = new LinkedList<Edge<EI>>();

    for (int vertex = 0; vertex < _outEdges.size(); ++vertex) {
      if (! isDeletedVertex(vertex)) {
        for (Edge<EI> e : findEdges(vertex, anEdgeInfo, comp)) {
          result.add(e);
        }
      }
    }
    return result;
  }


  /** This removes the given vertex from this directed graph. This is not a lazy
   * implementation and therefore quite expensive (up to O(E)).
   *
   * @deprecated
   *
   * Don't use this function regularly. If this is necessary, use a
   * {@link DirectedBiGraph} instead
   *
   * @param vertex the vertex to remove from the graph
   */
  @Deprecated
  public void removeVertex(int vertex) {
    if (isVertex(vertex)) {
      removeVertexLazy(vertex);
      cleanupEdges();
    }
  }


  /** This removes the given vertex from this directed graph. This does not
   *  delete edges, so either the calling code does take care of the edges,
   *  or a call to cleanup() should be done when a set of vertices was removed.
   *
   *  This code should *always* run when a (non-deleted) vertex is removed
   *  from this directed graph.
   *
   * @param vertex the vertex to remove from the graph
   */
  public void removeVertexLazy(int vertex) {
    _outEdges.set(vertex, null);
    // register this vertex as being deleted
    _deletedVertices.set(vertex);

    // clear the entries in the registered property maps
    for(VertexPropertyMap<?> map : _vertexPropertyMaps.values()) {
      map.remove(vertex);
    }

    // clear the entries in the registered boolean property maps
    for(BitSet map : _vertexBooleanPropertyMaps.values()) {
      map.clear(vertex);
    }
  }


  /** Remove all edges pointing to a deleted vertex. To be called after
   *  subsequent calls to removeVertexLazy
   */
  public void cleanupEdges() {
    // remove the edges to this vertex from other vertices edge lists
    for (int v = 0; v < _outEdges.size(); ++v) {
      if (isVertex(v)) {
        EdgeContainer<EI> out = _outEdges.get(v);
        if (out != null) {
          Iterator<Edge<EI>> edgeIt = out.iterator();
          while (edgeIt.hasNext()) {
            Edge<EI> edge = edgeIt.next();
            if (! isVertex(edge.getTarget()))
              edgeIt.remove();
          }
        }
      } else {
        _outEdges.set(v, null);
      }
    }
  }

  /** Helper method that performs renumbering of edge targets and property
   *  maps based on the renumbering array passed as argument.
   *  @param newNumber an array where in the i'th position the new position is
   *     stored (which may equal to i) for non-deleted edges, and -1 for
   *     deleted edges.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected void compactRenumber(int[] newNumber) {
    // remove the obsolete buckets
    for (int i = 0; i < _deletedVertices.cardinality(); ++i) {
      _outEdges.remove(_outEdges.size() - 1);
    }
    _deletedVertices.clear();

    // now renumber the edge targets
    for (EdgeContainer<EI> out : _outEdges) {
      if (out != null) {
        for (Edge<EI> edge : out) {
          edge.setSource(newNumber[edge.getSource()]);
          edge.setTarget(newNumber[edge.getTarget()]);
        }
      }
    }

    // exchange the entries in the registered property maps
    for (int from = 0; from < newNumber.length; ++from) {
      int to = newNumber[from];
      if (to >= 0 && to != from) {
        for(VertexPropertyMap map : _vertexPropertyMaps.values()) {
          map.put(to, map.get(from));
          map.remove(from);
        }

        for(BitSet map : _vertexBooleanPropertyMaps.values()) {
          map.set(to, map.get(from));
          map.clear(from);
        }
      }
    }
  }

  /** Helper function that moves the edge container(s) from the <code>source</code>
   *  position to a new safe <code>target</code> position
   */
  protected void moveEdgeContainer(int source, int target) {
    _outEdges.set(target, _outEdges.get(source));
  }


  /** Compact this graph, i.e., fill the deleted vertices (holes) through
   *  renumbering the vertices in a stable way.
   */
  public void compactStable() {
    int[] newNumber = new int[_outEdges.size()];
    for (int i = 0; i < newNumber.length; ++i) {
      newNumber[i] = i;
    }

    // get the lowest deleted and the highest non-deleted vertex
    int targetIndex = _deletedVertices.nextSetBit(0);
    if (targetIndex < 0) return;
    int sourceIndex = targetIndex;
    while (sourceIndex < newNumber.length) {
      while (isDeletedVertex(sourceIndex)) {
        newNumber[sourceIndex] = -1;
        ++sourceIndex;
      }
      if (sourceIndex < newNumber.length) {
        newNumber[sourceIndex] = targetIndex;
        moveEdgeContainer(sourceIndex, targetIndex);
      }
      ++targetIndex;
      ++sourceIndex;
    }
    compactRenumber(newNumber);
  }


  /** Compact this graph, i.e., fill the deleted vertices (holes) through
   *  renumbering the vertices so that a minimal number of nodes is changed.
   */
  public void compact() {
    int[] newNumber = new int[_outEdges.size()];
    for (int i = 0; i < newNumber.length; ++i) {
      newNumber[i] = i;
    }

    // get the lowest deleted and the highest non-deleted vertex
    int targetIndex = _deletedVertices.nextSetBit(0);
    int sourceIndex = newNumber.length - 1;
    while (targetIndex >= 0 && targetIndex < sourceIndex) {
      while (isDeletedVertex(sourceIndex)) {
        newNumber[sourceIndex] = -1;
        --sourceIndex;
      }
      if (targetIndex < sourceIndex) {
        newNumber[sourceIndex] = targetIndex;
        moveEdgeContainer(sourceIndex, targetIndex);
        newNumber[targetIndex] = -1;
      }
      targetIndex = _deletedVertices.nextSetBit(targetIndex + 1);
      --sourceIndex;
    }
    compactRenumber(newNumber);
  }



  public String toString(int vertex, VertexPropertyMap<?> propMap) {
    StringBuilder strRep = new StringBuilder();

    String newline = System.getProperty("line.separator");

    if (! isDeletedVertex(vertex)) {
      strRep.append("Vertex ")
        .append((propMap == null || propMap.get(vertex) == null)
                ? vertex
                : propMap.get(vertex))
        .append(newline).append(" == out ===== ").append(newline);
      for (Edge<EI> edge : getOutEdges(vertex)) {
        strRep.append(edge.toString(propMap)).append(newline);
      }
    }
    return strRep.toString();
  }

  /** Produce a string representation of this graph and use the (possibly)
   *  given VertexPropertyMap to create a readable representation for the
   *  vertices. If propMap is null, the vertex number will be used.
   *
   * @return a <code>String</code> representation of this graph
   */
  public String toString(VertexPropertyMap<? extends Object> propMap) {
    StringBuilder strRep = new StringBuilder();

    for (int v : this) {
      strRep.append(toString(v, propMap));
    }

    return strRep.toString();
  }

  /** This overrides @see java.lang.Object#toString().
   *
   * @return a <code>String</code> representation of this graph
   */
  @Override
  public String toString() {
    return toString(getPropertyMap("names"));
  }


  public String asMatrix() {
    StringBuilder sb = new StringBuilder();
    for(int vi : this) {
      for(int vj : this) {
        sb.append(hasEdge(vi, vj) ? '*' : '.');
      }
      sb.append('\n');
    }
    return sb.toString();
  }

  /** register a property map with this graph, so that it can be retrieved
   *  by name and will be synchronized regarding delete and compact operations
   */
  public void register(String name, VertexPropertyMap<?> map) {
    _vertexPropertyMaps.put(name, map);
  }

  /** return the registered property map with the given name, if it exists */
  public VertexPropertyMap<?> getPropertyMap(String name) {
    return _vertexPropertyMaps.get(name);
  }

  /** register a property map with this graph, so that it can be retrieved
   *  by name and will be synchronized regarding delete and compact operations
   */
  public void register(String name, BitSet map) {
    _vertexBooleanPropertyMaps.put(name, map);
  }

  /** return the registered property map with the given name, if it exists */
  public BitSet getBooleanPropertyMap(String name) {
    return _vertexBooleanPropertyMaps.get(name);
  }

  /** Recursive helper function for the dfs methods.
   *  @param vertex the current vertex to visit
   *  @param visitor a {@link GraphVisitor}, to be called appropriately
   *  @param visited a BitSet indicating if the vertex is visited for the first
   *      time
   *  @param converse if true, the dfs runs on a lazy converse of a
   *      {@link DiGraph}, which means that the edge direction is inverse,
   *      i.e., getSource() returns the target node, getTarget() the source
   */
  private void dfsVisit(int vertex,
                        GraphVisitor<EI> visitor,
                        BitSet visited,
                        boolean converse) {
    visited.set(vertex);  // vertex gets gray
    visitor.discoverVertex(vertex, this);

    for (Edge<EI> edge : getOutEdges(vertex)) {
      int target = converse ? edge.getSource() : edge.getTarget();
      // is the target vertex white?
      if (! visited.get(target)) {
        visitor.treeEdge(edge, this);
        dfsVisit(target, visitor, visited, converse);
      } else {
        visitor.nonTreeEdge(edge, this);
      }
    }
    // vertex gets black
    visitor.finishVertex(vertex, this);
  }

  /** Visit all vertices of this graph in a depth first manner. The vertex list
   *  is traversed in order, and every non-visited vertex is then taken as
   *  start vertex, consecutively.
   *  The desired functionality can be implemented by the given {@link
   *  GraphVisitor} argument.
   */
  public void dfs(GraphVisitor<EI> visitor) {
    BitSet visited = new BitSet();
    for(int vertex = 0; vertex < _outEdges.size(); ++vertex) {
      if (! isDeletedVertex(vertex) && ! visited.get(vertex)) {
        visitor.startVertex(vertex, this);
        dfsVisit(vertex, visitor, visited, false);
      }
    }
  }

  /** Visit the vertices of this graph that are reachable from vertex in a depth
   *  first manner.
   *  The desired functionality can be implemented by the given {@link
   *  GraphVisitor} argument.
   */
  public void dfs(int vertex, GraphVisitor<EI> visitor) {
    dfsVisit(vertex, visitor, new BitSet(), false);
  }

  /** Visit the vertices of this graph that are reachable from vertex in a depth
   *  first manner.
   *  The desired functionality can be implemented by the given {@link
   *  GraphVisitor} argument.
   *  @param vertex the vertex to start the dfs at
   *  @param converse if true, the dfs runs on a lazy converse of a
   *       {@link DiGraph}, which means that the edge direction is inverse,
   *       i.e., getSource() returns the target node, getTarget() the source
   */
  public void dfsConverse(int vertex, GraphVisitor<EI> visitor) {
    dfsVisit(vertex, visitor, new BitSet(), true);
  }


  /** Private helper function for the bfs variants
   *
   *  Visit all vertices of this graph in a breadth first manner. The vertex
   *  list is traversed in order, and every non-visited vertex is then put onto
   *  a queue, consecutively. The desired functionality can be
   *  implemented by the given {@link GraphVisitor} argument.
   */
  private void bfsVisit(int startVertex, GraphVisitor<EI> visitor,
                        BitSet visited, boolean converse) {

    Queue<Integer> active = new LinkedList<Integer>();
    active.offer(startVertex);
    visited.set(startVertex);  // vertex gets gray
    visitor.discoverVertex(startVertex, this);
    while (active.peek() != null) {
      int vertex = active.poll();
      for (Edge<EI> outEdge : getOutEdges(vertex)) {
        int target = converse ? outEdge.getSource() : outEdge.getTarget();
        if (! visited.get(target)) {
          active.offer(target);
          visited.set(target);  // vertex gets gray
          visitor.discoverVertex(target, this);
          visitor.treeEdge(outEdge, this);
        } else {
          visitor.nonTreeEdge(outEdge, this);
        }
      }
      // vertex gets black
      visitor.finishVertex(vertex, this);
    }
  }

  /** Visit all vertices of this graph in a breadth first manner. The vertex
   *  list is traversed in order, and every non-visited vertex is then put onto
   *  a queue, consecutively. The desired functionality can be
   *  implemented by the given {@link GraphVisitor} argument.
   */
  public void bfs(GraphVisitor<EI> visitor) {
    BitSet visited = new BitSet(); // all bits are initially false
    for(int vertex = 0; vertex < _outEdges.size(); ++vertex) {
      if (! isDeletedVertex(vertex) && ! visited.get(vertex)) {
        visitor.startVertex(vertex, this);
        bfsVisit(vertex, visitor, visited, false);
      }
    }
  }

  /** Visit the vertices of this graph that are reachable from vertex in a
   *  breadth first manner.
   *  The desired functionality can be implemented by the given
   *  {@link GraphVisitor} argument.
   */
  public void bfs(int vertex, GraphVisitor<EI> visitor) {
    bfsVisit(vertex, visitor, new BitSet(), false);
  }

  /* Use converseLazy instead */

  /** Return the converse of this graph, i.e., if (u,v) \in E, then
   *  (v,u) \in E' (the converse), and the set of vertices is the same.
   *
  public DirectedGraph<EdgeInfo> converse() {
    DirectedGraph<EdgeInfo> result =
        new DirectedGraph<EdgeInfo>(getNumberOfVertices());
    for (EdgeContainer<EdgeInfo> out : _outEdges) {
      if (out != null) {
        for (Edge<EdgeInfo> edge : out) {
          result.newEdge(edge.getInfo(), edge.getTarget(), edge.getSource());
        }
      }
    }
    return result;
  }
  */

  /** Return the lazy converse of this graph, i.e., if (u,v) \in E, then
   *  this edge will be in outEdges of u, in the result of this function, the
   *  edge will be in outEdges of v, which means that source and target are
   *  inverted, i.e., edge.getSource() .getTarget() are the same as in the
   *  original graph, but turned around in the view of the converse, which means
   *  that for the converse, e.getTarget() is the *source* and e.getSoure() is
   *  the *target*, and the same edge objects are in both graphs, so *beware*.
   *
   *  This may save a lot of memory, but it needs careful thinking.
   */
  public DiGraph<EI> converseLazy() {
    DiGraph<EI> result =
        new DiGraph<EI>(getNumberOfVertices());
    for (EdgeContainer<EI> out : _outEdges) {
      if (out != null) {
        for (Edge<EI> edge : out) {
          int newSource = edge.getTarget();
          EdgeContainer<EI> newOut = result._outEdges.get(newSource);
          if (newOut == null) {
            newOut = newEdgeList();
            result._outEdges.set(newSource, newOut);
          }
          newOut.add(edge);
        }
      }
    }
    return result;
  }

  /** Returns the vertices in topological order, in case there are no cycles,
   *  null otherwise.
   */
  public List<Integer> topoSort(int start) throws CyclicGraphException {
    try {
      final List<Integer> result = new LinkedList<Integer>();
      dfs(start, new GraphVisitorAdapter<EI>() {
        private BitSet active = new BitSet();

        @Override
        public void discoverVertex(int v, DiGraph<EI> g) {
          active.set(v);
        }

        @Override
        public void finishVertex(int v, DiGraph<EI> g) {
          active.clear(v);
          result.add(0, v);
        }

        @Override
        public void nonTreeEdge(Edge<EI> e, DiGraph<EI> g) {
          if (active.get(e.getTarget()))
            throw new RuntimeException("Cycle");
        }
      });
      return result;
    }
    catch (RuntimeException rtex) {
      if (rtex.getMessage().equals("Cycle")) {
        throw new CyclicGraphException();
      } else {
        throw rtex;
      }
    }
  }
}

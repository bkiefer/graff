package de.dfki.lt.loot.digraph;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * <code>DirectedGraph</code> represents a directed graph. Vertices and edges
 * are managed in lists.
 *
 * @author Bernd Kiefer, DFKI
 * @author Joerg Steffen, DFKI
 * @version $Id$
 */
public class DirectedGraph<EdgeInfo> implements AbstractGraph<EdgeInfo> {

  /**
   * Contains the path to the dot.exe delivered with Graphviz.
   */
  private static final String WIN_DOT =
      "C:/Program Files (x86)/Graphviz 2.28/bin/dot.exe";

 /** For each vertex, this contains the outgoing and incoming edges of this
   *  graph.
   */
  protected ArrayList<EdgeContainer<EdgeInfo>> _outEdges;

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


  private static class SRRunnable implements Runnable {
    private Reader _r ;
    private StringBuilder _sb = new StringBuilder();

    public SRRunnable(InputStream in) { _r = new InputStreamReader(in); }

    public void run() {
      int c = 0;
      try {
        while ( (c = _r.read()) != -1)
          _sb.append((char)c);
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }
    }

    @Override
    public String toString() {
      return _sb.toString();
    }
  }

  /**
   * Creates a .gif or .png image for the given .dot graph.
   *
   * @param dotGraphPath
   *          the path to the .dot graph
   */
  public static void dot2png(Path dotGraphPath) {

    System.out.format("converting %s ..." , dotGraphPath);
    Process process;
    try {
      if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
        String command = String.format("%s -Tgif %2$s -o\"%2$s.gif\" -Kdot",
            WIN_DOT, dotGraphPath);
        process = Runtime.getRuntime().exec(command);
      } else {
        String[] command = { "sh", "-c",
            "dot -Tpng '" + dotGraphPath + "' -o'" + dotGraphPath + ".png' -Kdot"
        };
        process = Runtime.getRuntime().exec(command);
      }
      SRRunnable err = new SRRunnable(process.getErrorStream());
      SRRunnable out = new SRRunnable(process.getInputStream());
      Thread e = new Thread(err);
      e.run();
      Thread o = new Thread(out);
      o.run();
      System.out.println(" : " + process.waitFor() +
          "|" + err.toString() + "|" + out.toString());
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


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
      this.put(e.getTarget(), e);
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
      return this.get(to);
    }
  }

  public void forceSimpleGraph(boolean what) {
    _checkMultiEdges = what;
  }

  protected EdgeContainer<EdgeInfo> newEdgeList() {
    return _checkMultiEdges ? new SetEdgeContainer<EdgeInfo>() :
        new ArrayListEdgeContainer<EdgeInfo>();
  }

  protected EdgeContainer<EdgeInfo>
    getEdgeList(ArrayList<EdgeContainer<EdgeInfo>> edges, int vertex) {
    EdgeContainer<EdgeInfo> edgeList = edges.get(vertex);
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
  protected void setFrom(Edge<EdgeInfo> edge, int from) {
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
  protected void setTo(Edge<EdgeInfo> edge, int to) {
    edge.setTarget(to);
  }

  public boolean hasEdge(int from, int to) {
    return _outEdges.get(from).findTarget(to) != null;
  }

  /** This creates a new instance of <code>DirectedGraph</code>.
   */
  public DirectedGraph() {
    _outEdges = new ArrayList<EdgeContainer<EdgeInfo>>();
    _deletedVertices = new BitSet();
    _vertexPropertyMaps = new HashMap<String, VertexPropertyMap<?>>();
    _vertexBooleanPropertyMaps = new HashMap<String, BitSet>();
  }

  /** Create a new graph with n vertices and no edges */
  public DirectedGraph(int n) {
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

  public class VertexIterator {
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
    public int next() {
      int result = _current;
      ++_current;
      while (hasNext() && isDeletedVertex(_current)) {
        ++_current;
      }
      return result;
    }
  }

  /** This allows to iterate over all vertices of this graph */
  public VertexIterator vertices() {
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
  public Iterable<Edge<EdgeInfo>> getOutEdges(int vertex) {
    EdgeContainer<EdgeInfo> result = _outEdges.get(vertex);
    if (result == null)
      return Collections.emptyList();
    // return Collections.unmodifiableCollection(result);
    return result; // not nice, but needed for minimization
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
  public Edge<EdgeInfo> newEdge(EdgeInfo edgeInfo, int from, int to) {
    assert(isVertex(from) && isVertex(to));
    Edge<EdgeInfo> edge = new Edge<EdgeInfo>(edgeInfo, from, to);
    setFrom(edge, from);
    setTo(edge, to);
    return edge;
  }

  /** remove the given Edge */
  public void removeEdge(Edge<EdgeInfo> edge) {
    _outEdges.get(edge.getSource()).removeEdge(edge);
  }

  /** remove the given Edges */
  public void removeEdges(List<Edge<EdgeInfo>> edges) {
    for (Edge<EdgeInfo> edge : edges) {
      removeEdge(edge);
    }
  }

  /** This changes the to vertex of this edge to the given vertex.
   *
   * @param anEndVertex the new end vertex
   */
  public void changeEndVertex(Edge<EdgeInfo> edge, int anEndVertex) {
    // set new end vertex
    setTo(edge, anEndVertex);
  }


  /** This changes the from vertex of this edge to the given vertex.
   *
   * @param aStartVertex the new end vertex
   */
  public void changeStartVertex(Edge<EdgeInfo> edge, int aStartVertex) {
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
  public Edge<EdgeInfo> findEdge(
      int vertex,
      EdgeInfo anEdgeInfo,
      Comparator<EdgeInfo> comp) {

    for (Edge<EdgeInfo> edge : getOutEdges(vertex)) {
      if (comp.compare(edge.getInfo(), anEdgeInfo) == 0) {
        return edge;
      }
    }
    return null;
  }


  private class FilteredEdgeIterator
    implements Iterator<Edge<EdgeInfo>>, Iterable<Edge<EdgeInfo>> {

    final private EdgeInfo _info;
    final private Comparator<EdgeInfo> _comp;
    private Iterator<Edge<EdgeInfo>> _it;
    private Edge<EdgeInfo> _next;

    public FilteredEdgeIterator(int v, EdgeInfo i, Comparator<EdgeInfo> c) {
      _info = i;
      _comp = c;
      _it = getOutEdges(v).iterator();
      findNext();
    }

    private void findNext() {
      while (_it.hasNext()) {
        _next = _it.next();
        if (_comp.compare(_next.getInfo(), _info) == 0) return;
      }
      _next = null;
    }

    @Override
    public boolean hasNext() {
      return _next != null;
    }

    @Override
    public Edge<EdgeInfo> next() {
      Edge<EdgeInfo> result = _next;
      findNext();
      return result;
    }

    @Override
    public void remove() { throw new UnsupportedOperationException(); }

    @Override
    public Iterator<Edge<EdgeInfo>> iterator() { return this; }
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
  public Iterable<Edge<EdgeInfo>> findEdges(
      int vertex,
      EdgeInfo anEdgeInfo,
      Comparator<EdgeInfo> comp) {
    return new FilteredEdgeIterator(vertex, anEdgeInfo, comp);
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
  public List<Edge<EdgeInfo>> findEdges(
      EdgeInfo anEdgeInfo,
      Comparator<EdgeInfo> comp) {
    LinkedList<Edge<EdgeInfo>> result = new LinkedList<Edge<EdgeInfo>>();

    for (int vertex = 0; vertex < _outEdges.size(); ++vertex) {
      if (! isDeletedVertex(vertex)) {
        for (Edge<EdgeInfo> e : findEdges(vertex, anEdgeInfo, comp)) {
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
        EdgeContainer<EdgeInfo> out = _outEdges.get(v);
        if (out != null) {
          Iterator<Edge<EdgeInfo>> edgeIt = out.iterator();
          while (edgeIt.hasNext()) {
            Edge<EdgeInfo> edge = edgeIt.next();
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
    for (EdgeContainer<EdgeInfo> out : _outEdges) {
      if (out != null) {
        for (Edge<EdgeInfo> edge : out) {
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
      for (Edge<EdgeInfo> edge : getOutEdges(vertex)) {
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

    for ( VertexIterator it = vertices(); it.hasNext();) {
      strRep.append(toString(it.next(), propMap));
    }

    return strRep.toString();
  }

  /** This overrides @see java.lang.Object#toString().
   *
   * @return a <code>String</code> representation of this graph
   */
  @Override
  public String toString() {
    return this.toString(getPropertyMap("names"));
  }


  public String asMatrix() {
    StringBuilder sb = new StringBuilder();
    for(DirectedGraph<EdgeInfo>.VertexIterator it = vertices();
        it.hasNext();) {
      int vi = it.next();
      for(DirectedGraph<EdgeInfo>.VertexIterator jt = vertices();
          jt.hasNext();) {
        int vj = jt.next();
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
   *      {@link DirectedGraph}, which means that the edge direction is inverse,
   *      i.e., getSource() returns the target node, getTarget() the source
   */
  private void dfsVisit(int vertex,
                        GraphVisitor<EdgeInfo> visitor,
                        BitSet visited,
                        boolean converse) {
    visited.set(vertex);  // vertex gets gray
    visitor.discoverVertex(vertex, this);

    for (Edge<EdgeInfo> edge : getOutEdges(vertex)) {
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
  public void dfs(GraphVisitor<EdgeInfo> visitor) {
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
  public void dfs(int vertex, GraphVisitor<EdgeInfo> visitor) {
    dfsVisit(vertex, visitor, new BitSet(), false);
  }

  /** Visit the vertices of this graph that are reachable from vertex in a depth
   *  first manner.
   *  The desired functionality can be implemented by the given {@link
   *  GraphVisitor} argument.
   *  @param vertex the vertex to start the dfs at
   *  @param converse if true, the dfs runs on a lazy converse of a
   *       {@link DirectedGraph}, which means that the edge direction is inverse,
   *       i.e., getSource() returns the target node, getTarget() the source
   */
  public void dfsConverse(int vertex, GraphVisitor<EdgeInfo> visitor) {
    dfsVisit(vertex, visitor, new BitSet(), true);
  }


  /** Private helper function for the bfs variants
   *
   *  Visit all vertices of this graph in a breadth first manner. The vertex
   *  list is traversed in order, and every non-visited vertex is then put onto
   *  a queue, consecutively. The desired functionality can be
   *  implemented by the given {@link GraphVisitor} argument.
   */
  private void bfsVisit(int startVertex, GraphVisitor<EdgeInfo> visitor,
                        BitSet visited, boolean converse) {

    Queue<Integer> active = new LinkedList<Integer>();
    active.offer(startVertex);
    visited.set(startVertex);  // vertex gets gray
    visitor.discoverVertex(startVertex, this);
    while (active.peek() != null) {
      int vertex = active.poll();
      for (Edge<EdgeInfo> outEdge : getOutEdges(vertex)) {
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
  public void bfs(GraphVisitor<EdgeInfo> visitor) {
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
  public void bfs(int vertex, GraphVisitor<EdgeInfo> visitor) {
    bfsVisit(vertex, visitor, new BitSet(), false);
  }

  /**
   * This writes this graph in graphviz format to the given file so that it can
   * be processed with the graphviz package (http://www.graphviz.org/)
   *
   * @param fileName a <code>String</code> with the file name
   * @param printer a class with which special printing can be handled
   * @throws <code>IOException</code> if an error ccurs when writing the file
   */
  public void dotPrint(Path fileName, DirectedGraphPrinter<EdgeInfo> printer)
    throws IOException {

    PrintWriter out =
      new PrintWriter(Files.newBufferedWriter(
          fileName, Charset.defaultCharset()));
    out.println("digraph test { " + printer.getDefaultGraphAttributes());

    for (int node = 0; node < getNumberOfVertices(); ++node) {
      if (isVertex(node)) {
        printer.dotPrintNode(out, node);
      }
    }

    for (int node = 0; node < getNumberOfVertices(); ++node) {
      if (isVertex(node) && getOutEdges(node) != null) {
        for (Edge<EdgeInfo> edge : getOutEdges(node)) {
          printer.dotPrintEdge(out, edge);
        }
      }
    }
    out.println("}");
    out.close();
  }

  /**
   * This writes this graph in graphviz format to the given file so that it can
   * be processed with the graphviz package (http://www.graphviz.org/)
   *
   * @param fileName a <code>String</code> with the file name
   * @throws <code>IOException</code> if an error ccurs when writing the file
   */
  public void dotPrint(Path fileName) throws IOException {
    dotPrint(fileName, new SimplePrinter<EdgeInfo>(this));
  }

  /** Convert the graph into graphviz format and create a PNG graphic file
   *  out of that representation using the dot program.
   */
  public void printGraph(String name, DirectedGraphPrinter<EdgeInfo> printer) {
    try {
      Path filePath = new File("/tmp/" + name).toPath();
      dotPrint(filePath, printer);
      dot2png(filePath);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Convert the graph into graphviz format and create a PNG graphic file
   *  out of that representation using the dot program.
   */
  public void printGraph(String name) {
    try {
      Path filePath = new File("/tmp/" + name).toPath();
      dotPrint(filePath);
      dot2png(filePath);
    } catch (IOException e) {
      e.printStackTrace();
    }
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
  public DirectedGraph<EdgeInfo> converseLazy() {
    DirectedGraph<EdgeInfo> result =
        new DirectedGraph<EdgeInfo>(getNumberOfVertices());
    for (EdgeContainer<EdgeInfo> out : _outEdges) {
      if (out != null) {
        for (Edge<EdgeInfo> edge : out) {
          int newSource = edge.getTarget();
          EdgeContainer<EdgeInfo> newOut = result._outEdges.get(newSource);
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
      dfs(start, new GraphVisitorAdapter<EdgeInfo>() {
        private BitSet active = new BitSet();

        @Override
        public void discoverVertex(int v, DirectedGraph<EdgeInfo> g) {
          active.set(v);
        }

        @Override
        public void finishVertex(int v, DirectedGraph<EdgeInfo> g) {
          active.clear(v);
          result.add(0, v);
        }

        @Override
        public void nonTreeEdge(Edge<EdgeInfo> e, DirectedGraph<EdgeInfo> g) {
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

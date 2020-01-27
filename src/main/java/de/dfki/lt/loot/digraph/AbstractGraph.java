package de.dfki.lt.loot.digraph;

import java.util.*;

public abstract class AbstractGraph<EI> implements Graph<EI> {
  /** All VertexPropertyMaps registered with this DirectedGraph */
  protected HashMap<String, VertexPropertyMap<?>> _vertexPropertyMaps;

  protected HashMap<String, BitSet> _vertexBooleanPropertyMaps;

  protected AbstractGraph() {
    _vertexPropertyMaps = new HashMap<String, VertexPropertyMap<?>>();
    _vertexBooleanPropertyMaps = new HashMap<String, BitSet>();
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
   *      {@link Graph}, which means that the edge direction is inverse,
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
    for(int vertex = 0; vertex < getNumberOfVertices(); ++vertex) {
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
   *       {@link Graph}, which means that the edge direction is inverse,
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
    for(int vertex = 0; vertex < getNumberOfVertices(); ++vertex) {
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
}

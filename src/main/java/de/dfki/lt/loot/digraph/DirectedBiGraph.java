/*
 * Copyright 2019-2022 Jörg Steffen, Bernd Kiefer
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * ​https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package de.dfki.lt.loot.digraph;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;


/**
 * <code>DirectedGraph</code> represents a directed graph. Vertices and edges
 * are managed in lists.
 *
 * @author Bernd Kiefer, DFKI
 * @author Joerg Steffen, DFKI
 * @version $Id$
 */
public class DirectedBiGraph<EdgeInfo> extends DiGraph<EdgeInfo>
  implements AbstractBiGraph<EdgeInfo> {

 /** For each vertex, this contains the outgoing and incoming edges of this
   *  graph.
   */
  protected ArrayList<EdgeContainer<EdgeInfo>> _inEdges;

  /** This creates a new vertex. It either takes an available deleted vertex
   * or creates a completely new one.
   *
   * @return an empty vertex
   */
  @Override
  public int newVertex() {
    int v = super.newVertex();
    if (v >= _inEdges.size())
      _inEdges.add(null);
    return v;
  }


  /** Set the end vertex of edge and add it to the in edges of the new target.
   *  WARNING: the edge list of the old source is not changed. In most cases,
   *  doing this would result in a ConcurrentModificationException.
   *  The caller of this method must take care of removing the edge of the
   *  old edge list.
   *
   *  For secure changing of target node, @see changeEndVertex
   */
  @Override
  protected void setTo(Edge<EdgeInfo> edge, int to) {
    edge.setTarget(to);
    getEdgeList(_inEdges, to).add(edge);
  }

  /** This creates a new instance of <code>DirectedGraph</code>.
   */
  public DirectedBiGraph() {
    super();
    _inEdges = new ArrayList<EdgeContainer<EdgeInfo>>();
  }

  /** Create a new graph with n vertices and no edges */
  public DirectedBiGraph(int n) {
    super(n);
  }

  /** return the edges ending in vertex*/
  @Override
  public Iterable<Edge<EdgeInfo>> getInEdges(int vertex) {
    EdgeContainer<EdgeInfo> result = _inEdges.get(vertex);
    if (result == null)
      return Collections.emptyList();
    //return Collections.unmodifiableCollection(result);
    return result; // not nice, but needed for minimization
  }

  /** Return true if the node has at least one incoming edge. */
  public boolean hasInEdges(int vertex) {
    EdgeContainer<EdgeInfo> result = _inEdges.get(vertex);
    if (result == null) return false;
    return ! result.isEmpty();
  }

  /** remove the given Edge */
  @Override
  public void removeEdge(Edge<EdgeInfo> edge) {
    super.removeEdge(edge);
    _inEdges.get(edge.getTarget()).removeEdge(edge);
  }

  /** This changes the to vertex of this edge to the given vertex.
   *
   * @param anEndVertex the new end vertex
   */
  @Override
  public void changeEndVertex(Edge<EdgeInfo> edge, int anEndVertex) {
    // remove edge from old end vertex's incoming edge list
    _inEdges.get(edge.getTarget()).removeEdge(edge);

    // set new end vertex
    setTo(edge, anEndVertex);
  }


  /** This removes the given vertex from this directed graph.
   *
   * @param vertex the vertex to remove from the graph
   */
  @Override
  public void removeVertex(int vertex) {
    if (isVertex(vertex)) {
      // remove the edges from and to this vertex from other vertices
      // edge lists
      EdgeContainer<EdgeInfo> out = _outEdges.get(vertex);
      if (out != null) {
        for (Edge<EdgeInfo> edge : out) {
          _inEdges.get(edge.getTarget()).removeEdge(edge);
        }
        _outEdges.set(vertex, null);
      }
      EdgeContainer<EdgeInfo> in = _inEdges.get(vertex);
      if (in != null) {
        for (Edge<EdgeInfo> edge : in) {
          _outEdges.get(edge.getSource()).removeEdge(edge);
        }
        _inEdges.set(vertex, null);
      }
      super.removeVertexLazy(vertex);
    }
  }

  /** For this kind of graph, this is the same as remove, since there is no
   *  gain in not removing the edges.
   *
   * @param vertex the vertex to remove from the graph
   */
  @Override
  public void removeVertexLazy(int vertex) {
    removeVertex(vertex);
  }

  /** Remove all edges pointing to a deleted vertex */
  public void cleanupEdges() {
    // a no-op, see above
  };

  @Override
  protected void compactRenumber(int[] newNumber) {
    // remove the obsolete buckets
    for (int i = 0; i < _deletedVertices.cardinality(); ++i) {
      _inEdges.remove(_inEdges.size() - 1);
    }

    super.compactRenumber(newNumber);
  }


  @Override
  protected void moveEdgeContainer(int source, int target) {
    super.moveEdgeContainer(source, target);
    _inEdges.set(target, _inEdges.get(source));
  }

  @Override
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
      strRep.append(" == in  ===== ").append(newline);
      for (Edge<EdgeInfo> edge : getInEdges(vertex)) {
        strRep.append(edge.toString(propMap)).append(newline);
      }
    }
    return strRep.toString();
  }


  /** Recursive helper function for the dfs methods */
  private void dfsVisitInverse(int vertex,
                        GraphVisitor<EdgeInfo> visitor,
                        BitSet visited) {
    visited.set(vertex);  // vertex gets gray
    visitor.discoverVertex(vertex, this);

    for (Edge<EdgeInfo> edge : getInEdges(vertex)) {
      int target = edge.getTarget();
      // is the target vertex white?
      if (! visited.get(target)) {
        visitor.treeEdge(edge, this);
        dfsVisitInverse(target, visitor, visited);
      } else {
        visitor.nonTreeEdge(edge, this);
      }
    }
    // vertex gets black
    visitor.finishVertex(vertex, this);
  }

  /** Visit all vertices of this graph in a depth first manner. The vertex list
   *  is traversed in order, and every non-visited vertex is then taken as
   *  start vertex, consecutively. This dfs works on the inverse of the graph,
   *  i.e., it uses the incoming instead of the outgoing edges.
   *  The desired functionality can be implemented by the given {@link
   *  GraphVisitor} argument.
   */
  public void dfsInverse(GraphVisitor<EdgeInfo> visitor) {
    BitSet visited = new BitSet();
    for(int vertex = 0; vertex < _outEdges.size(); ++vertex) {
      if (! isDeletedVertex(vertex) && ! visited.get(vertex)) {
        visitor.startVertex(vertex, this);
        dfsVisitInverse(vertex, visitor, visited);
      }
    }
  }

  /** Visit the vertices of this graph that are reachable from vertex in a depth
   *  first manner. This dfs works on the inverse of the graph,
   *  i.e., it uses the incoming instead of the outgoing edges.
   *  The desired functionality can be implemented by the given {@link
   *  GraphVisitor} argument.
   */
  public void dfsInverse(int vertex, GraphVisitor<EdgeInfo> visitor) {
    dfsVisitInverse(vertex, visitor, new BitSet());
  }


  /** Private helper function for the bfs variants
   *
   *  Visit all vertices of this graph in a breadth first manner. The vertex
   *  list is traversed in order, and every non-visited vertex is then put onto
   *  a queue, consecutively. This bfs works on the inverse of the graph,
   *  i.e., it uses the incoming instead of the outgoing edges.
   *  The desired functionality can be implemented by the given
   *  {@link GraphVisitor} argument.
   */
  private void bfsVisitInverse(int startVertex, GraphVisitor<EdgeInfo> visitor,
                        BitSet visited) {

    Queue<Integer> active = new LinkedList<Integer>();
    active.offer(startVertex);
    visited.set(startVertex);  // vertex gets gray
    visitor.discoverVertex(startVertex, this);
    while (active.peek() != null) {
      int vertex = active.poll();
      for (Edge<EdgeInfo> outEdge : getInEdges(vertex)) {
        int target = outEdge.getTarget();
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
   *  a queue, consecutively. This bfs works on the inverse of the graph,
   *  i.e., it uses the incoming instead of the outgoing edges.
   *  The desired functionality can be implemented by the given
   *  {@link GraphVisitor} argument.
   */
  public void bfsInverse(GraphVisitor<EdgeInfo> visitor) {
    BitSet visited = new BitSet(); // all bits are initially false
    for(int vertex = 0; vertex < _outEdges.size(); ++vertex) {
      if (! isDeletedVertex(vertex) && ! visited.get(vertex)) {
        visitor.startVertex(vertex, this);
        bfsVisitInverse(vertex, visitor, visited);
      }
    }
  }

  /** Visit the vertices of this graph that are reachable from vertex in a
   *  breadth first manner.This bfs works on the inverse of the graph,
   *  i.e., it uses the incoming instead of the outgoing edges.
   *
   *  The desired functionality can be implemented by the given
   *  {@link GraphVisitor} argument.
   */
  public void bfsInverse(int vertex, GraphVisitor<EdgeInfo> visitor) {
    bfsVisitInverse(vertex, visitor, new BitSet());
  }

}

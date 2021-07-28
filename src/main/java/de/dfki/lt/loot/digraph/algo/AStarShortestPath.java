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

package de.dfki.lt.loot.digraph.algo;

import java.util.*;
import java.util.function.Function;

import de.dfki.lt.loot.digraph.*;
import de.dfki.lt.loot.digraph.weighted.OrderedMonoid;
import de.dfki.lt.loot.jada.FibonacciHeapBase;
import de.dfki.lt.loot.jada.FibonacciHeapBase.HeapNode;

public class AStarShortestPath <EdgeInfo, T> {

  // stores the real distance from the start point for every node that has been
  // visited
  private VertexPropertyMap<T> realDistance;

  // stores the estimated distance from the start point for every node that has
  // been visited (put onto the queue)
  private VertexPropertyMap<T> estimatedDistance;

  // stores backpointers for the currently best edge backwards in the direction
  // to the start vertex for any visited node. Can be used to read off the
  // shortest path in case there is one found
  private VertexPropertyMap<Edge<EdgeInfo>> predecessor;

  /** @param h is the estimation function how expensive it will be to reach the
   *         target from the given vertex
   */
  private Integer shortestPath(Graph<EdgeInfo> g, int startVertex,
      final OrderedMonoid<T> ops, final Function<Edge<EdgeInfo>, T> getWeight,
      VertexBooleanPropertyMap endVertices, final Function<Integer, T> h) {
    predecessor = new VertexListPropertyMap<Edge<EdgeInfo>>(g);
    realDistance = new VertexListPropertyMap<T>(g);
    estimatedDistance = new VertexListPropertyMap<T>(g);
    VertexPropertyMap<HeapNode<Integer>> heapNodes =
        new VertexListPropertyMap<HeapNode<Integer>>(g);

    Comparator<Integer> compareVertices =
      new Comparator<Integer>() {
      public int compare(Integer vertex1, Integer vertex2) {
        T dist1 = estimatedDistance.get(vertex1);
        T dist2 = estimatedDistance.get(vertex2);
        if (dist1 == null) {
          return (dist2 == null) ? 0 : 1;
        }
        if (dist2 == null) return -1;
        return ops.compare(dist1, dist2);
      }
    };

    FibonacciHeapBase<Integer> q =
      new FibonacciHeapBase<Integer>(compareVertices);

    // We have two distance maps, one for the "real", and the other for the
    // estimated cost and the heap must consider the estimated cost for
    // comparison (see https://en.wikipedia.org/wiki/A*_search_algorithm)

    realDistance.put(startVertex, ops.getZero());
    // must be h(start) instead of zero
    estimatedDistance.put(startVertex, h.apply(startVertex));
    heapNodes.put(startVertex, q.insert(startVertex));

    while (! q.isEmpty()) {
      HeapNode<Integer> minNode = q.minimum();
      int vertex = q.getValue(minNode);
      q.removeMinimum();
      heapNodes.remove(vertex);
      // stop prematurely if a final vertex is reached
      if (endVertices.get(vertex)) {
        return vertex;
      }

      T vertexDistance = realDistance.get(vertex);
      if (vertexDistance == null)
        break; // unreachable node, we're finished
      for(Edge<EdgeInfo> outEdge : g.getOutEdges(vertex)) {
        T alt = ops.add(vertexDistance, getWeight.apply(outEdge));

        // might be applied to a undirected graph (edges inversed)
        int target = outEdge.getTargetForSource(vertex);

        if (realDistance.get(target) == null
            || ops.compare(alt, realDistance.get(target)) < 0) {
          realDistance.put(target, alt);
          estimatedDistance.put(target, ops.add(alt, h.apply(target)));
          HeapNode<Integer> targetHeapNode = heapNodes.get(target);
          if (targetHeapNode == null) {
            heapNodes.put(target, q.insert(target));
          } else {
            q.decreaseKey(targetHeapNode);
          }
          predecessor.put(target, outEdge);
        }
      }
    }
    return null;
  }

  /** Return the shortest path between startVertex and endVertex */
  public List<Edge<EdgeInfo>> shortestPath(Graph<EdgeInfo> g,
      int startVertex, int endVertex, OrderedMonoid<T> weightOps,
      Function<Edge<EdgeInfo>, T> getWeight, final Function<Integer, T> h) {
    List<Integer> end = new ArrayList<Integer>(1);
    end.add(endVertex);
    return shortestPath(g, startVertex, end, weightOps, getWeight, h);
  }

  /** Return the shortest paths to any vertex in the graph (?) */
  public List<Edge<EdgeInfo>> shortestPath(Graph<EdgeInfo> g,
      int startVertex, OrderedMonoid<T> weightOps,
      Function<Edge<EdgeInfo>, T> getWeight, final Function<Integer, T> h) {
    List<Integer> end = Collections.emptyList();
    return shortestPath(g, startVertex, end, weightOps, getWeight, h);
  }

  /** Return the shortest path starting at startVertex and ending in one of the
   *  nodes contained in endVertices.
   */
  public List<Edge<EdgeInfo>> shortestPath(Graph<EdgeInfo> g,
      int startVertex, List<Integer> endVertices,
      OrderedMonoid<T> weightOps, Function<Edge<EdgeInfo>, T> getWeight,
      final Function<Integer, T> h) {
    VertexBooleanPropertyMap end = new VertexBooleanPropertyMap(g);
    for (int finalVertex : endVertices) end.put(finalVertex, true);
    Integer shortest = shortestPath(g, startVertex, weightOps, getWeight, end, h);

    List<Edge<EdgeInfo>> result = new LinkedList<Edge<EdgeInfo>>();
    // is there a connection from start to end: distance < infinity?
    if (shortest != null) {
      int last = shortest;
      Edge<EdgeInfo> curr = predecessor.get(shortest);
      while (curr != null) {
        result.add(0, curr);
        // might be the reverse edge for an undirected graph
        curr = predecessor.get(last = curr.getSourceForTarget(last));
      }
    }
    return result;
  }
}

package de.dfki.lt.loot.digraph.algo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import de.dfki.lt.loot.digraph.AbstractGraph;
import de.dfki.lt.loot.digraph.Edge;
import de.dfki.lt.loot.digraph.VertexBooleanPropertyMap;
import de.dfki.lt.loot.digraph.VertexListPropertyMap;
import de.dfki.lt.loot.digraph.VertexPropertyMap;
import de.dfki.lt.loot.digraph.weighted.OrderedMonoid;
import de.dfki.lt.loot.digraph.weighted.WeightFunction;
import de.dfki.lt.loot.jada.FibonacciHeapBase;
import de.dfki.lt.loot.jada.FibonacciHeapBase.HeapNode;

public class DijkstraShortestPath<EdgeInfo, T> {

  // stores the distance from the start point for every node that has been
  // visited (put onto the queue)
  private VertexPropertyMap<T> distance;

  // stores backpointers for the currently best edge backwards in the direction
  // to the start vertex for any visited node. Can be used to read off the
  // shortest path in case there is one found
  private VertexPropertyMap<Edge<EdgeInfo>> predecessor;

  private Integer shortestPath(AbstractGraph<EdgeInfo> g, int startVertex,
      final OrderedMonoid<T> ops, final WeightFunction<EdgeInfo, T> getWeight,
      VertexBooleanPropertyMap endVertices) {
    predecessor = new VertexListPropertyMap<Edge<EdgeInfo>>(g);
    distance = new VertexListPropertyMap<T>(g);
    VertexPropertyMap<HeapNode<Integer>> heapNodes =
        new VertexListPropertyMap<HeapNode<Integer>>(g);

    Comparator<Integer> compareVertices =
      new Comparator<Integer>() {
      public int compare(Integer vertex1, Integer vertex2) {
        T dist1 = distance.get(vertex1);
        T dist2 = distance.get(vertex2);
        if (dist1 == null) {
          return (dist2 == null) ? 0 : 1;
        }
        if (dist2 == null) return -1;
        return ops.compare(dist1, dist2);
      }
    };

    FibonacciHeapBase<Integer> q =
      new FibonacciHeapBase<Integer>(compareVertices);

    distance.put(startVertex, ops.getZero());
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

      T vertexDistance = distance.get(vertex);
      if (vertexDistance == null)
        break; // unreachable node, we're finished
      for(Edge<EdgeInfo> outEdge : g.getOutEdges(vertex)) {
        T alt = ops.add(vertexDistance, getWeight.get(outEdge));

        int target = outEdge.getTarget();

        if (distance.get(target) == null
            || ops.compare(alt, distance.get(target)) < 0) {
          distance.put(outEdge.getTarget(), alt);
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


  public List<Edge<EdgeInfo>> shortestPath(AbstractGraph<EdgeInfo> g,
      int startVertex, int endVertex, OrderedMonoid<T> weightOps,
      WeightFunction<EdgeInfo, T> getWeight) {
    List<Integer> end = new ArrayList<Integer>(1);
    end.add(endVertex);
    return shortestPath(g, startVertex, end, weightOps, getWeight);
  }

  public List<Edge<EdgeInfo>> shortestPath(AbstractGraph<EdgeInfo> g,
      int startVertex, OrderedMonoid<T> weightOps,
      WeightFunction<EdgeInfo, T> getWeight) {
    List<Integer> end = Collections.emptyList();
    return shortestPath(g, startVertex, end, weightOps, getWeight);
  }

  public List<Edge<EdgeInfo>> shortestPath(AbstractGraph<EdgeInfo> g,
      int startVertex, List<Integer> endVertices,
      OrderedMonoid<T> weightOps, WeightFunction<EdgeInfo, T> getWeight) {
    VertexBooleanPropertyMap end = new VertexBooleanPropertyMap(g);
    for (int finalVertex : endVertices) end.put(finalVertex, true);
    Integer shortest = shortestPath(g, startVertex, weightOps, getWeight, end);

    List<Edge<EdgeInfo>> result = new LinkedList<Edge<EdgeInfo>>();
    // is there a connection from start to end: distance < infinity?
    if (shortest != null) {
      Edge<EdgeInfo> curr = predecessor.get(shortest);
      while (curr != null) {
        result.add(0, curr);
        curr = predecessor.get(curr.getSource());
      }
    }
    return result;
  }


  public VertexPropertyMap<T> getDistance() {
    return distance;
  }

  public VertexPropertyMap<Edge<EdgeInfo>> getPredecessor() {
    return predecessor;
  }

}

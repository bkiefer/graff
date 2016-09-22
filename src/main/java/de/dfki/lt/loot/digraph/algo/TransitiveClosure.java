package de.dfki.lt.loot.digraph.algo;

import static de.dfki.lt.loot.digraph.algo.SccReduction.acyclicSccReduction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.dfki.lt.loot.digraph.DiGraph;
import de.dfki.lt.loot.digraph.Edge;
import de.dfki.lt.loot.digraph.VertexListPropertyMap;
import de.dfki.lt.loot.digraph.VertexPropertyMap;

public class TransitiveClosure {
  /** Recursive helper function for the acyclicClosure method */
  private static <EdgeInfo> void dfsVisit(DiGraph<EdgeInfo> graph,
      VertexPropertyMap<Set<Integer>> closure, int vertex) {
    Set<Integer> myClosure = new HashSet<Integer>();
    closure.put(vertex, myClosure);

    for (Edge<EdgeInfo> edge : graph.getOutEdges(vertex)) {
      int target = edge.getTarget();
      // is the target vertex white?
      if (closure.get(target) == null) {
        dfsVisit(graph, closure, target);
      }
      myClosure.add(target);
      myClosure.addAll(closure.get(target));
    }
  }

  /** Transitive closure on an acyclic graph, implemented with DFS.
   *  @return the closure as VertexPropertyMap, mapping each vertex to all its
   *          reachable nodes.
   */
  public static <EdgeInfo> VertexPropertyMap<Set<Integer>>
  acyclicClosure(DiGraph<EdgeInfo> graph) {
    VertexPropertyMap<Set<Integer>> closure =
        new VertexListPropertyMap<Set<Integer>>(graph);
    for(int vertex = 0; vertex < graph.getNumberOfVertices(); ++vertex) {
      if (! graph.isDeletedVertex(vertex) && closure.get(vertex) == null) {
        dfsVisit(graph, closure, vertex);
      }
    }
    return closure;
  }

  /*
  public static <EdgeInfo> void
  destructiveAcyclicReduction(DirectedGraph<EdgeInfo> graph,
      List<List<Integer>> components){
    VertexPropertyMap<Integer> representatives =
        new VertexListPropertyMap<Integer>(graph);
    for (List<Integer> component : components) {
      int representative = component.get(0);
      for (int i = 0; i < component.size(); ++i) {
        representatives.put(i, representative);
      }
    }
    for (List<Integer> component : components) {
      int representative = component.get(0);
      Iterator<Edge<EdgeInfo>> edgeIt =
          graph.getOutEdges(representative).iterator();
      while (edgeIt.hasNext()) {
        Edge<EdgeInfo> edge = edgeIt.next();
        if (component.contains(edge.getTarget())) {
          edgeIt.remove();
        } else {
          graph.changeEndVertex(edge, representatives.get(edge.getTarget()));
        }
      }
      for (int i = 1; i < component.size(); ++i) {
        int vertex = component.get(i);
        edgeIt = graph.getInEdges(vertex).iterator();
        while (edgeIt.hasNext()) {
          edgeIt.next();
          edgeIt.remove();
        }
        edgeIt = graph.getOutEdges(vertex).iterator();
        while (edgeIt.hasNext()) {
          Edge<EdgeInfo> edge = edgeIt.next();
          if (! component.contains(edge.getTarget())) {
            graph.newEdge(edge.getInfo(), representative,
                representatives.get(edge.getTarget()));
          }
          edgeIt.remove();
        }
      }
    }
  }

  /** Transitive closure on an cyclic graph. First compute the strongly
   *  connected components, reduce the graph to its acyclic residue, do the
   *  transitive closure on the acyclic part, and add the proper values to all
   *  non-representatives of the original graph.
   *
   *  @return the closure as VertexPropertyMap, mapping each vertex to all its
   *          reachable nodes.
   *
  public static  <EdgeInfo> VertexPropertyMap<Set<Integer>>
  destructiveTransitiveClosure(DirectedGraph<EdgeInfo> graph) {
    // first compute the strongly connected components
    TarjanVisitor<EdgeInfo> visitor = new TarjanVisitor<EdgeInfo>();
    graph.dfs(visitor);
    // now do a destructive acyclic reduction of the graph
    List<List<Integer>> components = visitor.getSCCs();
    destructiveAcyclicReduction(graph, components);
    // now do transitive closure on the acyclic graph
    VertexPropertyMap<Set<Integer>> closure = acyclicClosure(graph);
    // and extend it to the components
    for (List<Integer> component : components) {
      // add all component vertices to the closure
      closure.get(component.get(0)).addAll(component);
    }
    for (List<Integer> component : components) {
      Set<Integer> myClosure = closure.get(component.get(0));
      List<Integer> toIterate = new ArrayList<Integer>(myClosure.size());
      toIterate.addAll(myClosure);
      for (int rep: toIterate) {
        myClosure.addAll(closure.get(rep));
      }

      // for all non-representative nodes, set the closure to the same set as
      // for the representative
      for (int v : component) {
        closure.put(v, myClosure);
      }
    }
    return closure;
  }
  */


  /** Transitive closure on an cyclic graph. First compute the strongly
   *  connected components, reduce the graph to its acyclic residue, do the
   *  transitive closure on the acyclic part, and add the proper values to all
   *  non-representatives of the original graph.
   *
   *  @return the closure as VertexPropertyMap, mapping each vertex to all its
   *          reachable nodes.
   */
  @SuppressWarnings("unchecked")
  public static <EdgeInfo> VertexPropertyMap<Set<Integer>>
  transitiveClosure(DiGraph<EdgeInfo> graph, boolean reflexive) {
    DiGraph<EdgeInfo> reduction = acyclicSccReduction(graph);

    // now do transitive closure on the acyclic graph
    VertexPropertyMap<Set<Integer>> redClosure = acyclicClosure(reduction);
    VertexPropertyMap<List<Integer>> origSccs =
        (VertexPropertyMap<List<Integer>>) reduction.getPropertyMap("originalSCCs");
    //VertexPropertyMap<Integer> redRepresentatives =
    //    (VertexPropertyMap<Integer>) graph.getPropertyMap("redRepresentatives");
    // and extend it to the components
    VertexPropertyMap<Set<Integer>> closure =
        new VertexListPropertyMap<Set<Integer>>(graph);
    // v is the representative of the SCC we're working on
    for (int v = 0; v < reduction.getNumberOfVertices(); ++v) {
      List<Integer> component = origSccs.get(v);
      int srcRepresentative = component.get(0);
      // add all component vertices to the closure
      Set<Integer> srcClosure = new HashSet<Integer>();
      closure.put(srcRepresentative, srcClosure);
      // when the graph is simple trivial components don't reach themselves
      if (component.size() > 1 || reflexive) {
        srcClosure.addAll(component);
      }
      for (int redVertex : redClosure.get(v)) {
        srcClosure.addAll(origSccs.get(redVertex));
      }
      // for all non-representative nodes, set the closure to the same set as
      // for the representative
      for (int i = 1; i < component.size(); ++i) {
        closure.put(component.get(i), srcClosure);
      }
    }
    return closure;
  }

  public static <EdgeInfo>
  void warshallTransitiveClosure(DiGraph<EdgeInfo> g) {
    // for (int k = 0; k < n; k++)
    //  for (int i = 0; i < n; i++)
    //    for (int j = 0; j < n; j++)
    //      R[i,j] = R[i,j] || (R[i,k] && R[k,j]);

    for(int k : g) {
      // if (! g.hasEdge(vi, vi)) g.newEdge(null, vi, vi);
      for(int i : g) {
        for (int j : g){
          if (! g.hasEdge(i, j))
            if (g.hasEdge(i, k) && g.hasEdge(k, j))
              g.newEdge(null, i, j);
        }
      }
    }
  }
  
}

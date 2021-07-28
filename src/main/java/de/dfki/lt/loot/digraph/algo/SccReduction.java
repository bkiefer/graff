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

import de.dfki.lt.loot.digraph.DiGraph;
import de.dfki.lt.loot.digraph.Edge;
import de.dfki.lt.loot.digraph.VertexListPropertyMap;
import de.dfki.lt.loot.digraph.VertexPropertyMap;
import java.util.List;

/** Computes the acyclic residue of a graph containing cycles.
 *  All strongly connected components are reduced to a single node.
 *
 * @author kiefer
 */
public class SccReduction {

  /** This function reduces a cyclic graph to its acyclic residue, collapsing
   *  all strongly connected components to single vertices
   * @param graph
   * @return the acyclic residue, and a VertexPropertyMap that is registered
   *    with the original graph and relates the vertices of the old graph to
   *    the (representative) vertices of the reduced graph, as well as a
   *    VertexPropertyMap that relates the vertices of the reduction to the
   *    SCCs of the original graph.
   *
   *    The returned graph may not be simple, i.e., contain multiple edges from
   *    one node u into another node v.
   */
  public static <EdgeInfo> DiGraph<EdgeInfo>
  acyclicSccReduction(DiGraph<EdgeInfo> graph){
    // first compute the strongly connected components
    TarjanVisitor<EdgeInfo> visitor = new TarjanVisitor<EdgeInfo>();
    graph.dfs(visitor);
    List<List<Integer>> components = visitor.getSCCs();
    // acyclic reduction of graph
    DiGraph<EdgeInfo> reduction = new DiGraph<EdgeInfo>();
    // map new vertices to old vertices
    VertexPropertyMap<Integer> orig2redRep =
        new VertexListPropertyMap<Integer>(graph);
    //graph.register("redRepresentatives", orig2redRep);
    VertexPropertyMap<List<Integer>> componentsMap =
        new VertexListPropertyMap<List<Integer>>(reduction);
    reduction.register("originalSCCs", componentsMap);
    // create all vertices in the reduced graph and associate them with the old
    // graph
    for (List<Integer> component : components) {
      int redVertex = reduction.newVertex();
      for (int v : component)
        orig2redRep.put(v, redVertex);
      componentsMap.put(redVertex, component);
    }
    for (List<Integer> component : components) {
      int srcRepresentative = component.get(0);
      int redVertex = orig2redRep.get(srcRepresentative);
      for (Edge<EdgeInfo> edge : graph.getOutEdges(srcRepresentative)) {
        if (! component.contains(edge.getTarget())) {
          // find the representative for the target vertex
          reduction.newEdge(edge.getInfo(),
              redVertex, orig2redRep.get(edge.getTarget()));
        }
      }
      for (int i = 1; i < component.size(); ++i) {
        int vertex = component.get(i);
        for (Edge<EdgeInfo> edge : graph.getOutEdges(vertex)) {
          if (! component.contains(edge.getTarget())) {
            reduction.newEdge(edge.getInfo(),
                redVertex, orig2redRep.get(edge.getTarget()));
          }
        }
      }
    }
    return reduction;
  }
}

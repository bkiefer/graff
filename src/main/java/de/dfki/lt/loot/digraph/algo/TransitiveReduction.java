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
import de.dfki.lt.loot.digraph.VertexBooleanPropertyMap;
import de.dfki.lt.loot.digraph.VertexListPropertyMap;
import de.dfki.lt.loot.digraph.VertexPropertyMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author kiefer
 */
public class TransitiveReduction {

  /**
   * {@link TransitiveReduction} changes an <b>acyclic</b> graph such that all
   * edges (a,c) are removed when (a, b) and (b, c) are edges of the graph.
   *
   * @param <EdgeInfo> ignored here
   *
   * @author Bernd Kiefer, DFKI
   * @version $Id$
   */
  public static <EdgeInfo> void transitiveReduction(DiGraph<EdgeInfo> graph){
    // closure in the end contains all nodes that can be reached from a certain
    // vertex, e.g., the transitive closure
    VertexPropertyMap<List<Integer>> closure = new VertexListPropertyMap<>(graph);

    VertexBooleanPropertyMap reached = new VertexBooleanPropertyMap(graph);

    for (int v : graph) {
      List<Integer> newList = new ArrayList<>();
      newList.add(v);
      closure.put(v, newList);
    }

    // iterate over the nodes in the hierarchy in topological order. Because
    // to the true (inverse) argument to the constructor, *top* is the last
    // vertex in the vector
    TopoOrderVisitor<EdgeInfo> topoVisitor = new TopoOrderVisitor<>(true);
    graph.dfs(topoVisitor);
    List<Integer> topoRevList = topoVisitor.getSortedVertices();

    // store the reverse topo number of each vertex in this array: we need this to
    // compare the edges. vertices closer to *top* get bigger numbers
    final VertexPropertyMap<Integer> topoPos = new VertexListPropertyMap<>(graph);
    int i = 0;
    for (Iterator<Integer> topoReverseIt = topoRevList.iterator();
        topoReverseIt.hasNext(); ++i) {
      topoPos.put(topoReverseIt.next(), i);
    }

    for (Iterator<Integer> topoReverseIt = topoRevList.iterator();
        topoReverseIt.hasNext();) {
      int v = topoReverseIt.next();
      reached.clear();
      reached.set(v);

      ArrayList<Edge<EdgeInfo>> target = new ArrayList<>();
      for(Edge<EdgeInfo> e : graph.getOutEdges(v)) {
        target.add(e);
      }
      Collections.sort(target, new Comparator<Edge<EdgeInfo>>() {
        @Override
        public int compare(Edge<EdgeInfo> o1, Edge<EdgeInfo> o2) {
          // TODO: CHECK THAT IT'S NOT THE OTHER WAY ROUND
          return topoPos.get(o2.getTarget()) - topoPos.get(o1.getTarget());
        }
      });

      List<Integer> vClosure = closure.get(v);
      for(Edge<EdgeInfo> e : target) {
        int w = e.getTarget();
        if (! reached.get(w)) {
          List<Integer> wClosure = closure.get(w);
          for (int z : wClosure) {
            if (! reached.get(z)) {
              reached.set(z);
              vClosure.add(z);
            }
          }
        } else {
          // remove edge from v to w
          graph.removeEdge(e);
        }
      }
    }
  }


}

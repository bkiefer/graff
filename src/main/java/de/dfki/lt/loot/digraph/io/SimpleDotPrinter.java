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

package de.dfki.lt.loot.digraph.io;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import de.dfki.lt.loot.digraph.Edge;
import de.dfki.lt.loot.digraph.Graph;
import de.dfki.lt.loot.digraph.VertexBooleanPropertyMap;
import de.dfki.lt.loot.digraph.VertexPropertyMap;

/** Class to print a directed graph in graphviz syntax. If a VertexPropertyMap
 *  "names" is registered with the graph, it is used to print the node labels,
 *  otherwise, the nodes are just points.
 *
 *  Edges are printed including the edge info.
 *
 */
public class SimpleDotPrinter<T> implements GraphElementPrinter<T> {
  @SuppressWarnings("unused")
  private Graph<T> _graph;
  private VertexPropertyMap<String> _nodeNames;
  
  public static String HIGHLIGHT = ", color=\"red\"";

  private Set<Integer> _nodesToHighlight = new HashSet<>();
  private Set<Edge<T>> _edgesToHighlight = new HashSet<>();

  @SuppressWarnings("unchecked")
  public void startGraph(PrintWriter out, Graph<T> graph) {
    _graph = graph;
    _nodeNames = (VertexPropertyMap<String>) graph.getPropertyMap("names");
    out.println("digraph test { ");
  }

  public void endGraph(PrintWriter out) {
    out.println("}");
    _graph = null;
    _nodeNames = null;
  }

  public void highlightNode(int node) {
    _nodesToHighlight.add(node);
  }
  
  public void highlightEdge(Edge<T> edge) {
    _edgesToHighlight.add(edge);
  }
  
  public void printNode(PrintWriter out, int node) {
    out.print("n" + node);
    String hl = _nodesToHighlight.contains(node) ? HIGHLIGHT : "";
    if (_nodeNames != null) {
      out.print(
          " [label=\"" + _nodeNames.get(node) + "(" + node + ")\" " + hl + "]");
    } else {
      out.print(" [shape=point " + hl + "]");
    }
    out.println(";");
  }

  public void printEdge(PrintWriter out, Edge<T> edge) {
    out.print("n"
        + edge.getSource()
        + " -> n"
        + edge.getTarget()
        + "[ label= ");
    out.print("\"" + edge.getInfo() +"\"");
    if (_edgesToHighlight.contains(edge)) {
      out.print(" " + HIGHLIGHT);
    }
    out.println("];");
  }
}

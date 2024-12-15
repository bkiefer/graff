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
  private Graph<T> _graph;
  private VertexPropertyMap<String> _nodeNames;

  private boolean addNodeNumberToName = false;

  public static String HIGHLIGHT = ", color=\"red\"";

  private VertexBooleanPropertyMap _nodesToHighlight;
  private Set<Edge<T>> _edgesToHighlight = new HashSet<>();

  private String _preamble = "layout=neato;\n"
      + "overlap=false;\n"
      + "splines=true;\n"
      + "mode=KK;\n"
      + "//Damping=.5;\n"
      + "node [shape=\"box\"];";

  @Override
  @SuppressWarnings("unchecked")
  public void startGraph(PrintWriter out, Graph<T> graph) {
    _graph = graph;
    _nodeNames = (VertexPropertyMap<String>) graph.getPropertyMap("names");
    _nodesToHighlight = new VertexBooleanPropertyMap(_graph);

    out.println("digraph test { ");
    out.println(_preamble);
  }

  @Override
  public void endGraph(PrintWriter out) {
    out.println("}");
    _graph = null;
    _nodeNames = null;
  }

  public void addNodeNumberToName(boolean val) {
    addNodeNumberToName = val;
  }

  public void setPreamble(String s) {
    _preamble = s;
  }

  public void highlightNode(int node) {
    _nodesToHighlight.set(node);
  }

  public void highlightEdge(Edge<T> edge) {
    _edgesToHighlight.add(edge);
  }

  private String escapeDoubleQuotes(String s) {
    return s.replaceAll("\"", "\\\\\"");
  }

  @Override
  public void printNode(PrintWriter out, int node) {
    out.print("n" + node);
    if (_nodeNames != null) {
      String name = escapeDoubleQuotes(_nodeNames.get(node));
      if (addNodeNumberToName) {
        name += "(" + node + ")";
      }
      out.print(" [label=\"" + name + "\"");
    } else {
      out.print(" [shape=point");
    }
    if (_nodesToHighlight.get(node)) {
      out.print(HIGHLIGHT);
    }
    out.println("];");
  }

  @Override
  public void printEdge(PrintWriter out, Edge<T> edge) {
    out.print("n" + edge.getSource() + " -> n" + edge.getTarget()
        + "[ label=\"");
    out.print(escapeDoubleQuotes(edge.getInfo().toString()));
    out.print('"');
    if (_edgesToHighlight.contains(edge)) {
      out.print(HIGHLIGHT);
    }
    out.println("];");
  }
}

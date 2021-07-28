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

package de.dfki.lt.loot.fsa;

import java.io.PrintWriter;
import java.util.ArrayList;

import de.dfki.lt.loot.digraph.Edge;
import de.dfki.lt.loot.digraph.Graph;
import de.dfki.lt.loot.digraph.VertexPropertyMap;
import de.dfki.lt.loot.digraph.io.GraphElementPrinter;

class FsaDotPrinter<EdgeInfo> implements GraphElementPrinter<EdgeInfo> {
  public String defaultNodeAttributes =
      "shape=circle, width=.6, fixedsize=true";

  private FiniteAutomaton<EdgeInfo> _graph;
  private VertexPropertyMap<String> _nodeNames;
  public String defaultEdgeAttributes = null;

  @SuppressWarnings("unchecked")
  @Override
  public void startGraph(PrintWriter out, Graph<EdgeInfo> graph) {
    _graph = (FiniteAutomaton<EdgeInfo>)graph;
    _nodeNames = (VertexPropertyMap<String>) _graph.getPropertyMap("names");
    out.println("digraph test { rankdir=LR; splines=polyline;");
  }

  private String escapeForDot(String in) {
    String out = in.replaceAll("([^\\\\])\"", "$1\\\\\"");
    return (out.charAt(0) == '"') ? "\\" + out : out;
  }

  public void endGraph(PrintWriter out) {
    out.println("}");
    _graph = null;
    _nodeNames = null;
  }

  /**
   * Print a state of the FSA, taking into account special printing for start
   * and final states.
   */
  public void printNode(PrintWriter out, int node) {
    out.print("n" + node);
    ArrayList<String> attribs = new ArrayList<String>();
    if (defaultNodeAttributes != null)
      attribs.add(defaultNodeAttributes);
    if (_nodeNames != null)
      attribs.add(" label=\"" + _nodeNames.get(node) +"\"");
    if (node == _graph._initialState)
      attribs.add(" color=green ");
    if (_graph.isFinalState(node))
      attribs.add(" style=filled, fillcolor=red ");

    if (!attribs.isEmpty()) {
      out.print("[");
      for (int i = 0; i<attribs.size(); ++i){
        if (i > 0) out.print(",");
        out.print(attribs.get(i));
      }
      out.print("]");
    }
    out.println(";");
  }

  /** Print a FSA transition */
  public void printEdge(PrintWriter out, Edge<EdgeInfo> edge) {
    EdgeInfo transChar = edge.getInfo();
    out.print("n" + edge.getSource() + " -> n" + edge.getTarget()
              + "[ ");
    if (defaultEdgeAttributes != null) {
      out.print(defaultEdgeAttributes + ", ");
    }
    if (_graph.isEpsilon(transChar)) {
      out.println("label=\"\u03B5\", fontcolor=red];");
    }
    else {
      out.println("label=\"" + escapeForDot(transChar.toString()) + "\"];");
    }
  }
}
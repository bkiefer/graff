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

import de.dfki.lt.loot.digraph.Edge;
import de.dfki.lt.loot.digraph.Graph;
import de.dfki.lt.loot.digraph.io.GraphElementPrinter;

/**
 * This writes this graph in vcg format so that it can be processed with the
 * VCG tool (http://rw4.cs.uni-sb.de/users/sander/html/gsvcg1.html)
 */
public class FsaVcgPrinter<EdgeInfo> implements GraphElementPrinter<EdgeInfo> {
  private FiniteAutomaton<EdgeInfo> _graph;

  @Override
  public void printNode(PrintWriter out, int node) {
    if (_graph.isVertex(node)) {
      out.print("node: { title: \"" + node + "\" ");
      if (node == _graph._initialState) {
        out.print("color: green ");
      }
      if (_graph.isFinalState(node)) {
        out.print("bordercolor: red ");
      }
      out.println("}");
    }
  }

  @Override
  public void printEdge(PrintWriter out, Edge<EdgeInfo> edge) {
    EdgeInfo transChar = edge.getInfo();
    out.println("edge: { sourcename: \"" + edge.getSource()
        + "\" targetname: \"" + edge.getTarget()
        + "\" label: ");
    if (_graph.isEpsilon(transChar)) {
      out.println("\"e\" textcolor:red }");
    }
    else {
      out.println("\"" + edge.getInfo() + "\" }");
    }
  }

  @Override
  public void startGraph(PrintWriter out, Graph<EdgeInfo> graph) {
    _graph = (FiniteAutomaton<EdgeInfo>)graph;
    out.println("graph: { orientation: left_to_right display_edge_labels: yes");
  }

  @Override
  public void endGraph(PrintWriter out) {
    out.println("}");
  }

}

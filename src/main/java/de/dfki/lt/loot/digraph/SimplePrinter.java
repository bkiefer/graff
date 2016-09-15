package de.dfki.lt.loot.digraph;

import java.io.PrintWriter;

/** Class to print a directed graph in graphviz syntax. If a VertexPropertyMap
 *  "names" is registered with the graph, it is used to print the node labels,
 *  otherwise, the nodes are just points.
 *
 *  Edges are printed including the edge info.
 *
 */
public class SimplePrinter<T> implements DirectedGraphPrinter<T> {
  @SuppressWarnings("unused")
  private DiGraph<T> _graph;
  private VertexPropertyMap<String> _nodeNames;

  @SuppressWarnings("unchecked")
  public SimplePrinter(DiGraph<T> graph) {
    _graph = graph;
    _nodeNames = (VertexPropertyMap<String>) graph.getPropertyMap("names");
  }

  public String getDefaultGraphAttributes() { return ""; }

  public  void dotPrintNode(PrintWriter out, int node) {
    out.print("n" + node);
    if (_nodeNames != null) {
      out.print(" [label=\"" + _nodeNames.get(node) + "(" +  node + ")\"]");
    } else {
      out.print(" [shape=point]");
    }
    out.println(";");
  }

  public void dotPrintEdge(PrintWriter out, Edge<T> edge) {
    out.print("n"
        + edge.getSource()
        + " -> n"
        + edge.getTarget()
        + "[ label= ");
    out.println("\"" + edge.getInfo() +"\"];");
  }
}

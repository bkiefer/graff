package de.dfki.lt.loot.digraph.io;

import java.io.PrintWriter;

import de.dfki.lt.loot.digraph.Edge;
import de.dfki.lt.loot.digraph.Graph;
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

  public void printNode(PrintWriter out, int node) {
    out.print("n" + node);
    if (_nodeNames != null) {
      out.print(" [label=\"" + _nodeNames.get(node) + "(" +  node + ")\"]");
    } else {
      out.print(" [shape=point]");
    }
    out.println(";");
  }

  public void printEdge(PrintWriter out, Edge<T> edge) {
    out.print("n"
        + edge.getSource()
        + " -> n"
        + edge.getTarget()
        + "[ label= ");
    out.println("\"" + edge.getInfo() +"\"];");
  }
}

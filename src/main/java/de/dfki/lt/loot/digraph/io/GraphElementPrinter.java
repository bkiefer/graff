package de.dfki.lt.loot.digraph.io;

import java.io.PrintWriter;

import de.dfki.lt.loot.digraph.Edge;
import de.dfki.lt.loot.digraph.Graph;

public interface GraphElementPrinter<EdgeInfo> {
  void printNode(PrintWriter out, int node);

  void printEdge(PrintWriter out, Edge<EdgeInfo> edge);

  void startGraph(PrintWriter out, Graph<EdgeInfo> graph);

  void endGraph(PrintWriter out);
}

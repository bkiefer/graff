package de.dfki.lt.loot.digraph;

import java.io.PrintWriter;

public interface DirectedGraphPrinter<EdgeInfo> {
  public abstract void dotPrintNode(PrintWriter out, int node);

  public abstract void dotPrintEdge(PrintWriter out, Edge<EdgeInfo> edge);

  public abstract String getDefaultGraphAttributes();
}

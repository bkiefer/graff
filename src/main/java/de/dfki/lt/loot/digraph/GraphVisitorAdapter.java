package de.dfki.lt.loot.digraph;

public class GraphVisitorAdapter<EdgeInfo> implements GraphVisitor<EdgeInfo> {

  @Override
  public void discoverVertex(int v, DirectedGraph<EdgeInfo> g) {}

  @Override
  public void finishVertex(int v, DirectedGraph<EdgeInfo> g) {}

  @Override
  public void nonTreeEdge(Edge<EdgeInfo> e, DirectedGraph<EdgeInfo> g) {}

  @Override
  public void startVertex(int v, DirectedGraph<EdgeInfo> g) {}

  @Override
  public void treeEdge(Edge<EdgeInfo> e, DirectedGraph<EdgeInfo> g) {}

}

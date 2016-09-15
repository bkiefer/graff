package de.dfki.lt.loot.digraph;

public class GraphVisitorAdapter<EdgeInfo> implements GraphVisitor<EdgeInfo> {

  @Override
  public void discoverVertex(int v, DiGraph<EdgeInfo> g) {}

  @Override
  public void finishVertex(int v, DiGraph<EdgeInfo> g) {}

  @Override
  public void nonTreeEdge(Edge<EdgeInfo> e, DiGraph<EdgeInfo> g) {}

  @Override
  public void startVertex(int v, DiGraph<EdgeInfo> g) {}

  @Override
  public void treeEdge(Edge<EdgeInfo> e, DiGraph<EdgeInfo> g) {}

}

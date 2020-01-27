package de.dfki.lt.loot.digraph;

public class GraphVisitorAdapter<EdgeInfo> implements GraphVisitor<EdgeInfo> {

  @Override
  public void discoverVertex(int v, Graph<EdgeInfo> g) {}

  @Override
  public void finishVertex(int v, Graph<EdgeInfo> g) {}

  @Override
  public void nonTreeEdge(Edge<EdgeInfo> e, Graph<EdgeInfo> g) {}

  @Override
  public void startVertex(int v, Graph<EdgeInfo> g) {}

  @Override
  public void treeEdge(Edge<EdgeInfo> e, Graph<EdgeInfo> g) {}

}

package de.dfki.lt.loot.digraph;

public class CollectTimesVisitor<EdgeInfo> implements GraphVisitor<EdgeInfo> {
  private VertexPropertyMap<Integer> discovery, finish;
  
  private int time;
  
  public CollectTimesVisitor() {
    time = 1;
  }
  
  public void startVertex(int v, DirectedGraph<EdgeInfo> g) {
    if (discovery == null) {
      discovery = new VertexListPropertyMap<Integer>(g);
      finish = new VertexListPropertyMap<Integer>(g);
    }
  }
  
  public void discoverVertex(int v, DirectedGraph<EdgeInfo> g) {
    discovery.put(v, time);
    ++time;
  }
  
  public void finishVertex(int v, DirectedGraph<EdgeInfo> g) {
    finish.put(v, time);
    ++time;
  }
  
  public void treeEdge(Edge<EdgeInfo> e, DirectedGraph<EdgeInfo> g) {}
  
  public void nonTreeEdge(Edge<EdgeInfo> e, DirectedGraph<EdgeInfo> g) {}
  
  VertexPropertyMap<Integer> getDiscoveryMap() {
    return discovery;
  }
  
  VertexPropertyMap<Integer> getFinishMap() {
    return finish;
  }
  
}

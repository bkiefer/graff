package de.dfki.lt.loot.digraph.algo;

import de.dfki.lt.loot.digraph.Graph;
import de.dfki.lt.loot.digraph.Edge;
import de.dfki.lt.loot.digraph.GraphVisitor;
import de.dfki.lt.loot.digraph.VertexListPropertyMap;
import de.dfki.lt.loot.digraph.VertexPropertyMap;

public class BfsTimesVisitor<EdgeInfo> implements GraphVisitor<EdgeInfo> {
  private VertexPropertyMap<Integer> discovery;

  public BfsTimesVisitor() {
  }

  public void startVertex(int v, Graph<EdgeInfo> g) {
    if (discovery == null) {
      discovery = new VertexListPropertyMap<Integer>(g);
    }
    discovery.put(v, 0);
    // System.out.println("Start Vertex " + v + " " + time);
  }

  public void discoverVertex(int v, Graph<EdgeInfo> g) {
    // System.out.println("Discover Vertex " + v + " " + time);
  }

  public void finishVertex(int v, Graph<EdgeInfo> g) {
    // System.out.println("Finish Vertex " + v + " " + time);
  }

  public void treeEdge(Edge<EdgeInfo> e, Graph<EdgeInfo> g) {
    //System.out.println("Put on Queue " + e.getTarget() + " " + time);
    discovery.put(e.getTarget(), discovery.get(e.getSource()) + 1);
  }

  public void nonTreeEdge(Edge<EdgeInfo> e, Graph<EdgeInfo> g) {}

  VertexPropertyMap<Integer> getDiscoveryMap() {
    return discovery;
  }
}

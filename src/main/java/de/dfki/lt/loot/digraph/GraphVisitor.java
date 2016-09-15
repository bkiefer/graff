package de.dfki.lt.loot.digraph;

public interface GraphVisitor<EdgeInfo> {

  /** In the BFS/DFS variants that visits each vertex of the graph, this method
   *  is called for the start vertices where the real search begins.
   */
  void startVertex(int v, DiGraph<EdgeInfo> g);

  /** This method is called when a vertex turns from white to gray */
  void discoverVertex(int v, DiGraph<EdgeInfo> g);

  /** This method is called when a vertex turns from gray to black */
  void finishVertex(int v, DiGraph<EdgeInfo> g);

  /** This method is called when a tree edge is used */
  void treeEdge(Edge<EdgeInfo> e, DiGraph<EdgeInfo> g);

  /** This method is called when a non tree edge is used */
  void nonTreeEdge(Edge<EdgeInfo> e, DiGraph<EdgeInfo> g);

}

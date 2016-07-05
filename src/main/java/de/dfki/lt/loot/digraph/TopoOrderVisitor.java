package de.dfki.lt.loot.digraph;

import java.util.ArrayList;
import java.util.List;


/**
 * {@link TopoOrderVisitor} implements the {@link DfsGraphVisitor} interface to
 * calculate the topological order of the vertices of a graph.
 *
 * @param <EdgeInfo>
 *          the kind of information assigned to the edges of the traversed graph
 *
 * @author Joerg Steffen, DFKI
 * @version $Id$
 */
public class TopoOrderVisitor<EdgeInfo> extends GraphVisitorAdapter<EdgeInfo> {

  /**
   * Here we collect the sorted vertices.
   */
  private List<Integer> sortedVertices;

  /**
   * Creates a new instance of {@link TopoOrderVisitor}.
   */
  public TopoOrderVisitor() {
    this.sortedVertices = new ArrayList<>();
  }


  /**
   * Returns the topological sorted vertices after the visitor is finished.
   *
   * @return the list of topological sorted vertices
   */
  public List<Integer> getSortedVertices() {
    return this.sortedVertices;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void finishVertex(int v, DirectedGraph<EdgeInfo> g) {

    // add a finished vertex at the start of the list
    this.sortedVertices.add(0, v);
  }
}

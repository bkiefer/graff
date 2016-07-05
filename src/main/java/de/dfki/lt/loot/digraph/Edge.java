package de.dfki.lt.loot.digraph;

/**
 * <code>Edge</code> represents an edge in the directed graph
 * This is just a small container object, kept as small as possible.
 *
 * @author Bernd Kiefer, DFKI
 * @author Joerg Steffen, DFKI
 * @version $Id$
 */
public class Edge<EdgeInfo> {

  /** This contains the info assigned to the edge. */
  private EdgeInfo _info;

  /** This contains the start vertex of the edge. */
  private int _from;

  /** This contains the end vertex of the edge. */
  private int _to;

  /**
   * This creates a new instance of <code>Edge</code> and adds it
   * to the appropriate vertex's edge lists. This constructor has default
   * visibility because only {@link #newEdge(EdgeInfo, int, int)}
   * should be able to create a new edge instance.
   *
   * @param anEdgeInfo an <code>EdgeInfo</code> with the info assigned to
   *                   this edge
   * @param from       source node of this edge
   * @param to         target node of this edge
   */
  Edge(EdgeInfo anEdgeInfo, int from, int to) {
    _info = anEdgeInfo;
    _from = from;
    _to = to;
  }

  /** returns the info assigned to this edge.
   * @return a <code>EdgeInfo</code> with the info
   */
  public EdgeInfo getInfo() {
    return this._info;
  }

  /** returns the start vertex of the edge.
   * @return the start vertex
   */
  public int getSource() {
    return this._from;
  }

  /** returns the end vertex of the edge.
   * @return the end vertex
   */
  public int getTarget() {
    return this._to;
  }

  /** sets the info assigned to this edge.
   * @return a <code>EdgeInfo</code> with the info
   */
  public void setInfo(EdgeInfo anEdgeInfo) {
    this._info = anEdgeInfo;
  }

  /** returns the start vertex of the edge. Only for package (friends)
   * @return the start vertex
   */
  void setSource(int startVertex) {
    this._from = startVertex;
  }

  /** returns the end vertex of the edge. Only for package (friends)
   * @return the end vertex
   */
  void setTarget(int endVertex) {
    this._to = endVertex;
  }

  /** This overrides @see java.lang.Object#toString().
   *
   * @return a <code>String</code> with the representation of this edge
   */
  public String toString(VertexPropertyMap<?> vertexNames) {
    if (vertexNames == null) {
      return toString();
    }
    StringBuilder strRep = new StringBuilder();
    strRep.append(vertexNames.get(_from)).append("-")
      .append(_info).append("->").append(vertexNames.get(_to));
    return strRep.toString();
  }

  /** This overrides @see java.lang.Object#toString().
   *
   * @return a <code>String</code> with the representation of this edge
   */
  @Override
  public String toString() {
    StringBuilder strRep = new StringBuilder();
    strRep.append(_from).append("-").append(_info).append("->").append(_to);
    return strRep.toString();
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object o) {
    if (! (o instanceof Edge))
      return false;
    Edge<EdgeInfo> e = (Edge<EdgeInfo>) o;
    if (_from == e._from && _to == e._to) {
      return (_info == null || e._info == null) ? _info == e._info
          : _info.equals(e._info);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return (_from << 3 + _to) << 3 + _info.hashCode();
  }

}

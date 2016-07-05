package de.dfki.lt.loot.fsa.algo;

import java.util.BitSet;
import java.util.Comparator;
import java.util.TreeSet;

import de.dfki.lt.loot.digraph.AbstractGraph;
import de.dfki.lt.loot.digraph.Edge;
import de.dfki.lt.loot.fsa.AbstractAutomaton;

public class MinimizationBrzowski {


  /**
   * This propagates a non-equivalence between two nodes (identified by their
   * given indices in the given array) in the given matrix.
   *
   * @param nodes an array of <code>Vertex</code>s
   * @param i an <code>int</code> with the index of the first node
   * @param j an <code>int</code> with the index of the second node
   * @param equivalent a <code>boolean</code> matrix that contains a
   * <code>false</code> at matrix[i][j] (if i > j; vice versa otherwise)
   */
  private static <EdgeInfo> void propagate(AbstractAutomaton<EdgeInfo> graph,
    AbstractGraph<EdgeInfo> converse,
    Comparator<EdgeInfo> comp, int i, int j, BitSet notEquivalent) {

    int nodesLength = graph.getNumberOfVertices();

    if (converse.getOutEdges(i) == null || converse.getOutEdges(j) == null) return;

    // iterate over incoming edges of node i
    for (Edge<EdgeInfo> iEdge : converse.getOutEdges(i)) {

      // get transition char
      EdgeInfo iTrans = iEdge.getInfo();
      // get start node
      int iStartVertex = iEdge.getSource();

      // search for incoming edges of node j with the same transition char
      for (Edge<EdgeInfo> jEdge : converse.getOutEdges(j)) {

        // get transition char
        EdgeInfo jTrans = jEdge.getInfo();

        // we can skip the incoming edges with smaller edge info
        int compResult = comp.compare(jTrans, iTrans);
        // if (compResult < 0) { continue; }

        if (compResult == 0) {
          // get start node
          int jStartVertex = jEdge.getSource();
          // switch start node indices, so that iStartVertex always contains the
          // larger index; required because we use a LOWER triangular matrix
          if (iStartVertex < jStartVertex) {
            int temp = iStartVertex;
            iStartVertex = jStartVertex;
            jStartVertex = temp;
          }
          // check if the start nodes are marked as equivalen; if yes, mark them
          // as non-equivalent and propagate the change
          int index = jStartVertex * nodesLength + iStartVertex;
          if (! notEquivalent.get(index)) {
            notEquivalent.set(index);
            propagate(graph, converse, comp,
                iStartVertex, jStartVertex, notEquivalent);
          }
          continue;
        }

        // when we're here, this means that jTrans > iTrans; we can then stop
        // searching since the edges are sorted
        // break;
      }
    }
  }

  /**
   * This checks if two edge lists are "equal", i.e., have the same length and
   * the same defined/undefined transitions.
   *
   * Since the lists are sorted, we only have to traverse them in parallel.
   *
   * @param edgeList1 a <code>List</code> with edges
   * @param edgeList2 a <code>List</code> with edges
   * @return a <code>boolean</code> indicating if the edge lists are equals
   */
  private static <EdgeInfo> boolean equalEdgesByLabel(
    Comparator<EdgeInfo> comp,
    Iterable<Edge<EdgeInfo>> edgeList1,
    Iterable<Edge<EdgeInfo>> edgeList2) {

    TreeSet<EdgeInfo> edges1 = new TreeSet<EdgeInfo>(comp);
    for (Edge<EdgeInfo> edge : edgeList1) {
      edges1.add(edge.getInfo());
    }

    for (Edge<EdgeInfo> edge : edgeList2) {
      if (! edges1.remove(edge.getInfo())) return false;
    }
    // no mismatch found, so the lists are equal
    return edges1.isEmpty();
  }

  /**
   * This computes the equvalent classes from the given nodes.
   *
   * @param nodes an array of <code>Vertex</code>s
   * @return an array of <code>int</code>s that contains the node number of the
   * equivalence class representative for each node; if representative[i] == i,
   * node i is one of the representatives; if representative[i] == j, then node
   * j is the representative of the equivalence class i belongs to
   */
  private static <EdgeInfo> int[] computeEquivalentVertices(
    AbstractAutomaton<EdgeInfo> graph, Comparator<EdgeInfo> comp) {

    int nodesLength = graph.getNumberOfVertices();
    BitSet notEquivalent = new BitSet(nodesLength * nodesLength);
    AbstractGraph<EdgeInfo> converse = graph.converseLazy();

    // check pairs of nodes for equivalence: they are not equivalent if:
    // - one of them is a final node and the other is not
    // - they have different outgoing edges
    for (int i = 0; i < nodesLength; i++) {
      if (graph.isVertex(i)) {
        // we can skip the case j == i since each node is obviously equivalent
        // with itself
        for (int j = 0; j < i; j++) {
          if (graph.isVertex(j)) {
            if (graph.isFinalState(i) != graph.isFinalState(j) ||
                ! equalEdgesByLabel(comp, graph.getOutEdges(i), graph.getOutEdges(j))){
              // nodes are not equivalent
              notEquivalent.set(j * nodesLength + i);
              // propagate the new found states difference through the rest of the
              // graph
              propagate(graph, converse, comp, i, j, notEquivalent);
            }
          }
        }
      }
    }

    /*
    // print matrix
    System.out.print("   ");
    for (int i = 0; i < nodes.length; i++) {
      System.out.format("%-3d", i);
    }
    System.out.println();
    for (int i = 0; i < nodes.length; i++) {
      System.out.format("%-3d", i);
      for (int j = 0; j <= i ; j++) {
        System.out.print(! notEquivalent.get(j * nodes.length + i) ? "X  ": "O  ");
      }
      System.out.println();
    }
    */

    // from the matrix, compute the representatives for each node:
    // this is the minimal node with which it is equivalent
    int[] representative = new int[nodesLength];
    representative[0] = 0;
    for (int i = 0; i < nodesLength; i++) {
      if (graph.isVertex(i)) {
        int j = 0;
        while (j <= i
               && graph.isVertex(j)
               && notEquivalent.get(j * nodesLength + i)) {
          j++ ;
        }
        representative[i] = j;
      }
    }

    /*
    // print representatives
    System.out.println();
    for (int i = 0; i< nodes.length; i++) {
      System.out.print(representative[i] + " ");
    }
    System.out.println();
    */
    return representative;
  }

  /**
   * This checks if two edge lists are "equal", i.e., have the same length and
   * the same defined/undefined transitions.
   *
   * Since the lists are sorted, we only have to traverse them in parallel.
   *
   * @param edgeList1 a <code>List</code> with edges
   * @param edgeList2 a <code>List</code> with edges
   * @return a <code>boolean</code> indicating if the edge lists are equals
   *
   * requires edge list sorting, which is why it is not used at the moment.
  private boolean
  equalEdges(List<Edge<EdgeInfo>> edgeList1, List<Edge<EdgeInfo>> edgeList2) {

    // if the lists differ in size, we are already finished
    if (edgeList1 == null || edgeList2 == null) {
      return (edgeList1 == edgeList2);
    } else {
      if (edgeList1.size() != edgeList2.size()) {
        return false;
      }
    }

    // iterate in parallel over both lists
    Iterator<Edge<EdgeInfo>> edgeIter2 = edgeList2.iterator();

    for (Edge<EdgeInfo> edge1 : edgeList1) {
      // return false when the first mismatch is found
      if (edge1.getEdgeInfo() != edgeIter2.next().getEdgeInfo()){
        return false;
      }
    }

    // no mismatch found, so the lists are equal
    return true;
  }
   */


  /**
   * This sorts the node's edges according to their transition character
   * requires edge containers to be lists, which is why it is not used
   * currently.
  private static <EdgeInfo> void sortEdges(final AbstractAutomaton<EdgeInfo> graph) {
    // renumber nodes and sort all edge lists according to the transition
    // character
    Comparator<Edge<EdgeInfo>> edgeComp = new Comparator<Edge<EdgeInfo>>() {
      public int compare(Edge<EdgeInfo> edge1, Edge<EdgeInfo> edge2) {
        return graph.compare(edge1.getInfo(), edge2.getInfo());
      }
    };
    for (int i = 0, iMax = graph.getNumberOfVertices(); i < iMax; ++i) {
      if (graph.isVertex(i)) {
        // sort the edge lists
        if (! graph.getInEdges(i).isEmpty()) {
          Collections.sort(graph.getInEdges(i), edgeComp);
        }
        if (! graph.getOutEdges(i).isEmpty()) {
          Collections.sort(graph.getOutEdges(i), edgeComp);
        }
      }
    }
  }
  */

  /**
   *  This computes the minimal automaton equivalent to this one.
   *
   *  The current automaton is turned destructively into its minimal
   *  counterpart. Only use this method on a determinized automaton, which can
   *  be created with {@link #determinize()}.
   */
  public static <EdgeInfo> void minimize(
    AbstractAutomaton<EdgeInfo> graph, Comparator<EdgeInfo> comp) {

    // sort edges
    //sortEdges(graph);

    // this array will hold the node number of the equivalence class
    // representative for each node; if representative[i] == i, node i is one
    // of the representatives; if representative[i] == j, then node j is the
    // representative of the equivalence class i belongs to
    int[] representative = computeEquivalentVertices(graph, comp);

    Minimization.reduceAutomaton(graph, representative, comp);
  }


}

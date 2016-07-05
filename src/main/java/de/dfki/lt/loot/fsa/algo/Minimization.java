package de.dfki.lt.loot.fsa.algo;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import de.dfki.lt.loot.digraph.AbstractGraph;
import de.dfki.lt.loot.digraph.Edge;
import de.dfki.lt.loot.fsa.AbstractAutomaton;

public class Minimization {

  static <EdgeInfo> void reduceAutomaton(AbstractAutomaton<EdgeInfo> fsa,
    int[] representative, Comparator<EdgeInfo> comp) {
    // the new initial state is the representative of the equivalence class to
    // which the old initial state belongs
    fsa.setInitialState(representative[fsa.getInitialState()]);

    // adjust the automaton according to the equivalence classes
    for (int i = 0; i < representative.length; i++) {
      if (fsa.isVertex(i)) {
        if (representative[i] == i) {
          if (fsa.getOutEdges(i) != null) {
            // change the target nodes of all edges that start at a
            // representative node and end in a non-representative node
            for (Edge<EdgeInfo> edge : fsa.getOutEdges(i)) {
              int targetVertex = edge.getTarget();
              // if the target node is non-representative, change the edge
              // target to the corresponding representative node
              if (representative[targetVertex] != targetVertex) {
                fsa.changeEndVertex(edge, representative[targetVertex]);
              }
            }
          }
        }
      }
    }
    // delete all non-representative nodes from the graph
    for (int i = 0; i < fsa.getNumberOfVertices(); i++) {
      if (fsa.isVertex(i) && representative[i] != i) {
        fsa.removeVertexLazy(i);
      }
    }
    // remove all edges pointing to deleted vertices
    fsa.cleanupEdges();
  }

  /** Translate this automaton into an automaton with the minimal possible
   *  number of states.
   *
   *  References for this algorithm
   *  Hopcroft "An n log n algorithm for minimizing the states in a finite
   *            automaton" ('71)
   *  Gries "Describing an algorithm by Hopcroft" ('73)
   *  Watson "A taxonomy of finite automata minimization algorithms" ('93)
   *  Naming of the variables etc. follows the Watson paper, Algorithm 4.8
   *
   * The algorithm in pseudo code:

1  Partition <- {F , F^c} // The initial partition
2  for all a \in Alphabet do
3    Add((min(F , F^c), a), Wait) // The initial waiting set
4  while Wait not empty do
5    (W , a) <- TakeSome(Wait)         // takes some splitter in W and remove it
6    for each P in Partition which is split by (W , a) do
7       P' , P'' <- (W , a)|P                 // Compute the split
8       Replace P by P' and P'' in Partition  // Refine the partition
9       for all b in Alphabet do              // Update the waiting set
10        if (P, b) in Wait then
11          Replace (P, b) by (P', b) and (P'', b) in W
12        else
13          Add((min(P', P''), b), W)

How to find out if p is splittable by W, a:
  Find s, t from p where delta(p,a) in W and delta(q, a) not in W

   */
  public static <EdgeInfo> void minimize(
    AbstractAutomaton<EdgeInfo> graph, Comparator<EdgeInfo> comp) {
    // the result is a partition of the vertices, and we need two efficient
    // views for these subsets:
    // a) is state v in set p : use the classNo
    // b) iterate over the elements of p : use P[classNo]
    // and since the split operations are working destructively on the
    // lists in P, elements of P are changed automatically in the right way

    final int noStates = graph.getNumberOfVertices();
    // view a) the classNo is the ID of the partition a vertex is in. The
    // classNo[noStates] takes care of the fail state for non-total FSAs
    int[] classNo = new int[noStates + 1] ;

    // view b) List no. i contains all the vertices of partition set i
    ArrayList<List<Integer>> Partition = new ArrayList<List<Integer>>();

    // all Q sets are represented by their classNo in L
    @SuppressWarnings("unchecked")
    SortedSet<EdgeInfo>[] Wait = new SortedSet[noStates + 2];
    // Set<Pair<Integer, EdgeInfo>> L = new HashSet<Pair<Integer, EdgeInfo>>();

    int addToL = 0;
    {
      // Pseudocode line 1
      List<Integer> finalStates = new LinkedList<Integer>();
      List<Integer> nonFinalStates = new LinkedList<Integer>();

      for(int v = 0; v < classNo.length; ++v) {
        if (graph.isVertex(v)) {
          if (graph.isFinalState(v)) {
            finalStates.add(v);
            classNo[v] = 0;
          } else {
            nonFinalStates.add(v);
            classNo[v] = 1;
          }
        }
      }
      List<Integer> dead = new LinkedList<Integer>();
      dead.add(noStates); // the fail state
      classNo[noStates] = 2;

      Partition.add(finalStates);    // final states
      Partition.add(nonFinalStates); // nonfinal states
      Partition.add(dead);
      if (finalStates.size() > nonFinalStates.size()) {
        addToL = 1;
      }
    }

    // Pseudocode lines 2-3, Wait takes the role of W.
    int firstInWait = addToL, lastInWait = 2;
    Wait[addToL] = new TreeSet<EdgeInfo>(comp);
    Wait[2] = new TreeSet<EdgeInfo>(comp);
    for (EdgeInfo c : graph.getAlphabet()) {
      Wait[2].add(c);
      Wait[addToL].add(c);
    }

    AbstractGraph<EdgeInfo> converseGraph = graph.converseLazy();
    BitSet D = new BitSet();
    do {
      // get next from Wait:
      while (firstInWait <= lastInWait &&
          (Wait[firstInWait] == null || Wait[firstInWait].isEmpty()))
        ++firstInWait;
      if (firstInWait > lastInWait) break;
      int WclassNo = firstInWait;
      List<Integer> W = Partition.get(WclassNo);
      EdgeInfo a = Wait[firstInWait].first();
      Wait[firstInWait].remove(a);

      // the only states Q that might be splittable according to `a' are those
      // where q \in Q and p \in delta^-1(Q1, a)
      D.clear();
      for (int wstate : W) {
        if (wstate == noStates)
          // What are the in edges of the dead state? The missing outedges
          // of other vertices
          for (int v = 0; v < noStates; ++v) {
            if (graph.isVertex(v) && graph.findEdge(v, a, comp) == null)
              D.set(classNo[v]);
          }
        else
          // the outedges of convGraph are the inEdges of graph
          for (Edge<EdgeInfo> inEdge : converseGraph.getOutEdges(wstate)) {
            if (comp.compare(inEdge.getInfo(), a) == 0) {
              // since it's a lazy converse, it uses the edges of graph!
              D.set(classNo[inEdge.getSource()]);
            }
          }
      }
      // D now contains the list of possibly splittable P in Part

      int oldPsize = Partition.size();

      // Check if really splittable and do so eventually
      for (int pclassNo = D.nextSetBit(0); pclassNo >= 0;
           pclassNo = D.nextSetBit(pclassNo + 1)) {

        List<Integer> P = Partition.get(pclassNo);
        List<Integer> PPrime = new LinkedList<Integer>();
        // now split P :
        // move all with p in P, delta(p, a) in W from P to PPrime
        // At the end of the loop, P = P \ P'
        for (Iterator<Integer> p_it = P.iterator(); p_it.hasNext();) {
          int p = p_it.next();
          int targetClass = classNo[noStates];
          if (p != noStates) {
            Edge<EdgeInfo> trans = graph.findEdge(p, a, comp);
            if (trans != null)
              targetClass = classNo[trans.getTarget()];
          }
          if (targetClass != WclassNo) {
            PPrime.add(p);
            p_it.remove();
          }
        }
        if (P.isEmpty()) { // not splittable
          P.addAll(PPrime);
          continue;
        }
        if (PPrime.isEmpty()) continue; // not splittable

        // this will be the class number of the next P'
        int currentClassNo = Partition.size();
        Partition.add(PPrime);

        // for b : b \in Alphabet (Pseudocode line 9)
        for (EdgeInfo info : graph.getAlphabet()) {
          int addToClass = currentClassNo;
          // Pseudocode line 10
          if (Wait[pclassNo] != null && Wait[pclassNo].contains(info)) {
            // replace P by P' and P'': P is already P', so add P'' (=Q0')
            // which must be a new, currently unused bucket of Wait
            // addToClass = currentClassNo; // nothing to do
          } else { // P not contained in Wait
            // Pseudocode line 13
            if (P.size() < PPrime.size()) {
              addToClass = pclassNo;
            } // else addToClass = currentClassNo; // nothing to do
          }
          if (Wait[addToClass] == null) {
            Wait[addToClass] = new TreeSet<EdgeInfo>(comp);
          }
          Wait[addToClass].add(info);
          if (addToClass > lastInWait) lastInWait = addToClass;
          if (addToClass < firstInWait) firstInWait = addToClass;
        }
      }
      // now adapt the class numbers for vertices in newly created equivalence
      // classes. This may not change in the loop before because we need the
      // old P attributions, not the P'
      for(int newClassNo = oldPsize; newClassNo < Partition.size(); ++newClassNo) {
        for(int v : Partition.get(newClassNo)) {
          classNo[v] = newClassNo;
        }
      }
    } while (true);
    //System.out.println(L);System.out.println(P);

    // Now determine representatives and delete non-representative nodes
    int[] representative = new int[graph.getNumberOfVertices()];
    for (int i = 0; i < representative.length; ++i) {
      if (graph.isVertex(i)) {
        representative[i] = Partition.get(classNo[i]).get(0);
      }
    }
    reduceAutomaton(graph, representative, comp);

  }

}

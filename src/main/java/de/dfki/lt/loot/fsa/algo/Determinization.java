package de.dfki.lt.loot.fsa.algo;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.hash.TIntHashSet;
import gnu.trove.stack.TIntStack;
import gnu.trove.stack.array.TIntArrayStack;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import de.dfki.lt.loot.digraph.Edge;
import de.dfki.lt.loot.fsa.AbstractAutomaton;

public class Determinization {

  /**
   * This expands the given set of states of the non-deterministic automaton
   * with all states that can be reached via epsilon transitions.
   *
   * @param nfaStateSet
   * a <code>Set<Vertex<Integer, Character>></code>
   * with states of the non-deterministic automaton
   */
  private static <EdgeInfo> void expandWithEpsilonClosure(
    AbstractAutomaton<EdgeInfo> graph,
    TIntHashSet nfaStateSet) {

    // store all states to process in a stack
    final TIntStack toCheck = new TIntArrayStack();
    nfaStateSet.forEach(new TIntProcedure() {
      @Override
      public boolean execute(int value) {
        toCheck.push(value);
        return true;
      }
    });

    // loop until the stack is empty
    while (!(toCheck.size() == 0)) {
      // get top element from stack
      int currentState = toCheck.pop();
      // get all states that can be reached via epsilon transition, to
      // expand state set and update toCheck
      for (Edge<EdgeInfo> oneEdge : graph.getOutEdges(currentState)) {
        if (graph.isEpsilon(oneEdge.getInfo())) {
          int targetState =
            oneEdge.getTarget();
          if (!nfaStateSet.contains(targetState)) {
            nfaStateSet.add(targetState);
            toCheck.push(targetState);
          }
        }
      }
    }
  }


  /**
   * This returns the set of states of the non-deterministic automaton that
   * can be reached from the given states via the given transition character
   *
   * @param nfaStateSet
   * a <code>TIntHashSet</code>
   * with states of the non-deterministic automaton
   * @param transChar a <code>char</code> with the transition character
   * @return a <code>TIntHashSet</code>
   * with the reachable states
   */
  private static <EdgeInfo> TIntHashSet move(
      final AbstractAutomaton<EdgeInfo> graph,
      TIntHashSet nfaStateSet,
      final EdgeInfo transChar,
      final Comparator<EdgeInfo> comp) {

    // init result set
    final TIntHashSet targetStates = new TIntHashSet();

    nfaStateSet.forEach(new TIntProcedure() {
      @Override
      public boolean execute(int state) {
        // get all edges with the transition character
        Iterable<Edge<EdgeInfo>> outEdges =
            graph.findEdges(state, transChar, comp);
        // add target states of these edges to result
        for (Edge<EdgeInfo> oneEdge : outEdges) {
          targetStates.add(oneEdge.getTarget());
        }
        return true;
      }
    });

    return targetStates;
  }


  /**
   * This checks if the given set of states of the non-deterministic automaton
   * contains any final state.
   *
   * @param nfaStateSet
   * a <code>TIntHashSet</code>
   * with states of the non-deterministic automaton
   * @return a <code>boolean</code> that is <code>true</code> if any final state
   * is found
   */
  private static <EdgeInfo> boolean isFinal(
      final AbstractAutomaton<EdgeInfo> graph,
    TIntHashSet nfaStateSet) {

    final boolean[] result = { false };
    nfaStateSet.forEach(new TIntProcedure() {
      @Override
      public boolean execute(int oneState) {
        if (graph.isFinalState(oneState)) {
          result[0] = true;
          return false;
        }
        return true;
      }
    });
    return result[0];
  }

  /**
   * This initializes the queue of unmarked DFA states, represented by sets of
   * NFA states.
   *
   * @param nfa2dfaStates a <code>Map</code> that maps NFA state sets to a
   * single state of the DFA
   * @param detAutomaton a <code>FinateAutomaton</code> with the deterministic
   * automaton
   * @return a <code>Queue</code> with a single unmarked DFA state that is the
   * initial state
   */
  private static <EdgeInfo> Queue<TIntHashSet> initUnmarkedStates(
      AbstractAutomaton<EdgeInfo> nonDetAutomaton,
      Map<TIntHashSet, Integer> nfa2dfaStates,
      AbstractAutomaton<EdgeInfo> detAutomaton) {

    // init
    Queue<TIntHashSet> unmarkedStates = new LinkedList<TIntHashSet>();

    // get epsilon closure of initial NFA state; this corresponds to the initial
    // state of the DFA
    TIntHashSet initialEpsClosure = new TIntHashSet();
    initialEpsClosure.add(nonDetAutomaton.getInitialState());
    expandWithEpsilonClosure(nonDetAutomaton, initialEpsClosure);

    // take the initial DFA state and map it to the epsilon closure of the
    // NFA initial state
    int dfaInitState = detAutomaton.newVertex();
    detAutomaton.setInitialState(dfaInitState);
    if (isFinal(nonDetAutomaton, initialEpsClosure)) {
      detAutomaton.setFinalState(dfaInitState);
    }
    nfa2dfaStates.put(initialEpsClosure, dfaInitState);

    // add the state to unmarked: since every new DFA state will only be marked
    // once, we implement this using a queue of states; marking will then be
    // equivalent to removing the state from the unmarked state queue
    unmarkedStates.add(initialEpsClosure);

    return unmarkedStates;
  }


  /**
   * This computes a deterministic automaton that accepts the same language as
   * this non-deterministic automaton into detAutomaton
   *
   * @param detAutomaton the resulting deterministic automaton. Must be a fresh
   *           automaton and is passed as parameter to make it possible to use
   *           this method for subclasses.
   */
  public static <EdgeInfo> void determinize(
    AbstractAutomaton<EdgeInfo> nonDetAutomaton,
    Comparator<EdgeInfo> comp,
    AbstractAutomaton<EdgeInfo> detAutomaton) {
    // detAutomaton will contain the result DFA

    // this maps NFA state sets to a single state of the DFA
    Map<TIntHashSet, Integer> nfa2dfaStates =
      new HashMap<TIntHashSet, Integer>();

    // this contains the set of unmarked DFA states, represented by the
    // corresponding NFA state sets
    Queue<TIntHashSet> unmarkedStates
      = initUnmarkedStates(nonDetAutomaton, nfa2dfaStates, detAutomaton);

    // is there still a new DFA state we have to handle?
    while (! unmarkedStates.isEmpty()) {

      // this set of NFA states represents a single DFA state
      TIntHashSet nfaStateSet = unmarkedStates.remove();
      // this is the corresponding DFA state
      int dfaState = nfa2dfaStates.get(nfaStateSet);

      // iterate over every transition character used in this automaton
      for (EdgeInfo oneChar : nonDetAutomaton.getAlphabet()) {
        // compute the set of states that can be reached from the NFA states in
        // nfaStateSet over a transition with character oneChar
        TIntHashSet transNfaStateSet =
          move(nonDetAutomaton, nfaStateSet, oneChar, comp);

        // and do the epsilon closure of this set
        expandWithEpsilonClosure(nonDetAutomaton, transNfaStateSet);
        // if no state can be reached, we just continue
        if (transNfaStateSet.isEmpty()) {
          continue;
        }

        // this new subset of NFA states is either a known or a new state
        // of the DFA
        Integer transDfaState = nfa2dfaStates.get(transNfaStateSet);
        if (transDfaState == null) {
          // this subset represents a new DFA node, add a new node to the
          // deterministic automaton
          transDfaState = detAutomaton.newVertex();
          // add the new state to the final states if the NFA state set
          // contains a final state
          if (isFinal(nonDetAutomaton, transNfaStateSet)) {
            detAutomaton.setFinalState(transDfaState);
          }
          // created mapping from NFA state set to DFA state
          nfa2dfaStates.put(transNfaStateSet, transDfaState);
          // this node still has to be treated, so add it to the "unmarked" list
          unmarkedStates.add(transNfaStateSet);
        }
        // TODO when the edges of the original automaton carry weights, the
        // edge label used here should not come from the alphabet, but be
        // computed from the edges used in the `move' operation.
        // This is not the PROPER determinization of weighted automata, just
        // a heuristic
        // add the newly computed transition to the resulting DFA
        detAutomaton.newEdge(oneChar, dfaState, transDfaState);
      }
    }
  }



}

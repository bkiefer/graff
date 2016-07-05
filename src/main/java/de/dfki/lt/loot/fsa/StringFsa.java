package de.dfki.lt.loot.fsa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dfki.lt.loot.digraph.Edge;
import de.dfki.lt.loot.fsa.algo.Determinization;
import de.dfki.lt.loot.fsa.algo.Minimization;

/**
 * {@link StringFsa} extends {@link FiniteAutomaton} to be used with strings
 * as edge info.
 *
 * @author Bernd Kiefer, DFKI
 * @author Joerg Steffen, DFKI
 */
public class StringFsa extends FiniteAutomaton<String> {

  /**
   * Creates a new instance of {@link StringFsa}.
   */
  public StringFsa() {

    super();
    this._comp = new Comparator<String>() {

      /**
       * {@inheritDoc}
       */
      @Override
      public int compare(String arg1, String arg2) {

        // EPSILON is smaller than anything
        if (arg1 == null) {
          return (arg2 == null) ? 0 : -1;
        }
        if (arg2 == null) {
          return 1;
        }
        return arg1.compareTo(arg2);
      }
    };
  }


  /**
   * Returns a determinized version of this automaton.
   *
   * @return a deterministic finite state automaton
   */
  public StringFsa determinize() {

    StringFsa fsa = new StringFsa();
    Determinization.determinize(this, this._comp, fsa);
    return fsa;
  }


  /**
   * Returns a minimized version of this deterministic finite state automaton.
   *
   * @return a minimized deterministic finite state automaton
   */
  public StringFsa minimize() {

    StringFsa copy = this.copy();
    Minimization.minimize(copy, this._comp);

    return copy;
  }


  /**
   * {@inheritDoc}
   * Provides a copy method that returns an instance of {@code StringFsa}.
   */
  @Override
  public StringFsa copy() {

    StringFsa copy = new StringFsa();
    copy.setInitialState(copy.newVertex());

    Map<Integer, Integer> stateMap = new HashMap<>();

    stateMap.put(this.getInitialState(), copy.getInitialState());
    if (this.isFinalState(this.getInitialState())) {
      copy.setFinalState(copy.getInitialState());
    }
    copyState(this.getInitialState(), copy, stateMap);

    return copy;
  }


  /**
   * Save this automaton in a text based format at the given path.
   *
   * @param textPath
   *          the path where to write the text to
   */
  public void saveAsText(Path textPath) {

    try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(
      textPath, StandardCharsets.UTF_8))) {

      List<Integer> collectedFinalStates = new ArrayList<>();

      // write initial state
      out.println(this.getInitialState());

      // for each state, write its outgoing edges
      VertexIterator verticesIterator = this.vertices();
      while (verticesIterator.hasNext()) {
        int vertex = verticesIterator.next();
        for (Edge<String> oneEdge : this.getOutEdges(vertex)) {
          String edgeInfo = oneEdge.getInfo();
          if (null == edgeInfo) {
            edgeInfo = "EPSILON";
          }
          out.println(
            String.format("%d %d %s",
              oneEdge.getSource(),
              oneEdge.getTarget(),
              edgeInfo));
        }
        if (this.isFinalState(vertex)) {
          collectedFinalStates.add(vertex);
        }
      }

      // write final states
      for (int oneFinalState : collectedFinalStates) {
        out.println(oneFinalState);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  /**
   * Initializes this automaton from text based format at the given path.
   *
   * @param textPath
   *          the path from where to read the text
   */
  public void readFromText(Path textPath) {

    Map<Integer, Integer> stateMap = new HashMap<>();

    try (BufferedReader in = Files.newBufferedReader(
      textPath, StandardCharsets.UTF_8)) {

      // first line contains the initial state
      int initialState = Integer.parseInt(in.readLine());
      stateMap.put(initialState, getNewState(initialState, stateMap));
      this.setInitialState(getNewState(initialState, stateMap));

      String line;
      while (true) {
        // read edges
        line = in.readLine();
        String[] edgeParts = line.split(" ");
        if (edgeParts.length != 3) {
          // all edges have been read
          break;
        }
        int startState = Integer.parseInt(edgeParts[0]);
        int endState = Integer.parseInt(edgeParts[1]);
        String label = edgeParts[2];
        if (label.equals("EPSILON")) {
          label = null;
        }
        this.newEdge(
          label,
          getNewState(startState, stateMap),
          getNewState(endState, stateMap));
      }

      // read final states;
      // line already contains the first final state
      this.setFinalState(getNewState(Integer.parseInt(line), stateMap));
      while ((line = in.readLine()) != null) {
        this.setFinalState(getNewState(Integer.parseInt(line), stateMap));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  /**
   * Get the new state for the give state using the given state map. If not
   * already available, the new state will be created.
   *
   * @param state the state
   * @param stateMap mapping of states to new states
   * @return the new state
   */
  private int getNewState(int state, Map<Integer, Integer> stateMap) {

    Integer newState = stateMap.get(state);
    if (null != newState) {
      return newState;
    }
    newState = this.newVertex();
    stateMap.put(state, newState);
    return newState;
  }
}

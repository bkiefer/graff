package de.dfki.lt.loot.fsa;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.dfki.lt.loot.digraph.DirectedGraph;
import de.dfki.lt.loot.digraph.DirectedGraphPrinter;
import de.dfki.lt.loot.digraph.Edge;
import de.dfki.lt.loot.digraph.GraphVisitorAdapter;
import de.dfki.lt.loot.digraph.VertexListPropertyMap;
import de.dfki.lt.loot.digraph.VertexPropertyMap;
import de.dfki.lt.loot.fsa.algo.Determinization;

public class FiniteAutomaton<EdgeInfo> extends DirectedGraph<EdgeInfo>
implements AbstractAutomaton<EdgeInfo> {

  /** A mini sub automaton in this automaton */
  public class SubAutomaton {
    private int _initialState;
    private int _finalState;

    public SubAutomaton() {
      _initialState = newVertex();
      setFinalState(_initialState);
    }

    public SubAutomaton(int iState, int fState) {
      this._initialState = iState;
      this._finalState = fState;
    }

    public int getInitialState() {
      return _initialState;
    }

    public int getFinalState() {
      return _finalState;
    }

    public void setInitialState(int newInitialState) {
      _initialState = newInitialState;
    }

    public void setFinalState(int newFinalState) {
      _finalState = newFinalState;
    }

    public SubAutomaton copy() {
      Map<Integer, Integer> stateMap = new HashMap<>();

      SubAutomaton automatonCopy = new SubAutomaton();
      automatonCopy.setFinalState(-1);
      stateMap.put(this._initialState, automatonCopy.getInitialState());
      copyState(this._initialState, stateMap);
      Integer copyFinalState = stateMap.get(this.getFinalState());
      if (null != copyFinalState) {
        automatonCopy.setFinalState(stateMap.get(this.getFinalState()));
      }

      return automatonCopy;
    }

    private void copyState(int state, Map<Integer, Integer> stateMap) {
      for (Edge<EdgeInfo> oneOutEdge : FiniteAutomaton.this.getOutEdges(state)) {
        int targetState = oneOutEdge.getTarget();
        // if this state was already copied, there is an entry in the state map
        Integer targetStateCopy = stateMap.get(targetState);
        if (null == targetStateCopy) {
          targetStateCopy = FiniteAutomaton.this.newVertex();
          stateMap.put(targetState, targetStateCopy);
          copyState(targetState, stateMap);
        }
        FiniteAutomaton.this.newEdge(
          oneOutEdge.getInfo(), stateMap.get(state), targetStateCopy);
      }
    }

    /* TODO: I can not see where this is used */
    public void delete() {
      BitSet states = new BitSet();
      states.set(this.getInitialState());
      collectStates(this.getInitialState(), states);

      int currentState = -1;
      while ((currentState = states.nextSetBit(currentState + 1)) != -1) {
        FiniteAutomaton.this.removeVertexLazy(currentState);
      }
      FiniteAutomaton.this.cleanupEdges();
    }
  }

  /**
   * This contains the representation of an epsilon transition.
   */
  //public static final EdgeInfo EPSILON = new EdgeInfo();
  protected final EdgeInfo EPSILON;

  /**
   * This contains the node representing the initial state of the automaton.
   */
  protected int _initialState;

  /**
   * This contains the collection of final states of the automaton.
   */
  protected BitSet _finalStates;

  /** A comparator for EdgeInfos */
  protected Comparator<EdgeInfo> _comp;

  /**
   * This contains the symbols of the input alphabet.
   */
  private Collection<EdgeInfo> alphabet;


  /**
   * This creates a new instance of <code>FiniteAutomaton</code> that contains
   * no states.
   * @param eps      the single epsilon element in this automaton
   * @param alphabet the data structure to store the alphabet
   */
  protected FiniteAutomaton(EdgeInfo eps, Collection<EdgeInfo> alphabet) {
    EPSILON = eps;
    // init initial and final states
    this.setInitialState(-1);
    _finalStates = new BitSet();
    //this.graph.register("finalStates", finalStates);
    // init alphabet
    this.setAlphabet(alphabet);
  }

  /**
   * This creates a new instance of <code>FiniteAutomaton</code> that contains
   * no states.
   */
  public FiniteAutomaton() {
    this(null, new HashSet<EdgeInfo>());
  }

/**
   * This returns the initial state of the automaton.
   *
   * @return a <code>Node<Integer,EdgeInfo></code> with the
   * initial state of the automaton
   */
  public int getInitialState() {
    return this._initialState;
  }

  /**
   * This sets the field {@link #_initialState} to the given parameter.
   *
   * @param aStartNode a <code>Node<Integer,EdgeInfo></code>
   * that contains the initial state of the automaton
   */
  public void setInitialState(int aStartNode) {
    this._initialState = aStartNode;
  }


  /**
   * This returns the the collection of final states of the automaton.
   *
   * @return a <code>Collection<Node<Integer,EdgeInfo>></code>
   * with the final states of the automaton
   */
  public List<Integer> getFinalStates() {
    List<Integer> result = new ArrayList<Integer>(_finalStates.cardinality());
    for (int i = _finalStates.nextSetBit(0)
           ; i >= 0
           ; i = _finalStates.nextSetBit(i+1)) {
      result.add(i);
    }
    return result;
  }

  /** is <code>state</code> a final state?
   *  @return <code>true</code> if that is the case
   */
  public boolean isFinalState(int state) {
    return _finalStates.get(state);
  }

  /**
   * This makes <code>state</code> a final state.
   * @param state a state
   */
  public void setFinalState(int state) {
    _finalStates.set(state);
  }

  /**
   * This makes <code>state</code> a final state.
   * @param state a state
   */
  protected void setNonFinalState(int state) {
    _finalStates.clear(state);
  }


  /** Return true if the info is an epsilon */
  public boolean isEpsilon(EdgeInfo info) {
    return (EPSILON == null ? info == EPSILON : info.equals(EPSILON));
  }

  /** Return the value of the EdgeInfo representation for epsilon */
  public EdgeInfo getEpsilon() {
    return EPSILON;
  }

  /**
   * This returns the symbols of the input alphabet.
   *
   * @return a <code>Collection<EdgeInfo></code> with the symbols of the input
   * alphabet
   */
  public Collection<EdgeInfo> getAlphabet() {
    return this.alphabet;
  }

  /**
   * This sets the field {@link #alphabet} to the given parameter.
   *
   * @param anAlphabet a <code>Collection<EdgeInfo></code> that contains the
   * symbols of the input alphabet
   */
  protected void setAlphabet(Collection<EdgeInfo> anAlphabet) {
    this.alphabet = anAlphabet;
  }

  public int compare(EdgeInfo e1, EdgeInfo e2) {
    return _comp.compare(e1, e2);
  }

  /**
   * This creates a new transition in the automaton.
   *
   * @param transChar a <code>EdgeInfo</code> with the alphabet symbol of the
   * transition
   * @param aStartState a <code>int</code>
   * with the start state of the transition
   * @param anEndState a <code>int</code>
   * with the end state of the transition
   * @return a <code>Edge<EdgeInfo></code> with the
   * newly created transition
   */
  @Override
  public Edge<EdgeInfo> newEdge(
      EdgeInfo transChar,
      int aStartState,
      int anEndState) {

    // only add transChar to alphabet if it is not Epsilon
    if (! isEpsilon(transChar)) {
      this.getAlphabet().add(transChar);
    }
    return super.newEdge(transChar, aStartState, anEndState);
  }


  @Override
  public void changeEndVertex(Edge<EdgeInfo> edge, int target) {
    for (Edge<EdgeInfo> e : findEdges(edge.getSource(), edge.getInfo(), _comp)){
      if (e.getTarget() == target) return;
    }
    super.changeEndVertex(edge, target);
  }

  /** we add a virtual `dead end' vertex where all non-existent edges point to.
   * this is necessary because some algorithms only work with total graphs,
   * meaning that delta is a total function V x A --> V.
   */
  public void makeTotal() {
    int deadEnd = newVertex();
    for(int v = 0; v < getNumberOfVertices(); ++v) {
      if (isVertex(v)) {
        for (EdgeInfo info : getAlphabet()) {
          if (findEdge(v, info, _comp) == null) {
            newEdge(info, v, deadEnd);
          }
        }
      }
    }
  }


  private void deleteIneffective(BitSet visited) {
    int nextIneff = visited.nextClearBit(0);
    while (nextIneff < getNumberOfVertices()) {
      if (isVertex(nextIneff))
        removeVertexLazy(nextIneff);
      nextIneff = visited.nextClearBit(nextIneff + 1);
    }
    cleanupEdges();
  }

  /** Find all states that are effective in this FSA. Those have to be a)
   *  reachable from the start state and b) some final state must be reachable
   *  from them.
   *
   *  To compute this, do a forward DFS from the start state. All non-visited
   *  states are ineffective and can be deleted.
   *  Then, do a backwards DFS from all the end states. All non-visited states
   *  are ineffective.
   */
  public void removeDeadStates() {
    final BitSet visited = new BitSet();
    dfs(_initialState, new GraphVisitorAdapter<EdgeInfo>() {
      @Override
      public void discoverVertex(int v, DirectedGraph<EdgeInfo> g){
        visited.set(v);
      }
    });
    deleteIneffective(visited);
    DirectedGraph<EdgeInfo> converse = converseLazy();
    visited.clear();
    for (int fin : getFinalStates()) {
      converse.dfsConverse(fin, new GraphVisitorAdapter<EdgeInfo>() {
        @Override
        public void discoverVertex(int v, DirectedGraph<EdgeInfo> g){
          visited.set(v);
        }
      });
    }
    deleteIneffective(visited);
  }

  /** Override this method to ensure that a deleted vertex is neither start nor
   *  final node after deletion
   * @throws IllegalStateException if the state to delete is the start state
   *            or the last final state
   */
  @Override
  public void removeVertexLazy(int v) {
    if (isFinalState(v)) {
      setNonFinalState(v);
      if (_finalStates.isEmpty()) {
        throw new IllegalStateException("Removed node " + v
            + " is last final state");
      }
    }
    if (v == _initialState) {
      throw new IllegalStateException("Removed node " + v + " is start state");
    }
    super.removeVertexLazy(v);
  }

  // ======================================================================
  // I/O methods
  // ======================================================================

  private class FsaPrinter implements DirectedGraphPrinter<EdgeInfo> {
    public String defaultNodeAttributes =
        "shape=circle, width=.6, fixedsize=true";

    private VertexPropertyMap<String> _nodeNames;
    public String defaultEdgeAttributes = null;

    @SuppressWarnings("unchecked")
    public FsaPrinter(DirectedGraph<EdgeInfo> graph) {
      _nodeNames = (VertexPropertyMap<String>) graph.getPropertyMap("names");
    }

    private String escapeForDot(String in) {
      String out = in.replaceAll("([^\\\\])\"", "$1\\\\\"");
      return (out.charAt(0) == '"') ? "\\" + out : out;
    }

    public String getDefaultGraphAttributes() {
      return "rankdir=LR; splines=polyline;";
    }

    /**
     * Print a state of the FSA, taking into account special printing for start
     * and final states.
     */
    public void dotPrintNode(PrintWriter out, int node) {
      out.print("n" + node);
      ArrayList<String> attribs = new ArrayList<String>();
      if (defaultNodeAttributes != null)
        attribs.add(defaultNodeAttributes);
      if (_nodeNames != null)
        attribs.add(" label=\"" + _nodeNames.get(node) +"\"");
      if (node == _initialState)
        attribs.add(" color=green ");
      if (isFinalState(node))
        attribs.add(" style=filled, fillcolor=red ");

      if (!attribs.isEmpty()) {
        out.print("[");
        for (int i = 0; i<attribs.size(); ++i){
          if (i > 0) out.print(",");
          out.print(attribs.get(i));
        }
        out.print("]");
      }
      out.println(";");
    }

    /** Print a FSA transition */
    public void dotPrintEdge(PrintWriter out, Edge<EdgeInfo> edge) {
      EdgeInfo transChar = edge.getInfo();
      out.print("n" + edge.getSource() + " -> n" + edge.getTarget()
                + "[ ");
      if (defaultEdgeAttributes != null) {
        out.print(defaultEdgeAttributes + ", ");
      }
      if (isEpsilon(transChar)) {
        out.println("label=\"\u03B5\", fontcolor=red];");
      }
      else {
        out.println("label=\"" + escapeForDot(transChar.toString()) + "\"];");
      }
    }
  }


  /**
   * This writes this fsa in graphviz format to the given file so that it can
   * be processed with the graphviz package (http://www.graphviz.org/)
   *
   * @param fileName a <code>String</code> with the file name
   * @throws <code>IOException</code> if an error occurs when writing the file
   */
  @Override
  public void dotPrint(Path fileName) throws IOException {
    final DirectedGraphPrinter<EdgeInfo> printer = new FsaPrinter(this);
    dotPrint(fileName, printer);
  }


  /**
   * This writes this graph in vcg format to the given file so that it can be
   * processed with the VCG tool
   * (http://rw4.cs.uni-sb.de/users/sander/html/gsvcg1.html)
   *
   * @param fileName a <code>String</code> with the file name
   * @throws <code>IOException</code> if an error occurs when writing the file
   */
  public void vcgPrint(String fileName)
    throws IOException {

    PrintWriter out =
      new PrintWriter(
          new BufferedWriter(
              new OutputStreamWriter(
                  new FileOutputStream(fileName))));
    out.println("graph: { orientation: left_to_right display_edge_labels: yes");

    for (int node = 0 ; node < getNumberOfVertices(); ++node) {
      if (isVertex(node)) {
        out.print("node: { title: \"" + node + "\" ");
        if (node == _initialState) {
          out.print("color: green ");
        }
        if (isFinalState(node)) {
          out.print("bordercolor: red ");
        }
        out.println("}");
      }

      for (Edge<EdgeInfo> edge : getOutEdges(node)) {
        EdgeInfo transChar = edge.getInfo();
        out.println("edge: { sourcename: \"" + node
            + "\" targetname: \"" + edge.getTarget()
            + "\" label: ");
        if (isEpsilon(transChar)) {
          out.println("\"e\" textcolor:red }");
        }
        else {
          out.println("\"" + edge.getInfo() + "\" }");
        }
      }
    }

    out.println("}");
    out.close();
  }


  /**
   * This overrides @see java.lang.Object#toString().
   *
   * @return a <code>String</code> representation of this automaton
   */
  @Override
  public String toString() {
    String newline = System.getProperty("line.separator");

    StringBuilder strRep = new StringBuilder();

    strRep.append("alphabet: ").append(newline)
    .append(this.getAlphabet()).append(newline);
    strRep.append("initial state: ").append(newline)
    .append(this.getInitialState()).append(newline);

    strRep.append("final states: ").append(newline);
    for (int v = 0; v < getNumberOfVertices(); ++v) {
      if (isFinalState(v)) {
        strRep.append(v).append(newline);
      }
    }

    strRep.append("transitions: ").append(newline);
    for (int v = 0; v < getNumberOfVertices(); ++v) {
      if (isVertex(v)) {
        for (Edge<EdgeInfo> oneOutEdge : getOutEdges(v)) {
          strRep.append(oneOutEdge.toString()).append(newline);
        }
      }
    }

    return strRep.toString();
  }


  // ======================================================================
  // STANDARD AUTOMATA CONSTRUCTION
  // ======================================================================

  /** This constructs and automaton that matches the given "character".
   *
   * @param edgeInfo a <code>char</code> with the edge info
   */
  public SubAutomaton newCharAutomaton(EdgeInfo edgeInfo) {
    SubAutomaton result = new SubAutomaton();
    // create a new final state
    int finalState = newVertex();
    // the new state is the new final state
    result.setFinalState(finalState);
    // connect the new state and the initial state
    newEdge(edgeInfo, result.getInitialState(), finalState);
    return result;
  }


  /** This constructs an automaton that matches a set of "characters."
   *
   * @param chars the respective set of characters
   */
  public SubAutomaton newCharSetAutomaton(Set<EdgeInfo> chars) {
    SubAutomaton result = new SubAutomaton();
    // created new initial and final state
    int initState = result.getInitialState();
    int finalState = newVertex();
    result.setFinalState(finalState);

    for(EdgeInfo c : chars) {
      // link states according to slides
      newEdge(c, initState, finalState);
    }
    return result;
  }

  /**
   * This creates an automaton that matches the language of this automaton or
   * the language of the given rhs automaton.
   *
   * The current automaton is modified so that it contains the result automaton
   * after calling this method. The rhs will be modified and should not be used
   * afterwards.
   *
   * @param this a <code>FiniteAutomaton</code> with the left hand side of the
   * alternative
   * @param rhs a <code>FiniteAutomaton</code> with the right hand side of the
   * alternative
   */
  public SubAutomaton alternative(SubAutomaton lhs, SubAutomaton rhs) {
    // the result automaton will be created by modifying the rhs automaton

    // created new initial and final state
    int newInitialState = this.newVertex();
    int newFinalState = this.newVertex();

    // link states according to slides
    this.newEdge(EPSILON, newInitialState, lhs.getInitialState());
    this.newEdge(EPSILON, newInitialState, rhs.getInitialState());
    this.newEdge(EPSILON, lhs.getFinalState(), newFinalState);
    this.newEdge(EPSILON, rhs.getFinalState(), newFinalState);

    // define new initial and final state in the result automaton
    lhs.setInitialState(newInitialState);
    lhs.setFinalState(newFinalState);

    rhs.setInitialState(-1);
    rhs.setFinalState(-1);
    return lhs;
  }


  /**
   * This creates an automaton that matches the `or' of the language of each
   * of the subautomata in the list, which may not be empty.
   *
   * The current automaton is modified so that the first automaton in the list
   * contains the result after calling this method.
   *
   * @param alternatives a list of <code>SubAutomaton</code> for which we
   *        return an n-ary alternative
   */
  public SubAutomaton multiAlternative(Collection<SubAutomaton> alternatives) {
    if (alternatives.isEmpty()) {
      throw new IllegalArgumentException("alternatives list may not be empty");
    }
    // the result automaton will be created by modifying the first automaton
    // in the collection

    // created new initial and final state
    int newInitialState = this.newVertex();
    int newFinalState = this.newVertex();

    SubAutomaton result = null;
    // link states according to slides
    for (SubAutomaton alternative : alternatives) {
      this.newEdge(EPSILON, newInitialState, alternative.getInitialState());
      this.newEdge(EPSILON, alternative.getFinalState(), newFinalState);
      if (result == null) {
        result = alternative;
        alternative.setInitialState(newInitialState);
        alternative.setFinalState(newFinalState);
      } else {
        alternative.setInitialState(-1);
        alternative.setFinalState(-1);
      }
    }
    return result;
  }

  /**
   * This modifies the automaton in a way that it matches an arbitrary
   * number (including zero) of the of char sequences it matches now.
   *
   * @param this the <code>FiniteAutomaton</code> to wrap with a
   * kleene star
   */
  public SubAutomaton kleene(SubAutomaton sub) {
    // create new initial and final states
    int newInitialState = this.newVertex();
    int newFinalState = this.newVertex();
    // connect new initial and final state with an epsilon transition
    this.newEdge(EPSILON, newInitialState, newFinalState);
    // connect the old final and initial state with an epsilon transition
    this.newEdge(EPSILON, sub.getFinalState(),
        sub.getInitialState());
    // connect the new states to the former initial and final state
    this.newEdge(EPSILON, newInitialState, sub.getInitialState());
    this.newEdge(EPSILON, sub.getFinalState(), newFinalState);
    // update initial state and final state
    sub.setInitialState(newInitialState);
    sub.setFinalState(newFinalState);
    return sub;
  }


  /**
   * This concatenates the given automatons. The first automaton is modified so
   * that it contains the result automaton
   *
   * @param lhs a <code>FiniteAutomaton</code>
   * @param rhs a <code>FiniteAutomaton</code> to concatenate with lhs
   */
  public SubAutomaton concatenate(SubAutomaton lhs, SubAutomaton rhs) {

    // merge the final state of the first automaton with the initial state of
    // the second automaton:
    // all outgoing edges of the initial state of the second automaton must
    // start at the final state of the first automaton

    Iterator<Edge<EdgeInfo>> rhsOutEdges =
      _outEdges.get(rhs.getInitialState()).iterator();
    while (rhsOutEdges.hasNext()) {
      Edge<EdgeInfo> edge = rhsOutEdges.next();
      rhsOutEdges.remove();
      setFrom(edge, lhs.getFinalState());
    }
    // delete old initial state
    removeVertexLazy(rhs.getInitialState());
    // update final state
    lhs.setFinalState(rhs.getFinalState());

    // make sure rhs is empty now
    rhs.setInitialState(-1);
    rhs.setFinalState(-1);

    return lhs;
  }

  /**
   * Concatenates the given subautomatons. The first subautomaton of the list is
   * modified so that it contains the resulting subautomaton.
   *
   * @param subautomatons
   *          the list of subautomatons to concatenate
   * @return the modified first subautomaton of the input list with the
   * concatenated subautomatons
   */
  public SubAutomaton multiConcatenate(List<SubAutomaton> subautomatons) {

    SubAutomaton currentAut = null;
    for (int i = 0; i < subautomatons.size(); i++) {
      if (currentAut == null) {
        currentAut = subautomatons.get(i);
        continue;
      }
      currentAut = this.concatenate(currentAut, subautomatons.get(i));
    }

    return currentAut;
  }

  /** Get initial and final state from the given subautomaton belonging to this
   *  FSA
   * @param sub
   */
  public void setStates(SubAutomaton sub) {
    this.setInitialState(sub.getInitialState());
    this.setFinalState(sub.getFinalState());
  }

  /** Get a subautomaton for the full automaton. This is only possible if the
   *  full automaton has only one final state. If this is not the case, an
   *  exception will be thrown.
   */
  public SubAutomaton getSubAutomaton() {
    if (_finalStates.cardinality() != 1)
      throw new IllegalStateException("Automaton must have exactly one final state");
    SubAutomaton result = new SubAutomaton();
    result._initialState = _initialState;
    result._finalState = getFinalStates().get(0);
    return result;
  }

  public void concatenate(FiniteAutomaton<EdgeInfo> successor) {
    VertexListPropertyMap<Integer> succ2here =
        new VertexListPropertyMap<Integer>(successor);
    // add all vertices and edges of the successor
    for (VertexIterator it = successor.vertices(); it.hasNext();) {
      int v = it.next();
      if (successor.isVertex(v)) {
        succ2here.put(v, newVertex());
      }
    }
    for (VertexIterator it = successor.vertices(); it.hasNext();) {
      int v = it.next();
      if (successor.isVertex(v)) {
        int from = succ2here.get(v);
        for (Edge<EdgeInfo> e : successor.getOutEdges(v)) {
          int to = succ2here.get(e.getTarget());
          newEdge(e.getInfo(), from, to);
        }
      }
    }
    // draw epsilon edges from this graphs final states to the successor start
    // state and make all the old final vertices nonfinal
    int succStart = succ2here.get(successor.getInitialState());
    for (int f : getFinalStates()) {
      newEdge(getEpsilon(), f, succStart);
      setNonFinalState(f);
    }
    // make the representatives of the successor's final states final
    for (int f : successor.getFinalStates()) {
      setFinalState(succ2here.get(f));
    }
  }


  /**
   * Modifies the given subautomaton in a way that it matches an arbitrary
   * number (excluding zero) of the char sequences it matches now.
   *
   * @param sub
   *          a subautomaton to wrap with a plus
   * @return the modified subautomaton with the plus
   */
  public SubAutomaton plus(SubAutomaton sub) {

    // create new initial and final states
    int newInitialState = this.newVertex();
    int newFinalState = this.newVertex();

    // connect the old final state to the old initial state and the new final
    // state with epsilon transitions
    newEdge(EPSILON, sub.getFinalState(), sub.getInitialState());
    newEdge(EPSILON, sub.getFinalState(), newFinalState);

    // connect the new initial state to the former initial state
    newEdge(EPSILON, newInitialState, sub.getInitialState());

    // update initial state and final state
    sub.setInitialState(newInitialState);
    sub.setFinalState(newFinalState);

    return sub;
  }


  /**
   * Modifies the given subautomaton in a way that it optionally matches the
   * char sequences it matches now.
   *
   * @param sub
   *          a subautomaton to wrap with a question mark
   * @return the modified subautomaton with the question mark
   */
  public SubAutomaton optional(SubAutomaton sub) {

    // create new initial and final states
    int newInitialState = this.newVertex();
    int newFinalState = this.newVertex();

    // connect new initial and final state with an epsilon transition
    newEdge(EPSILON, newInitialState, newFinalState);
    // connect the old final state to the new final state with epsilon
    // transitions
    newEdge(EPSILON, sub.getFinalState(), newFinalState);
    // connect the new initial state to the former initial state
    newEdge(EPSILON, newInitialState, sub.getInitialState());

    // update initial state and final state
    sub.setInitialState(newInitialState);
    sub.setFinalState(newFinalState);

    return sub;
  }


  /**
   * Returns a subautomaton that accepts between {@code min} and {@code max}
   * (including both) concatenated repetitions of the language of the given
   * automaton.
   *
   * @param sub
   *          the subautomaton
   * @param min
   *          the minimum repetition
   * @param max
   *          the maximum repetition
   * @return the result subautomaton
   */
  public SubAutomaton repeat(SubAutomaton sub, int min, int max) {

    if (min > max) {
      min = max;
    }

    max = max - min;

    // create concatenation of min automatons (non-optional)
    SubAutomaton currentAut;
    if (min == 0) {
      currentAut = new SubAutomaton();
    }
    else if (min == 1) {
      currentAut = sub.copy();
    }
    else {
      List<SubAutomaton> minSubs = new ArrayList<SubAutomaton>();
      while (min > 0) {
        minSubs.add(sub.copy());
        min--;
      }
      currentAut = multiConcatenate(minSubs);
    }

    // add concatenation of max automatons (optional);
    // optional: add epsilon edges from initial state to all final states
    if (max > 0) {
      List<SubAutomaton> maxSubs = new ArrayList<SubAutomaton>();
      while (max > 0) {
        SubAutomaton maxSub = sub.copy();
        newEdge(EPSILON, maxSub.getInitialState(), maxSub.getFinalState());
        maxSubs.add(maxSub);
        max--;
      }
      SubAutomaton maxSubsConcatenated = multiConcatenate(maxSubs);
      currentAut = concatenate(currentAut, maxSubsConcatenated);
    }

    return currentAut;
  }


  /**
   * Returns a deep copy of this finite automaton.
   *
   * @return the copy of this finite automaton
   */
  public FiniteAutomaton<EdgeInfo> copy() {

    FiniteAutomaton<EdgeInfo> copy = new FiniteAutomaton<EdgeInfo>();
    copy._comp = this._comp;
    copy.setInitialState(copy.newVertex());

    Map<Integer, Integer> stateMap = new HashMap<>();

    stateMap.put(this._initialState, copy.getInitialState());
    if (this.isFinalState(this._initialState)) {
      copy.setFinalState(copy.getInitialState());
    }
    copyState(this._initialState, copy, stateMap);

    return copy;
  }


  /**
   * Recursively copies the given state and all states reachable from it to the
   * the given automaton copy
   *
   * @param state
   *          the start state
   * @param copy
   *          the automaton copy
   * @param stateMap
   *          a map of states of the original automaton to their equivalents
   *          in the new automaton
   */
  protected void copyState(
      int state, FiniteAutomaton<EdgeInfo> copy,
      Map<Integer, Integer> stateMap) {

    for (Edge<EdgeInfo> oneOutEdge : this.getOutEdges(state)) {
      int targetState = oneOutEdge.getTarget();
      // if this state was already copied, there is an entry in the state map
      Integer targetStateCopy = stateMap.get(targetState);
      if (null == targetStateCopy) {
        targetStateCopy = copy.newVertex();
        stateMap.put(targetState, targetStateCopy);
        if (this.isFinalState(targetState)) {
          copy.setFinalState(targetStateCopy);
        }
        copyState(targetState, copy, stateMap);
      }
      copy.newEdge(
        oneOutEdge.getInfo(), stateMap.get(state), targetStateCopy);
    }
  }


  /**
   * Returns a new finite automaton that is the complement of this automaton.
   *
   * @return the complement automaton
   */
  public FiniteAutomaton<EdgeInfo> complement() {

    FiniteAutomaton<EdgeInfo> complement = this.copy();

    // complement creation:
    // all final states become non-final states;
    // all non-final states become final states
    for (VertexIterator it = complement.vertices(); it.hasNext();) {
      int v = it.next();
      if (complement.isFinalState(v)) {
        complement.setNonFinalState(v);
      }
      else {
        complement.setFinalState(v);
      }
    }

    return complement;
  }


  /**
   * Adds the given finite automaton to this automaton and return a sub
   * automaton for accessing its initial and final states.<br>
   * The initial and final states of the automaton added are only available via
   * the returned sub automaton!
   *
   * @param aut
   *          the finite automaton to add
   * @return the corresponding sub automaton
   */
  public SubAutomaton addAutomaton(FiniteAutomaton<EdgeInfo> aut) {

    Map<Integer, Integer> statesMap = new HashMap<>();
    BitSet newFinalStates = new BitSet();

    // add states
    VertexIterator it = aut.vertices();
    while (it.hasNext()) {
      int state = it.next();
      int newState = this.newVertex();
      statesMap.put(state, newState);
      if (aut.isFinalState(state)) {
        newFinalStates.set(newState);
      }
    }
    // add edges
    it = aut.vertices();
    while (it.hasNext()) {
      int state = it.next();
      int newSourceState = statesMap.get(state);
      for (Edge<EdgeInfo> oneOutEdge : aut.getOutEdges(state)) {
        int newTargetState = statesMap.get(oneOutEdge.getTarget());
        this.newEdge(oneOutEdge.getInfo(), newSourceState, newTargetState);
      }
    }

    // to make sure that there is only ONE final state, add epsilon edges from
    // the final states to a new final state
    int newFinalState = this.newVertex();
    int currentFinalState = -1;
    while((currentFinalState = newFinalStates.nextSetBit(currentFinalState + 1)) != -1) {
      this.newEdge(EPSILON, currentFinalState, newFinalState);
    }
    return new SubAutomaton(
      statesMap.get(aut.getInitialState()), newFinalState);
  }


  /**
   * Checks if this automaton accepts the same language as the given automaton.
   *
   * @param aut
   *          a finite automaton
   * @return a flag indicating equivalence
   */
  public boolean isEquivalent(FiniteAutomaton<EdgeInfo> aut) {

    // get the complement of this automaton
    FiniteAutomaton<EdgeInfo> complement = this.complement();
    // change the complement so that it has only ONE final state
    int newFinalState = complement.newVertex();
    int currentFinalState = -1;
    while ((currentFinalState = complement._finalStates.nextSetBit(currentFinalState + 1)) != -1) {
      complement.newEdge(EPSILON, currentFinalState, newFinalState);
    }
    complement._finalStates.clear();
    complement.setFinalState(newFinalState);
    // get the corresponding sub automaton
    SubAutomaton subComplement =
      new SubAutomaton(complement.getInitialState(), newFinalState);

    // add the other automaton
    SubAutomaton subAut = complement.addAutomaton(aut);

    // create union
    complement.alternative(subComplement, subAut);

    // determinize and finalize automaton
    complement.setInitialState(subComplement.getInitialState());
    complement._finalStates.clear();
    complement.setFinalState(subComplement.getFinalState());

    FiniteAutomaton<EdgeInfo> detAut = new FiniteAutomaton<EdgeInfo>();
    detAut._comp = this._comp;
    Determinization.determinize(complement, this._comp, detAut);
    // TODO activate again when minimization is working correctly
    //Minimization.minimize(detAut, this._comp);

    // automaton are equivalent if resulting automaton accepts everything
    return detAut.acceptsAll();
  }


  /**
   * Collects all states reachable from the given state in the given bit set.
   *
   * @param state
   *          the start state
   * @param states
   *          the collected states
   */
  private void collectStates(int state, BitSet states) {

    for (Edge<EdgeInfo> oneOutEdge : FiniteAutomaton.this.getOutEdges(state)) {
      int targetState = oneOutEdge.getTarget();
      // if this state was already visited, it is in the state set
      if (!states.get(targetState)) {
        states.set(targetState);
        collectStates(targetState, states);
      }
    }
  }


  /**
   * Checks if this automaton only accepts the empty language.
   *
   * @return a flag indicating emptiness
   */
  public boolean isEmpty() {

    FinalStateCollector<EdgeInfo> dfsVisitor = new FinalStateCollector<>();
    this.dfs(dfsVisitor);
    return dfsVisitor.getFinalStates().isEmpty();
  }


  /**
   * Checks if this automaton accepts everything.
   *
   * @return a flag indicating if the automaton accepts all
   */
  public boolean acceptsAll() {

    FinalStateCollector<EdgeInfo> dfsVisitor = new FinalStateCollector<>();
    this.dfs(dfsVisitor);
    return dfsVisitor.getFinalStates().cardinality()
      == this.getNumberOfActiveVertices();
  }


  /**
   * Visitor to collect all reachable final states of the automaton.
   *
   * @param <F>
   *          the kind of edge info used in the graph
   */
  public class FinalStateCollector<F> extends GraphVisitorAdapter<EdgeInfo> {

    /**
     * Keeps track of all reachable final states
     */
    private BitSet collectedFinalStates = new BitSet();

    /**
     * Returns the reachable final states.
     *
     * @return the reachable final states
     */
    public BitSet getFinalStates() {

      return this.collectedFinalStates;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void discoverVertex(int v, DirectedGraph<EdgeInfo> g) {

      if (FiniteAutomaton.this.isFinalState(v)) {
        this.collectedFinalStates.set(v);
      }
    }
  }
}

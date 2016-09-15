package de.dfki.lt.loot.fsa;

import java.util.Collection;

import de.dfki.lt.loot.digraph.Graph;

public interface AbstractAutomaton<EdgeInfo> extends Graph<EdgeInfo> {

  // Automaton specific methods

  public abstract int getInitialState();

  public abstract void setInitialState(int vertex);

  public abstract boolean isFinalState(int vertex);

  public abstract void setFinalState(int vertex);

  public abstract Collection<EdgeInfo> getAlphabet();

  public abstract boolean isEpsilon(EdgeInfo info);
}

package de.dfki.lt.loot.digraph;


public interface EdgeContainer<T> extends Iterable<Edge<T>> {

  public abstract boolean add(Edge<T> e);

  public abstract boolean removeEdge(Edge<T> e);

  public abstract Edge<T> findTarget(int to);

  /** get an edge with the appropriate type info.
   *
   *  only meaningful for graphs where the labels are unique.
   */
  //public abstract Edge<T> get(T e);

  public abstract boolean isEmpty();

  public abstract int size();

}

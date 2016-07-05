package de.dfki.lt.loot.digraph.weighted;

import java.util.Comparator;

public interface OrderedMonoid<T> extends Comparator<T> {

  public abstract T add(T w1, T w2);

  public abstract T getZero();

}

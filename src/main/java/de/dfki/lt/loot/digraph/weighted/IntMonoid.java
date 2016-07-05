package de.dfki.lt.loot.digraph.weighted;

public class IntMonoid implements OrderedMonoid<Integer> {
  public Integer add(Integer w1, Integer w2) { return w1 + w2; }

  public int compare(Integer w1, Integer w2) { return w1 - w2; }

  public Integer getZero() { return 0; }
}


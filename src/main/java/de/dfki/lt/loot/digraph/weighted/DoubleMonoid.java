package de.dfki.lt.loot.digraph.weighted;

public class DoubleMonoid implements OrderedMonoid<Double> {
  @Override
  public int compare(Double o1, Double o2) {
    return (int)Math.signum(o1 - o2);
  }

  @Override
  public Double add(Double w1, Double w2) { return w1 + w2; }

  @Override
  public Double getZero() { return Double.valueOf(0); }
}

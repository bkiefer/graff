package de.dfki.lt.loot.jada;

public interface IntIntTrieWalker {

  /** Enter a node during a walk */
  public void startNode(IntIntTrie node);

  /** Visit edge with value val before recursive descent */
  public void beforeEdge(int val);

  /** Visit edge with value val after recursive descent */
  public void afterEdge(int val);

  /** Visit the node after all its edges have been visited */
  public void endNode(IntIntTrie node);
}

package de.dfki.lt.loot.jada;

import java.util.Comparator;
/**
 * Fibonacci Heap
 *
 * @ref http://en.wikipedia.org/wiki/Fibonacci_heap
 * @ref http://www.cse.yorku.ca/~aaw/Jason/FibonacciHeapAlgorithm.html
 * @author Erel Segal http://tora.us.fm/rentabrain
 * @author Bernd Kiefer of the Java version
 * @date 2010-11-11
 */

public class FibonacciHeap<Key extends Comparable<? super Key>, Data>
   extends FibonacciHeapBase<Pair<Key, Data>> {

  public static class HeapNode<Key, Data> extends
  FibonacciHeapBase.HeapNode<Pair<Key, Data>> {
    HeapNode(Key k, Data d) {
      super(new Pair<Key, Data>(k, d));
    }

    public Key getKey() { return this.data.getFirst(); }

    public Data getData() { return this.data.getSecond(); }
  }

  /** Create a new Fibonacci heap */
  public FibonacciHeap() {
    super(new Comparator<Pair<Key, Data>>() {
      @Override
      public int compare(Pair<Key, Data> arg0, Pair<Key, Data> arg1) {
        return arg0.getFirst().compareTo(arg1.getFirst());
      }
    });
  }

  /** Insert a new pair of data and key into this heap, and return an object
   *  that represents the pair in this heap.
   *
   *  @return a HeapNode that contains the key and data objects and can be used
   *          to decrease the key.
   */
  @SuppressWarnings("unchecked")
  public HeapNode<Key, Data> insert(Data d, Key k) {
    if (debug) System.out.println("insert " + d);
    ++count;
    return (HeapNode<Key, Data>)insertNode(new HeapNode<Key, Data>(k, d));
  }


 /** Decrease the key of the HeapNode node to newKey.
   *  The new key must be strictly smaller than the old one, otherwise, an
   *  IllegalArgumentException will be thrown.
   */
  public void decreaseKey(HeapNode<Key, Data> node, Key newKey) {
    Pair<Key, Data> old = node.data;
    if (newKey.compareTo(old.getFirst()) >= 0)
      throw new IllegalArgumentException("Trying to decrease key "
          + "to a greater key");
    old.setFirst(newKey);
    super.decreaseKey(node);
  }

  @SuppressWarnings("unchecked")
  public HeapNode<Key, Data> minimum() {
    return (HeapNode<Key, Data>)super.minimum();
  }
}


/*
 * Copyright 2019-2022 Jörg Steffen, Bernd Kiefer
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * ​https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package de.dfki.lt.loot.jada;

import java.util.Comparator;
import java.util.Vector;
/**
 * Fibonacci Heap
 *
 * @ref http://en.wikipedia.org/wiki/Fibonacci_heap
 * @ref http://www.cse.yorku.ca/~aaw/Jason/FibonacciHeapAlgorithm.html
 * @author Erel Segal http://tora.us.fm/rentabrain
 * @author Bernd Kiefer of the Java version
 * @date 2010-11-11
 */

public class FibonacciHeapBase<Data> {

  private Comparator<Data> _comparator;

  /** The heap is a min-heap sorted by Key. */
  public static class HeapNode<Data> {

    /** The associated data */
    protected Data data;

    /** number of children. used in the removeMinimum algorithm */
    private int degree;
    /** mark used in the decreaseKey algorithm */
    private boolean mark;

    //int count; // total nr of elements in tree, including this. For debug only

    /** pointers in a circular doubly linked list */
    private HeapNode<Data> previous;
    private HeapNode<Data> next;
    /** pointer to the first child in the list of children */
    private HeapNode<Data> child;
    private HeapNode<Data> parent;

    protected HeapNode(Data d) {
      data = d;
      degree = 0;
      mark = false;
      child = null;
      parent = null;
      previous = next = this; // doubly linked circular list
    }

    private boolean isSingle() {
      return (this == this.next);
    }

    // inserts a new node after this node
    private void insert(HeapNode<Data> other) {
      if (other == null)
        return;

      // For example: given 1->2->3->4->1, insert a->b->c->d->a after node 3:
      //	result: 1->2->3->a->b->c->d->4->1

      this.next.previous = other.previous;
      other.previous.next = this.next;

      this.next = other;
      other.previous = this;
    }

    private void remove() {
      this.previous.next = this.next;
      this.next.previous = this.previous;
      this.next = this.previous = this;
    }

    private void addChild(HeapNode<Data> other) {
      // Fibonacci-Heap-Link(other,current)
      if (child == null)
        child = other;
      else
        child.insert(other);
      other.parent = this;
      other.mark = false;
      degree++;
      //count += other.count;
    }

    private void removeChild(HeapNode<Data> other) {
      // Trying to remove a child from a non-parent?
      assert (other.parent == this);
      if (other.isSingle()) {
        assert (child == other); // Trying to remove a non-child?
        child = null;
      } else {
        if (child == other)
          child = other.next;
        other.remove(); // from list of children
      }
      other.parent = null;
      other.mark = false;
      degree--;
      //count -= other.count;
    }

    private void printTree(StringBuffer out) {
      out.append(data).append(':')
      .append(degree).append(':')
      .append(mark ? '1' : '0');
      if (child != null) {
        out.append("(");
        HeapNode<Data> n = child;
        do {
          assert (n != this); // node is child of itself?
          n.printTree(out);
          out.append(' ');
          n = n.next;
        } while (n != child);
        out.append(')');
      }
    }

    private String printAll() {
      StringBuffer out = new StringBuffer();
      HeapNode<Data> n = this;
      do {
        n.printTree(out);
        out.append(" ");
        n = n.next;
      } while (n != this);
      return out.toString();
    }

    @Override
    public String toString() {
      StringBuffer out = new StringBuffer();
      out.append(data);
      return out.toString();
    }
  } // FibonacciHeapNode


  /** a circular doubly-linked list of nodes */
  private HeapNode<Data> rootWithMinKey;
  /** total number of elements in heap */
  protected int count;

  // @SuppressWarnings({ "rawtypes", "unchecked" })
  protected HeapNode<Data> insertNode(HeapNode<Data> newNode) {
    //if (debug) System.out.println("insert " + newNode);
    if (rootWithMinKey == null) {
      // insert the first element into the heap:
      rootWithMinKey = newNode;
    } else {
      // insert the root of new tree to the list of roots
      rootWithMinKey.insert(newNode);
      if (_comparator.compare(newNode.data, rootWithMinKey.data) < 0)
        rootWithMinKey = newNode;
    }
    return newNode;
  }

  public boolean debug, debugRemoveMin, debugDecreaseKey;

  /** Create a new Fibonacci heap */
  public FibonacciHeapBase(Comparator<Data> comparator) {
    rootWithMinKey = null;
    count = 0;
    debug = false;
    debugRemoveMin = false;
    debugDecreaseKey = false;
    _comparator = comparator;
  }

  /** return true if this heap is empty, false otherwise */
  public boolean isEmpty() {
    assert(count == 0 || rootWithMinKey != null);
    return count == 0;
  }

  /** Return the HeapNode with the currently minimal key. It is possible that
   *  the heap contains multiple elements with equal minimal key, in this case,
   *  which node is returned is not determined.
   */
  public HeapNode<Data> minimum() {
    if (rootWithMinKey == null)
      throw new EmptyHeapException("no minimum element, heap is empty");
    return rootWithMinKey;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("  count=").append(count).append("  roots=");
    if (rootWithMinKey != null)
      sb.append(rootWithMinKey.printAll());
    return sb.toString();
  }

  /** Merge heap other into this heap. After the operation, other will be empty
   */
  public void merge(FibonacciHeapBase<Data> other) {
    // Fibonacci-Heap-Union
    rootWithMinKey.insert(other.rootWithMinKey);
    if (rootWithMinKey == null
        || (other.rootWithMinKey != null
            && _comparator.compare(other.rootWithMinKey.data,
                rootWithMinKey.data) < 0)){
      this.rootWithMinKey = other.rootWithMinKey;
    }
    count += other.count;
    // set other heap empty
    other.rootWithMinKey = null;
    other.count = 0;
  }

  public int size() {
    return count;
  }

  /** Insert a new pair of data and key into this heap, and return an object
   *  that represents the pair in this heap.
   *
   *  @return a HeapNode that contains the key and data objects and can be used
   *          to decrease the key.
   */
  public HeapNode<Data> insert(Data d) {
    if (debug) System.out.println("insert " + d);
    ++count;
    // create a new tree with a single myKey:
    return insertNode(new HeapNode<Data>(d));
  }


  /** remove the minimum element from this heap.
  *
  *  @prerequisite the heap may not be empty, otherwise, an EmptyHeapException
  */
  public void removeMinimum() {  // Fibonacci-Heap-Extract-Min, CONSOLIDATE
   if (rootWithMinKey == null)
     throw new EmptyHeapException("trying to remove from an empty heap");

   if (debug) System.out.println("removeMinimum");
   --count;

   /// Phase 1: Make all the removed root's children new roots:
   // Make all children of root new roots:
   if (rootWithMinKey.child != null) {
     if (debugRemoveMin) {
       System.out.println("root's children: " +
                          rootWithMinKey.child.printAll());
     }
     HeapNode<Data> c = rootWithMinKey.child;
     do {
       c.parent = null;
       c = c.next;
     } while (c != rootWithMinKey.child);
     rootWithMinKey.child = null; // removed all children
     rootWithMinKey.insert(c);
   }
   if (debugRemoveMin) {
     System.out.println("  roots after inserting children: " + toString());
   }


   /// Phase 2-a: handle the case where we delete the last myKey:
   if (rootWithMinKey.next == rootWithMinKey) {
     if (debugRemoveMin) System.out.println("  removed the last");
     assert(count == 0);
     rootWithMinKey = null;
     return;
   }

   /// Phase 2: merge roots with the same degree:
   // make room for a new degree
   //degreeRoots.setSize(maxDegree + 1);
   HeapNode<Data> currentPointer = rootWithMinKey.next;
   int maxDegree = 0;
   do {
     if (currentPointer.degree > maxDegree)
       maxDegree = currentPointer.degree;
     currentPointer = currentPointer.next;
   } while (currentPointer != rootWithMinKey);
   Vector<HeapNode<Data>> degreeRoots = new Vector<HeapNode<Data>>(maxDegree + 2);
   for (int i = 0; i < maxDegree + 2; ++i) degreeRoots.add(null);
   currentPointer = rootWithMinKey.next;
   int currentDegree;
   do {
     currentDegree = currentPointer.degree;
     if (debugRemoveMin) {
       System.out.println("  roots starting from currentPointer: " +
                          currentPointer.printAll());
       System.out.println("  checking root " + currentPointer
                          + " with degree " + currentDegree);
     }

     HeapNode<Data> current = currentPointer;
     currentPointer = currentPointer.next;
      // merge the two roots with the same degree:
     while (degreeRoots.get(currentDegree) != null) {
       // another root with the same degree
       HeapNode<Data> other = degreeRoots.get(currentDegree);
       if (_comparator.compare(current.data, other.data) > 0) {
         HeapNode<Data> help = current;
         current = other;
         other = help;
       }
       // now current.key() <= other.key() - make other a child of current:
       other.remove(); // remove from list of roots
       current.addChild(other);
       if (debugRemoveMin)
         System.out.println("  added " + other + " as child of " + current);
       degreeRoots.set(currentDegree, null); //
       ++currentDegree;
       while (currentDegree >= degreeRoots.size())
         degreeRoots.add(null);
     }
     // keep the current root as the first of its degree in the degrees array:
     degreeRoots.set(currentDegree, current);
   } while (currentPointer != rootWithMinKey);

   /// Phase 3: remove the current root, and calculate the new rootWithMinKey:
   //delete rootWithMinKey;
   rootWithMinKey.degree = -1; // mark it as removed
   rootWithMinKey = null;

   for (int d = 0; d < degreeRoots.size(); ++d) {
     if (debugRemoveMin) System.out.print("  degree " + d + ": ");
     HeapNode<Data> current = degreeRoots.get(d);
     if (current != null) {
       if (debugRemoveMin) System.out.println(" " + current);

       current.next = current.previous = current;
       insertNode(current);
     } else {
       if (debugRemoveMin) System.out.println("  no node");
     }
   }
 }


  /** Signal that the value of data has been decreased
   *  The new key must be strictly smaller than the old one, otherwise, an
   *  IllegalArgumentException will be thrown.
   */
  public void decreaseKey(HeapNode<Data> node) {
    if (node.degree < 0)
      throw new IllegalArgumentException(
          "Trying to call decreaseKey with a removed heap node");
    // Check if the new key violates the heap invariant:
    HeapNode<Data> parent = node.parent;
    if (parent == null) { // root node - just make sure the minimum is correct
      if (_comparator.compare(node.data, rootWithMinKey.data) < 0)
        rootWithMinKey = node;
      return; // heap invariant not violated - nothing more to do
    } else if (_comparator.compare(parent.data, node.data) <= 0) {
      return; // heap invariant not violated - nothing more to do
    }

    for(;;) {
      parent.removeChild(node);
      insertNode(node);
      if (debugDecreaseKey) {
        System.out.println("  removed " + node + " as child of " + parent);
        System.out.println("  roots after remove: " + rootWithMinKey.printAll());
      }

      if (parent.parent == null) {
        // parent is a root - nothing more to do
        break;
      } else if (!parent.mark) {
        // parent is not a root and is not marked - just mark it
        parent.mark = true;
        break;
      } else {
        node = parent;
        parent = parent.parent;
      }
    }
  }

  /** Remove node from the heap. The value of the minusInfinity key must be
   *  smaller than any key on the heap, otherwise, and IllegalArgumentException
   *  will be thrown.
   */
  public void remove(final HeapNode<Data> node) {
    final Comparator<Data> save = _comparator;
    // Create a comparator that makes node the smallest of all entries
    _comparator = new Comparator<Data>() {
      @Override
      public int compare(Data o1, Data o2) {
        if (o1 == node.data)
          return (o2 == node.data) ? 0 : -1;
        if (o2 == node.data) return 1;
        return save.compare(o1, o2);
      }
    };
    decreaseKey(node);
    removeMinimum();
    _comparator = save;
  }

  public Data getValue(HeapNode<Data> h) {
    return h.data;
  }

}
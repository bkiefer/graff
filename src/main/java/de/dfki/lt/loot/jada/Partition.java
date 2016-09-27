package de.dfki.lt.loot.jada;

import java.util.Iterator;

/** A union-find data structure to effectively store a partition of a set with
 *  n members
 */
public class Partition {

  /** The size of the partition, only correct for the representatives */
  private int[] size;

  /** The implementation of a tree (star) structure, tree[i] = j means edge
   *  i -> j is in the tree.
   *  Must be acyclic, except for loops, which signal the representative
   *  (which may be a singleton).
   */
  private int[] tree;

  /** The members of an equivalence class, starting from the representative.
   * In some sense the "inverse" of tree. members is the head of the
   * list and membersLast the tail.
   * membersLast[head] == i ==> members[i] == i
   */
  private int[] members;
  private int[] membersLast;

  /** Create a partition of n elements with each element is its own partition */
  public Partition(int n) {
    tree = new int[n];
    members = new int[n];
    membersLast = new int[n];
    size = new int[n];
    for (int i=0; i < n; i++) {
      tree[i] = i;
      members[i] = i;
      membersLast[i] = i;
      size[i] = 1;
    }
  }

  /** Find the representative of an equivalence class, doing path compression */
  public int findRepresentative (int a) {
    while (tree[a] != tree[tree[a]])
      a = tree[a] = tree[tree[a]];
    return tree[a];
  }

  /** Merge the equivalence classes of a and b and return the new
   *  representative.
   */
  public int union(int a, int b) {
    a = findRepresentative(a);
    b = findRepresentative(b);
    if (a == b)
      return b;
    if (size[a] > size[b]) {
      // exchange a and b --> merge b into a
      int h = b;
      b = a;
      a = h;
    }
    // merge a into b
    tree[a] = b;
    size[b] += size[a];
    // concatenate ListB + ListA
    int tailB = membersLast[b];
    members[tailB] = a;
    membersLast[b] = membersLast[a];
    return b;
  }

  /** Are a and b in the same equivalence class? */
  public boolean equiv(int a, int b) {
    return findRepresentative(a) == findRepresentative(b);
  }

  /** Iterate over the members of an equivalence class.
   * @param elt some element
   * @return an iterator over the members of the equivalence class elt belongs to.
   */
  public Iterable<Integer> getMembers(final int elt) {
    return new Iterable<Integer>() {
      int e = findRepresentative(elt);
      @Override
      public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
          @Override
          public boolean hasNext() {
            return (e >= 0);
          }

          @Override
          public Integer next() {
            // if (!hasNext())
            int result = e;
            e = members[e];
            if (result == e) { // tail of list reached
              e = -1;
            }
            return result;
          }
        };
      };
    };
  }


  /** Return the size of the equivalence class elt is a member of */
  public int size(int elt) {
    elt = findRepresentative(elt);
    return size[elt];
  }

}

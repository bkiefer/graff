package de.dfki.lt.loot.jada;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class IntIntTrie implements Comparable<IntIntTrie>, Iterable<Integer> {

  // The 'character' stored in this trie node
  int _value;
  // if this node is a final node, this is the id of its Info node
  int _finalId;
  // the children of this trie node
  ArrayList<IntIntTrie> _subs;

  private IntIntTrie(int val) {
    _value = val;
    _subs = null;
    _finalId = -1;
  }

  /** the _value in the root node contains the new ID counter of the trie, and
   *  the _finalId potentially the Kleene star character
   */
  public IntIntTrie() { this(0); }

  /** Set the kleene star character */
  // public setStar(char c) { _finalId = c; }

  /** helper for private addSequence method, returns a given sibling Trie node,]
   *  if one exists, or creates a new one.
   *
   *  TODO: Seeking is linear, might need improvement
   */
  private IntIntTrie find(int id) {
    IntIntTrie node = null;
    if (_subs == null) {
      node = new IntIntTrie(id);
      _subs = new ArrayList<IntIntTrie>(1);
      _subs.add(node);
    } else {
      for (IntIntTrie sub : _subs) {
        if (sub._value == id) {
          node = sub;
          break;
        }
      }
      if (node == null) {
        node = new IntIntTrie(id);
        _subs.add(node);
      }
    }
    return node;
  }

  /** private helper for public addSequence methods */
  private int addSequence(int[] ids, int newId, int pos) {
    if (pos == ids.length) {
      if (_finalId < 0)
        _finalId = newId;
      return _finalId;
    }
    IntIntTrie node = find(ids[pos]);
    return node.addSequence(ids, newId, ++pos);
  }

  /** Add this int sequence to the trie */
  public int addSequence(int ... seq) {
    int newId = addSequence(seq, _value, 0);
    if (newId == _value)
      ++_value;
    return newId;
  }

  /** Add the char sequence of string to this trie (interpreted as int[]) */
  public int addSequence(String string) {
    int[] seq = new int[string.length()];
    for(int i = 0; i < seq.length; ++i) {
      seq[i] = string.charAt(i);
    }
    return addSequence(seq);
  }

  @Override
  public int compareTo(IntIntTrie o2) { return this._value - o2._value; }

  /** Optimize trie for fast access */
  public void optimize() {
    if (_subs == null) return;
    Collections.sort(_subs);
    for (IntIntTrie sub : _subs) sub.optimize();
  }

  public int size() {
    return _value;
  }

  /** walk a given branch or create one. Uses binarySearch for finding the
   *  right branch, and inserts in the right position for fast access.
   */
  public IntIntTrie add(int val) {
    if (_subs == null) {
      _subs = new ArrayList<IntIntTrie>();
      _subs.add(new IntIntTrie(val));
      return _subs.get(0);
    }
    IntIntTrie toFind = new IntIntTrie(val);
    int pos = Collections.binarySearch(_subs, toFind);
    if (pos < 0) {
      pos = - pos - 1;
      _subs.add(pos, new IntIntTrie(val));
    }
    return _subs.get(pos);
  }

  public void setFinalId(int id) {
    _finalId = id;
  }

  public Iterator<Integer> iterator() {
    return new Iterator<Integer>() {
      Iterator<IntIntTrie> _curr = _subs.iterator();

      @Override
      public boolean hasNext() {
        return _curr.hasNext();
      }

      @Override
      public Integer next() {
        return _curr.next()._value;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  public void walkTrie(IntIntTrieWalker walker) {
    walker.startNode(this);
    if (_subs != null) {
      for (IntIntTrie val : _subs) {
        walker.beforeEdge(val._value);
        val.walkTrie(walker);
        walker.afterEdge(val._value);
      }
    }
    walker.endNode(this);
  }

  @Override
  public String toString() {
    return "{" + _value + ", "  + _finalId + "}";
  }

  /* ######################################################################
   * DictIterator methods
   * ###################################################################### */

  /** try to find a continuation of the current node */
  public IntIntTrie extend(int val) {
    if (_subs == null)
      return null;
    IntIntTrie toFind = new IntIntTrie(val);
    int pos = Collections.binarySearch(_subs, toFind);
    return pos < 0 ? null : _subs.get(pos);
  }

  /** Return the node under seq */
  public IntIntTrie find(int[] seq) {
    IntIntTrie curr = this;
    for(int i : seq) {
      curr = curr.extend(i);
      if (curr == null) break;
    }
    return curr;
  }

  /** Return the id corresponding to this node */
  public int getFinalId() {
    return _finalId;
  }

  /** is this a final node? */
  public boolean isFinal() {
    return _finalId >= 0;
  }

}

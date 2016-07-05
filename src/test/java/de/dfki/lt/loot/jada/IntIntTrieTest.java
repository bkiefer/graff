package de.dfki.lt.loot.jada;

import static org.junit.Assert.*;

import org.junit.Test;

public class IntIntTrieTest {
  @Test
  public void add1() {
    int[] in = { 1, 2, 3 };
    IntIntTrie foo = new IntIntTrie();
    foo.addSequence(in);
    IntIntTrie curr = foo;
    for (int i : in) curr = curr.extend(i);
    assertTrue(curr.isFinal());
    assertEquals(foo.size() - 1, curr.getFinalId());
  }

  @Test
  public void add2() {
    int[] in = { 1, 2, 3 };
    int[] in2 = { 1, 2, 3, 4 };
    IntIntTrie foo = new IntIntTrie();
    int a = foo.addSequence(in);
    int b = foo.addSequence(in2);
    assertTrue(a != b);
    assertTrue(foo.find(in2).getFinalId() == b);
    assertTrue(foo.find(in).getFinalId() == a);
  }

  @Test
  public void find1() {
    int[] in = { 1, 2, 3};
    int[] in1 = { 1 };
    int[] in2 = { 1, 2};
    IntIntTrie foo = new IntIntTrie();
    int a = foo.addSequence(in);
    assertFalse(foo.find(in1).isFinal());
    assertFalse(foo.find(in2).isFinal());
    assertTrue(foo.find(in).isFinal());
    assertTrue(foo.find(in).getFinalId() == a);
  }

  @Test
  public void find2() {
    int[] in = { 1, 2, 3};
    int[] in2 = { 1, 2, 1 };
    IntIntTrie foo = new IntIntTrie();
    foo.addSequence(in);
    assertNull(foo.find(in2));
    foo.addSequence(in2);
    foo.optimize();
    assertEquals(0, foo.find(in).getFinalId());
    assertEquals(1, foo.find(in2).getFinalId());
  }

  @Test
  public void testWalk() {
    int[] in = { 1, 2, 3};
    int[] in2 = { 1, 2, 1 };
    IntIntTrie foo = new IntIntTrie();
    foo.addSequence(in);
    foo.addSequence(in2);
    final int[] count = {0};
    foo.walkTrie(new IntIntTrieWalker() {

      @Override
      public void startNode(IntIntTrie node) {
        // TODO Auto-generated method stub
        ++count[0];
      }

      @Override
      public void beforeEdge(int val) {
        // TODO Auto-generated method stub

      }

      @Override
      public void afterEdge(int val) {
        // TODO Auto-generated method stub

      }

      @Override
      public void endNode(IntIntTrie node) {
        // TODO Auto-generated method stub

      }});
    assertEquals(5, count[0]);
  }

  @Test
  public void testAdd() {
    int[] in = { 1, 2, 2};
    int[] in2 = { 1, 2, 1 };
    int[] in3 = { 1, 2, 3 };
    IntIntTrie foo = new IntIntTrie();
    IntIntTrie curr = foo;
    for (int i : in) curr = curr.add(i);
    curr.setFinalId(0);
    curr = foo;
    for (int i : in2) curr = curr.add(i);
    curr.setFinalId(1);
    curr = foo;
    for (int i : in3) curr = curr.add(i);
    curr.setFinalId(2);
    assertEquals(0, foo.find(in).getFinalId());
    assertEquals(1, foo.find(in2).getFinalId());
    assertEquals(2, foo.find(in3).getFinalId());
  }

  @Test
  public void testExtend() {
    IntIntTrie foo = new IntIntTrie();
    foo.add(1);
    foo.add(2);
    foo.add(3);
    IntIntTrie curr = foo.extend(2);
    assertNotNull(curr);
    curr = foo.extend(72);
    assertNull(curr);
    assertNull(foo.extend(2).extend(9));
  }

  @Test
  public void testIterator() {
    IntIntTrie foo = new IntIntTrie();
    int[] test = { 1,2,3 };
    for (int i : test) foo.add(i);
    int j = 0;
    for (int i : foo) assertEquals(test[j++], i);
  }

}

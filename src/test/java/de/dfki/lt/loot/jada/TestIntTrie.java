package de.dfki.lt.loot.jada;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestIntTrie {
  @Test
  public void add1() {
    int[] in = { 1, 2, 3};
    IntTrie<String> foo = new IntTrie<String>();
    String a = "abce";
    assertTrue(foo.getOrAdd(in, a) == a);
  }

  @Test
  public void add2() {
    int[] in = { 1, 2, 3};
    IntTrie<String> foo = new IntTrie<String>();
    String a = "abce";
    assertTrue(foo.getOrAdd(in, a) == a);
    String b = "asdf";
    assertFalse(foo.getOrAdd(in, b) ==b);
  }

  @Test
  public void find1() {
    int[] in = { 1, 2, 3};
    IntTrie<String> foo = new IntTrie<String>();
    String a = "abce";
    assertTrue(foo.getOrAdd(in, a) == a);
    assertNotNull(foo.findSequence(in));
  }

  @Test
  public void find2() {
    int[] in = { 1, 2, 3};
    int[] in2 = { 1, 2, 1 };
    IntTrie<String> foo = new IntTrie<String>();
    String a = "abce";
    String b = "cdef";
    foo.getOrAdd(in, a);
    assertNull(foo.findSequence(in2));
    foo.getOrAdd(in2, b);
    assertTrue(foo.getValue(in) == a);
    assertTrue(foo.getValue(in2) == b);
  }

  @Test
  public void find3() {
    int[] in = { 1, 2, 3};
    int[] in2 = { 1, 2, 3, 4 };
    IntTrie<String> foo = new IntTrie<String>();
    String a = "abce";
    String b = "cdef";
    foo.getOrAdd(in, a);
    assertNull(foo.findSequence(in2));
    foo.getOrAdd(in2, b);
    assertTrue(foo.getValue(in) == a);
    assertTrue(foo.getValue(in2) == b);
  }


}

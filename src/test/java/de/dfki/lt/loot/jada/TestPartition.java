package de.dfki.lt.loot.jada;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

public class TestPartition {

  @Test
  public void testCreation() {
    Partition p = new Partition(5);
    for(int i = 0; i < 5; ++i) {
      assertEquals(i, p.findRepresentative(i));
    }
    assertEquals(5, p.size());
  }

  @Test
  public void testUnion() {
    Partition p = new Partition(10);
    p.union(3, 5);
    p.union(5, 3);
    for(int i = 0; i < 10; ++i) {
      if (i != 5 && i != 3)
        assertEquals(i, p.findRepresentative(i));
    }

    for(int i = 0; i < 10; ++i) {
      for(int j = 0; j < 10; ++j) {
        if ((i == 3 && j == 5) || (i == 5 && j == 3) || (i == j))
          assertTrue(p.equiv(i, j));
        else
          assertFalse(p.equiv(i, j));
      }
    }
  }

  @Test
  public void testUnion2() {
    Partition p = new Partition(10);
    assertEquals(1, p.size(3));
    p.union(3, 5);
    assertEquals(2, p.size(3));
    assertEquals(2, p.size(5));
    p.union(7, 1);
    assertEquals(2, p.size(1));
    assertEquals(2, p.size(7));
    p.union(9, 1);
    assertEquals(3, p.size(1));
    assertEquals(3, p.size(7));
    assertEquals(3, p.size(9));
    p.union(9, 3);
    assertEquals(5, p.size(1));
    assertEquals(5, p.size(7));
    assertEquals(5, p.size(9));
    assertEquals(5, p.size(3));
    assertEquals(5, p.size(5));
    for(int i = 0; i < 10; ++i) {
      for(int j = 0; j < 10; ++j) {
        if (((i & 1) != 0 && (j & 1) != 0) || (i == j))
          assertTrue(p.equiv(i, j));
        else
          assertFalse(p.equiv(i, j));
      }
    }
  }

  @Test
  public void testMembers() {
    Partition p = new Partition(10);
    HashSet<Integer> set = new HashSet<>();
    for (int i : p.getMembers(3)) set.add(i);
    assertEquals(1, set.size());
    assertTrue(set.contains(3));
    p.union(3, 5);
    set.clear();
    for (int i : p.getMembers(3)) set.add(i);
    assertEquals(2, set.size());
    assertTrue(set.contains(3));
    assertTrue(set.contains(5));
    p.union(7, 1);
    set.clear();
    for (int i : p.getMembers(3)) set.add(i);
    assertEquals(2, set.size());
    assertTrue(set.contains(3));
    assertTrue(set.contains(5));
    set.clear();
    for (int i : p.getMembers(1)) set.add(i);
    assertEquals(2, set.size());
    assertTrue(set.contains(1));
    assertTrue(set.contains(7));
    p.union(9, 1);
    set.clear();
    for (int i : p.getMembers(1)) set.add(i);
    assertEquals(3, set.size());
    assertTrue(set.contains(1));
    assertTrue(set.contains(7));
    assertTrue(set.contains(9));
    p.union(9, 3);
    set.clear();
    for (int i : p.getMembers(1)) set.add(i);
    assertEquals(5, set.size());
    assertTrue(set.contains(1));
    assertTrue(set.contains(7));
    assertTrue(set.contains(9));
    assertTrue(set.contains(3));
    assertTrue(set.contains(5));
  }

}

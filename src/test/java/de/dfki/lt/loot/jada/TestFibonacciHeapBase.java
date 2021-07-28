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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;
import java.util.Vector;

import org.junit.Test;

import de.dfki.lt.loot.jada.FibonacciHeapBase.HeapNode;

public class TestFibonacciHeapBase {

  private class MyData {
    public MyData(String ss, int ii) { s = ss; i = ii; }
    String s;
    double i;
    public String toString(){ return "<"+s+","+i+">"; }
  }

  private class MDComparator implements Comparator<MyData> {
    public int compare(MyData d1, MyData d2) {
      return (int)Math.signum(d1.i - d2.i);
    }
  }


  @Test
  public void create() {
    FibonacciHeapBase<MyData> h =
        new FibonacciHeapBase<MyData>(new MDComparator());
    assertNotNull(h);
  }

  @Test
  public void isEmpty() {
    FibonacciHeapBase<MyData> h =
        new FibonacciHeapBase<MyData>(new MDComparator());
    assertTrue(h.isEmpty());
  }

  @Test(expected= EmptyHeapException.class)
  public void illegalEmptyRemove() {
    FibonacciHeapBase<MyData> h =new FibonacciHeapBase<MyData>(new MDComparator());
    h.removeMinimum();
  }

  @Test(expected= EmptyHeapException.class)
  public void illegalEmptyAccess() {
    FibonacciHeapBase<MyData> h =
        new FibonacciHeapBase<MyData>(new MDComparator());
    h.minimum();
  }

  @Test
  public void inOut0() {
    Object[] testInput = { "a",4, "b",2, "c", 7, "d", 5, "e", 1, "f", 8 } ;
    Object[] testOutput = { "e", 1, "b",2, "a",4, "d", 5, "c", 7, "f", 8 } ;
    FibonacciHeapBase<MyData> h = new FibonacciHeapBase<MyData>(new MDComparator());
    for (int i = 0; i < testInput.length; i += 2) {
      h.insert(new MyData((String) testInput[i], (Integer) testInput[i + 1]));
    }
    int j = 0;
    while (! h.isEmpty()) {
      HeapNode<MyData> node = h.minimum();
      assertEquals(testOutput[j], h.getValue(node).s);
      assertTrue((Integer)testOutput[j+1] == h.getValue(node).i);
      j += 2;
      h.removeMinimum();
    }
  }

  @Test
  public void inOut1() {
    Object[] testInput = { "a",4, "b",2, "c", 7, "d", 5, "e", 1, "f", 8 } ;
    Object[] testOutput = { "e", 1, "b",2, "a",4, "d", 5, "c", 7, "f", 8 } ;
    FibonacciHeapBase<MyData> h =
        new FibonacciHeapBase<MyData>(new MDComparator());
    for (int i = testInput.length - 2 ; i >= 0; i -= 2) {
      h.insert(new MyData((String) testInput[i], (Integer) testInput[i + 1]));
    }
    int j = 0;
    while (! h.isEmpty()) {
      HeapNode<MyData> node = h.minimum();
      assertEquals(testOutput[j], h.getValue(node).s);
      assertTrue((Integer)testOutput[j+1] == h.getValue(node).i);
      j += 2;
      h.removeMinimum();
    }
  }

  @Test
  public void decreaseKeys() {
    Object[] testInput = { "a",400, "b",200, "c", 70, "d", 50, "e", 10,
        "f", 80 } ;
    Object[] testOutput = { "e", 10, "d", 50, "a",40, "b",20 , "c", 7, "d", 5,
        "d", 5 } ;
    FibonacciHeapBase<MyData> h =
        new FibonacciHeapBase<MyData>(new MDComparator());
    //h.debug = h.debugDecreaseKey = true;

    Vector <HeapNode<MyData>> nodes =
      new Vector <HeapNode<MyData>>(testInput.length / 2);
    for (int i = 0; i < testInput.length; i += 2) {
      nodes.add(h.insert(new MyData((String) testInput[i], (Integer) testInput[i + 1])));
    }

    int j = 0;
    assertEquals(h.getValue(h.minimum()).s, testOutput[j++]);
    assertTrue(h.getValue(h.minimum()).i == (Integer) testOutput[j++]);
    h.removeMinimum();
    assertEquals(h.getValue(h.minimum()).s, testOutput[j++]);
    assertTrue(h.getValue(h.minimum()).i == (Integer) testOutput[j++]);

    nodes.set(4, null);

    for (int i=0; i<nodes.size(); ++i) {
      if (nodes.get(i) == null) // minimum - already removed
        continue;
      h.getValue(nodes.get(i)).i = h.getValue(nodes.get(i)).i/10;
      h.decreaseKey(nodes.get(i));
      assertEquals(h.getValue(h.minimum()).s, testOutput[j++]);
      assertTrue(h.getValue(h.minimum()).i == (Integer) testOutput[j++]);
    }
  }


  @Test
  public void addAfterDecrease() {
    Object[] testInput = { "c", 70, "b",200, "a",400, "d", 50, "e", 10,
        "f", 80 } ;
    Object[] testOutput = { "h", 4, "f", 8, "e", 10, "b", 20, "g", 30, "d", 50,
        "c", 70, "a", 400 } ;
    FibonacciHeapBase<MyData> h =
        new FibonacciHeapBase<MyData>(new MDComparator());
    //h.debug = h.debugDecreaseKey = true;

    Vector <HeapNode<MyData>> nodes =
      new Vector <HeapNode<MyData>>(testInput.length / 2);
    for (int i = 0; i < testInput.length; i += 2) {
      nodes.add(h.insert(new MyData((String) testInput[i],
          (Integer) testInput[i + 1])));
    }

    for (int i=1; i<nodes.size(); i += 4 ) {
      h.getValue(nodes.get(i)).i = h.getValue(nodes.get(i)).i/10;
      h.decreaseKey(nodes.get(i));
    }
    h.insert(new MyData("g", 30));
    h.insert(new MyData("h", 4));

    int j = 0;
    while (! h.isEmpty()) {
      HeapNode<MyData> node = h.minimum();
      assertEquals(testOutput[j], h.getValue(node).s);
      assertTrue((Integer)testOutput[j+1] == h.getValue(node).i);
      j += 2;
      h.removeMinimum();
    }
  }

  @Test
  public void mergeHeaps() {
    Object[] testInput1 = { "a",400, "b",200, "c", 70, "d", 50, "e", 10,
        "f", 80 } ;
    Object[] testInput2 = { "e", 10, "d", 50, "a",40, "b", 20 , "c", 7, "d", 5,
        "d", 5 } ;
    Object[] testOutput = { "d", 5, "d", 5, "c", 7, "e", 10, "e", 10, "b", 20,
        "a", 40, "d", 50, "d", 50, "c", 70, "f", 80, "b", 200, "a", 400 };
    FibonacciHeapBase<MyData> h1 =
        new FibonacciHeapBase<MyData>(new MDComparator());
    //h1.debug = h1.debugRemoveMin = true;
    for (int i = 0; i < testInput1.length; i += 2) {
      h1.insert(new MyData((String) testInput1[i], (Integer) testInput1[i + 1]));
    }
    FibonacciHeapBase<MyData> h2 =
        new FibonacciHeapBase<MyData>(new MDComparator());
    //h2.debug = h2.debugDecreaseKey = h2.debugRemoveMin = true;
    for (int i = 0; i < testInput2.length; i += 2) {
      h2.insert(new MyData((String) testInput2[i], (Integer) testInput2[i + 1]));
    }
    h1.merge(h2);
    assertTrue(h2.isEmpty());
    int j = 0;
    while (! h1.isEmpty()) {
      HeapNode<MyData> node = h1.minimum();
      assertEquals(testOutput[j], h1.getValue(node).s);
      assertTrue((Integer)testOutput[j+1] == h1.getValue(node).i);
      j += 2;
      h1.removeMinimum();
    }
  }

  @Test
  public void removeNode() {
    Object[] testInput = { "a",4, "b",2, "c", 7, "d", 5, "e", 1, "f", 8 } ;
    Object[] testOutput = { "e", 1, "b",2, "a",4, "c", 7, "f", 8 } ;
    FibonacciHeapBase<MyData> h =
        new FibonacciHeapBase<MyData>(new MDComparator());
    Vector<HeapNode<MyData>> nodes = new Vector<HeapNode<MyData>>(6);
    for (int i = 0; i < testInput.length; i += 2) {
      nodes.add(h.insert(new MyData((String) testInput[i], (Integer) testInput[i + 1])));
    }
    h.remove(nodes.get(3));
    int j = 0;
    while (! h.isEmpty()) {
      HeapNode<MyData> node = h.minimum();
      assertEquals(testOutput[j], h.getValue(node).s);
      assertTrue((Integer)testOutput[j+1] == h.getValue(node).i);
      j += 2;
      h.removeMinimum();
    }
  }
}

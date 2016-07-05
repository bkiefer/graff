package de.dfki.lt.loot.jada;

import static de.dfki.lt.loot.util.TestUtil.getTestResourceDir;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.junit.Test;

import de.dfki.lt.loot.jada.FibonacciHeap.HeapNode;

public class FibonacciHeapTest {

  @Test
  public void create() {
    FibonacciHeap<String, Integer> h = new FibonacciHeap<String, Integer>();
    assertNotNull(h);
  }


  @Test(expected= IllegalArgumentException.class)
  public void illegalIncreaseKey() {
    FibonacciHeap<Integer, String> h = new FibonacciHeap<Integer, String>();
    HeapNode<Integer, String> node = h.insert("a", 10);
    h.decreaseKey(node, 20);
  }


  @Test
  public void inOut0() {
    Object[] testInput = { "a",4, "b",2, "c", 7, "d", 5, "e", 1, "f", 8 } ;
    Object[] testOutput = { "e", 1, "b",2, "a",4, "d", 5, "c", 7, "f", 8 } ;
    FibonacciHeap<Integer, String> h = new FibonacciHeap<Integer, String>();
    for (int i = 0; i < testInput.length; i += 2) {
      h.insert((String) testInput[i], (Integer) testInput[i + 1]);
    }
    int j = 0;
    while (! h.isEmpty()) {
      HeapNode<Integer, String> node = h.minimum();
      assertEquals(testOutput[j], node.getData());
      assertEquals(testOutput[j+1], node.getKey());
      j += 2;
      h.removeMinimum();
    }
  }

  @Test
  public void inOut1() {
    Object[] testInput = { "a",4, "b",2, "c", 7, "d", 5, "e", 1, "f", 8 } ;
    Object[] testOutput = { "e", 1, "b",2, "a",4, "d", 5, "c", 7, "f", 8 } ;
    FibonacciHeap<Integer, String> h = new FibonacciHeap<Integer, String>();
    for (int i = testInput.length - 2 ; i >= 0; i -= 2) {
      h.insert((String) testInput[i], (Integer) testInput[i + 1]);
    }
    int j = 0;
    while (! h.isEmpty()) {
      HeapNode<Integer, String> node = h.minimum();
      assertEquals(testOutput[j], node.getData());
      assertEquals(testOutput[j+1], node.getKey());
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
    FibonacciHeap<Integer, String> h = new FibonacciHeap<Integer, String>();
    //h.debug = h.debugDecreaseKey = true;

    Vector <HeapNode<Integer, String>> nodes =
      new Vector <HeapNode<Integer, String>>(testInput.length / 2);
    for (int i = 0; i < testInput.length; i += 2) {
      nodes.add(h.insert((String) testInput[i], (Integer) testInput[i + 1]));
    }

    int j = 0;
    assertEquals(h.minimum().getData(), testOutput[j++]);
    assertEquals(h.minimum().getKey(), testOutput[j++]);
    h.removeMinimum();
    assertEquals(h.minimum().getData(), testOutput[j++]);
    assertEquals(h.minimum().getKey(), testOutput[j++]);

    nodes.set(4, null);

    for (int i=0; i<nodes.size(); ++i) {
      if (nodes.get(i) == null) // minimum - already removed
        continue;
      h.decreaseKey(nodes.get(i), nodes.get(i).getKey()/10);
      assertEquals(h.minimum().getData(), testOutput[j++]);
      assertEquals(h.minimum().getKey(), testOutput[j++]);
    }
  }


  @Test
  public void addAfterDecrease() {
    Object[] testInput = { "c", 70, "b",200, "a",400, "d", 50, "e", 10,
        "f", 80 } ;
    Object[] testOutput = { "h", 4, "f", 8, "e", 10, "b", 20, "g", 30, "d", 50,
        "c", 70, "a", 400 } ;
    FibonacciHeap<Integer, String> h = new FibonacciHeap<Integer, String>();
    //h.debug = h.debugDecreaseKey = true;

    Vector <HeapNode<Integer, String>> nodes =
      new Vector <HeapNode<Integer, String>>(testInput.length / 2);
    for (int i = 0; i < testInput.length; i += 2) {
      nodes.add(h.insert((String) testInput[i], (Integer) testInput[i + 1]));
    }

    for (int i=1; i<nodes.size(); i += 4 ) {
      h.decreaseKey(nodes.get(i), nodes.get(i).getKey()/10);
    }
    h.insert("g", 30);
    h.insert("h", 4);

    int j = 0;
    while (! h.isEmpty()) {
      HeapNode<Integer, String> node = h.minimum();
      assertEquals(testOutput[j], node.getData());
      assertEquals(testOutput[j+1], node.getKey());
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
    FibonacciHeap<Integer, String> h1 = new FibonacciHeap<Integer, String>();
    //h1.debug = h1.debugRemoveMin = true;
    for (int i = 0; i < testInput1.length; i += 2) {
      h1.insert((String) testInput1[i], (Integer) testInput1[i + 1]);
    }
    FibonacciHeap<Integer, String> h2 = new FibonacciHeap<Integer, String>();
    //h2.debug = h2.debugDecreaseKey = h2.debugRemoveMin = true;
    for (int i = 0; i < testInput2.length; i += 2) {
      h2.insert((String) testInput2[i], (Integer) testInput2[i + 1]);
    }
    h1.merge(h2);
    assertTrue(h2.isEmpty());
    int j = 0;
    while (! h1.isEmpty()) {
      HeapNode<Integer, String> node = h1.minimum();
      assertEquals(testOutput[j], node.getData());
      assertEquals(testOutput[j+1], node.getKey());
      j += 2;
      h1.removeMinimum();
    }
  }

  @Test
  public void removeNode() {
    Object[] testInput = { "a",4, "b",2, "c", 7, "d", 5, "e", 1, "f", 8 } ;
    Object[] testOutput = { "e", 1, "b",2, "a",4, "c", 7, "f", 8 } ;
    FibonacciHeap<Integer, String> h = new FibonacciHeap<Integer, String>();
    Vector<HeapNode<Integer, String>> nodes =
      new Vector<HeapNode<Integer, String>>(6);
    for (int i = 0; i < testInput.length; i += 2) {
      nodes.add(h.insert((String) testInput[i], (Integer) testInput[i + 1]));
    }
    h.remove(nodes.get(3));
    int j = 0;
    while (! h.isEmpty()) {
      HeapNode<Integer, String> node = h.minimum();
      assertEquals(testOutput[j], node.getData());
      assertEquals(testOutput[j+1], node.getKey());
      j += 2;
      h.removeMinimum();
    }
  }

  /*
  @Test
  public void stressTest0() {
    Random r = new Random(new Date().getTime());
    int noOfTrials = 1000;
    int maxHeapSize = 7000;
    FibonacciHeapBase<Integer> fh = new FibonacciHeapBase<Integer>(
        new Comparator<Integer>(){
          @Override
          public int compare(Integer o1, Integer o2) {
            return o1 - o2;
          }
        });
    List<Integer> elts = new LinkedList<Integer>();
    for (int trials = 0; trials < noOfTrials; ++trials) {
      for (int i = 0; i < maxHeapSize; ++i) {
        int next = r.nextInt(maxHeapSize);
        elts.add(next);
        fh.insert(next);
      }
      Collections.sort(elts);
      while (! fh.isEmpty()) {
        Integer out = fh.getValue(fh.minimum());
        fh.removeMinimum();
        assertEquals(elts.get(0), out);
        elts.remove(0);
      }
    }
  }

  private class Int implements Comparable<Int> {
    int val;

    public Int(int i) { val =i; }

    @Override
    public int compareTo(Int arg0) {
      return val - arg0.val;
    }
  }


  @Test
  public void stressTest1() {
    Random r = new Random(new Date().getTime());
    int noOfTrials = 1000;
    int maxHeapSize = 7000;
    FibonacciHeap<Int, Int> fh = new FibonacciHeap<Int, Int>();
    List<HeapNode> elts = new LinkedList<HeapNode>();
    for (int trials = 0; trials < noOfTrials; ++trials) {
      for (int i = 0; i < maxHeapSize; ++i) {
        int next = r.nextInt(maxHeapSize);
        elts.add(fh.insert(new Int(next), new Int(next)));
      }
      while (! fh.isEmpty()) {
        if (r.nextBoolean()) {
          HeapNode h = fh.minimum();
          Integer out = fh.getData().val;
          fh.removeMinimum();
          assertTrue(elts.remove(h));
        } else {
          HeapNode h = elts.get(r.nextInt(elts.size()));
          int out = fh.getKey().val;
          fh.decreaseKey(h, new Int(out - 1));
        }
      }
    }
  }
  */

  private static void readTestFile(String fileName)
      throws NumberFormatException, IOException {
    BufferedReader in = new BufferedReader(new FileReader(fileName));
    FibonacciHeap<Double, Integer> h =
        new FibonacciHeap<Double, Integer>();
    String line;
    HashMap<Integer, HeapNode<Double, Integer>> nodes =
        new HashMap<Integer, HeapNode<Double, Integer>>();
    while ((line = in.readLine()) != null) {
      String[] fields = line.split("\\s+");
      int id = Integer.parseInt(fields[1]);
      HeapNode<Double, Integer> n;
      double current = Double.parseDouble(fields[2]);
      switch(fields[0].charAt(0)) {
      case 'd': // decrease Key
        n = nodes.get(id);
        assertNotNull(n);
        double lower = Double.parseDouble(fields[3]);
        assertTrue(current > lower);
        assertTrue(current == n.getKey());
        h.decreaseKey(n, lower);
        break;
      case 'r': // remove minimum
        n = h.minimum();
        h.removeMinimum();
        assertTrue(n == nodes.get(id));
        nodes.remove(id);
        assertTrue(current == n.getKey());
        break;
      case 'i':
        assertFalse(nodes.containsKey(id));
        nodes.put(id, h.insert(id, current));
        break;
      }
    }
    // System.out.println(h.size());
  }

  @Test
  public void testAllFiles() throws NumberFormatException, IOException {
    for (File f : new File(getTestResourceDir(), "fibonacci").listFiles()) {
      if (f.isFile())
        readTestFile(f.getAbsolutePath());
    }
  }

  public static void main(String[] args)
      throws NumberFormatException, IOException {
    readTestFile(new File(getTestResourceDir(), "fibonacci/fibtest.txt")
    .getAbsolutePath());
  }
}

package de.dfki.lt.loot.fsa;

import static de.dfki.lt.loot.digraph.io.GraphPrinterFactory.printGraph;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import de.dfki.lt.loot.digraph.TestDirectedGraph;
import de.dfki.lt.loot.fsa.algo.Minimization;
import de.dfki.lt.loot.fsa.algo.MinimizationBrzowski;

public class TestMinimization {
  String[] in = {
      "0%0 a 1%0%1",
      "0%0 a 1%0 b 2%0 c 2%0 d 3%0 e 4%4 f 3%1%2%3",
      "0%0 a 1%1 b 2%0 c 3%3 d 4%0 b 5%5 d 6%2%4%6",
      "0%0 7 1%1 6 0%1 8 2%1 9 3%2 7 4%4 6 5%4 9 3%5 7 6%6 6 5%6 9 3%1%4%6%3",
      "0%0 a 1%1 b 2%0 a 3%3 b 2%2",
      "0%0 a 1%0 b 2%0 c 3%4 d 5%5 e 6%2 f 4%2 d 5%1 f 4%1 d 5%3 b 7%3 g 8%9 e 10%7 d 9%8 b 7%5%6%9%10",
  };

  String[] out = {
      "0%0 a 1%0%1",
      "0%0 a 1%0 b 1%0 c 1%0 d 1%0 e 2%2 f 1%1",
      "0%0 a 1%1 b 2%0 c 3%0 b 3%3 d 2%2",
      "0%0 7 1%1 6 0%1 8 2%1 9 3%2 7 4%4 6 2%4 9 3%1%3%4",
      "0%0 a 1%1 b 2%2",
      "0%0 a 1%0 b 1%0 c 2%1 f 4%1 d 5%2 g 3%2 b 4%3 b 4%4 d 5%5 e 6%5%6",
  };


  final static boolean PRINT_GRAPHS = true;

  /**
   * Get the new state for the give state using the given state map. If not
   * already available, the new state will be created.
   *
   * @param state the state
   * @param stateMap mapping of states to new states
   * @return the new state
   */
  private int getNewState(CharFsa fsa, int state, Map<Integer, Integer> stateMap) {

    Integer newState = stateMap.get(state);
    if (null != newState) {
      return newState;
    }
    newState = fsa.newVertex();
    stateMap.put(state, newState);
    return newState;
  }

  /**
   * Initializes this automaton from text based format at the given path.
   *
   * @param textPath
   *          the path from where to read the text
   * @throws IOException
   */
  public int[] readFromText(String text, CharFsa fsa) {
    Map<Integer, Integer> stateMap = new HashMap<Integer, Integer>();

    String[] lines = text.split("%");

    // first line contains the initial state
    int initialState = Integer.parseInt(lines[0]);
    stateMap.put(initialState, getNewState(fsa, initialState, stateMap));
    fsa.setInitialState(getNewState(fsa, initialState, stateMap));

    String line;
    int l = 1;
    int edgeCount = 0;
    while (true) {
      // read edges
      line = lines[l++];
      String[] edgeParts = line.split(" ");
      if (edgeParts.length != 3) {
        // all edges have been read
        break;
      }
      edgeCount++;
      int startState = Integer.parseInt(edgeParts[0]);
      int endState = Integer.parseInt(edgeParts[2]);
      String label = edgeParts[1];
      fsa.newEdge(
        label.charAt(0),
        getNewState(fsa, startState, stateMap),
        getNewState(fsa, endState, stateMap));
    }

    // read final states;
    // line already contains the first final state
    fsa.setFinalState(getNewState(fsa, Integer.parseInt(line), stateMap));
    int finalStatesCount = 1;
    while (l < lines.length) {
      line = lines[l++];
      fsa.setFinalState(getNewState(fsa, Integer.parseInt(line), stateMap));
      finalStatesCount++;
    }
    //fsa.makeTotal();

    int [] result = { stateMap.size(), edgeCount, finalStatesCount };
    return result;
  }


  @Test
  public void testMinimizationHopcroft() {
    for (int i = 0; i < in.length; ++i) {
      CharFsa expected = new CharFsa();
      int[] expected_res = readFromText(out[i], expected);
      if (PRINT_GRAPHS) printGraph(expected, i+"_min1exp.dot");

      CharFsa fsa = new CharFsa();
      int[] res = readFromText(in[i], fsa);
      if (PRINT_GRAPHS) {
        printGraph(fsa, i+"_min0in.dot");
        System.out.println(String.format("%d alphabet symbols",
            fsa.getAlphabet().size()));
        System.out.println(String.format("%d states read", res[0]));
        System.out.println(String.format("%d edges read", res[1]));
        System.out.println(String.format("%d final states read", res[2]));
        System.out.println("states: " + fsa.getNumberOfActiveVertices());
      }
      Minimization.minimize(fsa, fsa.getComparator());
      if (PRINT_GRAPHS) printGraph(fsa, i+"_min2hout.dot");

      assertEquals("" + i, expected.getNumberOfActiveVertices(),
          fsa.getNumberOfActiveVertices());
      assertEquals("" + i, expected.getFinalStates().size(),
          fsa.getFinalStates().size());
      assertEquals("" + i, expected_res[1], TestDirectedGraph.countEdges(fsa));
    }
  }

  @Test
  public void testMinimizationBrzowski() {
    for (int i = 0; i < in.length; ++i) {
      CharFsa expected = new CharFsa();
      int[] expected_res = readFromText(out[i], expected);

      CharFsa fsa = new CharFsa();
      @SuppressWarnings("unused")
      int[] res = readFromText(in[i], fsa);

      MinimizationBrzowski.minimize(fsa, fsa.getComparator());
      if (PRINT_GRAPHS) printGraph(fsa, i+"_min2bout.dot");

      assertEquals("" + i, expected.getNumberOfActiveVertices(),
          fsa.getNumberOfActiveVertices());
      assertEquals("" + i, expected.getFinalStates().size(),
          fsa.getFinalStates().size());
      assertEquals("" + i, expected_res[1], TestDirectedGraph.countEdges(fsa));
    }
  }

  private static boolean problemPersists(StringFsa one) {
    StringFsa two = one.minimize();
    return ! one.isEquivalent(two);
  }

  /*
  public static void main0(String[] args) {
    StringFsa sprout = new StringFsa();
    sprout.readFromText(
      Paths.get("src/test/resources/fsa/expected-min-sprout-aut-2.txt"));

    StringFsa newDetAut = new StringFsa();
    newDetAut.readFromText(
      Paths.get("src/test/resources/fsa/det-new-aut-2.txt"));
    StringFsa graff = newDetAut.minimize();
    TIntIntHashMap homomorph = new TIntIntHashMap();
    int sproutV = sprout.getInitialState();
    int graffV = graff.getInitialState();
    homomorph.put(sproutV, graffV);
    Deque<Integer> active = new ArrayDeque<Integer>();
    active.push(sproutV);
    boolean schrott = false;
    while (! active.isEmpty()) {
      sproutV = active.poll();
      graffV = homomorph.get(sproutV);
      for (Edge<String> e : sprout.getOutEdges(sproutV)) {
        int sv = e.getTarget();
        Edge<String> ge = graff.findEdge(graffV, e.getInfo(), graff._comp);
        if (ge == null) {
          System.out.println("Non-matching: " + sv + " " + e +
              " " + null + " " + ge);
          schrott = true;
        }
        else {
          int gv = ge.getTarget();
          if (homomorph.containsKey(sv)) {
            if (gv != homomorph.get(sv)) {
              System.out.println("Non-matching: " + sv + " " + e +
                  " " +gv + " " + ge);
              schrott = true;
            }
          } else {
            homomorph.put(sv, gv);
            active.offer(sv);
          }
        }
      }
    }
  }
  */

  /** To automatically reduce the problem size when we find new automata which
   *  are problematic.
   */
  public static void main(String[] args) {
    StringFsa det0 = new StringFsa();
    det0.readFromText(
        Paths.get("src/test/resources/fsa/det-new-aut-2.txt"));
    StringFsa det = det0.copy();
    int totalRounds = det.getNumberOfActiveVertices();

    List<Integer> deleted = new ArrayList<Integer>();
    for (int round = 0; round < totalRounds; ++round) {
      System.out.println(round + "  " + det.getNumberOfActiveVertices());
      for (int v = 0; v < det.getNumberOfVertices(); ++v) {
        // don't delete the start or the last final state
        if (det.isVertex(v)
            && det.getInitialState() != v
            && (! det.isFinalState(v) || det.getFinalStates().size() > 1)) {
          det.removeVertex(v);
          det.removeDeadStates();
          if (problemPersists(det)) {
            deleted.add(v);
          } else {
            det = det0.copy();
            for (int del : deleted) {
              det.removeVertex(del);
            }
          }
        }
        det = det0.copy();
        for (int del : deleted) {
          det.removeVertex(del);
        }
        det.saveAsText(Paths.get("/tmp/reduced.txt"));
      }
    }
  }
}

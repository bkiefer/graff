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

package de.dfki.lt.loot.fsa;

import java.io.*;
import java.util.*;

import de.dfki.lt.loot.digraph.*;
import de.dfki.lt.loot.fsa.algo.Determinization;

public class CharFsa extends FiniteAutomaton<Character> {

  public CharFsa() {
     super();
     _comp = new Comparator<Character>() {
       public int compare(Character arg1, Character arg2) {
         // EPSILON is smaller than anything
         if (arg1 == null) {
           return (arg2 == null) ? 0 : -1;
         }
         if (arg2 == null) {
           return 1;
         }
         return arg1.charValue() - arg2.charValue();
       }
     };
  }

  public Comparator<Character> getComparator() { return _comp; }

  public boolean deterministicMatch(String input) {
    int current = getInitialState();
    for (int i = 0; i < input.length(); ++i) {
      char currChar = input.charAt(i);
      Edge<Character> nextNode =
        findEdge(current, currChar, _comp);
      if (nextNode == null)
        return false;
      current = nextNode.getTarget();
    }
    return isFinalState(current);
  }

  public static CharFsa compileRegex(String regex) {
    boolean result = false;
    RegexParser p = new RegexParser(regex);
    try {
      p.setDebugLevel(0);  // can be used to trace the parser
      result = p.parse();
    }
    catch (IOException ioex) {
      return null;
    }
    if (result) {
      return p.getAutomaton();
    }
    return null;
  }

  public CharFsa determinize() {
    CharFsa result = new CharFsa();
    Determinization.determinize(this, this._comp, result);
    return result;
  }


  private void addWord(String word) {
    int current = getInitialState();
    for (int i = 0; i < word.length(); ++i) {
      Edge<Character> outEdge =
        findEdge(current, word.charAt(i), _comp);
      int nextState = -1;
      if (outEdge == null) {
        nextState = newVertex();
        newEdge(word.charAt(i), current, nextState);
      } else {
        nextState = outEdge.getTarget();
      }
      current = nextState;
    }
    setFinalState(current);
  }


  public static ArrayList<String> readLexicon(String filename)
    throws FileNotFoundException, IOException {
    ArrayList<String> result = new ArrayList<String>();

    BufferedReader in =
      new BufferedReader(new InputStreamReader(new FileInputStream(filename),
                                               "ISO-8859-1"));
    String line;
    while ((line = in.readLine()) != null) {
      result.add(line);
    }
    in.close();
    return result;
  }

  public static CharFsa lexiconAutomaton(Collection<String> lexicon){
    CharFsa result = new CharFsa();
    result.setInitialState(result.newVertex());
    for(String line : lexicon) {
      result.addWord(line);
    }
    return result;
  }


  /** Build an nondeterministic Automaton from a list of words */
  public static CharFsa readLexiconAutomaton(String filename)
    throws FileNotFoundException, IOException {

    BufferedReader in =
      new BufferedReader(new InputStreamReader(new FileInputStream(filename),
                                               "ISO-8859-1"));
    CharFsa result = new CharFsa();
    result.setInitialState(result.newVertex());
    String line = null;
    while ((line = in.readLine()) != null) {
      result.addWord(line);
    }
    in.close();
    System.out.println(result.getNumberOfVertices() + " Zustände");
    return result;
  }


  /* DEBUGGING CODE TO BE ACTIVATED WHEN NEEDED
  public void
    compareFsaRec(int curr, CharFsa arg, int argCurr, StringBuilder sb) {
    Iterator<Edge<Character>> edgeIt = getOutEdges(curr).iterator();
    Iterator<Edge<Character>> argEdgeIt = arg.getOutEdges(argCurr).iterator();

    while (edgeIt.hasNext() || argEdgeIt.hasNext()) {
      Edge<Character> edge = (edgeIt.hasNext() ? edgeIt.next() : null);
      Edge<Character> argEdge = (argEdgeIt.hasNext() ? argEdgeIt.next() : null);
      if (edge == null) {
        System.out.println(argEdge + " not in first automaton "
                           + sb.toString());
      } else {
        if (argEdge == null) {
          System.out.println(edge + " not in second automaton" + sb.toString());
        } else {
          while (edgeIt.hasNext()
                 && (edge.getInfo() < argEdge.getInfo())) {
            System.out.println(argEdge + " not in first automaton"
                               + sb.toString());
            edge = edgeIt.next();
          }
          if (! edgeIt.hasNext()) continue;
          while (argEdgeIt.hasNext()
                 && (edge.getInfo() > argEdge.getInfo())) {
            System.out.println(edge + " not in second automaton"
                               + sb.toString());
            argEdge = argEdgeIt.next();
          }
          if (! argEdgeIt.hasNext()) continue;
          sb.append(edge.getInfo());
          compareFsaRec(edge.getTarget(), arg, argEdge.getTarget(), sb);
          sb.deleteCharAt(sb.length() - 1);
        }
      }
    }
  }

  public static CharFsa buildLexiconAutomaton0(String filename)
    throws IOException {

    CharFsa hopcroft = readLexiconAutomaton(filename);
    Minimization.minimize(hopcroft, hopcroft._comp);
    hopcroft.compact();
    System.out.println(hopcroft.getNumberOfActiveVertices() + " Zustände");
    /*
    CharFsa brzowski = readLexiconAutomaton(filename);
    MinimizationBrzowski.minimize(brzowski);
    brzowski.compact();
    System.out.println(brzowski.graph.getNumberOfVertices() + " Zustände");

    brzowski.compareFsaRec(brzowski.getInitialState(),
                           hopcroft,
                           hopcroft.getInitialState(),
                           new StringBuilder());
    *//*

    return hopcroft;
  }

  // code to reduce lexicon for finding an error in hopcroft implementation
  public static CharFsa reduceLexionAutomaton(String filename)
    throws IOException {
    List<String> lexicon = readLexicon(filename);

    Set<String> sublexicon = new HashSet<String>();
    sublexicon.addAll(lexicon);
    int reduceBy = 1000;

    while (reduceBy > 0) {
      int j = 0;
      while (j < lexicon.size()) {
        int i;
        for (i = 0; i < reduceBy && j + i < lexicon.size(); ++i) {
          sublexicon.remove(lexicon.get(j+i));
        }
        CharFsa hopcroft = lexiconAutomaton(sublexicon);
        Minimization.minimize(hopcroft, hopcroft._comp);
        if (hopcroft.checkLexicon(sublexicon)) {
        for (i = 0; i < reduceBy && j + i < lexicon.size(); ++i) {
          sublexicon.add(lexicon.get(j+i));
        }
        System.out.print('-');
        } else {
          System.out.print('+');
        }
        j += i;
      }
      System.out.println(reduceBy);
      lexicon.clear();
      lexicon.addAll(sublexicon);
      reduceBy /= 10;
    }

    BufferedWriter out =
      new BufferedWriter(new OutputStreamWriter
                         (new FileOutputStream(fileBaseName(filename) + ".red"),
                          "UTF-8"));
    for(String word : lexicon) {
      out.write(word); out.write('\n');
    }
    out.close();
    return null;
  }
  */

  public boolean checkLexicon(Collection<String> lexicon) {
    for (String line : lexicon) {
      if (! deterministicMatch(line)) return false;
    }
    return true;
  }

  public boolean checkLexiconAutomaton(String filename)
    throws FileNotFoundException, IOException {
    boolean ok = true;
    BufferedReader in
      = new BufferedReader(
          new InputStreamReader(new FileInputStream(filename),
                                "ISO-8859-1"));
    String line = null;
    while ((line = in.readLine()) != null) {
      if (! deterministicMatch(line)) {
        System.out.println(line + " missing");
        ok = false;
      }
    }
    in.close();
    return ok;
  }

  private void printRec(int state, PrintStream out, StringBuilder sb)
    throws IOException {
    if (isFinalState(state)) {
      out.println(sb.toString());
    }
    for(Edge<Character> edge : getOutEdges(state)) {
      sb.append(edge.getInfo().charValue());
      printRec(edge.getTarget(), out, sb);
      sb.deleteCharAt(sb.length() - 1);
    }
  }

  public void printWordList(String filename) throws IOException {
    PrintStream out = new PrintStream(filename, "ISO-8859-1");
    StringBuilder sb = new StringBuilder();
    printRec(getInitialState(), out, sb);
    out.close();
  }

  /*
  private static String fileBaseName(String filename) {
    return filename.substring(0,filename.lastIndexOf('.'));
  }

  public static void main(String[] args) throws IOException {
    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    CharFsa lexicon = buildLexiconAutomaton0(args[0]);
    //lexicon.checkLexiconAutomaton(args[0]);
    //lexicon.printWordList(fileBaseName(args[0])+".out");
    while (true) {
      String line =input.readLine();
      if (line == null || line.length() == 0)
        break;
      if (! lexicon.deterministicMatch(line)) {
        System.out.print("nicht ");
      }
      System.out.println("im Lexicon");
    }
  }
   */
}

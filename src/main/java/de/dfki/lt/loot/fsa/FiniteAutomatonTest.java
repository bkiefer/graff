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

import static de.dfki.lt.loot.digraph.io.GraphPrinterFactory.printGraph;
import java.io.IOException;

import de.dfki.lt.loot.fsa.algo.Minimization;
import de.dfki.lt.loot.fsa.algo.MinimizationBrzowski;

/**
 * <code>FiniteAutomatonTest</code> is a test class for {@link FiniteAutomaton}.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id: FiniteAutomatonTest.java,v 1.2 2005/11/15 17:56:46 steffen Exp $
 */
public class FiniteAutomatonTest {

  /**
   * This is the main method. It requires no argument
   *
   * @param args an array of <code>String</code>s with the arguments;
   * not used here
   */
  public static void main(String[] args) throws IOException {

    // define test regular expressions
    String[] testRegExps = {
      "X*|Y", "(X*|Y)", "A*BA", "ABA*", "(A|B)", "ABAB(X*|Y)*", "X|Y*",
      "(AX|BY)*", "((AX)|BY)*", "([a-c]a[a-c])*",
      "(xy|yx)*b"
    };

    if (args.length > 0) {
      testRegExps = args;
    }
    for (int i = 0; i < testRegExps.length; i++) {
      // init automaton
      CharFsa automaton = CharFsa.compileRegex(testRegExps[i]);
      // process regex
      if (automaton != null) {
        // show result automaton
        //System.out.println(automaton);
        printGraph(automaton, i + "-0.dot");

        // determinize automaton
        CharFsa detAutomaton = automaton.determinize();
        // show determinized automaton
        //System.out.println(detAutomaton);
        // print determinized automaton in VCG format
        printGraph(detAutomaton, i + "-1det.dot");

        // minimize automaton (Hopcroft)
        Minimization.minimize(detAutomaton, detAutomaton._comp);
        // show minimized automaton
        //System.out.println(detAutomaton);
        // print minimized automaton in VCG format
        printGraph(detAutomaton, i + "-2min.dot");

        detAutomaton = CharFsa.compileRegex(testRegExps[i]).determinize();
        // minimize automaton
        MinimizationBrzowski.minimize(detAutomaton, detAutomaton._comp);
        // show minimized automaton
        //System.out.println(detAutomaton);
        // print minimized automaton in VCG format
        printGraph(detAutomaton, i + "-3min.dot");

        /*
        // compact automaton
        detAutomaton.compact();
        // show minimized automaton
        //System.out.println(detAutomaton);
        // print minimized automaton in VCG format
        detAutomaton.printGraph(i + "-3cpt.dot");
        */
      }
    }
  }
}

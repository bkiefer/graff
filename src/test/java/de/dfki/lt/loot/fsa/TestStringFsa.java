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
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.dfki.lt.loot.fsa.FiniteAutomaton.SubAutomaton;
import de.dfki.lt.loot.fsa.algo.Minimization;

/**
 * {@link TestStringFsa} is a test class for {@link StringFsa}.
 *
 * @author Joerg Steffen, DFKI
 */
public class TestStringFsa {

  /**
   * Tests {@link FiniteAutomaton#concatenate(SubAutomaton, SubAutomaton)}.
   */
  @Test
  public void testConcatenate() {

    StringFsa aut = new StringFsa();

    StringFsa.SubAutomaton subAut1 = aut.newCharAutomaton("test1");
    StringFsa.SubAutomaton subAut2 = aut.newCharAutomaton("test2");
    StringFsa.SubAutomaton subAut3 = aut.concatenate(subAut1, subAut2);

    aut.setInitialState(subAut3.getInitialState());
    aut.setFinalState(subAut3.getFinalState());
    StringFsa det = aut.determinize();
    StringFsa min = det.minimize();

    //min.dotPrint(Paths.get("concat.dot"));
    assertThat(min.getNumberOfActiveVertices(), is(3));

    assertThat(aut.isEquivalent(det), is(true));
    assertThat(aut.isEquivalent(min), is(true));
    assertThat(det.isEquivalent(min), is(true));
  }


  /**
   * Tests {@link FiniteAutomaton#multiConcatenate(List)}.
   */
  @Test
  public void testConcatenateList() {

    StringFsa aut = new StringFsa();

    List<StringFsa.SubAutomaton> subAuts = new ArrayList<>();
    subAuts.add(aut.newCharAutomaton("test1"));
    subAuts.add(aut.newCharAutomaton("test2"));
    subAuts.add(aut.newCharAutomaton("test3"));

    StringFsa.SubAutomaton concAut = aut.multiConcatenate(subAuts);

    aut.setInitialState(concAut.getInitialState());
    aut.setFinalState(concAut.getFinalState());
    StringFsa det = aut.determinize();
    StringFsa min = det.minimize();
    //min.dotPrint(Paths.get("concatList.dot"));
    assertThat(min.getNumberOfActiveVertices(), is(4));

    assertThat(aut.isEquivalent(det), is(true));
    assertThat(aut.isEquivalent(min), is(true));
    assertThat(det.isEquivalent(min), is(true));
  }


  /**
   * Tests {@link FiniteAutomaton#alternative(SubAutomaton, SubAutomaton)}.
   */
  @Test
  public void testAlternative() {

    StringFsa aut = new StringFsa();

    StringFsa.SubAutomaton subAut1 = aut.newCharAutomaton("test1");
    StringFsa.SubAutomaton subAut2 = aut.newCharAutomaton("test2");

    StringFsa.SubAutomaton subAut3 = aut.alternative(subAut1, subAut2);

    aut.setInitialState(subAut3.getInitialState());
    aut.setFinalState(subAut3.getFinalState());
    StringFsa det = aut.determinize();

    //det.dotPrint(Paths.get("alternative.dot"));
    assertThat(det.getNumberOfActiveVertices(), is(3));

    StringFsa min = det.minimize();

    //min.dotPrint(Paths.get("alternative-min.dot"));
    assertThat(min.getNumberOfActiveVertices(), is(2));

    assertThat(aut.isEquivalent(det), is(true));
    assertThat(aut.isEquivalent(min), is(true));
    assertThat(det.isEquivalent(min), is(true));
  }


  /**
   * Tests {@link FiniteAutomaton#multiAlternative(java.util.Collection)}.
   */
  @Test
  public void testAlternativeList() {

    StringFsa aut = new StringFsa();

    List<StringFsa.SubAutomaton> subAuts = new ArrayList<>();
    subAuts.add(aut.newCharAutomaton("test1"));
    subAuts.add(aut.newCharAutomaton("test2"));
    subAuts.add(aut.newCharAutomaton("test3"));

    StringFsa.SubAutomaton unionAut = aut.multiAlternative(subAuts);

    aut.setInitialState(unionAut.getInitialState());
    aut.setFinalState(unionAut.getFinalState());
    StringFsa det = aut.determinize();

    //det.dotPrint(Paths.get("alternativeList.dot"));
    assertThat(det.getNumberOfActiveVertices(), is(4));

    StringFsa min = det.minimize();

    //min.dotPrint(Paths.get("alternativeList-min.dot"));
    assertThat(min.getNumberOfActiveVertices(), is(2));

    assertThat(aut.isEquivalent(det), is(true));
    assertThat(aut.isEquivalent(min), is(true));
    assertThat(det.isEquivalent(min), is(true));
  }


  /**
   * Tests {@link FiniteAutomaton#kleene(SubAutomaton)}.
   */
  @Test
  public void testKleene() {

    StringFsa aut = new StringFsa();

    StringFsa.SubAutomaton subAut1 = aut.newCharAutomaton("test1");

    StringFsa.SubAutomaton subAut2 = aut.kleene(subAut1);

    aut.setInitialState(subAut2.getInitialState());
    aut.setFinalState(subAut2.getFinalState());
    StringFsa det = aut.determinize();

    //det.dotPrint(Paths.get("kleene.dot"));
    assertThat(det.getNumberOfActiveVertices(), is(2));

    StringFsa min = det.minimize();

    //min.dotPrint(Paths.get("kleene-min.dot"));
    assertThat(min.getNumberOfActiveVertices(), is(1));

    assertThat(aut.isEquivalent(det), is(true));
    assertThat(aut.isEquivalent(min), is(true));
    assertThat(det.isEquivalent(min), is(true));
  }


  /**
   * Tests {@link FiniteAutomaton#plus(SubAutomaton)}.
   */
  @Test
  public void testPlus() {

    StringFsa aut = new StringFsa();

    StringFsa.SubAutomaton subAut1 = aut.newCharAutomaton("test1");

    StringFsa.SubAutomaton subAut2 = aut.plus(subAut1);

    aut.setInitialState(subAut2.getInitialState());
    aut.setFinalState(subAut2.getFinalState());
    StringFsa det = aut.determinize();
    StringFsa min = det.minimize();

    //aut.dotPrint(Paths.get("plus.dot"));
    assertThat(min.getNumberOfActiveVertices(), is(2));

    assertThat(aut.isEquivalent(det), is(true));
    assertThat(aut.isEquivalent(min), is(true));
    assertThat(det.isEquivalent(min), is(true));
  }


  /**
   * Tests {@link FiniteAutomaton#optional(SubAutomaton)}.
   */
  @Test
  public void testOptional() {

    StringFsa aut = new StringFsa();

    StringFsa.SubAutomaton subAut1 = aut.newCharAutomaton("test1");

    StringFsa.SubAutomaton subAut2 = aut.optional(subAut1);

    aut.setInitialState(subAut2.getInitialState());
    aut.setFinalState(subAut2.getFinalState());
    StringFsa det = aut.determinize();
    StringFsa min = det.minimize();

    //min.dotPrint(Paths.get("optional.dot"));
    assertThat(min.getNumberOfActiveVertices(), is(2));

    assertThat(aut.isEquivalent(det), is(true));
    assertThat(aut.isEquivalent(min), is(true));
    assertThat(det.isEquivalent(min), is(true));
  }


  /**
   * Tests {@link FiniteAutomaton#repeat(SubAutomaton, int, int)}.
   */
  @Test
  public void testRepeat10() {

    StringFsa aut = new StringFsa();

    StringFsa.SubAutomaton subAut1 = aut.newCharAutomaton("test1");

    StringFsa.SubAutomaton subAut2 = aut.repeat(subAut1, 1, 0);

    aut.setInitialState(subAut2.getInitialState());
    aut.setFinalState(subAut2.getFinalState());
    StringFsa det = aut.determinize();
    StringFsa min = det.minimize();

    //min.dotPrint(Paths.get("repeat1-0.dot"));
    assertThat(min.getNumberOfActiveVertices(), is(1));

    assertThat(aut.isEquivalent(det), is(true));
    assertThat(aut.isEquivalent(min), is(true));
    assertThat(det.isEquivalent(min), is(true));
  }


  /**
   * Tests {@link FiniteAutomaton#repeat(SubAutomaton, int, int)}.
   */
  @Test
  public void testRepeat00() {

    StringFsa aut = new StringFsa();

    StringFsa.SubAutomaton subAut1 = aut.newCharAutomaton("test1");

    StringFsa.SubAutomaton subAut2 = aut.repeat(subAut1, 0, 0);

    aut.setInitialState(subAut2.getInitialState());
    aut.setFinalState(subAut2.getFinalState());
    StringFsa det = aut.determinize();
    StringFsa min = det.minimize();

    //min.dotPrint(Paths.get("repeat0-0.dot"));
    assertThat(min.getNumberOfActiveVertices(), is(1));

    assertThat(aut.isEquivalent(det), is(true));
    assertThat(aut.isEquivalent(min), is(true));
    assertThat(det.isEquivalent(min), is(true));
  }


  /**
   * Tests {@link FiniteAutomaton#repeat(SubAutomaton, int, int)}.
   */
  @Test
  public void testRepeat01() {

    StringFsa aut = new StringFsa();

    StringFsa.SubAutomaton subAut1 = aut.newCharAutomaton("test1");

    StringFsa.SubAutomaton subAut2 = aut.repeat(subAut1, 0, 1);

    aut.setInitialState(subAut2.getInitialState());
    aut.setFinalState(subAut2.getFinalState());
    StringFsa det = aut.determinize();
    StringFsa min = det.minimize();

    //min.dotPrint(Paths.get("repeat0-1.dot"));
    assertThat(min.getNumberOfActiveVertices(), is(2));

    assertThat(aut.isEquivalent(det), is(true));
    assertThat(aut.isEquivalent(min), is(true));
    assertThat(det.isEquivalent(min), is(true));
  }


  /**
   * Tests {@link FiniteAutomaton#repeat(SubAutomaton, int, int)}.
   */
  @Test
  public void testRepeat03() {

    StringFsa aut = new StringFsa();

    StringFsa.SubAutomaton subAut1 = aut.newCharAutomaton("test1");

    StringFsa.SubAutomaton subAut2 = aut.repeat(subAut1, 0, 3);

    aut.setInitialState(subAut2.getInitialState());
    aut.setFinalState(subAut2.getFinalState());
    StringFsa det = aut.determinize();
    StringFsa min = det.minimize();

    //min.dotPrint(Paths.get("repeat0-3.dot"));
    assertThat(min.getNumberOfActiveVertices(), is(4));

    assertThat(aut.isEquivalent(det), is(true));
    assertThat(aut.isEquivalent(min), is(true));
    assertThat(det.isEquivalent(min), is(true));
  }


  /**
   * Tests {@link FiniteAutomaton#repeat(SubAutomaton, int, int)}.
   */
  @Test
  public void testRepeat11() {

    StringFsa aut = new StringFsa();

    StringFsa.SubAutomaton subAut1 = aut.newCharAutomaton("test1");

    StringFsa.SubAutomaton subAut2 = aut.repeat(subAut1, 1, 1);

    aut.setInitialState(subAut2.getInitialState());
    aut.setFinalState(subAut2.getFinalState());
    StringFsa det = aut.determinize();
    StringFsa min = det.minimize();

    //min.dotPrint(Paths.get("repeat1-1.dot"));
    assertThat(min.getNumberOfActiveVertices(), is(2));

    assertThat(aut.isEquivalent(det), is(true));
    assertThat(aut.isEquivalent(min), is(true));
    assertThat(det.isEquivalent(min), is(true));
  }


  /**
   * Tests {@link FiniteAutomaton#repeat(SubAutomaton, int, int)}.
   */
  @Test
  public void testRepeat13() {

    StringFsa aut = new StringFsa();

    StringFsa.SubAutomaton subAut1 = aut.newCharAutomaton("test1");

    StringFsa.SubAutomaton subAut2 = aut.repeat(subAut1, 1, 3);

    aut.setInitialState(subAut2.getInitialState());
    aut.setFinalState(subAut2.getFinalState());
    StringFsa det = aut.determinize();
    StringFsa min = det.minimize();

    //min.dotPrint(Paths.get("repeat1-3.dot"));
    assertThat(min.getNumberOfActiveVertices(), is(4));

    assertThat(aut.isEquivalent(det), is(true));
    assertThat(aut.isEquivalent(min), is(true));
    assertThat(det.isEquivalent(min), is(true));
  }


  /**
   * Tests {@link FiniteAutomaton#repeat(SubAutomaton, int, int)}.
   */
  @Test
  public void testRepeat24() {

    StringFsa aut = new StringFsa();

    StringFsa.SubAutomaton subAut1 = aut.newCharAutomaton("test1");

    StringFsa.SubAutomaton subAut2 = aut.repeat(subAut1, 2, 4);

    aut.setInitialState(subAut2.getInitialState());
    aut.setFinalState(subAut2.getFinalState());
    StringFsa det = aut.determinize();
    StringFsa min = det.minimize();

    //min.dotPrint(Paths.get("repeat2-4.dot"));
    assertThat(min.getNumberOfActiveVertices(), is(5));

    assertThat(aut.isEquivalent(det), is(true));
    assertThat(aut.isEquivalent(min), is(true));
    assertThat(det.isEquivalent(min), is(true));
  }


  /**
   * Tests {@link FiniteAutomaton#complement()}.
   */
  @Test
  public void testComplement1() {

    StringFsa aut = new StringFsa();
    StringFsa.SubAutomaton subAut1 = aut.newCharAutomaton("test1");
    StringFsa.SubAutomaton subAut2 = aut.newCharAutomaton("test2");

    StringFsa.SubAutomaton subAut3 =
      aut.concatenate(subAut1, subAut2);

    aut.setInitialState(subAut3.getInitialState());
    aut.setFinalState(subAut3.getFinalState());
    StringFsa det = aut.determinize();
    StringFsa min = det.minimize();

    FiniteAutomaton<String> complement = min.complement();

    //complement.dotPrint(Paths.get("complement1.dot"));
    assertThat(complement.getNumberOfActiveVertices(), is(3));

    assertThat(aut.isEquivalent(det), is(true));
    assertThat(aut.isEquivalent(min), is(true));
    assertThat(det.isEquivalent(min), is(true));
  }


  /**
   * Tests {@link FiniteAutomaton#complement()}.
   */
  @Test
  public void testComplement2() {

    StringFsa aut = new StringFsa();
    StringFsa.SubAutomaton subAut1 = aut.newCharAutomaton("test1");
    StringFsa.SubAutomaton subAut2 = aut.newCharAutomaton("test2");

    StringFsa.SubAutomaton subAut3 = aut.alternative(subAut1, subAut2);

    aut.setInitialState(subAut3.getInitialState());
    aut.setFinalState(subAut3.getFinalState());
    StringFsa det = aut.determinize();
    StringFsa min = det.minimize();

    FiniteAutomaton<String> complement = min.complement();

    //complement.dotPrint(Paths.get("complement2.dot"));
    assertThat(complement.getNumberOfActiveVertices(), is(2));

    assertThat(aut.isEquivalent(det), is(true));
    assertThat(aut.isEquivalent(min), is(true));
    assertThat(det.isEquivalent(min), is(true));
  }


  /**
   * Tests {@link FiniteAutomaton#isEquivalent(FiniteAutomaton)}.
   */
  @Test
  public void testEquivalence() {

    StringFsa aut1 = new StringFsa();
    StringFsa.SubAutomaton subAut1 = aut1.newCharAutomaton("test1");
    aut1.setInitialState(subAut1.getInitialState());
    aut1.setFinalState(subAut1.getFinalState());

    StringFsa aut2 = new StringFsa();
    StringFsa.SubAutomaton subAut2 = aut2.newCharAutomaton("test1");
    aut2.setInitialState(subAut2.getInitialState());
    aut2.setFinalState(subAut2.getFinalState());

    assertThat(aut1.isEquivalent(aut2), is(true));
  }


  /**
   * Tests {@link Minimization#minimize(AbstractAutomaton, java.util.Comparator)}.
   */
  @Test
  public void testMinimization() {

    StringFsa sproutDetAut = new StringFsa();
    sproutDetAut.readFromText(
      Paths.get("src/test/resources/fsa/det-sprout-aut.txt"));

    StringFsa newDetAut = new StringFsa();
    newDetAut.readFromText(
      Paths.get("src/test/resources/fsa/det-new-aut.txt"));

    StringFsa expectedMinSproutAut = new StringFsa();
    expectedMinSproutAut.readFromText(
      Paths.get("src/test/resources/fsa/expected-min-sprout-aut.txt"));
    assertThat(expectedMinSproutAut.getNumberOfActiveVertices(), is(66));

    assertThat(sproutDetAut.isEquivalent(newDetAut), is(true));
    assertThat(sproutDetAut.isEquivalent(expectedMinSproutAut), is(true));
    assertThat(newDetAut.isEquivalent(expectedMinSproutAut), is(true));

    StringFsa minAut = newDetAut.minimize();
    assertThat(minAut.getNumberOfActiveVertices(), is(66));
    assertThat(minAut.isEquivalent(expectedMinSproutAut), is(true));
    assertThat(minAut.isEquivalent(newDetAut), is(true));
  }


  /**
   * Tests {@link Minimization#minimize(AbstractAutomaton, java.util.Comparator)}.
   */
  @Test
  public void testMinimization2() {

    StringFsa sproutDetAut = new StringFsa();
    sproutDetAut.readFromText(
      Paths.get("src/test/resources/fsa/det-sprout-aut-2.txt"));

    StringFsa newDetAut = new StringFsa();
    newDetAut.readFromText(
      Paths.get("src/test/resources/fsa/det-new-aut-2.txt"));

    StringFsa expectedMinSproutAut = new StringFsa();
    expectedMinSproutAut.readFromText(
      Paths.get("src/test/resources/fsa/expected-min-sprout-aut-2.txt"));
    assertThat(expectedMinSproutAut.getNumberOfActiveVertices(), is(901));

    assertThat(sproutDetAut.isEquivalent(newDetAut), is(true));
    assertThat(sproutDetAut.isEquivalent(expectedMinSproutAut), is(true));
    assertThat(newDetAut.isEquivalent(expectedMinSproutAut), is(true));

    StringFsa minAut = newDetAut.minimize();
    assertThat(minAut.getNumberOfActiveVertices(), is(901));
    assertThat(minAut.isEquivalent(expectedMinSproutAut), is(true));
    assertThat(minAut.isEquivalent(newDetAut), is(true));
  }

  public static void main(String[] args) {
    StringFsa a = new StringFsa();
    a.readFromText(Paths.get("/home/kiefer/tmp/autom.txt"));
    printGraph(a, "autom.txt");
    StringFsa b = a.determinize();
    printGraph(b, "automdet.txt");
  }
}

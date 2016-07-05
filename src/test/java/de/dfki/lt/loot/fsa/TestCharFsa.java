package de.dfki.lt.loot.fsa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

/**
 * {@link TestCharFsa} is a test class for {@link CharFsa}.
 *
 * @author Bernd Kiefer, DFKI
 * @author Joerg Steffen, DFKI
 * @version $Id$
 */
public class TestCharFsa {

  private static final String pathPattern = "/tmp/%s.dot";

  /**
   * Writes the given automatons as .gif images using the given id.
   *
   * @param automaton
   *          the automaton
   * @param detAutomaton
   *          the deterministic version of the automaton
   * @param id
   *          the id to use in the file name
   * @throws IOException
   *           if there is an error when creating the .gif images
   */
  private static void writeGraphs(
      CharFsa automaton, CharFsa detAutomaton, String id)
      throws IOException {

    // dot print automaton
    Path automatonPath = Paths.get(String.format(pathPattern, id));
    automaton.dotPrint(automatonPath);
    // convert to .png
    FiniteAutomaton.dot2png(automatonPath);
    // dot print determinized automaton
    Path detAutomatonPath = Paths.get(String.format(pathPattern, id+"det"));
    detAutomaton.dotPrint(detAutomatonPath);
    // convert to .png
    FiniteAutomaton.dot2png(detAutomatonPath);
  }


  /**
   * Tests automaton creation for a regular expression with kleene star.
   *
   * @throws IOException
   *           if conversion to .gif images fails
   */
  @Test
  public void testKleene()
      throws IOException {

    String testRegExp = "X*";
    // create automaton and determine it
    CharFsa automaton = CharFsa.compileRegex(testRegExp);
    CharFsa detAutomaton = automaton.determinize();
    // write automatons as .gif images
    //writeGraphs(automaton, detAutomaton, "01");
    // check automatons
    assertEquals(4, automaton.getNumberOfActiveVertices());
    assertEquals(2, detAutomaton.getNumberOfActiveVertices());
  }


  /**
   * Tests automaton creation for a regular expression with a concatenation.
   *
   * @throws IOException
   *           if conversion to .gif images fails
   */
  @Test
  public void testConcatenation()
      throws IOException {

    String testRegExp = "AB";
    // create automaton and determine it
    CharFsa automaton = CharFsa.compileRegex(testRegExp);
    CharFsa detAutomaton = automaton.determinize();
    // write automatons as .gif images
    //writeGraphs(automaton, detAutomaton, "02");
    // check automatons
    assertEquals(3, automaton.getNumberOfActiveVertices());
    assertEquals(3, detAutomaton.getNumberOfActiveVertices());
  }


  /**
   * Tests automaton creation for a regular expression with a disjunction.
   *
   * @throws IOException
   *           if conversion to .gif images fails
   */
  @Test
  public void testDisjunction()
      throws IOException {

    String testRegExp = "A|B";
    // create automaton and determine it
    CharFsa automaton = CharFsa.compileRegex(testRegExp);
    CharFsa detAutomaton = automaton.determinize();
    // write automatons as .gif images
    //writeGraphs(automaton, detAutomaton, "03");
    // check automatons
    assertEquals(6, automaton.getNumberOfActiveVertices());
    assertEquals(3, detAutomaton.getNumberOfActiveVertices());
  }


  /**
   * Tests automaton creation for a regular expression with character sets.
   *
   * @throws IOException
   *           if conversion to .gif images fails
   */
  @Test
  public void testCharacterSets()
      throws IOException {

    String testRegExp = "[a-c]";
    // create automaton and determine it
    CharFsa automaton = CharFsa.compileRegex(testRegExp);
    CharFsa detAutomaton = automaton.determinize();
    // write automatons as .gif images
    //writeGraphs(automaton, detAutomaton, "04");
    // check automatons
    assertEquals(2, automaton.getNumberOfActiveVertices());
    assertEquals(2, detAutomaton.getNumberOfActiveVertices());
  }


  /**
   * Tests automaton creation for a complex regular expression.
   *
   * @throws IOException
   *           if conversion to .gif images fails
   */
  @Test
  public void testComplex1()
      throws IOException {

    String testRegExp = "X*|Y";
    // create automaton and determine it
    CharFsa automaton = CharFsa.compileRegex(testRegExp);
    CharFsa detAutomaton = automaton.determinize();
    // write automatons as .gif images
    //writeGraphs(automaton, detAutomaton, "05");
    // check automatons
    assertEquals(8, automaton.getNumberOfActiveVertices());
    assertEquals(3, detAutomaton.getNumberOfActiveVertices());
  }


  /**
   * Tests automaton creation for a complex regular expression.
   *
   * @throws IOException
   *           if conversion to .gif images fails
   */
  @Test
  public void testComplex2()
      throws IOException {

    String testRegExp = "A*BA";
    // create automaton and determine it
    CharFsa automaton = CharFsa.compileRegex(testRegExp);
    CharFsa detAutomaton = automaton.determinize();
    // write automatons as .gif images
    //writeGraphs(automaton, detAutomaton, "06");
    // check automatons
    assertEquals(6, automaton.getNumberOfActiveVertices());
    assertEquals(4, detAutomaton.getNumberOfActiveVertices());
  }


  /**
   * Tests automaton creation for a complex regular expression.
   *
   * @throws IOException
   *           if conversion to .gif images fails
   */
  @Test
  public void testComplex3()
      throws IOException {

    String testRegExp = "ABA*";
    // create automaton and determine it
    CharFsa automaton = CharFsa.compileRegex(testRegExp);
    CharFsa detAutomaton = automaton.determinize();
    // write automatons as .gif images
    //writeGraphs(automaton, detAutomaton, "07");
    // check automatons
    assertEquals(6, automaton.getNumberOfActiveVertices());
    assertEquals(4, detAutomaton.getNumberOfActiveVertices());
  }


  /**
   * Tests automaton creation for a complex regular expression.
   *
   * @throws IOException
   *           if conversion to .gif images fails
   */
  @Test
  public void testComplex4()
      throws IOException {

    String testRegExp = "ABAB(X*|Y)*";
    // create automaton and determine it
    CharFsa automaton = CharFsa.compileRegex(testRegExp);
    CharFsa detAutomaton = automaton.determinize();
    // write automatons as .gif images
    //writeGraphs(automaton, detAutomaton, "08");
    // check automatons
    assertEquals(14, automaton.getNumberOfActiveVertices());
    assertEquals(7, detAutomaton.getNumberOfActiveVertices());
  }


  /**
   * Tests automaton creation for a complex regular expression.
   *
   * @throws IOException
   *           if conversion to .gif images fails
   */
  @Test
  public void testComplex5()
      throws IOException {

    String testRegExp = "(AX|BY)*";
    // create automaton and determine it
    CharFsa automaton = CharFsa.compileRegex(testRegExp);
    CharFsa detAutomaton = automaton.determinize();
    // write automatons as .gif images
    //writeGraphs(automaton, detAutomaton, "09");
    // check automatons
    assertEquals(10, automaton.getNumberOfActiveVertices());
    assertEquals(5, detAutomaton.getNumberOfActiveVertices());
  }


  /**
   * Tests automaton creation for a complex regular expression.
   *
   * @throws IOException
   *           if conversion to .gif images fails
   */
  @Test
  public void testComplex6()
      throws IOException {

    String testRegExp = "([a-c]a[a-c])*";
    // create automaton and determine it
    CharFsa automaton = CharFsa.compileRegex(testRegExp);
    CharFsa detAutomaton = automaton.determinize();
    // write automatons as .gif images
    //writeGraphs(automaton, detAutomaton, "10");
    // check automatons
    assertEquals(6, automaton.getNumberOfActiveVertices());
    assertEquals(4, detAutomaton.getNumberOfActiveVertices());
  }

  /**
   * Test concatenation of automata
   */
  @Test
  public void testConcat() {
    CharFsa auto1 = CharFsa.compileRegex("([a-c]a[a-c])*");
    CharFsa auto2 = CharFsa.compileRegex("bab*");
    auto1.concatenate(auto2);
    CharFsa auto3 =CharFsa.compileRegex("([a-c]a[a-c])*bab*");
    assertTrue(auto1.isEquivalent(auto3));
  }

  /**
   * Test getSubAutomaton
   */
  @Test
  public void testGetSubAutomaton() {
    CharFsa auto1 = CharFsa.compileRegex("([a-c]ab)");
    CharFsa auto2 = CharFsa.compileRegex("([a-c]ab)");
    CharFsa.SubAutomaton sub = auto1.getSubAutomaton();
    auto1.setStates(sub);
    assertTrue(auto2.isEquivalent(auto1));
  }

  /**
   * Test makeTotal
   */
  @Test
  public void testMakeTotal() {
    CharFsa auto1 = CharFsa.compileRegex("([a-c]*ab)");
    CharFsa auto2 = CharFsa.compileRegex("([a-c]*ab)");
    auto2.makeTotal();
    assertTrue(auto2.isEquivalent(auto1));
  }

  /**
   * Test makeTotal
   */
 @Test
 public void testRemoveDeadStates() throws IOException {
   CharFsa auto1 = CharFsa.compileRegex("([ab]*aaac)");
   CharFsa auto2 = CharFsa.compileRegex("([ab]*aaac)");
   auto2.makeTotal();
   writeGraphs(auto2, auto1, "dead");
   auto2.removeDeadStates();
   writeGraphs(auto2, auto1, "dead2");
   assertTrue(auto2.isEquivalent(auto1));
 }

 /** Test readLexicon
  */
 @Test
 public void testReadLexicon() throws IOException {
   CharFsa auto1 =
       CharFsa.readLexiconAutomaton(
           Paths.get("src/test/resources/fsa/top10000de.txt").toString());
   assertTrue(
       auto1.checkLexiconAutomaton(
           Paths.get("src/test/resources/fsa/top10000de.txt").toString()));
 }

 /** Test readLexicon2
  */
 @Test
 public void testReadLexicon2() throws IOException {
   List<String> lex;
   CharFsa auto1 =
       CharFsa.lexiconAutomaton(
           lex = CharFsa.readLexicon(
               Paths.get("src/test/resources/fsa/top10000de.txt").toString()));
   assertTrue(auto1.checkLexicon(lex));
 }

}

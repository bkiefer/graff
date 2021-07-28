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

package de.dfki.lt.loot.digraph;

/** Utilities for tests, including test graphs in string form.
 *
 * @author kiefer
 */
public class Utils {

  public static boolean print = false;

  /** Use this example to test your traversal methods, it's the last example
   *  on the first set of slides.
   */
  public static final String exampleGraph =
    "shirt --> tie jacket belt\n" +
    "tie --> jacket\n" +
    "belt --> jacket\n" +
    "watch --> \n" +
    "undershorts --> pants shoes\n" +
    "pants --> belt shoes\n" +
    "socks --> shoes\n";

  /** Use this example to test your traversal methods, it's the last example
   *  on the first set of slides.
   */
  public static final String exampleBfsGraph =
    "s --> r w\n" +
    "r --> s v\n" +
    "v --> r\n" +
    "w --> s t x\n" +
    "t --> w u x\n" +
    "u --> t y\n" +
    "x --> w t y\n" +
    "y --> x u\n";

  /** An example graph with cycles in it, e.g., for tests of scc reduction.
   */
  public static final String exampleGraphCyclic =
    "s --> w z\n" +
    "z --> w y\n" +
    "v --> w s\n" +
    "w --> x q\n" +
    "t --> u v\n" +
    "u --> t v\n" +
    "x --> z\n" +
    "q --> x\n" +
    "y --> x\n";

  /** An acyclic test graph.
   */
  public static final String exampleGraphAcyclic =
    "s --> w z\n" +
    "z --> y\n" +
    "v --> w s\n" +
    "w --> x q\n" +
    "t --> u v\n" +
    "u --> v\n" +
    "x --> z\n" +
    "q --> x\n";

  /** Use this example to test shortest path methods, it's the last example
   *  on the first set of slides.
   */
  public static final String exampleGraphWeightedEdges =
    "s --> w (2) z (4)\n" +
    "z --> w(5) y(9)\n" +
    "v --> w(1) s(6)\n" +
    "w --> x(6) q(3)\n" +
    "t --> u(1) v(9) s(8)\n" +
    "u --> t(5) v(7)\n" +
    "x --> z(8) u(7)\n" +
    "q --> x(2)\n" +
    "y --> x(5)\n";

  /** Use this example to test shortest path methods, it's the last example
   *  on the first set of slides.
   */
  public static final String exampleUndirGraphWeightedEdges =
    "s --> w(2) z(4)\n" +
    "z --> w(5) y(9)\n" +
    "v --> w(1) s(6)\n" +
    "w --> x(6) q(3)\n" +
    "t --> u(1) v(9) s(1)\n" +
    "u --> v(7)\n" +
    "x --> z(8) u(7)\n" +
    "q --> x(2)\n" +
    "y --> x(5)\n";

  /** Use this example to test A* search methods, it's the last example
   *  on the first set of slides.
   */
  public static final String exampleGraphNodePositions =
    "s (1.3,6) --> w z\n" +
    "z (1,4) --> w y\n" +
    "v (4,4) --> w s\n" +
    "w (3,2) --> x q\n" +
    "t (5,6) --> u v s\n" +
    "u (6,4) --> v\n" +
    "x (0,0) --> z u\n" +
    "q (4.5,0) --> x\n" +
    "y (0,2) --> x\n";
}

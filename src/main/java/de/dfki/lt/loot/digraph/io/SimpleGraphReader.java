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

package de.dfki.lt.loot.digraph.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.dfki.lt.loot.digraph.Edge;
import de.dfki.lt.loot.digraph.Graph;
import de.dfki.lt.loot.digraph.VertexListPropertyMap;
import de.dfki.lt.loot.digraph.VertexPropertyMap;
import de.dfki.lt.loot.util.Predicates.EqualsPredicate;

public class SimpleGraphReader {
  private static final Pattern pat =
      Pattern.compile("\\s*([^( ]+)(?:\\s*\\(([^)]*)\\))?");

  public static BiConsumer<Integer, String> nodeNoOp =
      (vertex, infoString) -> { return; };

  public static class Vector {
    double x, y;

    public Vector(double xx, double yy) { x = xx; y = yy; }

    public double euclidDist(Vector e) {
      return Math.sqrt(Math.pow(e.x - x, 2) + Math.pow(e.y - y, 2));
    }
  }

  /**
   * Creates a graph from a readable specification including edge weights.
   *
   * @param in
   *          the reader from which to read the graph
   * @return the graph
   * @throws IOException
   *           if there is an error when reading the graph
   */
  public static void readEdgeWeightGraph(Reader in,
      final Graph<Integer> result)
      throws IOException {
    readGraphGeneric(in, result, nodeNoOp,
        (edge, infoString) -> Integer.parseInt(infoString));
  }

  /**
   * Creates a graph from a readable specification including node positions.
   *
   * @param in
   *          the reader from which to read the graph
   * @return the graph
   * @throws IOException
   *           if there is an error when reading the graph
   */
  public static
  void readNodePositionGraph(Reader in,
      final Graph<Double> result)
      throws IOException {

    final VertexPropertyMap<Vector> nodePos = new VertexListPropertyMap<Vector>(result);
    result.register("nodePos", nodePos);

    readGraphGeneric(in, result,
        (vertex, infoString) -> {
          String[] c = infoString.split("\\s*,\\s*");
          nodePos.put(vertex,
              new Vector(Double.parseDouble(c[0]), Double.parseDouble(c[1])));
        },
        (edge, infoString) -> null);
  }

  /**
   * Creates a graph from a readable specification.
   *
   * @param in
   *          the reader from which to read the graph
   * @return the graph
   * @throws IOException
   *           if there is an error when reading the graph
   */
  public static void readGraph(Reader in, final Graph<String> result)
      throws IOException {
    readGraphGeneric(in, result, nodeNoOp,
        (BiFunction<Edge<String>, String, String>) (edge, infoString) -> "");
  }

  /**
   * create a graph from a readable specification, for exampes see the Utils
   * class in the test directory.
   */
  public static <EI> void readGraphGeneric(Reader in, Graph<EI> result,
      BiConsumer<Integer, String> nodeInfo,
      BiFunction<Edge<EI>, String, EI> edgeInfo)
    throws IOException {
    BufferedReader bin = new BufferedReader(in);

    EqualsPredicate<String> eqPred = new EqualsPredicate<String>();

    VertexPropertyMap<String> names = new VertexListPropertyMap<String>(result);
    result.register("names", names);

    String nextLine = null;
    while((nextLine = bin.readLine()) != null) {
      String[] fromTo = nextLine.split("\\s*-->\\s*");
      if (fromTo.length < 1)
        continue;
      Matcher node = pat.matcher(fromTo[0]);
      if (! node.matches()) {
        throw new IOException("Wrong input line: " + nextLine);
      }

      String fromName = node.group(1);
      List<Integer> nodes = names.findVertices(fromName, eqPred);
      int from = -1;
      if (nodes.isEmpty()) {
        from = result.newVertex();
        names.put(from, fromName);
      } else {
        from = nodes.get(0);
      }
      if (node.group(2) != null && ! node.group(2).trim().isEmpty()) {
        nodeInfo.accept(from, node.group(2).trim());
      }

      if (fromTo.length == 2) {
        Matcher nameAndWeights = pat.matcher(fromTo[1]);
        int start = 0;
        do {
          if (nameAndWeights.find(start)) {
            String toName = nameAndWeights.group(1);
            nodes = names.findVertices(toName, eqPred);
            int to = -1;
            if (nodes.isEmpty()) {
              to = result.newVertex();
              names.put(to, toName);
            } else {
              to = nodes.get(0);
            }
            Edge<EI> e = result.newEdge(null, from, to);
            //if (nameAndWeights.group(2) != null
            //    && ! nameAndWeights.group(2).trim().isEmpty()) {
              e.setInfo(edgeInfo.apply(e, nameAndWeights.group(2)));
            //}
            start = nameAndWeights.end();
          } else break;
        } while (true);
      }
    }
  }
}

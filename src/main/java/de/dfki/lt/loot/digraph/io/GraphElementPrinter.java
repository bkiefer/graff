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

import java.io.PrintWriter;

import de.dfki.lt.loot.digraph.Edge;
import de.dfki.lt.loot.digraph.Graph;

public interface GraphElementPrinter<EdgeInfo> {
  void printNode(PrintWriter out, int node);

  void printEdge(PrintWriter out, Edge<EdgeInfo> edge);

  void startGraph(PrintWriter out, Graph<EdgeInfo> graph);

  void endGraph(PrintWriter out);
}

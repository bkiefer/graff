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

public interface GraphVisitor<EdgeInfo> {

  /** In the BFS/DFS variants that visits each vertex of the graph, this method
   *  is called for the start vertices where the real search begins.
   */
  void startVertex(int v, Graph<EdgeInfo> g);

  /** This method is called when a vertex turns from white to gray */
  void discoverVertex(int v, Graph<EdgeInfo> g);

  /** This method is called when a vertex turns from gray to black */
  void finishVertex(int v, Graph<EdgeInfo> g);

  /** This method is called when a tree edge is used */
  void treeEdge(Edge<EdgeInfo> e, Graph<EdgeInfo> g);

  /** This method is called when a non tree edge is used */
  void nonTreeEdge(Edge<EdgeInfo> e, Graph<EdgeInfo> g);

}

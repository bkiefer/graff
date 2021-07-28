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

package de.dfki.lt.loot.digraph.algo;

import de.dfki.lt.loot.digraph.Graph;
import de.dfki.lt.loot.digraph.GraphVisitorAdapter;
import java.util.ArrayList;
import java.util.List;


/**
 * {@link TopoOrderVisitor} implements the {@link DfsGraphVisitor} interface to
 * calculate the topological order of the vertices of a graph.
 *
 * @param <EdgeInfo>
 *          the kind of information assigned to the edges of the traversed graph
 *
 * @author Joerg Steffen, DFKI
 * @version $Id$
 */
public class TopoOrderVisitor<EdgeInfo> extends GraphVisitorAdapter<EdgeInfo> {

  /**
   * Here we collect the sorted vertices.
   */
  private List<Integer> _sortedVertices;

  /** collect the vertices in inverse topo order if true. */
  private boolean _inverse;

  /**
   * Creates a new instance of {@link TopoOrderVisitor}.
   */
  public TopoOrderVisitor() {
    this(false);
  }

  /**
   * Creates a new instance of {@link TopoOrderVisitor}.
   */
  public TopoOrderVisitor(boolean inverse) {
    _inverse = inverse;
    _sortedVertices = new ArrayList<>();
  }


  /**
   * Returns the topological sorted vertices after the visitor is finished.
   *
   * @return the list of topological sorted vertices
   */
  public List<Integer> getSortedVertices() {
    return _sortedVertices;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void finishVertex(int v, Graph<EdgeInfo> g) {

    // add a finished vertex at the start of the list
    if (_inverse)
      _sortedVertices.add(v);
    else
      _sortedVertices.add(0, v);
  }
}

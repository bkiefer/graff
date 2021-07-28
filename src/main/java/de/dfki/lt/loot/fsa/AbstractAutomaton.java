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

import java.util.Collection;

import de.dfki.lt.loot.digraph.Graph;

public interface AbstractAutomaton<EdgeInfo> extends Graph<EdgeInfo> {

  // Automaton specific methods

  public abstract int getInitialState();

  public abstract void setInitialState(int vertex);

  public abstract boolean isFinalState(int vertex);

  public abstract void setFinalState(int vertex);

  public abstract Collection<EdgeInfo> getAlphabet();

  public abstract boolean isEpsilon(EdgeInfo info);
}

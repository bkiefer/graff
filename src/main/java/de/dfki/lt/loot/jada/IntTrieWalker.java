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

package de.dfki.lt.loot.jada;

public interface IntTrieWalker<T> {

  /** Enter a node during a walk */
  public void startNode(IntTrie<T> node);

  /** Visit edge with value val before recursive descent */
  public void beforeEdge(int val);

  /** Visit edge with value val after recursive descent */
  public void afterEdge(int val);

  /** Visit the node after all its edges have been visited */
  public void endNode(IntTrie<T> node);
}

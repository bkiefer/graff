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

import java.util.ArrayList;

/** map things from and to integer IDs. Since we need both directions, this
 *  functionality is integrated here instead of using two containers.
 */
public abstract class IDMap<THING> {
  /** data structure to map from the id to the corresponding thing */
  protected ArrayList<THING> number2Thing;
  
  public IDMap() {
    number2Thing = new ArrayList<THING>();
  }
  
  public IDMap(int initialCapacity) {
    number2Thing = new ArrayList<THING>(initialCapacity);
  }
  
  final protected int registerNext(THING thing) {
    number2Thing.add(thing);
    return number2Thing.size() - 1;
  }
  
  public THING fromId(int id) {
    if (id >= number2Thing.size()) return null;
    return number2Thing.get(id);
  }
 
  public abstract boolean contains(THING thing);
  
  public int size() { return number2Thing.size(); }
}

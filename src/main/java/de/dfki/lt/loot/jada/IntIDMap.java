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

import gnu.trove.map.hash.TObjectIntHashMap;


/** map things from and to integer IDs. Since we need both directions, this
 *  functionality is integrated here instead of using two containers.
 */
public class IntIDMap<THING> extends IDMap<THING> {
  /** data structure to map from the thing to the corresponding id */
  protected TObjectIntHashMap<THING> type2Number;

  private static final int ILLEGAL_VALUE = -1;

  public IntIDMap() {
    super();
    type2Number = new TObjectIntHashMap<THING>();
  }

  public IntIDMap(int initialCapacity) {
    super(initialCapacity);
    type2Number = new TObjectIntHashMap<THING>(initialCapacity);
  }

  public int register(THING thing) {
    int newID = registerNext(thing);
    type2Number.put(thing, newID);
    return newID;
  }

  public int getId(THING thing) {
    return (type2Number.containsKey(thing) ? type2Number.get(thing)
                                        : ILLEGAL_VALUE);
  }

  @Override
  public boolean contains(THING thing) {
    return type2Number.containsKey(thing);
  }
  @Override
  @SuppressWarnings("unchecked")
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("{ ");
    for (Object o : type2Number.keys()) {
      THING t = (THING) o;
      sb.append("{" + t + "," + type2Number.get(t) + "} ");
    }
    sb.append("}");
    return sb.toString();
  }
}

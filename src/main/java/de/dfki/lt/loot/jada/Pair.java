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

public class Pair<KEYTYPE, VALUETYPE> {
  private KEYTYPE _first;
  private VALUETYPE _second;

  public Pair(KEYTYPE first, VALUETYPE second) {
    _first = first;
    _second = second;
  }

  public KEYTYPE getFirst() { return _first; }

  public void setFirst(KEYTYPE key) { _first = key; }

  public VALUETYPE getSecond() { return _second; }

  public void setSecond(VALUETYPE val) { _second = val; }

  @Override
  public boolean equals(Object o) {
    if (! (o instanceof Pair)) return false;
    @SuppressWarnings("rawtypes")
    Pair p = (Pair)o;
    return p._first.equals(_first) && p._second.equals(_second);
  }

  @Override
  public int hashCode() {
    return _first.hashCode() * 2053 + _second.hashCode();
  }

  @Override
  public String toString() {
    return "(" + _first + ", " + _second + ")";
  }
}

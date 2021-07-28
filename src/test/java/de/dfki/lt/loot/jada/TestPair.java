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

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

public class TestPair {
  @Test
  public void create() {
    Pair<String, String> s = new Pair<String, String>("a", "b");
    assertEquals("a", s.getFirst());
    assertEquals("b", s.getSecond());
  }

  @Test
  public void testEquals() {
    Pair<String, String> s = new Pair<String, String>("a", "b");
    Pair<String, String> t = new Pair<String, String>("a", "b");
    assertEquals(s, t);
  }

  @Test
  public void testHash() {
    HashSet<Pair<String, String>> hs = new HashSet<Pair<String, String>>();
    Pair<String, String> s = new Pair<String, String>("a", "b");
    hs.add(s);
    assertEquals(1, hs.size());
    Pair<String, String> t = new Pair<String, String>("a", "b");
    hs.add(t);
    assertEquals(1, hs.size());
    assertTrue(hs.contains(s));
    assertTrue(hs.contains(t));
    t.setSecond("c");
    assertFalse(hs.contains(t));
    assertFalse(s.equals(t));
    assertFalse(s.equals(hs));
    t.setFirst("d");
    t.setSecond("b");
    assertFalse(s.equals(t));
  }
}

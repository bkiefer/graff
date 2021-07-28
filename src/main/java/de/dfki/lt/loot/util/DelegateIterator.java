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

package de.dfki.lt.loot.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DelegateIterator<E> implements Iterator<E>, Iterable<E> {

  private Iterator<E>[] delegates;
  private int current;

  @SafeVarargs
  public DelegateIterator(Iterator<E> ... delegates) {
    this.delegates = delegates;
    this.current = 0;
  }

  @SuppressWarnings("unchecked")
  @SafeVarargs
  public DelegateIterator(Iterable<E> ... delegates) {
    this.delegates = new Iterator[delegates.length];
    int i = 0;
    for (Iterable<E> it : delegates) {
      this.delegates[i++] = it.iterator();
    }
    this.current = 0;
  }

  @Override
  public boolean hasNext() {
    while (this.current < delegates.length
        && ! delegates[current].hasNext()) ++current;
    return this.current < delegates.length;
  }

  @Override
  public E next() {
    if (this.current >= delegates.length) throw new NoSuchElementException();
    return delegates[current].next();
  }

  @Override
  public void remove() {
    delegates[current].remove();
  }

  @Override
  public Iterator<E> iterator() {
    return this;
  }

}

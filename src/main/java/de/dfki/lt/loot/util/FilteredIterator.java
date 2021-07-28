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

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Function;

public class FilteredIterator<E, T>
  implements Iterator<E>, Iterable<E> {

  final private T _info;
  final private Comparator<T> _comp;
  final private Function<E, T> _acc;

  private Iterator<E> _it;
  private E _next;

  public FilteredIterator(Iterable<E> it, T i, Comparator<T> comparator,
      Function<E, T> accessor) {
    _it = it.iterator();
    _info = i;
    _comp = comparator;
    _acc = accessor;

    findNext();
  }

  private void findNext() {
    while (_it.hasNext()) {
      _next = _it.next();
      if (_comp.compare(_acc.apply(_next), _info) == 0) return;
    }
    _next = null;
  }

  @Override
  public boolean hasNext() {
    return _next != null;
  }

  @Override
  public E next() {
    E result = _next;
    findNext();
    return result;
  }

  @Override
  public void remove() { throw new UnsupportedOperationException(); }

  @Override
  public Iterator<E> iterator() { return this; }
}

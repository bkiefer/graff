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
import java.util.function.BiPredicate;


/** Utilities for tests, including test graphs in string form.
 *
 * @author kiefer
 */
public class Predicates {

  /**
   * <code>EqualsPredicate</code> is an inner class that defines a functional
   * object used to check if two instances of the argument type are equal.
   */
  public static class EqualsPredicate<ArgumentType>
    implements BiPredicate<ArgumentType, ArgumentType> {

    /**
     * This compares the given objects of type ArgumentTyp and returns
     * <code>true</code> if they are compatible.
     *
     * @param arg1 a <code>ArgumentType</code> with the first argument
     * @param arg2 a <code>ArgumentType</code> with the second argument
     * @return a <code>boolean</code> indicating if the arguments are compatible
     */
    @Override
	public boolean test(ArgumentType arg1, ArgumentType arg2) {

      return arg1.equals(arg2);
    }
  }

  /**
   * <code>ComparableComparator</code> is an inner class that defines a functional
   * object used to compare two instances of the argument type using their
   * internal comparison method
   */
  public static class ComparablePredicate<ArgumentType extends Comparable<ArgumentType>>
    implements BiPredicate<ArgumentType, ArgumentType> {

    /**
     * This compares the given objects of type ArgumentTyp and returns
     * <code>true</code> if they are compatible.
     *
     * @param arg1 a <code>ArgumentType</code> with the first argument
     * @param arg2 a <code>ArgumentType</code> with the second argument
     * @return a <code>boolean</code> indicating if the arguments are compatible
     */
    @Override
	public boolean test(ArgumentType arg1, ArgumentType arg2) {

      return arg1.compareTo(arg2) == 0;
    }
  }

  /**
   * <code>ComparableComparator</code> is an inner class that defines a functional
   * object used to compare two instances of the argument type using their
   * internal comparison method
   */
  public static class ComparableComparator<ArgumentType extends Comparable<ArgumentType>>
    implements Comparator<ArgumentType> {

    /**
     * This compares the given objects of type ArgumentTyp and returns
     * <code>true</code> if they are compatible.
     *
     * @param arg1 a <code>ArgumentType</code> with the first argument
     * @param arg2 a <code>ArgumentType</code> with the second argument
     * @return a <code>boolean</code> indicating if the arguments are compatible
     */
    @Override
	public int compare(ArgumentType arg1, ArgumentType arg2) {

      return arg1.compareTo(arg2);
    }
  }

}

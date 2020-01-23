/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    public int compare(ArgumentType arg1, ArgumentType arg2) {

      return arg1.compareTo(arg2);
    }
  }

}

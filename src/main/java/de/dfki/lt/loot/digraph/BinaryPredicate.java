package de.dfki.lt.loot.digraph;

import java.util.Comparator;

/**
 * <code>BinaryPredicate</code> defines the interface of a functional object
 * used to compare two instances of the argument type.
 *
 * @author Bernd Kiefer, DFKI
 * @author Joerg Steffen, DFKI
 * @version $Id$
 */
public interface BinaryPredicate<ArgumentType> {

  /**
   * This compares the given objects of type ArgumentTyp and returns
   * <code>true</code> if they are compatible.
   *
   * @param arg1 an <code>ArgumentType</code> with the first argument
   * @param arg2 an <code>ArgumentType</code> with the second argument
   * @return a <code>boolean</code> indicating if the arguments are compatible
   */
  public boolean compare(ArgumentType arg1, ArgumentType arg2);


  /**
   * Defines a functional object used to check if two instances of the argument
   * type are equal.
   *
   * @param <ArgumentType>
   *          the kind of arguments to compare
   */
  public static class EqualsPredicate<ArgumentType>
      implements BinaryPredicate<ArgumentType> {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean compare(ArgumentType arg1, ArgumentType arg2) {

      return arg1.equals(arg2);
    }
  }


  /**
   * Defines a functional object used to compare two instances of the argument
   * type using their internal comparison method
   *
   * @param <ArgumentType>
   *          the kind of arguments to compare
   */
  public static class ComparableComparator
      <ArgumentType extends Comparable<ArgumentType>>
      implements Comparator<ArgumentType> {

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(ArgumentType arg1, ArgumentType arg2) {

      return arg1.compareTo(arg2);
    }
  }
}

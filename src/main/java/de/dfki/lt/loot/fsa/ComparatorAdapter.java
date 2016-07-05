package de.dfki.lt.loot.fsa;

import java.util.Comparator;

public class ComparatorAdapter<ArgumentType>
  implements de.dfki.lt.loot.digraph.BinaryPredicate<ArgumentType> {

  private Comparator<ArgumentType> _comp;

  ComparatorAdapter(Comparator<ArgumentType> comp) {
    _comp = comp;
  }

  public boolean compare(ArgumentType arg1,  ArgumentType arg2) {
    return _comp.compare(arg1, arg2) == 0;
  }
}
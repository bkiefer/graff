package de.dfki.lt.loot.digraph.algo;

import java.util.*;

import de.dfki.lt.loot.digraph.DirectedGraph;
import de.dfki.lt.loot.digraph.Edge;
import de.dfki.lt.loot.digraph.GraphVisitorAdapter;
import de.dfki.lt.loot.digraph.VertexListPropertyMap;
import de.dfki.lt.loot.digraph.VertexPropertyMap;

public class TarjanVisitor<EdgeInfo>
  extends GraphVisitorAdapter<EdgeInfo> {

  private VertexPropertyMap<Integer> _discovery;

  private VertexPropertyMap<Integer> _low;

  private Stack<Integer> _vertexStack;

  private List<List<Integer>> _components;

  private int time;

  public List<List<Integer>> getSCCs() {
    return _components;
  }

  @Override
  public void startVertex(int v, DirectedGraph<EdgeInfo> g) {
    // do the initialization
    if (_components == null) {
      _discovery = new VertexListPropertyMap<Integer>(g);
      _low = new VertexListPropertyMap<Integer>(g);
      _vertexStack = new Stack<Integer>();
      _components = new ArrayList<List<Integer>>();
      time = 0;
    }
  }

  @Override
  public void discoverVertex(int v, DirectedGraph<EdgeInfo> g) {
    ++time;
    // i also use _low to detect if a vertex is still in the queue, see later
    _low.put(v, time);
    _discovery.put(v, time);
    _vertexStack.push(v);
    /*
    System.out.println("disc: " + g.getPropertyMap("names").get(v)
                       + " " + _low.get(v));
                       */
  }

  @Override
  public void finishVertex(int v, DirectedGraph<EdgeInfo> g) {
    // do the computation of the lowest reachable vertex now
    /*
    System.out.println("fin: " + g.getPropertyMap("names").get(v)
                       + " " + _low.get(v));
                       */
    int low = _low.get(v);
    for(Edge<EdgeInfo> outEdge : g.getOutEdges(v)) {
      Integer targetLow = _low.get(outEdge.getTarget());
      if (targetLow != null) {
        low = Math.min(low, targetLow);
      }
    }
    if (low == _discovery.get(v)) {
      List<Integer> component = new ArrayList<Integer>();
      int nextVertex = -1;
      do {
        nextVertex = _vertexStack.pop();
        component.add(nextVertex);
        _low.remove(nextVertex);
      } while (nextVertex != v);
      _components.add(component);
    } else {
      _low.put(v, low);
    }
  }
}
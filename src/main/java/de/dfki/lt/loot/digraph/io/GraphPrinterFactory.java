package de.dfki.lt.loot.digraph.io;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import de.dfki.lt.loot.digraph.Edge;
import de.dfki.lt.loot.digraph.Graph;

public class GraphPrinterFactory {
  /**
   * Contains the path to the dot.exe delivered with Graphviz.
   */
  private static final String WIN_DOT =
      "C:/Program Files (x86)/Graphviz 2.28/bin/dot.exe";

  private static final GraphPrinterFactory _singleton = new GraphPrinterFactory();

  static {
    // default printer
    register(new SimpleDotPrinter<Object>(), (g -> g instanceof Graph<?>));
  }

  private static class SRRunnable implements Runnable {
    private Reader _r ;
    private StringBuilder _sb = new StringBuilder();

    public SRRunnable(InputStream in) { _r = new InputStreamReader(in); }

    public void run() {
      int c = 0;
      try {
        while ( (c = _r.read()) != -1)
          _sb.append((char)c);
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }
    }

    @Override
    public String toString() {
      return _sb.toString();
    }
  }


  private List<Predicate<Graph<?>>> _predicates;
  private List<GraphElementPrinter<?>> _printers;

  private GraphPrinterFactory() {
    _predicates = new ArrayList<>();
    _printers = new ArrayList<>();
  }

  public static void register(GraphElementPrinter<?> p, Predicate<Graph<?>> applicable) {
    getFactory()._predicates.add(applicable);
    getFactory()._printers.add(p);
  }

  public static void unregister(GraphElementPrinter<?> p, Predicate<Graph<?>> applicable) {
    if (_singleton == null) return;
    int index = _singleton._predicates.indexOf(applicable);
    if (index >= 0) {
      _singleton._predicates.remove(index);
      _singleton._printers.remove(index);
    }
  }

  public static GraphPrinterFactory getFactory() {
    return _singleton;
  }

  /** The more specific ones are at the end of the list */
  public GraphElementPrinter<?> getPrinter(Graph<?> g) {
    for(int i = _printers.size() - 1; i >= 0; --i) {
      if (_predicates.get(i).test(g)) return _printers.get(i);
    }
    return null;
  }

  /**
   * Creates a .gif or .png image for the given .dot graph.
   *
   * @param dotGraphPath
   *          the path to the .dot graph
   */
  public static void dot2png(Path dotGraphPath) {

    System.out.format("converting %s ..." , dotGraphPath);
    Process process;
    try {
      if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
        String command = String.format("%s -Tgif %2$s -o\"%2$s.gif\" -Kdot",
            WIN_DOT, dotGraphPath);
        process = Runtime.getRuntime().exec(command);
      } else {
        String[] command = { "sh", "-c",
            "dot -Tpng '" + dotGraphPath + "' -o'" + dotGraphPath + ".png' -Kdot"
        };
        process = Runtime.getRuntime().exec(command);
      }
      SRRunnable err = new SRRunnable(process.getErrorStream());
      SRRunnable out = new SRRunnable(process.getInputStream());
      Thread e = new Thread(err);
      e.run();
      Thread o = new Thread(out);
      o.run();
      System.out.println(" : " + process.waitFor() +
          "|" + err.toString() + "|" + out.toString());
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  /**
   * This writes this graph in graphviz format to the given file so that it can
   * be processed with the graphviz package (http://www.graphviz.org/)
   *
   * @param fileName a <code>String</code> with the file name
   * @param printer a class with which special printing can be handled
   * @throws <code>IOException</code> if an error ccurs when writing the file
   */
  public static <EI> void print(Graph<EI> g, Path fileName,
      GraphElementPrinter<EI> printer)
    throws IOException {

    PrintWriter out =
      new PrintWriter(Files.newBufferedWriter(
          fileName, Charset.defaultCharset()));
    printer.startGraph(out, g);

    for (int node = 0; node < g.getNumberOfVertices(); ++node) {
      if (g.isVertex(node)) {
        printer.printNode(out, node);
      }
    }

    for (int node = 0; node < g.getNumberOfVertices(); ++node) {
      if (g.isVertex(node) && g.getOutEdges(node) != null) {
        for (Edge<EI> edge : g.getOutEdges(node)) {
          printer.printEdge(out, edge);
        }
      }
    }
    printer.endGraph(out);
    out.close();
  }


  /**
   * This writes this graph in graphviz format to the given file so that it can
   * be processed with the graphviz package (http://www.graphviz.org/)
   *
   * @param fileName a <code>String</code> with the file name
   * @throws <code>IOException</code> if an error ccurs when writing the file
   */
  @SuppressWarnings("unchecked")
  public static <EI> void print(Graph<EI> g, Path fileName) throws IOException {
    print(g, fileName, (GraphElementPrinter<EI>) getFactory().getPrinter(g));
  }

  /** Convert the graph into graphviz format and create a PNG graphic file
   *  out of that representation using the dot program.
   */
  public static <EI> void printGraph(Graph<EI> g, String name,
      GraphElementPrinter<EI> printer) {
    try {
      Path filePath = new File("/tmp/" + name).toPath();
      print(g, filePath, printer);
      dot2png(filePath);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Convert the graph into graphviz format and create a PNG graphic file
   *  out of that representation using the dot program.
   */
  @SuppressWarnings("unchecked")
  public static <EI> void printGraph(Graph<EI> g, String name) {
    printGraph(g, name, (GraphElementPrinter<EI>) getFactory().getPrinter(g));
  }
}

package de.dfki.lt.loot.util;

public class Timer {
  private long totalSum;
  private long lastStart;
  private long divider = 1000;

  public Timer() { reset(); }

  public Timer(boolean start) {
    this();
    if (start) start();
  }

  @Override
  public String toString() { return Double.toString(((double) totalSum) / divider) ; }

  /** reset lap and total time */
  public void reset() {
    lastStart = -1;
    totalSum = 0;
  }

  /** Start the timer, not resetting summing up of laps */
  public void start() {
    lastStart = System.currentTimeMillis();
  }

  /** stop timer, and return the sum of all laps up to now */
  public double stop() {
    if (lastStart > 0) {
      totalSum += System.currentTimeMillis() - lastStart ;
    }
    lastStart = -1;
    return ((double) totalSum) / divider;
  }

  /** return time since start() (since last call to <code>start()</code>)
   *  in seconds
   */
  public double seconds() {
    return ((double) milliseconds()) / divider ;
  }

  /** return time since start() (since last call to <code>start()</code>)
   *  in seconds
   */
  public long milliseconds() {
    return totalSum;
  }

  /** return time since start() (since last call to <code>start()</code>)
   *  in milliseconds
   */
  public long lap() {
    if (lastStart > 0) {
      long now = System.currentTimeMillis();
      long lap = now - lastStart;
      totalSum += lap;
      lastStart = now;
      return lap;
    }
    return 0;
  }

  /** stop the timer, reset all counters and return the summed up time from last
   *  run
   */
  public double restart() {
    double result = stop(); reset(); start();
    return result;
  }

}


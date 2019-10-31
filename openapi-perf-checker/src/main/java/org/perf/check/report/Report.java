package org.perf.check.report;

public class Report {
  private final String clsName;
  private final String version;
  private final double timeMs;
  private final int iterations;
  private final String error;
  private final boolean excluded;

  public Report(String clsName, String version, double timeMs, int iterations, boolean excluded) {
    this.clsName = clsName;
    this.version = version;
    this.timeMs = timeMs;
    this.iterations = iterations;
    this.error = null;
    this.excluded = excluded;
  }

  public Report(String clsName, String version, String error) {
    this.clsName = clsName;
    this.version = version;
    this.timeMs = 0;
    this.iterations = 0;
    this.error = error;
    this.excluded = true;
  }

  public String getClsName() {
    return clsName;
  }

  public String getVersion() {
    return version;
  }

  public double getTimeMs() {
    return timeMs;
  }

  public int getIterations() {
    return iterations;
  }

  public String getError() {
    return error;
  }

  public boolean isExcluded() {
    return excluded;
  }
}

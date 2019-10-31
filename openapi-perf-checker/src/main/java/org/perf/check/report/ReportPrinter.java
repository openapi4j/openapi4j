package org.perf.check.report;

public class ReportPrinter {
  public static void printReports(Report... reports) {
    System.out.println();
    System.out.println("| Library           | Version       | Time          | Iterations    | % time  |");
    System.out.println("|-------------------|---------------|---------------|---------------|---------|");

    double max = 0;
    for (Report report : reports) {
      if (!report.isExcluded() && max < report.getTimeMs()) {
        max = report.getTimeMs();
      }
    }

    for (Report report : reports) {
      System.out.println(getFormattedReport(report, report.getTimeMs() * 100 / max));
    }
  }

  private static String getFormattedReport(Report report, double ratio) {
    if (report.getError() == null) {
      return String.format(
        "| %s| %s| %s| %s| %s|",
        rightpad(report.getClsName(), 18),
        rightpad(report.getVersion(), 14),
        rightpad(String.format("%.2f ms", report.getTimeMs()), 14),
        rightpad(String.valueOf(report.getIterations()), 14),
        !report.isExcluded() ? rightpad(String.format("%.1f", ratio), 8) : rightpad("excl.", 8));
    } else {
      return String.format(
        "| %s | %s | Error : %s |",
        rightpad(report.getClsName(), 18),
        rightpad(report.getVersion(), 14),
        report.getError());
    }
  }

  private static String rightpad(String text, int length) {
    return String.format("%-" + length + "." + length + "s", text);
  }
}

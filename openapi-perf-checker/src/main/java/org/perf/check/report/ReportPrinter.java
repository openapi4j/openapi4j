package org.perf.check.report;

import java.text.DecimalFormat;

public class ReportPrinter {
  private ReportPrinter() {}

  public static void printReports(Report... reports) {
    System.out.println();
    System.out.println("| Library           | Version       | Time          | Iterations    | Gain    |");
    System.out.println("|-------------------|---------------|---------------|---------------|---------|");

    double max = 0;
    for (Report report : reports) {
      if (report.isIncluded() && max < report.getTimeMs()) {
        max = report.getTimeMs();
      }
    }

    DecimalFormat decimalFormat = new DecimalFormat("0.##");

    for (Report report : reports) {
      System.out.println(getFormattedReport(report, decimalFormat.format(max / report.getTimeMs())));
    }
  }

  private static String getFormattedReport(Report report, String gain) {
    if (report.getError() == null) {
      return String.format(
        "| %s| %s| %s| %s| %s|",
        rightpad(report.getClsName(), 18),
        rightpad(report.getVersion(), 14),
        rightpad(String.format("%.2f ms", report.getTimeMs()), 14),
        rightpad(String.valueOf(report.getIterations()), 14),
        report.isIncluded() ? rightpad(gain, 8) : rightpad("excl.", 8));
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

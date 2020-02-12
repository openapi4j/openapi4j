package org.perf.check.operation;

import org.perf.check.report.Report;
import org.perf.check.report.ReportPrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class OperationPerfRunner {
  private static final String SCHEMA_FILE = "operation/api.yaml";

  public static void main(final String... args) throws Exception {
    List<Report> reports = new ArrayList<>();

    final OpenApi4j openApi4j = new OpenApi4j(SCHEMA_FILE);
    process(reports, openApi4j, openApi4j::validateJson, "application/json", 10000);
    process(reports, openApi4j, openApi4j::validateFormUrlEncoded, "form-urlencoded", 10000);
    process(reports, openApi4j, openApi4j::validateFormData, "form-data", 10000);
    process(reports, openApi4j, openApi4j::validateMultipartMixed, "multipart/mixed", 10000);
    process(reports, openApi4j, openApi4j::validateXml, "application/xml", 10000);

    ReportPrinter.printReports(reports.toArray(new Report[0]));
  }

  private static void process(List<Report> reports, OpenApi4j opValidator, Supplier<String> validate, String contentType, final int ntIt) {
    reports.add(load(opValidator, validate, contentType, ntIt));
  }

  private static Report load(OpenApi4j opValidator, Supplier<String> validate, String contentType, final int ntIt) {
    final long begin = System.nanoTime();

    for (int i = 1; i <= ntIt; i++) {
      String result = validate.get();
      if (result != null) {
        return new Report(
          contentType,
          opValidator.getVersion(),
          result);
      }
    }

    final long end = System.nanoTime();

    return new Report(
      contentType,
      opValidator.getVersion(),
      (end - begin) / 1_000_000f,
      ntIt,
      true);
  }
}

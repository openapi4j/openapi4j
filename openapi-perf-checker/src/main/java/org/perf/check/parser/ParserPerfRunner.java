package org.perf.check.parser;

import org.perf.check.report.Report;
import org.perf.check.report.ReportPrinter;

public class ParserPerfRunner {
  private static final String SCHEMA_FILE = "org/openapi4j/parser/api-definition.yaml";

  public static void main(final String... args) {
    final Swagger swagger = new Swagger();
    final OpenApi4j openApi4j = new OpenApi4j();

    ReportPrinter.printReports(
      load(swagger, swagger.getVersion(), 1, false),
      load(openApi4j, openApi4j.getVersion(), 1, false),
      load(swagger, swagger.getVersion(), 10, true),
      load(openApi4j, openApi4j.getVersion(), 10, true));
  }

  private static Report load(PerfParser parser, String version, final int ntIt, boolean excluded) {
    final long begin = System.nanoTime();

    for (int i = 1; i <= ntIt; i++) {
      String errMsg = parser.load(SCHEMA_FILE);

      if (errMsg != null) {
        return new Report(
          parser.getClass().getSimpleName(),
          version,
          errMsg);
      }
    }

    final long end = System.nanoTime();

    return new Report(
      parser.getClass().getSimpleName(),
      version,
      (end - begin) / 1_000_000f,
      ntIt,
      excluded);
  }
}

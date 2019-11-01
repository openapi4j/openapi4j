package org.perf.check.operation;

import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.validation.ValidationException;
import org.perf.check.report.Report;
import org.perf.check.report.ReportPrinter;

public class OperationPerfRunner {
  private static final String SCHEMA_FILE = "/operation/api.yaml";

  public static void main(final String... args) throws ValidationException, ResolutionException {
    final OpenApi4j openApi4j = new OpenApi4j(SCHEMA_FILE);

    ReportPrinter.printReports(
      load(openApi4j, openApi4j.getVersion(), 1, false),
      load(openApi4j, openApi4j.getVersion(), 10000, true));
  }

  private static Report load(OpenApi4j opValidator, String version, final int ntIt, boolean excluded) {
    final long begin = System.nanoTime();

    for (int i = 1; i <= ntIt; i++) {
      try {
        opValidator.validate();
      } catch (ValidationException e) {
        return new Report(
          opValidator.getClass().getSimpleName(),
          version,
          e.getLocalizedMessage());
      }
    }

    final long end = System.nanoTime();

    return new Report(
      opValidator.getClass().getSimpleName(),
      version,
      (end - begin) / 1_000_000f,
      ntIt,
      excluded);
  }
}

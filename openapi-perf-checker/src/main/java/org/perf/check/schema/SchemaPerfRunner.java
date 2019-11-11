package org.perf.check.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;

import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.util.TreeUtil;
import org.perf.check.report.Report;
import org.perf.check.report.ReportPrinter;

import java.io.IOException;
import java.util.Iterator;

public class SchemaPerfRunner {
  private static final String SCHEMA_FILE = "/schema/schema-draft4.json";
  private static final String DATA_FILE = "/schema/data.json";
  private static final String DATA_SCHEMAS = "schemas";

  public static void main(final String... args) throws IOException, ProcessingException, ResolutionException {
    final JsonNode schema = TreeUtil.json.readTree(SchemaPerfRunner.class.getResource(SCHEMA_FILE));
    final JsonNode data = TreeUtil.json.readTree(SchemaPerfRunner.class.getResource(DATA_FILE)).get(DATA_SCHEMAS);

    final Networknt networknt = new Networknt(schema);
    final OpenApi4j openApi4j = new OpenApi4j(schema);
    final JsonTools jsonTools = new JsonTools(schema);

    ReportPrinter.printReports(
      validate(networknt, data, networknt.getVersion(), 1_000, false),
      validate(openApi4j, data, openApi4j.getVersion(), 1_000, false),
      validate(jsonTools, data, jsonTools.getVersion(), 100, true));
  }

  private static Report validate(JsonValidator validator, JsonNode data, String version, final int ntIt, boolean excluded) {
    final long begin = System.nanoTime();

    for (int i = 1; i <= ntIt; i++) {
      Iterator<String> names = data.fieldNames();
      while (names.hasNext()) {
        JsonNode dataNode = data.get(names.next());
        String errMsg = validator.validate(dataNode);

        if (errMsg != null) {
          return new Report(
            validator.getClass().getSimpleName(),
            version,
            errMsg);
        }
      }
    }

    final long end = System.nanoTime();

    return new Report(
      validator.getClass().getSimpleName(),
      version,
      (end - begin) / 1_000_000f,
      ntIt,
      excluded);
  }
}

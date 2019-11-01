package org.perf.check.parser;

import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.parser.OpenApi3Parser;

class OpenApi4j implements PerfParser {
  @Override
  public String load(String schemaFile) {
    // Check parsing with validation
    try {
      new OpenApi3Parser().parse(getClass().getResource(schemaFile), false);
    } catch (ValidationException | ResolutionException e) {
      return e.toString();
    }

    return null;
  }

  @Override
  public String getVersion() {
    return OpenApi3Parser.class.getPackage().getImplementationVersion();
  }
}

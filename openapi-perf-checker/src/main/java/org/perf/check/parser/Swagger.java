package org.perf.check.parser;

import java.util.Collections;
import java.util.List;

import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.AuthorizationValue;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

class Swagger implements PerfParser {
  private final ParseOptions options = getParseOptions();
  private final List<AuthorizationValue> authorizationValues = Collections.emptyList();

  @Override
  public String load(String schemaFile) {
    SwaggerParseResult swaggerParseResult = new OpenAPIV3Parser().readLocation(schemaFile, authorizationValues, options);
    if (swaggerParseResult.getMessages().isEmpty()) {
      return null;
    }

    return String.join(", ", swaggerParseResult.getMessages());
  }

  @Override
  public String getVersion() {
    return OpenAPIV3Parser.class.getPackage().getImplementationVersion();
  }

  private ParseOptions getParseOptions() {
    ParseOptions parseOptions = new ParseOptions();
    parseOptions.setResolve(true);
    parseOptions.setResolveCombinators(false);
    parseOptions.setResolveFully(true);
    return parseOptions;
  }
}

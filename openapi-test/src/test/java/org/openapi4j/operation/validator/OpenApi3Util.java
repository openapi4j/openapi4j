package org.openapi4j.operation.validator;

import org.openapi4j.parser.OpenApi3Parser;
import org.openapi4j.parser.model.v3.OpenApi3;

import java.net.URL;

public class OpenApi3Util {
  public static OpenApi3 loadApi(String path) throws Exception {
    URL specPath = OpenApi3Util.class.getResource(path);

    return new OpenApi3Parser().parse(specPath, false);
  }
}

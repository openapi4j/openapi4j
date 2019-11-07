package org.openapi4j.parser;

import org.junit.Test;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.parser.model.v3.OpenApi3;

import java.net.URL;

public class IntegrationTest extends ParsingChecker {
  @Test(expected = ResolutionException.class)
  public void noSpec() throws Exception {
    checkParsing("/parser/not-a-file.yaml");
  }

  @Test
  public void parseWithValidation() throws Exception {
    URL specPath = getClass().getResource("/parser/oai-integration/api-with-examples.yaml");
    OpenApi3 api = new OpenApi3Parser().parse(specPath, true);
    checkFromResource(specPath, api);
  }

  @Test
  public void apiWithExamples() throws Exception {
    checkParsing("/parser/oai-integration/api-with-examples.yaml");
  }

  @Test
  public void callback() throws Exception {
    checkParsing("/parser/oai-integration/callback-example.yaml");
  }

  @Test
  public void link() throws Exception {
    checkParsing("/parser/oai-integration/link-example.yaml");
  }

  @Test
  public void petstore() throws Exception {
    checkParsing("/parser/oai-integration/petstore-expanded.yaml");
  }

  @Test
  public void uspto() throws Exception {
    checkParsing("/parser/oai-integration/uspto.yaml");
  }

  @Test
  public void api() throws Exception {
    checkParsing("/parser/oai-integration/api-definition.yaml");
  }
}

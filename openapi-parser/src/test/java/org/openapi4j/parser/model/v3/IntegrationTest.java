package org.openapi4j.parser.model.v3;

import org.junit.Test;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.parser.Checker;
import org.openapi4j.parser.OpenApi3Parser;

import java.net.URL;

public class IntegrationTest extends Checker {
  @Test(expected = ResolutionException.class)
  public void noSpec() throws Exception {
    validate("/foo/not-a-file.yaml");
  }

  @Test
  public void parseWithValidation() throws Exception {
    URL specPath = getClass().getResource("/model/v3/oai-integration/api-with-examples.yaml");
    OpenApi3 api = new OpenApi3Parser().parse(specPath, true);
    checkModel(specPath, api);
  }

  @Test
  public void apiWithExamples() throws Exception {
    validate("/model/v3/oai-integration/api-with-examples.yaml");
  }

  @Test
  public void callback() throws Exception {
    validate("/model/v3/oai-integration/callback-example.yaml");
  }

  @Test
  public void link() throws Exception {
    validate("/model/v3/oai-integration/link-example.yaml");
  }

  @Test
  public void petstore() throws Exception {
    validate("/model/v3/oai-integration/petstore-expanded.yaml");
  }

  @Test
  public void uspto() throws Exception {
    validate("/model/v3/oai-integration/uspto.yaml");
  }

  @Test
  public void fullOfKeywords() throws Exception {
    validate("/model/v3/oai-integration/fullOfKeywords.yaml");
  }
}

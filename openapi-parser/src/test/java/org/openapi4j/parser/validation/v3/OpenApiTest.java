package org.openapi4j.parser.validation.v3;

import org.junit.Test;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.parser.Checker;

public class OpenApiTest extends Checker {
  @Test
  public void openApi() throws Exception {
    validate("/validation/v3/openapi/valid/openapi.yaml");
  }

  //////////////////////////////////////////////////////////////
  // INVALID
  //////////////////////////////////////////////////////////////
  @Test(expected = ValidationException.class)
  public void openapi() throws Exception {
    validate("/validation/v3/openapi/invalid/openapi.yaml");
  }
}

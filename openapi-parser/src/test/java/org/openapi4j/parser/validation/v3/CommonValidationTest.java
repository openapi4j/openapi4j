package org.openapi4j.parser.validation.v3;

import org.junit.Test;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.parser.Checker;

public class CommonValidationTest extends Checker {
  @Test
  public void additionalProperties() throws Exception {
    validate("/validation/v3/additional-properties/valid/additional-properties.yaml");
  }

  @Test
  public void example() throws Exception {
    validate("/validation/v3/example/valid/example.yaml");
  }

  @Test
  public void externalDocs() throws Exception {
    validate("/validation/v3/externalDocs/valid/externalDocs.yaml");
  }

  @Test
  public void xml() throws Exception {
    validate("/validation/v3/xml/valid/xml.yaml");
  }

  //////////////////////////////////////////////////////////////
  // INVALID
  //////////////////////////////////////////////////////////////

  @Test(expected = ValidationException.class)
  public void malformedSpecInvalid() throws Exception {
    validate("/model/v3/invalid/malformed-spec.yaml");
  }

  @Test(expected = ResolutionException.class)
  public void additionalPropertiesInvalid() throws Exception {
    validate("/model/v3/invalid/additionalProperties.yaml");
  }

  @Test(expected = ValidationException.class)
  public void operationNoResponsesInvalid() throws Exception {
    validate("/validation/v3/operation/invalid/operationNoResponses.yaml");
  }
}

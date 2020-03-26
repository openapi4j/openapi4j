package org.openapi4j.parser.validation.v3;

import org.junit.Test;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.core.validation.ValidationSeverity;
import org.openapi4j.parser.Checker;

import static org.junit.Assert.assertEquals;

public class ParameterTest extends Checker {
  @Test
  public void parameter() throws Exception {
    validate("/validation/v3/parameter/valid/parameter.yaml");
  }

  @Test
  public void parameterAllowReserved() throws Exception {
    validate("/validation/v3/parameter/valid/parameterAllowReserved.yaml");
  }

  @Test
  public void parameterContent() throws Exception {
    validate("/validation/v3/parameter/valid/parameterContent.yaml");
  }

  @Test
  public void parameterStyles() throws Exception {
    validate("/validation/v3/parameter/valid/parameterStyles.yaml");
  }

  //////////////////////////////////////////////////////////////
  // INVALID
  //////////////////////////////////////////////////////////////
  @Test(expected = ValidationException.class)
  public void parameterInvalid() throws Exception {
    validate("/validation/v3/parameter/invalid/parameter.yaml");
  }

  @Test
  public void parameterAllowReservedInvalid() throws Exception {
    ValidationResults results = validate("/validation/v3/parameter/invalid/parameterAllowReserved.yaml");
    assertEquals(ValidationSeverity.WARNING, results.severity());
  }

  @Test(expected = ValidationException.class)
  public void parameterContentInvalid() throws Exception {
    validate("/validation/v3/parameter/invalid/parameterContent.yaml");
  }

  @Test(expected = ValidationException.class)
  public void parameterStylesInvalid() throws Exception {
    validate("/validation/v3/parameter/invalid/parameterStyles.yaml");
  }
}

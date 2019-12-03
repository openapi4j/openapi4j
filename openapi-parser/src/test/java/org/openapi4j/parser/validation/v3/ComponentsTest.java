package org.openapi4j.parser.validation.v3;

import org.junit.Test;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.parser.Checker;

public class ComponentsTest extends Checker {
  @Test
  public void components() throws Exception {
    validate("/validation/v3/components/valid/components.yaml");
  }

  //////////////////////////////////////////////////////////////
  // INVALID
  //////////////////////////////////////////////////////////////
  @Test(expected = ValidationException.class)
  public void componentsInvalid() throws Exception {
    validate("/validation/v3/components/invalid/components.yaml");
  }
}

package org.openapi4j.parser.validation.v3;

import org.junit.Test;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.parser.Checker;

public class HeaderTest extends Checker {
  @Test
  public void headerContent() throws Exception {
    validate("/validation/v3/header/valid/headerContent.yaml");
  }

  //////////////////////////////////////////////////////////////
  // INVALID
  //////////////////////////////////////////////////////////////
  @Test(expected = ValidationException.class)
  public void headerContentInvalid() throws Exception {
    validate("/validation/v3/header/invalid/headerContent.yaml");
  }
}

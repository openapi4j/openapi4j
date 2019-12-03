package org.openapi4j.parser.validation.v3;

import org.junit.Test;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.parser.Checker;

public class LicenseTest extends Checker {
  @Test
  public void license() throws Exception {
    validate("/validation/v3/license/valid/license.yaml");
  }

  //////////////////////////////////////////////////////////////
  // INVALID
  //////////////////////////////////////////////////////////////
  @Test(expected = ValidationException.class)
  public void licenseInvalid() throws Exception {
    validate("/validation/v3/license/invalid/license.yaml");
  }
}

package org.openapi4j.parser.validation.v3;

import org.junit.Test;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.parser.Checker;

public class SecurityTest extends Checker {
  @Test
  public void security() throws Exception {
    validate("/validation/v3/security/valid/security.yaml");
  }

  @Test
  public void securityRoot() throws Exception {
    validate("/validation/v3/security/valid/security-root.yaml");
  }

  //////////////////////////////////////////////////////////////
  // INVALID
  //////////////////////////////////////////////////////////////
  @Test(expected = ValidationException.class)
  public void securityInvalid() throws Exception {
    validate("/validation/v3/security/invalid/security.yaml");
  }

  @Test(expected = ValidationException.class)
  public void securityNoFlowInvalid() throws Exception {
    validate("/validation/v3/security/invalid/securityNoFlow.yaml");
  }
}

package org.openapi4j.parser.validation.v3;

import org.junit.Test;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.parser.Checker;

public class LinkTest extends Checker {
  @Test
  public void link() throws Exception {
    validate("/validation/v3/link/valid/link.yaml");
  }

  @Test
  public void linkPathParam() throws Exception {
    validate("/validation/v3/link/valid/linkPathParam.yaml");
  }

  @Test
  public void linkTargetParam() throws Exception {
    validate("/validation/v3/link/valid/linkTargetParam.yaml");
  }

  //////////////////////////////////////////////////////////////
  // INVALID
  //////////////////////////////////////////////////////////////
  @Test(expected = ValidationException.class)
  public void linkInvalid() throws Exception {
    validate("/validation/v3/link/invalid/link.yaml");
  }

  @Test(expected = ValidationException.class)
  public void linkPathParamInvalid() throws Exception {
    validate("/validation/v3/link/invalid/linkPathParam.yaml");
  }

  @Test(expected = ValidationException.class)
  public void linkTargetParamInvalid() throws Exception {
    validate("/validation/v3/link/invalid/linkTargetParam.yaml");
  }
}

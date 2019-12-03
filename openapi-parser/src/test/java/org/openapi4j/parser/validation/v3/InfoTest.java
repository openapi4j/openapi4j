package org.openapi4j.parser.validation.v3;

import org.junit.Test;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.parser.Checker;

public class InfoTest extends Checker {
  @Test
  public void advancedInfo() throws Exception {
    validate("/validation/v3/info/valid/advancedInfo.yaml");
  }

  @Test
  public void basicInfo() throws Exception {
    validate("/validation/v3/info/valid/basicInfo.yaml");
  }

  @Test
  public void minimalInfo() throws Exception {
    validate("/validation/v3/info/valid/minimalInfo.yaml");
  }

  //////////////////////////////////////////////////////////////
  // INVALID
  //////////////////////////////////////////////////////////////
  @Test(expected = ValidationException.class)
  public void minimalInfoInvalid() throws Exception {
    validate("/validation/v3/info/invalid/minimalInfo.yaml");
  }
}

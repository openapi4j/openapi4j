package org.openapi4j.parser.validation.v3;

import org.junit.Test;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.parser.Checker;

public class EncodingTest extends Checker {
  @Test
  public void encoding() throws Exception {
    validate("/validation/v3/encoding/valid/encoding.yaml");
  }

  //////////////////////////////////////////////////////////////
  // INVALID
  //////////////////////////////////////////////////////////////
  @Test(expected = ValidationException.class)
  public void encodingInvalid() throws Exception {
    validate("/validation/v3/encoding/invalid/encoding.yaml");
  }
}

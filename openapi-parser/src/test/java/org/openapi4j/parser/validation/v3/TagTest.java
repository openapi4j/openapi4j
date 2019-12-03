package org.openapi4j.parser.validation.v3;

import org.junit.Test;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.parser.Checker;

public class TagTest extends Checker {
  @Test
  public void tag() throws Exception {
    validate("/validation/v3/tag/valid/tag.yaml");
  }

  //////////////////////////////////////////////////////////////
  // INVALID
  //////////////////////////////////////////////////////////////
  @Test(expected = ValidationException.class)
  public void tagInvalid() throws Exception {
    validate("/validation/v3/tag/invalid/tag.yaml");
  }
}

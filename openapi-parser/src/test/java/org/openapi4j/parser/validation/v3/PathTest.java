package org.openapi4j.parser.validation.v3;

import org.junit.Test;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.parser.Checker;

public class PathTest extends Checker {
  @Test
  public void testPathWithReference() throws Exception {
    validate("/validation/v3/path/valid/pathWithReference.yaml");
  }

  @Test
  public void testPath() throws Exception {
    validate("/validation/v3/path/valid/path_param.yaml");
  }

  //////////////////////////////////////////////////////////////
  // INVALID
  //////////////////////////////////////////////////////////////
  @Test(expected = ValidationException.class)
  public void testPathWithReferenceInvalid() throws Exception {
    validate("/validation/v3/path/invalid/pathWithReference.yaml");
  }
}

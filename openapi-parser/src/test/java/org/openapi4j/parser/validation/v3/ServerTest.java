package org.openapi4j.parser.validation.v3;

import org.junit.Test;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.parser.Checker;

public class ServerTest extends Checker {
  @Test
  public void server() throws Exception {
    validate("/validation/v3/server/valid/server.yaml");
  }

  //////////////////////////////////////////////////////////////
  // INVALID
  //////////////////////////////////////////////////////////////
  @Test(expected = ValidationException.class)
  public void serverInvalid() throws Exception {
    validate("/validation/v3/server/invalid/server.yaml");
  }
}

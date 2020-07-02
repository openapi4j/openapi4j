package org.openapi4j.parser.validation.v3;

import org.junit.Test;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.parser.Checker;

public class ServerTest extends Checker {
  @Test
  public void server() throws Exception {
    validate("/validation/v3/server/valid/server.yaml");
  }

  @Test
  public void serverWithRelativeUrlIsValid() throws Exception {
    validate("/validation/v3/server/valid/server_relative_url.yaml");
  }

  @Test
  public void contactUrlRelativeToAbsServerUrlIsValid() throws Exception {
    validate("/validation/v3/server/valid/contact_relative_to_abs_server_url.yaml");
  }
  @Test
  public void contactUrlRelativeToRelativeServerUrlIsValid() throws Exception {
    validate("/validation/v3/server/valid/contact_relative_to_relative_server_url.yaml");
  }

  //////////////////////////////////////////////////////////////
  // INVALID
  //////////////////////////////////////////////////////////////
  @Test(expected = ValidationException.class)
  public void serverInvalid() throws Exception {
    validate("/validation/v3/server/invalid/server.yaml");
  }

  @Test(expected = ValidationException.class)
  public void serverWithoutUrlIsInvalid() throws Exception {
    validate("/validation/v3/server/invalid/server_no_url.yaml");
  }

  @Test(expected = ValidationException.class)
  public void serverWithWrongUrlIsInvalid() throws Exception {
    validate("/validation/v3/server/invalid/server_wrong_url.yaml");
  }

  @Test(expected = ValidationException.class)
  public void contactUrlRelativeToWrongAbsServerUrlIsInvalid() throws Exception {
    validate("/validation/v3/server/invalid/contact_relative_to_abs_server_url.yaml");
  }

  @Test(expected = ValidationException.class)
  public void contactUrlRelativeToWrongRelativeServerUrlIsInvalid() throws Exception {
    validate("/validation/v3/server/invalid/contact_relative_to_relative_server_url.yaml");
  }

  @Test(expected = ValidationException.class)
  public void contactUrlWrongRelativeToRelativeServerUrlIsInvalid() throws Exception {
    validate("/validation/v3/server/invalid/contact_wrong_relative_to_relative_server_url.yaml");
  }
}

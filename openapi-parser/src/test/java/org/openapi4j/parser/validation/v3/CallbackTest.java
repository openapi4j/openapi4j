package org.openapi4j.parser.validation.v3;

import org.junit.Test;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.parser.Checker;

public class CallbackTest extends Checker {
  @Test
  public void callbackSimple() throws Exception {
    validate("/validation/v3/callback/valid/callbackSimple.yaml");
  }

  @Test
  public void callbackWithBodyParameter() throws Exception {
    validate("/validation/v3/callback/valid/callbackWithBodyParameter.yaml");
  }

  @Test
  public void callbackWithPathParameter() throws Exception {
    validate("/validation/v3/callback/valid/callbackWithPathParameter.yaml");
  }

  @Test
  public void callbackWithReference() throws Exception {
    validate("/validation/v3/callback/valid/callbackWithReference.yaml");
  }

  @Test
  public void callbackWithResponseBodyParameter() throws Exception {
    validate("/validation/v3/callback/valid/callbackWithResponseBodyParameter.yaml");
  }

  @Test
  public void callbackWithResponseHeaderParameter() throws Exception {
    validate("/validation/v3/callback/valid/callbackWithResponseHeaderParameter.yaml");
  }

  //////////////////////////////////////////////////////////////
  // INVALID
  //////////////////////////////////////////////////////////////
  @Test(expected = ValidationException.class)
  public void callbackWithBodyParameterInvalid() throws Exception {
    validate("/validation/v3/callback/invalid/callbackWithBodyParameter.yaml");
  }

  @Test(expected = ValidationException.class)
  public void callbackWithPathParameterInvalid() throws Exception {
    validate("/validation/v3/callback/invalid/callbackWithPathParameter.yaml");
  }

  @Test(expected = ValidationException.class)
  public void callbackWithResponseBodyParameterInvalid() throws Exception {
    validate("/validation/v3/callback/invalid/callbackWithResponseBodyParameter.yaml");
  }

  @Test(expected = ValidationException.class)
  public void callbackWithResponseHeaderParameterInvalid() throws Exception {
    validate("/validation/v3/callback/invalid/callbackWithResponseHeaderParameter.yaml");
  }
}

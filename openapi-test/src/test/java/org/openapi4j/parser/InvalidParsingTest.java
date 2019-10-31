package org.openapi4j.parser;

import org.junit.Test;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.validation.ValidationException;

public class InvalidParsingTest extends ParsingChecker {
  @Test(expected = ValidationException.class)
  public void components() throws Exception {
    checkParsing("/parser/invalid/components.yaml");
  }

  @Test(expected = ValidationException.class)
  public void contact() throws Exception {
    checkParsing("/parser/invalid/contact.yaml");
  }

  @Test(expected = ResolutionException.class)
  public void discriminator() throws Exception {
    checkParsing("/parser/invalid/discriminator.yaml");
  }

  @Test(expected = ValidationException.class)
  public void license() throws Exception {
    checkParsing("/parser/invalid/license.yaml");
  }

  @Test(expected = ValidationException.class)
  public void links() throws Exception {
    checkParsing("/parser/invalid/link.yaml");
  }

  @Test(expected = ValidationException.class)
  public void malformedSpec() throws Exception {
    checkParsing("/parser/invalid/malformed-spec.yaml");
  }

  @Test(expected = ValidationException.class)
  public void parameter() throws Exception {
    checkParsing("/parser/invalid/parameter.yaml");
  }

  @Test(expected = ValidationException.class)
  public void openapi() throws Exception {
    checkParsing("/parser/invalid/openapi.yaml");
  }

  @Test(expected = ResolutionException.class)
  public void reference() throws Exception {
    checkParsing("/parser/invalid/reference.yaml");
  }

  @Test(expected = ResolutionException.class)
  public void referenceCyclingExtern() throws Exception {
    checkParsing("/parser/invalid/reference-cycling-extern1.yaml");
  }

  @Test(expected = ResolutionException.class)
  public void referenceCyclingIntern() throws Exception {
    checkParsing("/parser/invalid/reference-cycling-intern.yaml");
  }

  @Test(expected = ValidationException.class)
  public void security() throws Exception {
    checkParsing("/parser/invalid/security.yaml");
  }

  @Test(expected = ValidationException.class)
  public void servers() throws Exception {
    checkParsing("/parser/invalid/server.yaml");
  }
}

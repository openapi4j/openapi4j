package org.openapi4j.parser;

import org.junit.Test;

public class ValidParsingTest extends ParsingChecker {
  @Test
  public void additionalProperties() throws Exception {
    checkParsing("/parser/valid/additional-properties.yaml");
  }

  @Test
  public void callbacks() throws Exception {
    checkParsing("/parser/valid/callback.yaml");
  }

  @Test
  public void components() throws Exception {
    checkParsing("/parser/valid/components.yaml");
  }

  @Test
  public void contact() throws Exception {
    checkParsing("/parser/valid/contact.yaml");
  }

  @Test
  public void discriminator1() throws Exception {
    checkParsing("/parser/valid/discriminator1.yaml");
  }

  @Test
  public void discriminator2() throws Exception {
    checkParsing("/parser/valid/discriminator2.yaml");
  }

  @Test
  public void examples() throws Exception {
    checkParsing("/parser/valid/examples.yaml");
  }

  @Test
  public void license() throws Exception {
    checkParsing("/parser/valid/license.yaml");
  }

  @Test
  public void links() throws Exception {
    checkParsing("/parser/valid/link.yaml");
  }

  @Test
  public void parameter() throws Exception {
    checkParsing("/parser/valid/parameter.yaml");
  }

  @Test
  public void openapi() throws Exception {
    checkParsing("/parser/valid/openapi.yaml");
  }

  @Test
  public void security() throws Exception {
    checkParsing("/parser/valid/security.yaml");
  }

  @Test
  public void servers() throws Exception {
    checkParsing("/parser/valid/server.yaml");
  }
}

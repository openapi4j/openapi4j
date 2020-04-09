package org.openapi4j.core.model.v3;

import org.junit.Test;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.model.AuthOption;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OAI3ContextTest {
  @Test
  public void simple() throws ResolutionException {
    URL specPath = getClass().getResource("/parsing/discriminator.yaml");

    OAI3Context apiContext = new OAI3Context(specPath);
    assertEquals(specPath, apiContext.getBaseUrl());
    assertNotNull(apiContext.getReferenceRegistry().getRef("#/components/schemas/Cat"));
  }

  @Test
  public void simpleAuth() throws ResolutionException {
    URL specPath = getClass().getResource("/parsing/discriminator.yaml");

    OAI3Context apiContext = new OAI3Context(
      specPath,
      Arrays.asList(
        new AuthOption(AuthOption.Type.HEADER, "myHeader", "myValue"),
        new AuthOption(AuthOption.Type.QUERY, "myQueryParam", "myValue", url -> false)
      ));

    assertEquals(specPath, apiContext.getBaseUrl());
    assertNotNull(apiContext.getReferenceRegistry().getRef("#/components/schemas/Cat"));
  }

  @Test
  public void remote() throws ResolutionException, MalformedURLException {
    URL specPath = new URL("https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/petstore.yaml");

    OAI3Context apiContext = new OAI3Context(specPath);
    assertEquals(specPath, apiContext.getBaseUrl());
    assertNotNull(apiContext.getReferenceRegistry().getRef("#/components/schemas/Pet"));
  }
}

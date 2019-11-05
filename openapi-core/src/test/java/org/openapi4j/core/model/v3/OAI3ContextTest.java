package org.openapi4j.core.model.v3;

import org.junit.Test;
import org.openapi4j.core.exception.ResolutionException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OAI3ContextTest {
  @Test
  public void simple() throws URISyntaxException, ResolutionException {
    URL specPath = getClass().getResource("/parsing/discriminator.yaml");

    OAI3Context apiContext = new OAI3Context(specPath.toURI());
    assertEquals(specPath.toURI(), apiContext.getBaseUri());
    assertNotNull(apiContext.getReferenceRegistry().getRef("#/components/schemas/Cat"));
  }

  @Test
  public void remote() throws ResolutionException {
    URI specPath = URI.create("https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/petstore.yaml");

    OAI3Context apiContext = new OAI3Context(specPath);
    assertEquals(specPath, apiContext.getBaseUri());
    assertNotNull(apiContext.getReferenceRegistry().getRef("#/components/schemas/Pet"));
  }
}

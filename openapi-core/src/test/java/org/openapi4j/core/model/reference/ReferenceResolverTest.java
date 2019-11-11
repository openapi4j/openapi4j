package org.openapi4j.core.model.reference;

import com.fasterxml.jackson.databind.JsonNode;

import org.junit.Test;
import org.openapi4j.core.exception.DecodeException;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.model.v3.OAI3Context;
import org.openapi4j.core.util.TreeUtil;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class ReferenceResolverTest {
  @Test(expected = ResolutionException.class)
  public void referenceExternInvalid() throws Exception {
    URL specPath = getClass().getResource("/reference/invalid/reference-extern-missing.yaml");
    new OAI3Context(specPath.toURI());
  }

  @Test(expected = ResolutionException.class)
  public void referenceInternInvalid() throws Exception {
    URL specPath = getClass().getResource("/reference/invalid/reference-intern-missing.yaml");
    new OAI3Context(specPath.toURI());
  }

  @Test(expected = ResolutionException.class)
  public void referenceCyclingExtern() throws Exception {
    URL specPath = getClass().getResource("/reference/invalid/reference-cycling-extern1.yaml");
    new OAI3Context(specPath.toURI());
  }

  @Test(expected = ResolutionException.class)
  public void referenceCyclingIntern() throws Exception {
    URL specPath = getClass().getResource("/reference/invalid/reference-cycling-intern.yaml");
    new OAI3Context(specPath.toURI());
  }

  @Test(expected = DecodeException.class)
  public void referenceValid() throws Exception {
    URL specPath = getClass().getResource("/reference/valid/reference.yaml");
    OAI3Context apiContext = new OAI3Context(specPath.toURI());
    Reference reference = apiContext.getReferenceRegistry().getRef("reference2.yaml#/components/parameters/ARef");
    assertNotNull(reference.getContent());
    assertNotNull(reference.getMappedContent(Map.class));
    assertNotNull(reference.getMappedContent(LinkedHashMap.class)); // Cache code branch test
    reference.getMappedContent(List.class);
  }

  @Test(expected = ResolutionException.class)
  public void referenceWrongPathInvalid() throws Exception {
    URL specPath = getClass().getResource("/reference/invalid/reference-wrong-path.yaml");
    new OAI3Context(specPath.toURI());
  }

  @Test
  public void referenceValidWithDocument() throws Exception {
    URL specPath = getClass().getResource("/reference/valid/reference.yaml");
    JsonNode spec = TreeUtil.load(specPath);
    OAI3Context apiContext = new OAI3Context(specPath.toURI(), spec);
    Reference reference = apiContext.getReferenceRegistry().getRef("reference2.yaml#/components/parameters/ARef");
    assertNotNull(reference.getContent());
  }

  @Test(expected = DecodeException.class)
  public void referenceMappedContentException() throws Exception {
    URL specPath = getClass().getResource("/reference/valid/reference.yaml");
    OAI3Context apiContext = new OAI3Context(specPath.toURI());
    Reference reference = apiContext.getReferenceRegistry().getRef("reference2.yaml#/components/parameters/ARef");
    reference.getMappedContent(URL.class);
  }

  @Test
  public void linkValid() throws Exception {
    URL specPath = getClass().getResource("/reference/valid/link.yaml");
    JsonNode spec = TreeUtil.load(specPath);
    OAI3Context apiContext = new OAI3Context(specPath.toURI(), spec);
    Reference reference = apiContext.getReferenceRegistry().getRef("#/paths/~12.0~1repositories~1{username}~1{slug}~1pullrequests~1{pid}/get");
    assertNotNull(reference.getContent());
  }
}

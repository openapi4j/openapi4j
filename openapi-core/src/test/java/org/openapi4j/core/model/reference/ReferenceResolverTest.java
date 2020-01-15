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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
    // From resolved base URI
    Reference reference = apiContext.getReferenceRegistry().getRef(specPath.toURI().resolve("reference2.yaml"), "reference2.yaml#/components/parameters/ARef");
    assertNotNull(reference.getContent());
    assertNotNull(reference.getMappedContent(Map.class));
    assertNotNull(reference.getMappedContent(LinkedHashMap.class)); // Cache code branch test
    reference.getMappedContent(List.class);
    // From context/registry base uri
    reference = apiContext.getReferenceRegistry().getRef("reference2.yaml#/components/parameters/ARef");
    assertNotNull(reference.getContent());
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
    Reference sameReference = apiContext.getReferenceRegistry().getRef(null, "reference2.yaml#/components/parameters/ARef");
    assertNotNull(reference.getContent());
    assertEquals(reference, sameReference);
  }

  @Test
  public void referenceValueNull() throws Exception {
    URL specPath = getClass().getResource("/reference/valid/reference.yaml");
    assertEquals(specPath.toURI(), ReferenceUri.resolve(specPath.toURI(), null));
  }

  @Test
  public void referenceWithRelativePathSetupTwice() throws Exception {
    URL specPath = getClass().getResource("/reference/valid/identical_relative_ref/api.yaml");
    JsonNode spec = TreeUtil.load(specPath);
    OAI3Context apiContext = new OAI3Context(specPath.toURI(), spec);

    assertEquals(3, apiContext.getReferenceRegistry().getReferences().size());

    Reference refType1 = apiContext.getReferenceRegistry().getRef("testType.yaml#/TestType");
    assertNotNull(refType1.getContent());
    Reference refType2 = apiContext.getReferenceRegistry().getRef("schema2/testType.yaml#/TestType");
    assertNotNull(refType2.getContent());
    assertNotEquals(refType1, refType2);
  }

  @Test
  public void referenceWithRelativePathSetupTwiceExternalResolution() throws Exception {
    URL specPath = getClass().getResource("/reference/valid/identical_relative_ref/api.yaml");
    JsonNode spec = TreeUtil.load(specPath);
    OAI3Context apiContext = new OAI3Context(specPath.toURI(), spec);

    assertEquals(3, apiContext.getReferenceRegistry().getReferences().size());

    String urlString = specPath.toString();

    String absoluteUriPrefix = urlString.substring(0, urlString.indexOf("api.yaml"));
    String correctRelativeUri = "schema2/schema2.yaml#/Schema2";
    String incorrectRelativeUri = "schema2/" + correctRelativeUri;
    String correctAbsoluteUri = absoluteUriPrefix + correctRelativeUri;
    String incorrectAbsoluteUri = absoluteUriPrefix + incorrectRelativeUri;

    // Search with absolute values
    Reference correctAbsoluteReference = apiContext.getReferenceRegistry().getRef(correctAbsoluteUri);
    Reference incorrectAbsoluteReference = apiContext.getReferenceRegistry().getRef(incorrectAbsoluteUri);
    assertNotNull(correctAbsoluteReference);
    assertNull(incorrectAbsoluteReference);
    // Search with relative values
    Reference correctSchema2RelativeReference = apiContext.getReferenceRegistry().getRef(correctRelativeUri);
    Reference incorrectSchema2RelativeReference = apiContext.getReferenceRegistry().getRef(incorrectRelativeUri);
    assertNotNull(correctSchema2RelativeReference);
    assertNull(incorrectSchema2RelativeReference);
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

package org.openapi4j.operation.validator.validation;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.operation.validator.OpenApi3Util;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.Response;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.model.impl.DefaultRequest;
import org.openapi4j.operation.validator.model.impl.DefaultResponse;
import org.openapi4j.operation.validator.util.ContentType;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Path;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class OperationContentTypeTest {
  private static OpenApi3 api;
  private static RequestValidator requestValidator;
  private Path path;
  private Operation operation;

  @BeforeClass
  public static void setup() throws Exception {
    api = OpenApi3Util.loadApi("/operation/operationContentType.yaml");
    requestValidator = new RequestValidator(api);
  }

  @Test
  public void testFormUrlEncoded() throws Exception {
    final String body = "fieldInt=1&fieldString=value%202&fieldBool= true &fieldFloat=1.2&fieldArray=1&fieldArray=2";

    // will fall back to UTF-8
    checkRequest("common", "application/x-www-form-urlencoded; charset=WRONG-ENCODING", body);
    checkRequest("common", "application/x-www-form-urlencoded", body);

    checkRequest("common", "application/x-www-form-urlencoded; charset=utf-8", body);
  }

  @Test
  public void testMultipart() throws Exception {
    final String body
      = "--1234\r\n"
      + "Content-Disposition: form-data; name=\"file\"; filename=\"foo.file\"\r\n"
      + "Content-Type: text/whatever\r\n"
      + "\r\n"
      + "This is the content of the file\n"
      + "\r\n"
      + "--1234\r\n"
      + "Content-Disposition: form-data; name=\"fieldInt\"\r\n"
      + "\r\n"
      + "1\r\n"
      + "--1234\r\n"
      + "Content-Disposition: form-data; name=\"fieldString\"\r\n"
      + "\r\n"
      + "value 2\r\n"
      + "--1234\r\n"
      + "Content-Disposition: form-data; name=\"fieldArray\"\r\n"
      + "\r\n"
      + "1\r\n"
      + "--1234\r\n"
      + "Content-Disposition: form-data; name=\"fieldBool\"\r\n"
      + "\r\n"
      + "true  \r\n"
      + "--1234\r\n"
      + "Content-Disposition: form-data; name=\"fieldFloat\"\r\n"
      + "\r\n"
      + "1.2\r\n"
      + "--1234\r\n"
      + "Content-Disposition: form-data; name=\"fieldArray\"\r\n"
      + "\r\n"
      + "2\r\n"
      + "--1234\r\n"
      + "Content-Disposition: form-data; name=\"fieldObject\"\r\n" // default serialization is application/json
      + "\r\n"
      + "{\"id\":\"myId\"}\r\n"
      + "--1234\r\n"
      + "Content-Disposition: form-data; name=\"fieldArray\"\r\n"
      + "\r\n"
      + "3\r\n"
      + "--1234--\r\n";

    checkRequest("common", "multipart/form-data;boundary=\"1234\"", body);
  }

  @Test
  public void testMultipartMixed() throws Exception {
    final String body
      = "--1234\r\n"
      + "Content-Disposition: form-data; name=\"file\"; filename=\"foo.file\"\r\n"
      + "Content-Type: text/plain\r\n"
      + "\r\n"
      + "This is the content of the file\n"
      + "\r\n"
      + "--1234\r\n"
      + "Content-Disposition: form-data; name=\"fieldInt\"\r\n"
      + "\r\n"
      + "1\r\n"
      + "--1234\r\n"
      + "Content-Disposition: form-data; name=\"fieldString\"\r\n"
      + "\r\n"
      + "value 2\r\n"
      + "--1234\r\n"
      + "Content-Disposition: form-data; name=\"fieldBool\"\r\n"
      + "\r\n"
      + "true  \r\n"
      + "--1234\r\n"
      + "Content-Disposition: form-data; name=\"fieldFloat\"\r\n"
      + "\r\n"
      + "1.2\r\n"
      + "--1234\r\n"
      + "Content-Disposition: form-data; name=\"fieldObject\"\r\n"
      + "Content-Type: application/json\r\n"
      + "\r\n"
      + "{\"id\":\"myId\"}\r\n"
      + "--1234\r\n"
      + "Content-Disposition: form-data; name=\"fieldArray\"\r\n"
      + "Content-Type: application/json\r\n"
      + "\r\n"
      + "[1, 2, 3]\r\n"
      + "--1234--\r\n";

    checkRequest("common", "multipart/mixed;boundary=\"1234\"", body);
  }

  @Test
  public void testJson() throws Exception {
    final String body
      = "{\n"
      + "  \"fieldInt\": 1,\n"
      + "  \"fieldString\": \"pokfpokdf\",\n"
      + "  \"fieldBool\": false,\n"
      + "  \"fieldFloat\": 1.2,\n"
      + "  \"fieldArray\": [1, 2, 3],\n"
      + "  \"fieldObject\": {\"id\":\"myId\"}\n"
      + "}";

    checkRequest("common", "application/json", body);
    checkResponse("common", "application/json", body);
  }

  @Test
  public void testXml() throws Exception {
    final String body
      = "<FooModel id=\"123\">\n"
      + "  <fieldInt>1</fieldInt>\n"
      + "  <sample:fieldString xmlns:sample=\"http://example.com/schema/sample\">a value</sample:fieldString>\n"
      + "  <fieldBool>true</fieldBool>\n"
      + "  <books><book>1</book><book>2</book></books>\n"
      + "  <fieldFloat>1</fieldFloat>\n"
      + "  <fieldArray>1</fieldArray>\n"
      + "  <fieldArray>2</fieldArray>\n"
      + "  <fieldArray>3</fieldArray>\n"
      + "  <fieldObject><id>myId</id></fieldObject>\n"
      + "</FooModel>";

    checkRequest("common", "application/xml", body);
    checkResponse("common", "application/xml", body);
  }

  @Test
  public void testXmlArray() throws Exception {
    final String body
      = "<i>1</i><i>2</i>\n"
      + "<i>3</i><i>4</i>\n"
      + "<i>5</i><i>6</i>\n";

    checkRequest("xmlFieldArray", "application/xml", body);
    checkResponse("xmlFieldArray", "application/xml", body);
  }

  @Test
  public void testXmlArrayWrapped() throws Exception {
    final String body
      = "<items>\n"
      + "  <items><id><id>1</id><id>2</id></id></items>\n"
      + "  <items><id><id>3</id><id>4</id></id></items>\n"
      + "  <items><id><id>5</id><id>6</id></id></items>\n"
      + "</items>";

    checkRequest("xmlFieldArrayWrapped", "application/xml", body);
    checkResponse("xmlFieldArrayWrapped", "application/xml", body);
  }

  @Test(expected = ValidationException.class)
  public void testXmlArrayMinItemsMissingRequest() throws Exception {
    checkRequest("xmlFieldArray", "application/xml", "");
  }

  @Test(expected = ValidationException.class)
  public void testXmlArrayMinItemsMissingResponse() throws Exception {
    checkResponse("xmlFieldArray", "application/xml", "");
  }

  @Test
  public void testDirect() {
    assertFalse(ContentType.isMultipartFormData("foo"));
    assertEquals(StandardCharsets.UTF_8.name(), ContentType.getCharSet(null));
  }

  private void checkRequest(String operationId, String contentType, String body) throws Exception {
    operation = api.getOperationById(operationId);
    path = api.getPathItemByOperationId(operationId);

    checkRequest(contentType, Body.from(body));
    // With input stream
    checkRequest(contentType, Body.from(new ByteArrayInputStream(body.getBytes())));
  }

  private void checkRequest(String contentType,
                            Body body) throws Exception {

    DefaultRequest.Builder builder = new DefaultRequest.Builder(Request.Method.POST, "/");

    Request rq = builder
      .header("Content-Type", contentType)
      .body(body)
      .build();

    requestValidator.validate(rq, path, operation);
  }

  private void checkResponse(String operationId,
                             String contentType,
                             String body) throws Exception {

    operation = api.getOperationById(operationId);
    path = api.getPathItemByOperationId(operationId);

    DefaultResponse.Builder builder = new DefaultResponse.Builder(200);

    Response resp = builder
      .header("Content-Type", contentType)
      .header("X-Rate-Limit", String.valueOf(1))
      .body(Body.from(body))
      .build();

    requestValidator.validate(resp, path, operation);
  }
}

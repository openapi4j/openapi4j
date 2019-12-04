package org.openapi4j.operation.validator.validation;

import org.junit.Test;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.operation.validator.OpenApi3Util;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.Response;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.model.impl.DefaultRequest;
import org.openapi4j.operation.validator.model.impl.DefaultResponse;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Path;

import java.io.ByteArrayInputStream;

public class OperationContentTypeTest {
  private Path path;
  private Operation operation;
  private RequestValidator requestValidator;

  @Test
  public void testFormUrlEncoded() throws Exception {
    final String body = "fieldInt=1&fieldString=value%202&fieldBool= true &fieldFloat=1.2&fieldArray=1&fieldArray=2";

    check("common", "application/x-www-form-urlencoded; charset=utf-8", body, false);
  }

  @Test
  public void testMultipart() throws Exception {
    final String body = "--1234\r\n" +
      "Content-Disposition: form-data; name=\"file\"; filename=\"foo.file\"\r\n" +
      "Content-Type: text/whatever\r\n" +
      "\r\n" +
      "This is the content of the file\n" +
      "\r\n" +
      "--1234\r\n" +
      "Content-Disposition: form-data; name=\"fieldInt\"\r\n" +
      "\r\n" +
      "1\r\n" +
      "--1234\r\n" +
      "Content-Disposition: form-data; name=\"fieldString\"\r\n" +
      "\r\n" +
      "value 2\r\n" +
      "--1234\r\n" +
      "Content-Disposition: form-data; name=\"fieldArray\"\r\n" +
      "\r\n" +
      "1\r\n" +
      "--1234\r\n" +
      "Content-Disposition: form-data; name=\"fieldBool\"\r\n" +
      "\r\n" +
      "true  \r\n" +
      "--1234\r\n" +
      "Content-Disposition: form-data; name=\"fieldFloat\"\r\n" +
      "\r\n" +
      "1.2\r\n" +
      "--1234\r\n" +
      "Content-Disposition: form-data; name=\"fieldArray\"\r\n" +
      "\r\n" +
      "2\r\n" +
      "--1234\r\n" +
      "Content-Disposition: form-data; name=\"fieldObject\"\r\n" + // default serialization is application/json
      "\r\n" +
      "{\"id\":\"myId\"}\r\n" +
      "--1234\r\n" +
      "Content-Disposition: form-data; name=\"fieldArray\"\r\n" +
      "\r\n" +
      "3\r\n" +
      "--1234--\r\n";

    check("common", "multipart/form-data;boundary=\"1234\"", body, false);
  }

  @Test
  public void testMultipartMixed() throws Exception {
    final String body = "--1234\r\n" +
      "Content-Disposition: form-data; name=\"file\"; filename=\"foo.file\"\r\n" +
      "Content-Type: text/plain\r\n" +
      "\r\n" +
      "This is the content of the file\n" +
      "\r\n" +
      "--1234\r\n" +
      "Content-Disposition: form-data; name=\"fieldInt\"\r\n" +
      "\r\n" +
      "1\r\n" +
      "--1234\r\n" +
      "Content-Disposition: form-data; name=\"fieldString\"\r\n" +
      "\r\n" +
      "value 2\r\n" +
      "--1234\r\n" +
      "Content-Disposition: form-data; name=\"fieldBool\"\r\n" +
      "\r\n" +
      "true  \r\n" +
      "--1234\r\n" +
      "Content-Disposition: form-data; name=\"fieldFloat\"\r\n" +
      "\r\n" +
      "1.2\r\n" +
      "--1234\r\n" +
      "Content-Disposition: form-data; name=\"fieldObject\"\r\n" +
      "Content-Type: application/json\r\n" +
      "\r\n" +
      "{\"id\":\"myId\"}\r\n" +
      "--1234\r\n" +
      "Content-Disposition: form-data; name=\"fieldArray\"\r\n" +
      "Content-Type: application/json\r\n" +
      "\r\n" +
      "[1, 2, 3]\r\n" +
      "--1234--\r\n";

    check("common", "multipart/mixed;boundary=\"1234\"", body, false);
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

    check("common", "application/json", body, true);
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

    check("common", "application/xml", body, true);
  }

  @Test
  public void testXmlArray() throws Exception {
    final String body
      = "<i>1</i><i>2</i>\n"
      + "<i>3</i><i>4</i>\n"
      + "<i>5</i><i>6</i>\n";

    check("xmlFieldArray", "application/xml", body, true);
  }

  @Test
  public void testXmlArrayWrapped() throws Exception {
    final String body
      = "<items>\n"
      + "  <items><id><id>1</id><id>2</id></id></items>\n"
      + "  <items><id><id>3</id><id>4</id></id></items>\n"
      + "  <items><id><id>5</id><id>6</id></id></items>\n"
      + "</items>";

    check("xmlFieldArrayWrapped", "application/xml", body, true);
  }

  @Test(expected = ValidationException.class)
  public void testXmlArrayMinItemsMissing() throws Exception {
    final String body = "";

    check("xmlFieldArray", "application/xml", body, true);
  }

  private void check(String operationId, String contentType, String body, boolean checkResponse) throws Exception {
    OpenApi3 api = OpenApi3Util.loadApi("/operation/operationContentType.yaml");
    operation = api.getOperationById(operationId);
    path = api.getPathItemByOperationId(operationId);

    requestValidator = new RequestValidator(api);

    checkRequest(contentType, Body.from(body));
    // With input stream
    checkRequest(contentType, Body.from(new ByteArrayInputStream(body.getBytes())));

    if (checkResponse) {
      checkResponse(contentType, body);
    }
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

  private void checkResponse(String contentType,
                             String body) throws Exception {

    DefaultResponse.Builder builder = new DefaultResponse.Builder(200);
    Response resp = builder
      .header("Content-Type", contentType)
      .header("X-Rate-Limit", String.valueOf(1))
      .body(Body.from(body))
      .build();

    requestValidator.validate(resp, path, operation);
  }
}

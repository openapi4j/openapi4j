package org.openapi4j.operation.validator.model.impl;

import org.junit.Test;
import org.openapi4j.operation.validator.OpenApi3Util;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.validation.RequestValidator;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Path;

public class DefaultRequestTest {
  @Test
  public void testFormUrlEncoded() throws Exception {
    OpenApi3 api = OpenApi3Util.loadApi("/operation/api.yaml");
    Operation operation = api.getOperationById("test");
    Path path = api.getPathItemByOperationId("test");

    DefaultRequest.Builder rqBuilder = new DefaultRequest.Builder(Request.Method.POST, "/");
    Request rq = rqBuilder
      .header("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
      .body(Body.from("fieldInt=1&fieldString=value%202&fieldBool= true &fieldFloat=1.2&fieldArray=1&fieldArray=2"))
      .build();

    RequestValidator requestValidator = new RequestValidator(api);
    requestValidator.validate(rq, path, operation);
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
      "1\r\n" +
      "--1234\r\n" +
      "Content-Disposition: form-data; name=\"fieldArray\"\r\n" +
      "\r\n" +
      "2\r\n" +
      "--1234--\r\n";

    OpenApi3 api = OpenApi3Util.loadApi("/operation/api.yaml");
    Operation operation = api.getOperationById("test");
    Path path = api.getPathItemByOperationId("test");

    DefaultRequest.Builder rqBuilder = new DefaultRequest.Builder(Request.Method.POST, "/");
    Request rq = rqBuilder
      .header("Content-Type", "multipart/form-data;boundary=\"1234\"")
      .body(Body.from(body))
      .build();

    RequestValidator requestValidator = new RequestValidator(api);
    requestValidator.validate(rq, path, operation);
  }

  @Test
  public void testJson() throws Exception {
    OpenApi3 api = OpenApi3Util.loadApi("/operation/api.yaml");
    Operation operation = api.getOperationById("test");
    Path path = api.getPathItemByOperationId("test");

    DefaultRequest.Builder rqBuilder = new DefaultRequest.Builder(Request.Method.POST, "/");
    Request rq = rqBuilder
      .header("Content-Type", "application/json")
      .body(Body.from("{\n" +
        "  \"fieldInt\": 1,\n" +
        "  \"fieldString\": \"pokfpokdf\",\n" +
        "  \"fieldBool\": false,\n" +
        "  \"fieldFloat\": 1.2,\n" +
        "  \"fieldArray\": [1, 2, 3]\n" +
        "}"))
      .build();

    RequestValidator requestValidator = new RequestValidator(api);
    requestValidator.validate(rq, path, operation);
  }

  @Test
  public void testXml() throws Exception {
    OpenApi3 api = OpenApi3Util.loadApi("/operation/api.yaml");
    Operation operation = api.getOperationById("test");
    Path path = api.getPathItemByOperationId("test");

    DefaultRequest.Builder rqBuilder = new DefaultRequest.Builder(Request.Method.POST, "/");
    Request rq = rqBuilder
      .header("Content-Type", "application/xml")
      .body(Body.from("<FooModel id=\"123\">\n" +
        "  <fieldInt>1</fieldInt>\n" +
        "  <sample:fieldString xmlns:sample=\"http://example.com/schema/sample\">a value</sample:fieldString>\n" +
        "  <fieldBool>true</fieldBool>\n" +
        "  <books><book>1</book><book>2</book></books>\n" +
        "  <fieldFloat>1</fieldFloat>\n" +
        "  <fieldArray>1</fieldArray>\n" +
        "  <fieldArray>2</fieldArray>\n" +
        "  <fieldArray>3</fieldArray>\n" +
        "</FooModel>"))
      .build();

    RequestValidator requestValidator = new RequestValidator(api);
    requestValidator.validate(rq, path, operation);
  }
}

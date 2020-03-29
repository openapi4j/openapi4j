package org.perf.check.operation;

import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.model.impl.DefaultRequest;
import org.openapi4j.operation.validator.validation.OperationValidator;
import org.openapi4j.operation.validator.validation.RequestValidator;
import org.openapi4j.parser.OpenApi3Parser;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Path;

import java.net.URL;

class OpenApi4j {
  private final Path path;
  private final Operation operation;
  private final RequestValidator requestValidator;

  OpenApi4j(String schemaFile) throws ValidationException, ResolutionException {
    URL specPath = getClass().getClassLoader().getResource(schemaFile);
    OpenApi3 api = new OpenApi3Parser().parse(specPath, true);
    operation = api.getOperationById("test");
    path = api.getPathItemByOperationId("test");
    requestValidator = new RequestValidator(api);
  }

  String validateFormUrlEncoded() {
    final String body = "fieldInt=1&fieldString=value%202&fieldBool= true &fieldFloat=1.2&fieldArray=1&fieldArray=2";

    return validateRequest("application/x-www-form-urlencoded; charset=utf-8", body);
  }

  String validateFormData() {
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

    return validateRequest("multipart/form-data;boundary=\"1234\"", body);
  }

  String validateMultipartMixed() {
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

    return validateRequest("multipart/mixed;boundary=\"1234\"", body);
  }

  String validateJson() {
    String body =
      "{\n" +
        "  \"fieldInt\": 1,\n" +
        "  \"fieldString\": \"pokfpokdf\",\n" +
        "  \"fieldBool\": false,\n" +
        "  \"fieldFloat\": 1.2,\n" +
        "  \"fieldArray\": [1, 2, 3],\n" +
        "  \"fieldObject\": {\"id\":\"myId\"}\n" +
        "}";

    return validateRequest("application/json", body);
  }

  String validateXml() {
    String body =
      "<FooModel id=\"123\">\n" +
        "  <fieldInt>1</fieldInt>\n" +
        "  <sample:fieldString xmlns:sample=\"http://example.com/schema/sample\">a value</sample:fieldString>\n" +
        "  <fieldBool>true</fieldBool>\n" +
        "  <books><book>1</book><book>2</book></books>\n" +
        "  <fieldFloat>1</fieldFloat>\n" +
        "  <fieldArray>1</fieldArray>\n" +
        "  <fieldArray>2</fieldArray>\n" +
        "  <fieldArray>3</fieldArray>\n" +
        "  <fieldObject><id>myId</id></fieldObject>\n" +
        "</FooModel>";

    return validateRequest("application/xml", body);
  }

  private String validateRequest(String contentType, String body) {
    DefaultRequest.Builder rqBuilder = new DefaultRequest.Builder("/", Request.Method.POST);
    Request rq = rqBuilder
      .header("Content-Type", contentType)
      .body(Body.from(body))
      .build();

    try {
      requestValidator.validate(rq, path, operation);
    } catch (ValidationException e) {
      return e.toString();
    }

    return null;
  }

  public String getVersion() {
    return OperationValidator.class.getPackage().getImplementationVersion();
  }
}

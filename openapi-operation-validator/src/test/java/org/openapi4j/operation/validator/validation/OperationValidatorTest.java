package org.openapi4j.operation.validator.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openapi4j.core.util.TreeUtil;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.Response;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.model.impl.DefaultRequest;
import org.openapi4j.operation.validator.model.impl.DefaultResponse;
import org.openapi4j.parser.OpenApi3Parser;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Path;

import java.io.IOException;
import java.net.URL;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openapi4j.operation.validator.model.Request.Method.GET;
import static org.openapi4j.operation.validator.model.Request.Method.POST;

public class OperationValidatorTest {
  private static OpenApi3 api;

  @BeforeClass
  public static void setup() throws Exception {
    URL specPath = OperationValidatorTest.class.getResource("/operation/operationValidator.yaml");
    api = new OpenApi3Parser().parse(specPath, false);
  }

  @Test
  public void pathCheck() {
    OperationValidator val = loadOperationValidator("paramCheck");

    // String can be also a number
    check(
      new DefaultRequest.Builder("/fixed/1/fixed/2/fixed/", GET).build(),
      val::validatePath,
      true);


    // 'string' is not a number
    check(
      new DefaultRequest.Builder("https://api.com/fixed/string/fixed/2/fixed/", GET).build(),
      val::validatePath,
      false);

    // wrong path
    check(
      new DefaultRequest.Builder("https://api.com/fixed/fixed/2/fixed/", GET).build(),
      val::validatePath,
      false);

    // Empty string is still valid
    check(
      new DefaultRequest.Builder("https://api.com/fixed/1/fixed//fixed/", GET).build(),
      val::validatePath,
      true);


    // Validation with full fixed path template
    val = loadOperationValidator("merge_parameters");

    check(
      new DefaultRequest.Builder("/merge_parameters", GET).build(),
      val::validatePath,
      true);

    check(
      new DefaultRequest.Builder("/foo/bar/merge_parameters", GET).build(),
      val::validatePath,
      false);

    check(
      new DefaultRequest.Builder("https://api.com/foo/bar/merge_parameters", GET).build(),
      val::validatePath,
      false);
  }

  @Test
  public void queryCheck() {
    OperationValidator val = loadOperationValidator("paramCheck");

    check(
      new DefaultRequest.Builder("/foo", GET).query("boolQueryParam=true").build(),
      val::validateQuery,
      true);

    check(
      new DefaultRequest.Builder("/foo", GET).query("boolQueryParam=false&intQueryParam=12").build(),
      val::validateQuery,
      true);

    // nullable
    check(
      new DefaultRequest.Builder("/foo", GET).query("boolQueryParam=false&intQueryParam=").build(),
      val::validateQuery,
      false);

    check(
      new DefaultRequest.Builder("/foo", GET).query("boolQueryParam=yes").build(),
      val::validateQuery,
      false);

    // required
    check(
      new DefaultRequest.Builder("/foo", GET).build(),
      val::validateQuery,
      false);
  }

  @Test
  public void headerCheck() {
    OperationValidator val = loadOperationValidator("paramCheck");

    check(
      new DefaultRequest.Builder("/foo", GET).header("pathStringHeaderParam", "foo").header("floatHeaderParam", "0.1").build(),
      val::validateHeaders,
      true);

    check(
      new DefaultRequest.Builder("/foo", GET).header("pathStringHeaderParam", "foo").header("floatHeaderParam", ".1").build(),
      val::validateHeaders,
      true);

    check(
      new DefaultRequest.Builder("/foo", GET).header("pathStringHeaderParam", "foo").header("floatHeaderParam", "0,1").build(),
      val::validateHeaders,
      false);

    // operation param required
    check(
      new DefaultRequest.Builder("/foo", GET).header("pathStringHeaderParam", "foo").build(),
      val::validateHeaders,
      false);

    // path param required
    check(
      new DefaultRequest.Builder("/foo", GET).header("floatHeaderParam", "0.1").build(),
      val::validateHeaders,
      false);
  }

  @Test
  public void cookieCheck() {
    OperationValidator val = loadOperationValidator("paramCheck");

    check(
      new DefaultRequest.Builder("/foo", GET).cookie("dtCookieParam", "1996-12-19T16:39:57-08:00").build(),
      val::validateCookies,
      true);

    // Not a date-time
    check(
      new DefaultRequest.Builder("/foo", GET).cookie("dtCookieParam", "1996-12-19").build(),
      val::validateCookies,
      false);

    // required
    check(
      new DefaultRequest.Builder("/foo", GET).build(),
      val::validateCookies,
      false);
  }

  @Test
  public void requestBodyCheck() {
    OperationValidator val = loadOperationValidator("rqBodyCheck");

    JsonNode body = JsonNodeFactory.instance.objectNode().set(
      "param",
      JsonNodeFactory.instance.textNode("foo"));

    check(
      new DefaultRequest.Builder("/foo", GET).header("Content-Type", "application/json").body(Body.from(body)).build(),
      val::validateBody,
      true);

    check(
      new DefaultRequest.Builder("/foo", GET).header("Content-Type", "application/json").build(),
      val::validateBody,
      false);

    check(
      new DefaultRequest.Builder("/foo", GET).body(Body.from(body)).build(),
      val::validateBody,
      false);

    check(
      new DefaultRequest.Builder("/foo", GET).header("Content-Type", "text/plain").body(Body.from(body)).build(),
      val::validateBody,
      false);

    check(
      new DefaultRequest.Builder("/foo", GET).header("Content-Type", "text/plain; charset=utf-8").body(Body.from("dummy")).build(),
      val::validateBody,
      true);

    check(
      new DefaultRequest.Builder("/foo", GET).header("Content-Type", "text/plain; charset=iso-8859-1").body(Body.from("dummy")).build(),
      val::validateBody,
      false);

    check(
      new DefaultRequest.Builder("/foo", GET).header("Content-Type", "image/png").body(Body.from("dummy")).build(),
      val::validateBody,
      true);

    check(
      new DefaultRequest.Builder("/foo", GET).build(),
      val::validateBody,
      false);
  }

  @Test
  public void discriminatorCheck() throws IOException {
    OperationValidator val = loadOperationValidator("discriminator");

    JsonNode body = TreeUtil.json.readTree("{\"pet_type\": \"Cat\", \"age\": 3}");
    check(
      new DefaultRequest.Builder("/discriminator", POST).header("Content-Type", "application/json").body(Body.from(body)).build(),
      val::validateBody,
      true);

    body = TreeUtil.json.readTree("{\"pet_type\": \"Dog\", \"bark\": true}");
    check(
      new DefaultRequest.Builder("/discriminator", POST).header("Content-Type", "application/json").body(Body.from(body)).build(),
      val::validateBody,
      true);

    body = TreeUtil.json.readTree("{\"pet_type\": \"Dog\", \"bark\": false, \"breed\": \"Dingo\"}");
    check(
      new DefaultRequest.Builder("/discriminator", POST).header("Content-Type", "application/json").body(Body.from(body)).build(),
      val::validateBody,
      true);

    body = TreeUtil.json.readTree("{\"age\": 3}");
    check(
      new DefaultRequest.Builder("/discriminator", POST).header("Content-Type", "application/json").body(Body.from(body)).build(),
      val::validateBody,
      false);

    body = TreeUtil.json.readTree("{\"pet_type\": \"Dog\", \"bark\": false, \"breed\": \"foo\"}");
    check(
      new DefaultRequest.Builder("/discriminator", POST).header("Content-Type", "application/json").body(Body.from(body)).build(),
      val::validateBody,
      false);
  }

  @Test
  public void mergePathToOperationParametersTest() {
    OperationValidator val = loadOperationValidator("merge_parameters");

    check(
      new DefaultRequest.Builder("/merge_parameters", GET).build(),
      val::validateHeaders,
      false);

    check(
      new DefaultRequest.Builder("/merge_parameters", GET).header("pathStringHeaderParam", "foo").header("refIntHeaderParameter", "-1").build(),
      val::validateHeaders,
      true);
  }

  @Test
  public void wrongDefinitionForBodyResponseTest() {
    OperationValidator val = loadOperationValidator("wrong_definition_for_body_response");

    check(
      new DefaultRequest.Builder("/wrong_definition_for_body_response", POST).build(),
      val::validateBody,
      true);

    check(
      new DefaultResponse.Builder(200).build(),
      val::validateBody,
      true);

    val = loadOperationValidator("wrong_definition_for_body_response2");

    check(
      new DefaultResponse.Builder(200).build(),
      val::validateBody,
      true);
  }

  @Test
  public void responseCheck() {
    OperationValidator val = loadOperationValidator("rqBodyCheck");

    check(
      new DefaultResponse.Builder(500).header("Content-Type", "application/json").build(),
      val::validateBody,
      true);

    check(
      new DefaultResponse.Builder(200).header("Content-Type", "text/plain").body(Body.from("dummy")).build(),
      val::validateBody,
      true);

    // Wrong content type
    check(
      new DefaultResponse.Builder(500).header("Content-Type", "foo").build(),
      val::validateBody,
      false);

    // No header validators
    check(
      new DefaultResponse.Builder(500).header("X-Rate-Limit", "1").build(),
      val::validateHeaders,
      true);

    val = loadOperationValidator("paramCheck");

    // No default response
    check(
      new DefaultResponse.Builder(500).build(),
      val::validateBody,
      false);

    check(
      new DefaultResponse.Builder(500).header("X-Rate-Limit", "1").build(),
      val::validateHeaders,
      false);
  }

  private OperationValidator loadOperationValidator(String opId) {
    Path path = api.getPathItemByOperationId(opId);
    Operation op = api.getOperationById(opId);

    return new OperationValidator(api, path, op);
  }

  private void check(Request rq,
                     BiConsumer<Request, ValidationResults> func,
                     boolean shouldBeValid) {

    ValidationResults results = new ValidationResults();
    func.accept(rq, results);

    System.out.println(results);

    if (shouldBeValid) {
      assertTrue(results.toString(), results.isValid());
    } else {
      assertFalse(results.toString(), results.isValid());
    }
  }

  private void check(Response resp,
                     BiConsumer<Response, ValidationResults> func,
                     boolean shouldBeValid) {

    ValidationResults results = new ValidationResults();
    func.accept(resp, results);

    if (shouldBeValid) {
      assertTrue(results.toString(), results.isValid());
    } else {
      assertFalse(results.toString(), results.isValid());
    }
  }
}

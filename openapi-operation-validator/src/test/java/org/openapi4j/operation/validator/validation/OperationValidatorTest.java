package org.openapi4j.operation.validator.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import org.junit.BeforeClass;
import org.junit.Test;
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

    check(
      new DefaultRequest.Builder(GET, "/fixed/1/fixed/2/fixed/").build(),
      val::validatePath,
      true);

    check(
      new DefaultRequest.Builder(GET, "/fixed/string/fixed/2/fixed/").build(),
      val::validatePath,
      false);

    // wrong path, parameters are not bound
    check(
      new DefaultRequest.Builder(GET, "/fixed/fixed/2/fixed/").build(),
      val::validatePath,
      false);

    // Empty string is still valid
    check(
      new DefaultRequest.Builder(GET, "/fixed/1/fixed//fixed/").build(),
      val::validatePath,
      true);
  }

  @Test
  public void queryCheck() {
    OperationValidator val = loadOperationValidator("paramCheck");

    check(
      new DefaultRequest.Builder(GET, "/foo").query("boolQueryParam=true").build(),
      val::validateQuery,
      true);

    check(
      new DefaultRequest.Builder(GET, "/foo").query("boolQueryParam=false&intQueryParam=12").build(),
      val::validateQuery,
      true);

    // nullable
    check(
      new DefaultRequest.Builder(GET, "/foo").query("boolQueryParam=false&intQueryParam=").build(),
      val::validateQuery,
      false);

    check(
      new DefaultRequest.Builder(GET, "/foo").query("boolQueryParam=yes").build(),
      val::validateQuery,
      false);

    // required
    check(
      new DefaultRequest.Builder(GET, "/foo").build(),
      val::validateQuery,
      false);
  }

  @Test
  public void headerCheck() {
    OperationValidator val = loadOperationValidator("paramCheck");

    check(
      new DefaultRequest.Builder(GET, "/foo").header("pathStringHeaderParam", "foo").header("floatHeaderParam", "0.1").build(),
      val::validateHeaders,
      true);

    check(
      new DefaultRequest.Builder(GET, "/foo").header("pathStringHeaderParam", "foo").header("floatHeaderParam", ".1").build(),
      val::validateHeaders,
      true);

    check(
      new DefaultRequest.Builder(GET, "/foo").header("pathStringHeaderParam", "foo").header("floatHeaderParam", "0,1").build(),
      val::validateHeaders,
      false);

    // operation param required
    check(
      new DefaultRequest.Builder(GET, "/foo").header("pathStringHeaderParam", "foo").build(),
      val::validateHeaders,
      false);

    // path param required
    check(
      new DefaultRequest.Builder(GET, "/foo").header("floatHeaderParam", "0.1").build(),
      val::validateHeaders,
      false);
  }

  @Test
  public void cookieCheck() {
    OperationValidator val = loadOperationValidator("paramCheck");

    check(
      new DefaultRequest.Builder(GET, "/foo").cookie("dtCookieParam", "1996-12-19T16:39:57-08:00").build(),
      val::validateCookies,
      true);

    // Not a date-time
    check(
      new DefaultRequest.Builder(GET, "/foo").cookie("dtCookieParam", "1996-12-19").build(),
      val::validateCookies,
      false);

    // required
    check(
      new DefaultRequest.Builder(GET, "/foo").build(),
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
      new DefaultRequest.Builder(GET, "/foo").header("Content-Type", "application/json").body(Body.from(body)).build(),
      val::validateBody,
      true);

    check(
      new DefaultRequest.Builder(GET, "/foo").header("Content-Type", "application/json").build(),
      val::validateBody,
      false);

    check(
      new DefaultRequest.Builder(GET, "/foo").body(Body.from(body)).build(),
      val::validateBody,
      false);

    check(
      new DefaultRequest.Builder(GET, "/foo").header("Content-Type", "text/plain").body(Body.from(body)).build(),
      val::validateBody,
      false);

    check(
      new DefaultRequest.Builder(GET, "/foo").build(),
      val::validateBody,
      false);
  }

  @Test
  public void mergePathToOperationParametersTest() {
    OperationValidator val = loadOperationValidator("merge_parameters");

    check(
      new DefaultRequest.Builder(GET, "/merge_parameters").build(),
      val::validateHeaders,
      false);

    check(
      new DefaultRequest.Builder(GET, "/merge_parameters").header("pathStringHeaderParam", "foo").header("refIntHeaderParameter", "-1").build(),
      val::validateHeaders,
      true);
  }

  @Test
  public void wrongDefinitionForBodyResponseTest() {
    OperationValidator val = loadOperationValidator("wrong_definition_for_body_response");

    check(
      new DefaultRequest.Builder(POST, "/wrong_definition_for_body_response").build(),
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

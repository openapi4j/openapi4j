package org.openapi4j.operation.validator.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import org.junit.Test;
import org.openapi4j.core.exception.EncodeException;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.model.impl.DefaultRequest;
import org.openapi4j.parser.OpenApi3Parser;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Path;

import java.net.URL;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openapi4j.operation.validator.model.Request.Method.GET;

public class OperationValidatorTest {
  @Test
  public void pathCheck() throws ResolutionException, ValidationException, EncodeException {
    OperationValidator val = loadOperationValidator("/fixed/{dataset}/fixed/{version}/fields/", "paramCheck");

    check(
      new DefaultRequest.Builder(GET, "/fixed/1/fixed/2/fields/").build(),
      val::validatePath,
      true);

    check(
      new DefaultRequest.Builder(GET, "/fixed/string/fixed/2/fields/").build(),
      val::validatePath,
      false);
  }

  @Test
  public void queryCheck() throws ResolutionException, ValidationException, EncodeException {
    OperationValidator val = loadOperationValidator("/fixed/{dataset}/fixed/{version}/fields/", "paramCheck");

    check(
      new DefaultRequest.Builder(GET, "/fixed/1/fixed/2/fields/").query("queryParam=true").build(),
      val::validateQuery,
      true);

    check(
      new DefaultRequest.Builder(GET, "/fixed/1/fixed/2/fields/").query("queryParam=yes").build(),
      val::validateQuery,
      false);

    check(
      new DefaultRequest.Builder(GET, "/fixed/1/fixed/2/fields/").build(),
      val::validateQuery,
      false);
  }

  @Test
  public void headerCheck() throws ResolutionException, ValidationException, EncodeException {
    OperationValidator val = loadOperationValidator("/fixed/{dataset}/fixed/{version}/fields/", "paramCheck");

    check(
      new DefaultRequest.Builder(GET, "/fixed/1/fixed/2/fields/").header("headerParam", "0.1").build(),
      val::validateHeaders,
      true);

    check(
      new DefaultRequest.Builder(GET, "/fixed/1/fixed/2/fields/").header("headerParam", ".1").build(),
      val::validateHeaders,
      true);

    check(
      new DefaultRequest.Builder(GET, "/fixed/1/fixed/2/fields/").header("headerParam", "0,1").build(),
      val::validateHeaders,
      false);

    check(
      new DefaultRequest.Builder(GET, "/fixed/1/fixed/2/fields/").build(),
      val::validateQuery,
      false);
  }

  @Test
  public void cookieCheck() throws ResolutionException, ValidationException, EncodeException {
    OperationValidator val = loadOperationValidator("/fixed/{dataset}/fixed/{version}/fields/", "paramCheck");

    check(
      new DefaultRequest.Builder(GET, "/fixed/1/fixed/2/fields/").cookie("cookieParam", "1996-12-19T16:39:57-08:00").build(),
      val::validateCookies,
      true);

    check(
      new DefaultRequest.Builder(GET, "/fixed/1/fixed/2/fields/").cookie("cookieParam", "1996-12-19").build(),
      val::validateCookies,
      false);

    check(
      new DefaultRequest.Builder(GET, "/fixed/1/fixed/2/fields/").build(),
      val::validateQuery,
      false);
  }

  @Test
  public void requestBodyCheck() throws ResolutionException, ValidationException, EncodeException {
    OperationValidator val = loadOperationValidator("/post", "rqBodyCheck");

    JsonNode body = JsonNodeFactory.instance.objectNode().set(
      "param",
      JsonNodeFactory.instance.textNode("foo"));

    check(
      new DefaultRequest.Builder(GET, "/post").header("Content-Type", "application/json").body(Body.from(body)).build(),
      val::validateBody,
      true);

    check(
      new DefaultRequest.Builder(GET, "/post").header("Content-Type", "application/json").build(),
      val::validateBody,
      false);

    check(
      new DefaultRequest.Builder(GET, "/post").body(Body.from(body)).build(),
      val::validateBody,
      false);

    check(
      new DefaultRequest.Builder(GET, "/post").header("Content-Type", "text/plain").body(Body.from(body)).build(),
      val::validateBody,
      false);

    check(
      new DefaultRequest.Builder(GET, "/post").build(),
      val::validateBody,
      false);
  }

  private OpenApi3 loadSpec(String path) throws ResolutionException, ValidationException {
    URL specPath = getClass().getResource(path);
    return new OpenApi3Parser().parse(specPath, true);
  }

  private OperationValidator loadOperationValidator(String oasPath, String opId) throws ResolutionException, ValidationException, EncodeException {
    OpenApi3 api = loadSpec("/operation/operationValidator.yaml");
    Path path = api.getPath(oasPath);
    Operation op = api.getOperationById(opId);
    return new OperationValidator(api, path, op);
  }

  private void check(Request rq,
                     BiConsumer<Request, ValidationResults> func,
                     boolean shouldBeValid) {

    ValidationResults results = new ValidationResults();
    func.accept(rq, results);

    if (shouldBeValid) {
      assertTrue(results.toString(), results.isValid());
    } else {
      assertFalse(results.toString(), results.isValid());
    }
  }
}

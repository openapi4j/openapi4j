package org.openapi4j.operation.validator.validation.operation;

import org.junit.Test;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.operation.validator.model.impl.DefaultRequest;
import org.openapi4j.operation.validator.model.impl.DefaultResponse;
import org.openapi4j.operation.validator.validation.OperationValidator;
import org.openapi4j.parser.OpenApi3Parser;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;
import org.openapi4j.parser.model.v3.Path;

import java.net.URL;

import static org.openapi4j.operation.validator.model.Request.Method.GET;

public class PathTest extends OperationValidatorTestBase {
  @Test
  public void checkReferences() throws Exception {
    URL specPath = OperationValidatorTestBase.class.getResource("/operation/operationValidator.yaml");
    OpenApi3 api = new OpenApi3Parser().parse(specPath, false);

    Path path = api.getPath("/refPath").getReference(api.getContext()).getMappedContent(Path.class);
    Operation op = path.getOperation("post");

    OperationValidator val = new OperationValidator(api, path, op);

    Body body = Body.from("{\"objectType\": \"string\",\"value\": \"foo\"}");

    check(
      new DefaultResponse.Builder(200).header("Content-Type", "application/json").body(body).build(),
      val::validateBody,
      true);
  }

  @Test
  public void pathCheck() throws Exception {
    OperationValidator val = loadOperationValidator("/operation/operationValidator.yaml", "paramCheck");

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

    // Empty string is not valid
    check(
      new DefaultRequest.Builder("https://api.com/fixed/1/fixed//fixed/", GET).build(),
      val::validatePath,
      false);


    // Validation with full fixed path template
    val = loadOperationValidator("/operation/operationValidator.yaml", "merge_parameters");

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
}

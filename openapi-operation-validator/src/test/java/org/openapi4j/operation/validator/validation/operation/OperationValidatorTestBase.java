package org.openapi4j.operation.validator.validation.operation;

import org.openapi4j.operation.validator.model.Request;
import org.openapi4j.operation.validator.model.Response;
import org.openapi4j.operation.validator.validation.OperationValidator;
import org.openapi4j.parser.OpenApi3Parser;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.schema.validator.ValidationData;

import java.net.URL;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OperationValidatorTestBase {
  protected OperationValidator loadOperationValidator(String path, String opId) throws Exception {
    URL specPath = OperationValidatorTestBase.class.getResource(path);
    OpenApi3 api = new OpenApi3Parser().parse(specPath, false);

    return new OperationValidator(
      api,
      api.getPathItemByOperationId(opId),
      api.getOperationById(opId));
  }

  protected void check(Request rq,
                     BiConsumer<Request, ValidationData<Void>> func,
                     boolean shouldBeValid) {

    ValidationData<Void> validation = new ValidationData<>();
    func.accept(rq, validation);

    System.out.println(validation.results());

    if (shouldBeValid) {
      assertTrue(validation.results().toString(), validation.isValid());
    } else {
      assertFalse(validation.results().toString(), validation.isValid());
    }
  }

  protected void check(Response resp,
                     BiConsumer<Response, ValidationData<Void>> func,
                     boolean shouldBeValid) {

    ValidationData<Void> validation = new ValidationData<>();
    func.accept(resp, validation);

    System.out.println(validation.results());

    if (shouldBeValid) {
      assertTrue(validation.results().toString(), validation.isValid());
    } else {
      assertFalse(validation.results().toString(), validation.isValid());
    }
  }
}

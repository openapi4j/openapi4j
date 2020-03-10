package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.exception.DecodeException;
import org.openapi4j.core.model.reference.Reference;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.AbsRefOpenApiSchema;
import org.openapi4j.parser.model.OpenApiSchema;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.ValidationContext;
import org.openapi4j.parser.validation.Validator;
import org.openapi4j.parser.validation.ValidatorBase;

import static org.openapi4j.core.validation.ValidationSeverity.ERROR;

abstract class Validator3Base<O extends OpenApi3, T> extends ValidatorBase<O, T> {
  protected static final ValidationResult REF_MISSING = new ValidationResult(ERROR, 109, "Missing $ref '%s'");
  protected static final ValidationResult REF_CONTENT_UNREADABLE = new ValidationResult(ERROR, 110, "Unable to read $ref content for '%s' pointer.");

  <F extends OpenApiSchema<F>> F getReferenceContent(final O api,
                                                     final AbsRefOpenApiSchema<F> schema,
                                                     final ValidationResults results,
                                                     final String crumb,
                                                     final Class<F> clazz) {

    Reference reference = schema.getReference(api.getContext());

    if (reference == null) {
      results.add(crumb, REF_MISSING, schema.getRef());
    } else {
      try {
        return reference.getMappedContent(clazz);
      } catch (DecodeException e) {
        results.add(crumb, REF_CONTENT_UNREADABLE, schema.getRef());
      }
    }

    return null;
  }

  <F extends OpenApiSchema<F>> void validateReference(final ValidationContext<OpenApi3> context,
                                                      final O api,
                                                      final AbsRefOpenApiSchema<F> schema,
                                                      final ValidationResults results,
                                                      final String crumb,
                                                      final Validator<OpenApi3, F> validator,
                                                      final Class<F> clazz) {

    F content = getReferenceContent(api, schema, results, crumb, clazz);
    if (content != null) {
      context.validate(api, content, validator, results);
    }
  }
}

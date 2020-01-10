package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.exception.DecodeException;
import org.openapi4j.core.model.reference.Reference;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.AbsRefOpenApiSchema;
import org.openapi4j.parser.model.OpenApiSchema;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.Validator;
import org.openapi4j.parser.validation.ValidatorBase;

abstract class Validator3Base<O extends OpenApi3, T> extends ValidatorBase<O, T> {
  protected static final String REF_MISSING = "Missing $ref '%s'";
  protected static final String REF_CONTENT_UNREADABLE = "Unable to read $ref content for '%s' pointer.";

  <F extends OpenApiSchema<F>> F getReferenceContent(final OpenApi3 api,
                                                     final AbsRefOpenApiSchema<F> schema,
                                                     final ValidationResults results,
                                                     final String crumb,
                                                     final Class<F> clazz) {

    Reference reference = schema.getReference(api.getContext());

    if (reference == null) {
      results.addError(String.format(REF_MISSING, schema.getRef()), crumb);
    } else {
      try {
        return reference.getMappedContent(clazz);
      } catch (DecodeException e) {
        results.addError(String.format(REF_CONTENT_UNREADABLE, schema.getRef()), crumb);
      }
    }

    return null;
  }

  <F extends OpenApiSchema<F>> void validateReference(final OpenApi3 api,
                                                      final AbsRefOpenApiSchema<F> schema,
                                                      final ValidationResults results,
                                                      final String crumb,
                                                      final Validator<OpenApi3, F> validator,
                                                      final Class<F> clazz) {

    F content = getReferenceContent(api, schema, results, crumb, clazz);
    if (content != null) {
      validator.validate(api, content, results);
    }
  }
}

package org.openapi4j.parser.model.v3;

import org.openapi4j.core.exception.DecodeException;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.core.model.reference.Reference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class ParameterUtil {
  private ParameterUtil() {
  }

  static List<Parameter> getIn(OAIContext context, Collection<Parameter> parameters, String in) {
    List<Parameter> inParameters = new ArrayList<>();

    if (parameters != null) {
      for (Parameter param : parameters) {
        if (param.isRef()) {
          Reference ref = context.getReferenceRegistry().getRef(param.getCanonicalRef());
          try {
            param = ref.getMappedContent(Parameter.class);
          } catch (DecodeException e) {
            // Will never happen
          }
        }

        if (in.equalsIgnoreCase(param.getIn())) {
          inParameters.add(param);
        }
      }
    }

    return inParameters;
  }
}

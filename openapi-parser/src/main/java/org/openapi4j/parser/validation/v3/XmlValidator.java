package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Xml;
import org.openapi4j.parser.validation.ValidationContext;
import org.openapi4j.parser.validation.Validator;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.CRUMB_EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.CRUMB_NAMESPACE;

public class XmlValidator extends Validator3Base<OpenApi3, Xml> {
  private static final Validator<OpenApi3, Xml> INSTANCE = new XmlValidator();

  private XmlValidator() {
  }

  public static Validator<OpenApi3, Xml> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(ValidationContext<OpenApi3> context, OpenApi3 api, Xml xml, ValidationResults results) {
    // VALIDATION EXCLUSIONS :
    // name, prefix, attribute, wrapped
    validateUri(xml.getNamespace(), results, false, false, CRUMB_NAMESPACE);
    validateMap(context, api, xml.getExtensions(), results, false, CRUMB_EXTENSIONS, Regexes.EXT_REGEX, null);
  }
}

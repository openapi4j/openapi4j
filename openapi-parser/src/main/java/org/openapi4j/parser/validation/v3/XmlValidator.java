package org.openapi4j.parser.validation.v3;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.core.validation.ValidationSeverity;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Xml;
import org.openapi4j.parser.validation.Validator;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.NAMESPACE;

class XmlValidator extends Validator3Base<OpenApi3, Xml> {
  private static final Validator<OpenApi3, Xml> INSTANCE = new XmlValidator();

  private XmlValidator() {
  }

  public static Validator<OpenApi3, Xml> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(OpenApi3 api, Xml xml, ValidationResults results) {
    // VALIDATION EXCLUSIONS :
    // name, prefix, attribute, wrapped
    validateUrl(xml.getNamespace(), results, false, NAMESPACE, ValidationSeverity.WARNING);
    validateMap(api, xml.getExtensions(), results, false, EXTENSIONS, Regexes.EXT_REGEX, null);
  }
}

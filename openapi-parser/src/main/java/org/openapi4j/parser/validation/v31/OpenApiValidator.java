package org.openapi4j.parser.validation.v31;

import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.ValidationContext;
import org.openapi4j.parser.validation.Validator;
import org.openapi4j.parser.validation.v3.ComponentsValidator;
import org.openapi4j.parser.validation.v3.ExternalDocsValidator;
import org.openapi4j.parser.validation.v3.PathValidator;
import org.openapi4j.parser.validation.v3.Regexes;
import org.openapi4j.parser.validation.v3.SecurityRequirementValidator;
import org.openapi4j.parser.validation.v3.ServerValidator;
import org.openapi4j.parser.validation.v3.TagValidator;
import org.openapi4j.parser.validation.v3.Validator3Base;

import java.util.regex.Pattern;

import static org.openapi4j.parser.validation.v3.OAI3Keywords.CRUMB_COMPONENTS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.CRUMB_EXTENSIONS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.CRUMB_EXTERNALDOCS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.CRUMB_INFO;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.CRUMB_OPENAPI;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.CRUMB_PATHS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.CRUMB_SECURITY;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.CRUMB_SERVERS;
import static org.openapi4j.parser.validation.v3.OAI3Keywords.CRUMB_TAGS;

public class OpenApiValidator extends Validator3Base<OpenApi3, OpenApi3> {
  private static final Pattern PATTERN_OAI3 = Pattern.compile("3\\.\\d+(\\.\\d+.*)?");

  private static final Validator<OpenApi3, OpenApi3> INSTANCE = new OpenApiValidator();

  private OpenApiValidator() {
  }

  public static Validator<OpenApi3, OpenApi3> instance() {
    return INSTANCE;
  }

  @Override
  public void validate(ValidationContext<OpenApi3> context, OpenApi3 root, OpenApi3 api, ValidationResults results) {
    validateString(api.getOpenapi(), results, true, PATTERN_OAI3, CRUMB_OPENAPI);
    validateField(context, api, api.getInfo(), results, true, CRUMB_INFO, InfoValidator.instance());
    validateList(context, api, api.getServers(), results, false, 0, CRUMB_SERVERS, ServerValidator.instance());
    validateMap(context, api, api.getPaths(), results, true, CRUMB_PATHS, Regexes.PATH_REGEX, PathValidator.instance());
    validateField(context, api, api.getComponents(), results, false, CRUMB_COMPONENTS, ComponentsValidator.instance());
    validateList(context, api, api.getSecurityRequirements(), results, false, 0, CRUMB_SECURITY, SecurityRequirementValidator.instance());
    validateList(context, api, api.getTags(), results, false, 0, CRUMB_TAGS, TagValidator.instance());
    validateField(context, api, api.getExternalDocs(), results, false, CRUMB_EXTERNALDOCS, ExternalDocsValidator.instance());
    validateMap(context, api, api.getExtensions(), results, false, CRUMB_EXTENSIONS, Regexes.EXT_REGEX, null);
  }
}

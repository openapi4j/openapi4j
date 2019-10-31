package org.openapi4j.parser;

import org.json.JSONException;
import org.openapi4j.core.exception.DecodeException;
import org.openapi4j.core.exception.EncodeException;
import org.openapi4j.core.model.OAI;
import org.openapi4j.core.util.Json;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.v3.OpenApi3Validator;
import org.skyscreamer.jsonassert.JSONAssert;

import java.net.URL;

class ParsingChecker {
  void checkParsing(String path) throws Exception {
    URL specPath = getClass().getResource(path);

    // Check parsing with validation
    OpenApi3 api = new OpenApi3Parser().parse(specPath, false);
    ValidationResults results = OpenApi3Validator.instance().validate(api);
    System.out.print(results.toString());

    // Check output
    checkFromResource(specPath, api);
  }

  <O extends OAI> void checkFromResource(URL resourcePath, OAI<O> api) throws EncodeException, JSONException, DecodeException {
    Object obj = Json.load(resourcePath, Object.class);
    String expected = Json.toJson(obj);

    String actual = Json.toJson(api);

    JSONAssert.assertEquals(expected, actual, true);
  }
}

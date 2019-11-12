package org.openapi4j.parser;

import org.json.JSONException;
import org.openapi4j.core.exception.DecodeException;
import org.openapi4j.core.exception.EncodeException;
import org.openapi4j.core.model.OAI;
import org.openapi4j.core.util.TreeUtil;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.v3.OpenApi3Validator;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.File;
import java.net.URL;

class ParsingChecker {
  void checkParsing(String path) throws Exception {
    URL specPath = getClass().getResource(path);

    // Check parsing with validation
    OpenApi3 api;

    if (specPath != null) {
      api = new OpenApi3Parser().parse(new File(specPath.toURI()), false);
    } else {
      api = new OpenApi3Parser().parse((File) null, false);
    }
    ValidationResults results = OpenApi3Validator.instance().validate(api);
    System.out.print(results.toString());

    // Check output
    checkFromResource(specPath, api);
  }

  void checkFromResource(URL resourcePath, OAI api) throws EncodeException, JSONException, DecodeException {
    Object obj = TreeUtil.load(resourcePath, Object.class);
    String expected = TreeUtil.toJson(obj);

    String actual = TreeUtil.toJson(api);

    JSONAssert.assertEquals(expected, actual, true);
  }
}

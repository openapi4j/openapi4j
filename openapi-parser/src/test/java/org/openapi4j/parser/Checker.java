package org.openapi4j.parser;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONException;
import org.openapi4j.core.exception.DecodeException;
import org.openapi4j.core.exception.EncodeException;
import org.openapi4j.core.util.TreeUtil;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.SerializationFlag;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.v3.OpenApi3Validator;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.File;
import java.net.URL;
import java.util.EnumSet;

public class Checker {
  protected ValidationResults validate(String path) throws Exception {
    URL specPath = getClass().getResource(path);

    // Check parsing with validation
    OpenApi3 api;

    if (specPath != null) {
      api = new OpenApi3Parser().parse(new File(specPath.toURI()), false);
    } else {
      api = new OpenApi3Parser().parse((File) null, false);
    }

    ValidationResults results;
    try {
      results = OpenApi3Validator.instance().validate(api);
    } catch (ValidationException ex) {
      System.out.println(ex.getResults());
      throw ex;
    }

    // Check output
    checkModel(specPath, api);

    return results;
  }

  protected void checkModel(URL resourcePath, OpenApi3 api) throws EncodeException, JSONException, DecodeException, JsonProcessingException {
    String expected = TreeUtil.toJson(TreeUtil.load(resourcePath, OpenApi3.class));
    String actual = TreeUtil.json.writeValueAsString(api.toNode(api.getContext(), false));

    JSONAssert.assertEquals("JSON toNode test failed", expected, actual, true);

    JSONAssert.assertEquals(
      "JSON toString test failed",
      expected,
      api.toString(api.getContext(), EnumSet.of(SerializationFlag.OUT_AS_JSON)),
      true);
  }
}

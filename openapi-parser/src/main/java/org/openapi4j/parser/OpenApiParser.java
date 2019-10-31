package org.openapi4j.parser;

import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.model.OAI;
import org.openapi4j.core.validation.ValidationException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class OpenApiParser<O extends OAI> {
  private static final String INVALID_FILE = "File must be specified";
  private static final String INVALID_URL = "Unable to read from url";

  public O parse(File specFile, boolean validate) throws ResolutionException, ValidationException {
    if (specFile == null) {
      throw new ResolutionException(INVALID_FILE);
    }

    try {
      return parse(specFile.toURI().toURL(), validate);
    } catch (MalformedURLException e) {
      throw new ResolutionException(INVALID_URL, e);
    }
  }

  public abstract O parse(URL url, boolean validate) throws ResolutionException, ValidationException;
}

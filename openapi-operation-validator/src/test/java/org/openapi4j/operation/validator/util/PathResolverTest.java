package org.openapi4j.operation.validator.util;

import org.junit.Test;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.core.model.v3.OAI3Context;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.operation.validator.validation.RequestValidatorTest;
import org.openapi4j.parser.OpenApi3Parser;
import org.openapi4j.parser.model.v3.OpenApi3;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

public class PathResolverTest {
    @Test
    public void getResolvedPath() throws Exception {
      // https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/petstore.yaml
      // anchor first
      OAIContext context = new OAI3Context(new URL("http://bit.ly/348ujup#anchor?query=value"));
      assertEquals("/348ujup", PathResolver.instance().getResolvedPath(context, ""));
      // query first
      context = new OAI3Context(new URL("http://bit.ly/348ujup?query=value#anchor"));
      assertEquals("/348ujup", PathResolver.instance().getResolvedPath(context, ""));
    }

    @Test
    public void findPathPattern() {
      List<Pattern> patternList = new ArrayList<>();
      patternList.add(Pattern.compile("/"));

      assertEquals("/", PathResolver.instance().findPathPattern(patternList, null).pattern());
      assertEquals("/", PathResolver.instance().findPathPattern(patternList, "").pattern());
    }

  @Test
  public void pathParams() throws ResolutionException, ValidationException {
    URL specPath = RequestValidatorTest.class.getResource("/util/path_resolver/path_params.yaml");
    OpenApi3 api = new OpenApi3Parser().parse(specPath, false);

    for (String path : api.getPaths().keySet()) {
      PathResolver.instance().solve(path);
    }
  }
}

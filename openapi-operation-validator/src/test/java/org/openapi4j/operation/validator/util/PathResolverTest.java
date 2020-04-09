package org.openapi4j.operation.validator.util;

import org.junit.Test;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.core.model.v3.OAI3Context;

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
}

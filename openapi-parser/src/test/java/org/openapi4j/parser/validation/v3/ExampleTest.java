package org.openapi4j.parser.validation.v3;

import org.junit.Test;
import org.openapi4j.parser.Checker;

public class ExampleTest extends Checker {
  @Test
  public void example() throws Exception {
    validate("/validation/v3/example/valid/example.yaml");
  }
}

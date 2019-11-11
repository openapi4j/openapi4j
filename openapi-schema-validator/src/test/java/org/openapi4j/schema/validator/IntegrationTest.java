package org.openapi4j.schema.validator;

import org.junit.Test;

public class IntegrationTest {
  @Test
  public void complexValidator() throws Exception {
    ValidationUtil.validate("/schema/integration/complex.json");
  }

  @Test
  public void userDiskValidator() throws Exception {
    ValidationUtil.validate("/schema/integration/userdisk.json");
  }
}

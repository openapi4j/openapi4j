package org.openapi4j.parser.validation.v3;

import org.junit.Test;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.parser.Checker;

public class SchemaTest extends Checker {
  @Test
  public void advancedSchemaWithReference() throws Exception {
    validate("/validation/v3/schema/valid/advancedSchemaWithReference.yaml");
  }

  @Test
  public void basicSchemaWithReference() throws Exception {
    validate("/validation/v3/schema/valid/basicSchemaWithReference.yaml");
  }

  @Test
  public void schemaType() throws Exception {
    validate("/validation/v3/schema/valid/schemaType.yaml");
  }

  //////////////////////////////////////////////////////////////
  // INVALID
  //////////////////////////////////////////////////////////////
  @Test(expected = ValidationException.class)
  public void readWriteSchemaInvalid() throws Exception {
    validate("/validation/v3/schema/invalid/readWriteSchema.yaml");
  }

  @Test(expected = ValidationException.class)
  public void schemaTypeInvalid() throws Exception {
    validate("/validation/v3/schema/invalid/schemaType.yaml");
  }
}

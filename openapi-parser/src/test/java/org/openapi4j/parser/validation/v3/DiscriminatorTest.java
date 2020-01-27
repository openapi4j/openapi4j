package org.openapi4j.parser.validation.v3;

import org.junit.Test;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.parser.Checker;

public class DiscriminatorTest extends Checker {
  @Test
  public void oneOfDiscriminator() throws Exception {
    validate("/validation/v3/discriminator/valid/oneOfDiscriminator.yaml");
  }

  @Test
  public void allOfDiscriminator() throws Exception {
    validate("/validation/v3/discriminator/valid/allOfDiscriminator.yaml");
  }

  @Test
  public void recursiveDiscriminator() throws Exception {
    validate("/validation/v3/discriminator/valid/recursiveDiscriminator.yaml");
  }

  //////////////////////////////////////////////////////////////
  // INVALID
  //////////////////////////////////////////////////////////////
  @Test(expected = ValidationException.class)
  public void allOfDiscriminatorInvalid() throws Exception {
    validate("/validation/v3/discriminator/invalid/allOfDiscriminator.yaml");
  }

  @Test(expected = ResolutionException.class)
  public void oneOfDiscriminatorInvalid() throws Exception {
    validate("/validation/v3/discriminator/invalid/oneOfDiscriminator.yaml");
  }

  @Test(expected = ValidationException.class)
  public void multiDiscriminatorInvalid() throws Exception {
    validate("/validation/v3/discriminator/invalid/multiDiscriminator.yaml");
  }
}

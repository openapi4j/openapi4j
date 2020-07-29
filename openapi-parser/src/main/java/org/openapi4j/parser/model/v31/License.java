package org.openapi4j.parser.model.v31;

public class License extends org.openapi4j.parser.model.v3.License {
  private String identifier;

  public String getIdentifier() {
    return identifier;
  }

  public License setIdentifier(String identifier) {
    this.identifier = identifier;
    return this;
  }

  @Override
  public License copy() {
    License copy = (License) super.copy();

    copy.setIdentifier(getIdentifier());

    return copy;
  }
}

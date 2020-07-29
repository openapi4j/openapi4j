package org.openapi4j.parser.model.v31;

public class Info extends org.openapi4j.parser.model.v3.Info {
  private License license;
  private String summary;

  // License
  public License getLicense() {
    return license;
  }

  public Info setLicense(License license) {
    this.license = license;
    return this;
  }

  public String getSummary() {
    return summary;
  }

  public Info setSummary(String summary) {
    this.summary = summary;
    return this;
  }

  @Override
  public Info copy() {
    Info copy = (Info) super.copy();

    copy.setLicense(getLicense());
    copy.setSummary(getSummary());

    return copy;
  }
}

package org.openapi4j.parser.model.v31;

import org.openapi4j.parser.model.v3.OpenApi3;

public class OpenApi31 extends OpenApi3 {
  private Info info;

  // Info
  public Info getInfo() {
    return info;
  }

  public OpenApi31 setInfo(Info info) {
    this.info = info;
    return this;
  }

  @Override
  public OpenApi3 copy() {
    OpenApi31 copy = (OpenApi31) super.copy();

    copy.setInfo(copyField(getInfo()));

    return copy;
  }
}

package org.openapi4j.parser.model.v3;

public class Header extends AbsParameter<Header> {

  @Override
  public Header copy() {
    Header copy = new Header();

    if (isRef()) {
      copy.setRef(getRef());
      copy.setCanonicalRef(getCanonicalRef());
    } else {
      super.copy(copy);
    }

    return copy;
  }
}

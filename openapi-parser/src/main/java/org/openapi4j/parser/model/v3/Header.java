package org.openapi4j.parser.model.v3;

import org.openapi4j.core.model.OAIContext;

public class Header extends AbsParameter<Header> {
  @Override
  protected Header copyReference() {
    Header copy = new Header();

    super.copyReference(copy);

    return copy;
  }

  @Override
  protected Header copyContent(OAIContext context, boolean followRefs) {
    Header copy = new Header();

    super.copyContent(context, copy, followRefs);

    return copy;
  }
}

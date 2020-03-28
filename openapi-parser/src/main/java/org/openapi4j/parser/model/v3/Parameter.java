package org.openapi4j.parser.model.v3;

import org.openapi4j.core.model.OAIContext;

import java.util.Objects;

@SuppressWarnings("unused")
public class Parameter extends AbsParameter<Parameter> {
  private String in;
  private String name;

  // Name
  public String getName() {
    return name;
  }

  public Parameter setName(String name) {
    this.name = name;
    return this;
  }

  // In
  public String getIn() {
    return in;
  }

  public Parameter setIn(String in) {
    this.in = in;
    return this;
  }

  @Override
  protected Parameter copyReference() {
    Parameter copy = new Parameter();
    super.copyReference(copy);
    return copy;
  }

  @Override
  protected Parameter copyContent(OAIContext context, boolean followRefs) {
    Parameter copy = new Parameter();

    super.copyContent(context, copy, followRefs);
    copy.setName(getName());
    copy.setIn(getIn());

    return copy;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Parameter parameter = (Parameter) o;

    if (isRef()) {
      return Objects.equals(getRef(), parameter.getRef());
    } else {
      if (!Objects.equals(name, parameter.name)) return false;
      return Objects.equals(in, parameter.in);
    }
  }

  @Override
  public int hashCode() {
    if (isRef()) {
      return getRef().hashCode();
    } else {
      int result = name != null ? name.hashCode() : 0;
      result = 31 * result + (in != null ? in.hashCode() : 0);
      return result;
    }
  }
}

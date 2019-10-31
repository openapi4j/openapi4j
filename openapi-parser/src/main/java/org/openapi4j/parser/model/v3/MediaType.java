package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MediaType extends AbsOpenApiSchema<OAI3, MediaType> {
  private Map<String, EncodingProperty> encoding;
  private Object example;
  private Map<String, Example> examples;
  @JsonUnwrapped
  private Extensions extensions;
  private Schema schema;

  // Schema
  public Schema getSchema() {
    return schema;
  }

  public MediaType setSchema(Schema schema) {
    this.schema = schema;
    return this;
  }

  // Example
  public Map<String, Example> getExamples() {
    return examples;
  }

  public MediaType setExamples(Map<String, Example> examples) {
    this.examples = examples;
    return this;
  }

  public boolean hasExample(String name) {
    return has(examples, name);
  }

  public Example getExample(String name) {
    return get(examples, name);
  }

  public MediaType setExample(String name, Example example) {
    if (examples == null) {
      examples = new HashMap<>();
    }
    examples.put(name, example);
    return this;
  }

  public MediaType removeExample(String name) {
    remove(examples, name);
    return this;
  }

  // Example
  public Object getExample() {
    return example;
  }

  public MediaType setExample(Object example) {
    this.example = example;
    return this;
  }

  // EncodingProperty
  public Map<String, EncodingProperty> getEncoding() {
    return encoding;
  }

  public MediaType setEncoding(Map<String, EncodingProperty> encoding) {
    this.encoding = encoding;
    return this;
  }

  public boolean hasEncodingProperty(String name) {
    return has(encoding, name);
  }

  public EncodingProperty getEncodingProperty(String name) {
    return get(encoding, name);
  }

  public MediaType setEncodingProperty(String name, EncodingProperty encodingProperty) {
    if (encoding == null) {
      encoding = new HashMap<>();
    }
    encoding.put(name, encodingProperty);
    return this;
  }

  public MediaType removeEncodingProperty(String name) {
    remove(encoding, name);
    return this;
  }

  // Extensions
  public Extensions getExtensions() {
    return extensions;
  }

  public MediaType setExtensions(Extensions extensions) {
    this.extensions = extensions;
    return this;
  }

  @Override
  public MediaType copy(OAIContext<OAI3> context, boolean followRefs) {
    MediaType copy = new MediaType();

    copy.setSchema(copyField(schema, context, followRefs));
    copy.setExample(example);
    copy.setExamples(copyMap(examples, context, followRefs));
    copy.setEncoding(copyMap(encoding, context, followRefs));
    copy.setExtensions(copyField(extensions, context, followRefs));

    return copy;
  }
}

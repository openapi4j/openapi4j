package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.openapi4j.core.model.OAIContext;

import java.util.HashMap;
import java.util.Map;

public class MediaType extends AbsExtendedOpenApiSchema<MediaType> {
  @JsonProperty("encoding")
  private Map<String, EncodingProperty> encodings;
  private Object example;
  private Map<String, Example> examples;
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
    return mapHas(examples, name);
  }

  public Example getExample(String name) {
    return mapGet(examples, name);
  }

  public MediaType setExample(String name, Example example) {
    if (examples == null) {
      examples = new HashMap<>();
    }
    examples.put(name, example);
    return this;
  }

  public MediaType removeExample(String name) {
    mapRemove(examples, name);
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
  public Map<String, EncodingProperty> getEncodings() {
    return encodings;
  }

  public MediaType setEncodings(Map<String, EncodingProperty> encodings) {
    this.encodings = encodings;
    return this;
  }

  public boolean hasEncoding(String name) {
    return mapHas(encodings, name);
  }

  public EncodingProperty getEncoding(String name) {
    return mapGet(encodings, name);
  }

  public MediaType setEncoding(String name, EncodingProperty encodingProperty) {
    if (encodings == null) {
      encodings = new HashMap<>();
    }
    encodings.put(name, encodingProperty);
    return this;
  }

  public MediaType removeEncoding(String name) {
    mapRemove(encodings, name);
    return this;
  }

  @Override
  public MediaType copy(OAIContext context, boolean followRefs) {
    MediaType copy = new MediaType();

    copy.setSchema(copyField(getSchema(), context, followRefs));
    copy.setExample(getExample());
    copy.setExamples(copyMap(getExamples(), context, followRefs));
    copy.setEncodings(copyMap(getEncodings(), context, followRefs));
    copy.setExtensions(copyMap(getExtensions()));

    return copy;
  }
}

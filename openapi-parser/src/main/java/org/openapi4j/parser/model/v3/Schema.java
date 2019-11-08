package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.v3.bind.SchemaDeserializer;
import org.openapi4j.parser.model.v3.bind.SchemaSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "UnusedReturnValue"})
@JsonDeserialize(using = SchemaDeserializer.class)
@JsonSerialize(using = SchemaSerializer.class)
public class Schema extends AbsExtendedRefOpenApiSchema<Schema> {
  private Schema additionalProperties;
  private Boolean additionalPropertiesAllowed;
  private Object defaultValue;
  private String description;
  private Boolean deprecated;
  private Discriminator discriminator;
  private List<String> enums;
  private Object example;
  private Boolean exclusiveMaximum;
  private Boolean exclusiveMinimum;
  private ExternalDocs externalDocs;
  private String format;
  private Schema itemsSchema;
  private Number maximum;
  private Number minimum;
  private Integer maxItems;
  private Integer minItems;
  private Integer maxLength;
  private Integer minLength;
  private Integer maxProperties;
  private Integer minProperties;
  private Number multipleOf;
  private Schema notSchema;
  private Boolean nullable;
  private String pattern;
  private Map<String, Schema> properties;
  private List<String> requiredFields;
  private List<Schema> allOfSchemas;
  private List<Schema> anyOfSchemas;
  private List<Schema> oneOfSchemas;
  private Boolean readOnly;
  private Boolean writeOnly;
  private String type;
  private String title;
  private Boolean uniqueItems;
  private Xml xml;

  // Title
  public String getTitle() {
    return title;
  }

  public Schema setTitle(String title) {
    this.title = title;
    return this;
  }

  // MultipleOf
  public Number getMultipleOf() {
    return multipleOf;
  }

  public Schema setMultipleOf(Number multipleOf) {
    this.multipleOf = multipleOf;
    return this;
  }

  // Maximum
  public Number getMaximum() {
    return maximum;
  }

  public Schema setMaximum(Number maximum) {
    this.maximum = maximum;
    return this;
  }

  // ExclusiveMaximum
  public Boolean getExclusiveMaximum() {
    return exclusiveMaximum;
  }

  public boolean isExclusiveMaximum() {
    return exclusiveMaximum != null ? exclusiveMaximum : false;
  }

  public Schema setExclusiveMaximum(Boolean exclusiveMaximum) {
    this.exclusiveMaximum = exclusiveMaximum;
    return this;
  }

  // Minimum
  public Number getMinimum() {
    return minimum;
  }

  public Schema setMinimum(Number minimum) {
    this.minimum = minimum;
    return this;
  }

  // ExclusiveMinimum
  public Boolean getExclusiveMinimum() {
    return exclusiveMinimum;
  }

  public boolean isExclusiveMinimum() {
    return exclusiveMinimum != null ? exclusiveMinimum : false;
  }

  public Schema setExclusiveMinimum(Boolean exclusiveMinimum) {
    this.exclusiveMinimum = exclusiveMinimum;
    return this;
  }

  // MaxLength
  public Integer getMaxLength() {
    return maxLength;
  }

  public Schema setMaxLength(Integer maxLength) {
    this.maxLength = maxLength;
    return this;
  }

  // MinLength
  public Integer getMinLength() {
    return minLength;
  }

  public Schema setMinLength(Integer minLength) {
    this.minLength = minLength;
    return this;
  }

  // Pattern
  public String getPattern() {
    return pattern;
  }

  public Schema setPattern(String pattern) {
    this.pattern = pattern;
    return this;
  }

  // MaxItems
  public Integer getMaxItems() {
    return maxItems;
  }

  public Schema setMaxItems(Integer maxItems) {
    this.maxItems = maxItems;
    return this;
  }

  // MinItems
  public Integer getMinItems() {
    return minItems;
  }

  public Schema setMinItems(Integer minItems) {
    this.minItems = minItems;
    return this;
  }

  // UniqueItems
  public Boolean getUniqueItems() {
    return uniqueItems;
  }

  public boolean isUniqueItems() {
    return uniqueItems != null ? uniqueItems : false;
  }

  public Schema setUniqueItems(Boolean uniqueItems) {
    this.uniqueItems = uniqueItems;
    return this;
  }

  // MaxProperties
  public Integer getMaxProperties() {
    return maxProperties;
  }

  public Schema setMaxProperties(Integer maxProperties) {
    this.maxProperties = maxProperties;
    return this;
  }

  // MinProperties
  public Integer getMinProperties() {
    return minProperties;
  }

  public Schema setMinProperties(Integer minProperties) {
    this.minProperties = minProperties;
    return this;
  }

  // RequiredField
  public List<String> getRequiredFields() {
    return requiredFields;
  }

  public Schema setRequiredFields(List<String> requiredFields) {
    this.requiredFields = requiredFields;
    return this;
  }

  public boolean hasRequiredFields() {
    return requiredFields != null;
  }

  public Schema addRequiredField(String requiredField) {
    requiredFields = listAdd(requiredFields, requiredField);
    return this;
  }

  public Schema insertRequiredField(int index, String value) {
    requiredFields = listAdd(requiredFields, index, value);
    return this;
  }

  public Schema removeRequiredField(String value) {
    listRemove(requiredFields, value);
    return this;
  }

  // Enum
  public List<String> getEnums() {
    return enums;
  }

  public Schema setEnums(List<String> enums) {
    this.enums = enums;
    return this;
  }

  public boolean hasEnums() {
    return enums != null;
  }

  public Schema addEnum(String value) {
    enums = listAdd(enums, value);
    return this;
  }

  public Schema insertEnum(int index, String value) {
    enums = listAdd(enums, index, value);
    return this;
  }

  public Schema removeEnum(String value) {
    listRemove(enums, value);
    return this;
  }

  // Type
  public String getType() {
    return type;
  }

  public Schema setType(String type) {
    this.type = type;
    return this;
  }

  // AllOfSchema
  public List<Schema> getAllOfSchemas() {
    return allOfSchemas;
  }

  public Schema setAllOfSchemas(List<Schema> value) {
    this.allOfSchemas = value;
    return this;
  }

  public boolean hasAllOfSchemas() {
    return allOfSchemas != null;
  }

  public Schema addAllOfSchema(Schema value) {
    allOfSchemas = listAdd(allOfSchemas, value);
    return this;
  }

  public Schema insertAllOfSchema(int index, Schema value) {
    allOfSchemas = listAdd(allOfSchemas, index, value);
    return this;
  }

  public Schema removeAllOfSchema(Schema value) {
    listRemove(allOfSchemas, value);
    return this;
  }

  // OneOfSchema
  public List<Schema> getOneOfSchemas() {
    return oneOfSchemas;
  }

  public Schema setOneOfSchemas(List<Schema> oneOfSchemas) {
    this.oneOfSchemas = oneOfSchemas;
    return this;
  }

  public boolean hasOneOfSchemas() {
    return oneOfSchemas != null;
  }

  public Schema addOneOfSchema(Schema value) {
    oneOfSchemas = listAdd(oneOfSchemas, value);
    return this;
  }

  public Schema insertOneOfSchema(int index, Schema value) {
    oneOfSchemas = listAdd(oneOfSchemas, index, value);
    return this;
  }

  public Schema removeOneOfSchema(Schema value) {
    listRemove(oneOfSchemas, value);
    return this;
  }

  // AnyOfSchema
  public List<Schema> getAnyOfSchemas() {
    return anyOfSchemas;
  }

  public Schema setAnyOfSchemas(List<Schema> anyOfSchemas) {
    this.anyOfSchemas = anyOfSchemas;
    return this;
  }

  public boolean hasAnyOfSchemas() {
    return anyOfSchemas != null;
  }

  public Schema addAnyOfSchema(Schema value) {
    anyOfSchemas = listAdd(anyOfSchemas, value);
    return this;
  }

  public Schema insertAnyOfSchema(int index, Schema anyOfSchema) {
    anyOfSchemas = listAdd(anyOfSchemas, index, anyOfSchema);
    return this;
  }

  public Schema removeAnyOfSchema(Schema value) {
    listRemove(anyOfSchemas, value);
    return this;
  }

  // NotSchema
  public Schema getNotSchema() {
    return notSchema;
  }

  public Schema setNotSchema(Schema notSchema) {
    this.notSchema = notSchema;
    return this;
  }

  // ItemsSchema
  public Schema getItemsSchema() {
    return itemsSchema;
  }

  public Schema setItemsSchema(Schema itemsSchema) {
    this.itemsSchema = itemsSchema;
    return this;
  }

  // Property
  public Map<String, Schema> getProperties() {
    return properties;
  }

  public Schema setProperties(Map<String, Schema> properties) {
    this.properties = properties;
    return this;
  }

  public boolean hasProperty(String name) {
    return mapHas(properties, name);
  }

  public Schema getProperty(String name) {
    return mapGet(properties, name);
  }

  public Schema setProperty(String name, Schema property) {
    if (properties == null) {
      properties = new HashMap<>();
    }
    properties.put(name, property);
    return this;
  }

  public Schema removeProperty(String name) {
    mapRemove(properties, name);
    return this;
  }

  // AdditionalProperties
  public Schema getAdditionalProperties() {
    return additionalProperties;
  }

  public Schema setAdditionalProperties(Schema additionalProperties) {
    this.additionalProperties = additionalProperties;

    additionalPropertiesAllowed = (additionalProperties != null) ? false : null;
    return this;
  }

  public boolean hasAdditionalProperties() {
    return additionalProperties != null;
  }

  public Boolean getAdditionalPropertiesAllowed() {
    return additionalPropertiesAllowed;
  }

  public boolean isAdditionalPropertiesAllowed() {
    return additionalPropertiesAllowed != null ? additionalPropertiesAllowed : true;
  }

  public Schema setAdditionalPropertiesAllowed(Boolean additionalPropertiesAllowed) {
    this.additionalPropertiesAllowed = additionalPropertiesAllowed;
    additionalProperties = null;

    return this;
  }

  // Description
  public String getDescription() {
    return description;
  }

  public Schema setDescription(String description) {
    this.description = description;
    return this;
  }

  // Format
  public String getFormat() {
    return format;
  }

  public Schema setFormat(String format) {
    this.format = format;
    return this;
  }

  // Default
  public Object getDefault() {
    return defaultValue;
  }

  public Schema setDefault(Object defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  // Nullable
  public Boolean getNullable() {
    return nullable;
  }

  public boolean isNullable() {
    return nullable != null ? nullable : false;
  }

  public Schema setNullable(Boolean nullable) {
    this.nullable = nullable;
    return this;
  }

  // Discriminator
  public Discriminator getDiscriminator() {
    return discriminator;
  }

  public Schema setDiscriminator(Discriminator discriminator) {
    this.discriminator = discriminator;
    return this;
  }

  // ReadOnly
  public Boolean getReadOnly() {
    return readOnly;
  }

  public boolean isReadOnly() {
    return readOnly != null ? readOnly : false;
  }

  public Schema setReadOnly(Boolean readOnly) {
    this.readOnly = readOnly;
    return this;
  }

  // WriteOnly
  public Boolean getWriteOnly() {
    return writeOnly;
  }

  public boolean isWriteOnly() {
    return writeOnly != null ? writeOnly : false;
  }

  public Schema setWriteOnly(Boolean writeOnly) {
    this.writeOnly = writeOnly;
    return this;
  }

  // Xml
  public Xml getXml() {
    return xml;
  }

  public Schema setXml(Xml xml) {
    this.xml = xml;
    return this;
  }

  // ExternalDocs
  public ExternalDocs getExternalDocs() {
    return externalDocs;
  }

  public Schema setExternalDocs(ExternalDocs externalDocs) {
    this.externalDocs = externalDocs;
    return this;
  }

  // Example
  public Object getExample() {
    return example;
  }

  public Schema setExample(Object example) {
    this.example = example;
    return this;
  }

  // Deprecated
  public Boolean getDeprecated() {
    return deprecated;
  }

  public boolean isDeprecated() {
    return deprecated != null ? deprecated : false;
  }

  public Schema setDeprecated(Boolean deprecated) {
    this.deprecated = deprecated;
    return this;
  }

  @Override
  protected Schema copyReference(OAIContext context) {
    Schema copy = new Schema();
    copy.setRef(getRef());
    return copy;
  }

  @Override
  protected Schema copyContent(OAIContext context, boolean followRefs) {
    Schema copy = new Schema();

    copy.setTitle(getTitle());
    copy.setMultipleOf(getMultipleOf());
    copy.setMaximum(getMaximum());
    copy.setExclusiveMaximum(getExclusiveMaximum());
    copy.setMinimum(getMinimum());
    copy.setExclusiveMinimum(getExclusiveMinimum());
    copy.setMaxLength(getMaxLength());
    copy.setMinLength(getMinLength());
    copy.setPattern(getPattern());
    copy.setMaxItems(getMaxItems());
    copy.setMinItems(getMinItems());
    copy.setUniqueItems(getUniqueItems());
    copy.setMaxProperties(getMaxProperties());
    copy.setMinProperties(getMinProperties());
    copy.setRequiredFields(copyList(getRequiredFields()));
    copy.setEnums(copyList(getEnums()));
    copy.setType(getType());
    copy.setAllOfSchemas(copyList(getAllOfSchemas(), context, followRefs));
    copy.setOneOfSchemas(copyList(getOneOfSchemas(), context, followRefs));
    copy.setAnyOfSchemas(copyList(getAnyOfSchemas(), context, followRefs));
    copy.setNotSchema(copyField(getNotSchema(), context, followRefs));
    copy.setItemsSchema(copyField(getItemsSchema(), context, followRefs));
    copy.setProperties(copyMap(getProperties(), context, followRefs));
    copy.setAdditionalProperties(copyField(getAdditionalProperties(), context, followRefs));
    copy.setAdditionalPropertiesAllowed(getAdditionalPropertiesAllowed());
    copy.setDescription(getDescription());
    copy.setFormat(getFormat());
    copy.setDefault(getDefault());
    copy.setNullable(getNullable());
    copy.setDiscriminator(copyField(getDiscriminator(), context, followRefs));
    copy.setReadOnly(getReadOnly());
    copy.setWriteOnly(getWriteOnly());
    copy.setXml(copyField(getXml(), context, followRefs));
    copy.setExternalDocs(copyField(getExternalDocs(), context, followRefs));
    copy.setExample(getExample());
    copy.setDeprecated(getDeprecated());
    copy.setExtensions(copyMap(getExtensions()));

    return copy;
  }
}

package org.openapi4j.operation.validator.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import org.junit.Test;
import org.openapi4j.operation.validator.util.TypeConverter;
import org.openapi4j.parser.model.v3.Schema;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TypeConverterTest {
  @Test
  public void convertObjectNullNode() {
    // no schema
    assertEquals(
      JsonNodeFactory.instance.nullNode(),
      TypeConverter.instance().convertObject(null, new HashMap<>()));

    Schema schema = new Schema();
    schema.setType("object");

    // no properties
    assertEquals(
      JsonNodeFactory.instance.nullNode(),
      TypeConverter.instance().convertObject(schema, new HashMap<>()));
    // empty properties
    schema.setProperties(new HashMap<>());
    assertEquals(
      JsonNodeFactory.instance.nullNode(),
      TypeConverter.instance().convertObject(schema, new HashMap<>()));
    // no content
    schema.setProperties(new HashMap<>());
    assertEquals(
      JsonNodeFactory.instance.nullNode(),
      TypeConverter.instance().convertObject(schema, null));

    // object of object
    schema
      .setProperty("foo", new Schema().setType("object")
        .setProperty("bar", new Schema().setType("integer")));

    Map<String, Object> value = new HashMap<>();
    value.put("bar", 1);
    Map<String, Object> rootValue = new HashMap<>();
    rootValue.put("foo", value);

    assertEquals(
      "{\"foo\":{\"bar\":1}}",
      TypeConverter.instance().convertObject(schema, rootValue).toString());
  }

  @Test
  public void convertObjectOfObject() {
    Schema schema = new Schema();
    schema
      .setType("object")
      .setProperty("foo", new Schema().setType("object")
        .setProperty("bar", new Schema().setType("integer")));

    Map<String, Object> bar = new HashMap<>();
    bar.put("bar", 1);
    Map<String, Object> foo = new HashMap<>();
    foo.put("foo", bar);

    assertEquals(
      "{\"foo\":{\"bar\":1}}",
      TypeConverter.instance().convertObject(schema, foo).toString());

    // wrong value
    foo.put("foo", "bar");
    assertEquals(
      "{\"foo\":null}",
      TypeConverter.instance().convertObject(schema, foo).toString());
  }

  @Test
  public void convertArrayNullNode() {
    // no schema
    assertEquals(
      JsonNodeFactory.instance.nullNode(),
      TypeConverter.instance().convertArray(null, new ArrayList<>()));

    Schema schema = new Schema();

    // no content
    assertEquals(
      JsonNodeFactory.instance.nullNode(),
      TypeConverter.instance().convertArray(schema, null));

    // empty content
    schema.setType("integer");
    assertEquals(
      JsonNodeFactory.instance.arrayNode(),
      TypeConverter.instance().convertArray(schema, new ArrayList<>()));

    // with values
    List<Object> values = new ArrayList<>();
    values.add(1);
    values.add(10);
    JsonNode convertedNode = TypeConverter.instance().convertArray(schema, values);
    assertEquals(1, convertedNode.get(0).intValue());
    assertEquals(10, convertedNode.get(1).intValue());
  }

  @Test
  public void convertArrayPrimitive() {
    Schema schema = new Schema();
    schema.setType("integer");

    List<Object> values = new ArrayList<>();
    values.add(1);
    values.add(10);

    JsonNode convertedNode = TypeConverter.instance().convertArray(schema, values);

    assertEquals(1, convertedNode.get(0).intValue());
    assertEquals(10, convertedNode.get(1).intValue());
  }

  @Test
  public void convertArrayObject() {
    Schema schema = new Schema();
    schema
      .setType("object")
      .setProperty("foo", new Schema().setType("object")
        .setProperty("bar", new Schema().setType("integer")));

    Map<String, Object> bar = new HashMap<>();
    bar.put("bar", 1);
    Map<String, Object> foo = new HashMap<>();
    foo.put("foo", bar);

    List<Object> value = new ArrayList<>();
    value.add(foo);

    assertEquals(
      "[{\"foo\":{\"bar\":1}}]",
      TypeConverter.instance().convertArray(schema, value).toString());

    // wrong value
    foo.put("foo", "bar");
    assertEquals(
      "[{\"foo\":null}]",
      TypeConverter.instance().convertArray(schema, value).toString());
  }

  @Test
  public void convertArrayArray() {
    Schema schema = new Schema();
    schema
      .setType("array")
      .setItemsSchema(new Schema().setType("integer"));

    List<Object> valueList = new ArrayList<>();
    valueList.add(1);
    valueList.add(2);
    List<Object> rootList = new ArrayList<>();
    rootList.add(valueList);


    assertEquals(
      "[[1,2]]",
      TypeConverter.instance().convertArray(schema, rootList).toString());

    // wrong value
    valueList.add("foo");
    assertEquals(
      "[[1,2,\"foo\"]]",
      TypeConverter.instance().convertArray(schema, rootList).toString());

    // wrong sub list
    rootList.clear();
    rootList.add("foo");
    assertEquals(
      "[null]",
      TypeConverter.instance().convertArray(schema, rootList).toString());
  }

  @Test
  public void convertPrimitiveNullNode() {
    // no schema
    assertEquals(
      JsonNodeFactory.instance.nullNode(),
      TypeConverter.instance().convertPrimitive(null, new ArrayList<>()));

    Schema schema = new Schema();

    // no content
    assertEquals(
      JsonNodeFactory.instance.nullNode(),
      TypeConverter.instance().convertPrimitive(schema, null));

    // wrong content
    schema.setType("integer");
    assertEquals(
      JsonNodeFactory.instance.textNode("wrong"),
      TypeConverter.instance().convertPrimitive(schema, "wrong"));
  }

  @Test
  public void convertPrimitiveValues() {
    Schema schema = new Schema();
    // INTEGER
    schema.setType("integer");
    // no format
    assertEquals(
      JsonNodeFactory.instance.numberNode(BigInteger.valueOf(1)),
      TypeConverter.instance().convertPrimitive(schema, 1));
    schema.setFormat("int32");
    assertEquals(
      JsonNodeFactory.instance.numberNode(Integer.valueOf(1)),
      TypeConverter.instance().convertPrimitive(schema, 1));
    schema.setFormat("int64");
    assertEquals(
      JsonNodeFactory.instance.numberNode(Long.valueOf(1)),
      TypeConverter.instance().convertPrimitive(schema, 1));

    // DECIMAL
    // no format
    schema.setType("number");
    schema.setFormat(null);
    assertEquals(
      JsonNodeFactory.instance.numberNode(BigDecimal.valueOf(1)),
      TypeConverter.instance().convertPrimitive(schema, 1));
    schema.setFormat("float");
    assertEquals(
      JsonNodeFactory.instance.numberNode(Float.valueOf(1)),
      TypeConverter.instance().convertPrimitive(schema, 1));
    schema.setFormat("double");
    assertEquals(
      JsonNodeFactory.instance.numberNode(Double.valueOf(1)),
      TypeConverter.instance().convertPrimitive(schema, 1));

    // BOOLEAN
    schema.setType("boolean");
    schema.setFormat(null);
    assertEquals(
      JsonNodeFactory.instance.booleanNode(true),
      TypeConverter.instance().convertPrimitive(schema, "TrUe"));
    assertEquals(
      JsonNodeFactory.instance.booleanNode(false),
      TypeConverter.instance().convertPrimitive(schema, "fAlSe"));
    assertEquals(
      JsonNodeFactory.instance.textNode("pofkpfosdkfsd"),
      TypeConverter.instance().convertPrimitive(schema, "pofkpfosdkfsd"));
  }
}

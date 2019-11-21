# OpenAPI Schema Object validator

Implementation of the [Schema Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject) specification for Open API 3.  
As a reminder, Schema Object is a subset of [JSON schema draft #00](https://tools.ietf.org/html/draft-wright-json-schema-validation-00) with additions. 

## Features

This module allows the validation of your data against a given schema.
The aim of this module is accuracy and compliancy with the specifications.

That said, there's options to enrich to current definitions with the `ValidationContext` object :
* ADDITIONAL_PROPS_RESTRICT : By default, Schema Object can have additional properties. This option let's you invert the behaviour.
* You can override keywords' and add your own validators! More on this with [extensions](#extensions).

Credits to [JSON-Schema-Test-Suite](https://github.com/json-schema-org/JSON-Schema-Test-Suite) where the majority of tests are coming from. 

## Installation

Add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>org.openapi4j</groupId>
    <artifactId>openapi-schema-validator</artifactId>
</dependency>
```
[![Release version](https://img.shields.io/nexus/r/org.openapi4j/openapi-schema-validator?style=for-the-badge&color=brightgreen&label=Release&server=https%3A%2F%2Foss.sonatype.org)](https://search.maven.org/search?q=g:org.openapi4j%20a:openapi-schema-validator)
[![Snapshot version](https://img.shields.io/nexus/s/org.openapi4j/openapi-schema-validator?style=for-the-badge&color=brightgreen&label=Snapshot&server=https%3A%2F%2Foss.sonatype.org)](https://oss.sonatype.org/content/repositories/snapshots/org/openapi4j/openapi-schema-validator/)

## Usage

```java
JsonNode schemaNode = // your schema tree node.
JsonNode contentNode = // your data.
SchemaValidator schemaValidator = new SchemaValidator("my_schema", schemaNode);

// Validation with exception
try {
    schemaValidator.validate(contentNode);
} catch(ValidationException ex) {
    System.out.println(ex.results());
}

// or validation without exception
ValidationResults results = new ValidationResults(); 
schemaValidator.validate(contentNode, results);
if (!results.isValid()) {
    System.out.println(results);
}
```

Activate fast failing behaviour :
```java
validationContext = new ValidationContext<>(apiContext);
validationContext.setFastFail(true);
```

You can easily locate the error(s) with the results as we keep the path of the validation.  
Here's few outputs of `ValidationResults` object :
```
my_schema.#/components/schemas/Lizard.discriminator : Property name in content 'petType' is not set.
my_schema.foo.schema.additionalProperties : Additional property 'bar' is not allowed.
my_schema.bar.#/definitions/c.#/definitions/b.#/definitions/a.type : Type expected 'integer', found 'string'.
my_schema.$ref.type : Type expected 'string', found 'integer'.
my_schema.maximum : '3.0' cannot be greater than '3.0' excluded.
my_schema.uniqueItems : Uniqueness is not respected 'bar'.
```

## Extensions

Without doing any anything with the options, the Schema validator is compliant with the specification.

So why ?

Extensions have two distinct goals :
- Overriding a known keyword.
- Adding your business validator.

The validation integrated in OpenAPI Specification with the Schema Object part covers only basic/medium scenarios but we all know that more complex validation rules are always needed in projects.
JSON Schema further drafts try to cover more and more scenarios, but we think it's a waste since it will never fulfill the needs.  
Schema Object is not perfect but has the advantage to not overly complexify the specification and its location is near the right spot.  
To illustrate the purpose, think about XML Schema Definition, Protocol Buffers, simpleSchema and other alternatives old or new, none of those go the JSON Schema way because nobody can maintain this for APIs.

This is where `extensions` will let you fill the gap !  
By implementing your own validation, you don't create you subset of the offical OpenAPI Specification, you use it at its maximum !

There's no overhead to use extensions, default validators are built directly with this process.

### Overriding a known keyword

You can override a known keyword with your own validator.  
Look at the example to start linking a known keyword with your implementation.

Feel free to contribute to those extensions if you think your implementation could help the community !
There's no plan yet for releasing contributions on validation extensions, for now it's "only" for sharing.  

### Business validator

Whatever you need, you has now the possibility to (re-)locate almost all the validation at this SINGLE front level and not after traversing all layers of the architecture before checking business values.  

Your project should be more readable, maintainable and performance increased without efforts.

Using specification extensions for new keywords is strongly recommended in this case.

Some ideas : checking full entity, dateStart > now() && dateStart < dateEnd, ...

### Examples :

Declaration examples :  
All JSON types are accepted, it's up to you to describe it and setup the validation in the corresponding validator.

```yaml
type: object
properties:
  sub-object:
    type: object
    properties:
      foo:
        type: string
    x-sub-object-val:  # it's up to you to place the trigger where your validation should occur.
      fooParam: 0.2 # You need parameters ?
x-myentity-val: null # Just trigger on all entity !
```
or  
```json
{
  "type": "string",
  "x-format-business": "my_business_format"
}
```

Instantiation :
```java
// Declare your instantiation function, this will be called as much as needed.
// This avoids reflection and lets you define any additional variables.
evi = new ExtValidatorInstance() {
    @Override
    public JsonValidator<OAI3> apply(ValidationContext context,
                                     JsonNode schemaNode,
                                     JsonNode schemaParentNode,
                                     SchemaValidator parentSchema) {
        // Call your specific constructor with additionnal argument if needed (i.e. "foo" in this example).
        // MyValidator extends BaseJsonValidator<OAI3>
        return new MyValidator(context, schemaNode, schemaParentNode, parentSchema, "foo");
    }
};

// Load an API context with the base URI for JSON references
apiContext = new OAI3Context(URI.create("/"), schemaNode); 
// Setup a validation context
validationContext = new ValidationContext<>(apiContext); 
// Link trigger 'x-myentity-val' (or known keyword such as maximum, format, ...) with MyValidator.
validationContext.addValidator("x-myentity-val", evi); 
schemaValidator = new SchemaValidator(validationContext, "entity_schema", schemaNode);
```

## Limitations

* Regular expressions : We do not conform to the [ECMA 262 regular expression](https://www.ecma-international.org/ecma-262/5.1/#sec-7.8.5) dialect. We use the provided dialect from the distribution.  
Since, the complete syntax is not widely supported, we think that we should be ok in most of cases. See JSON schema [recommendations](https://json-schema.org/understanding-json-schema/reference/regular_expressions.html) for regular expressions.

## Keyword support

Any keyword missing in the following table has to be considered NOT supported.

| Feature                           | Supported     | Notes                                                |
|-----------------------------------|---------------|------------------------------------------------------|
| **Schema Object**                 |               |                                                      |
|                                   |               |                                                      |
| $ref                              | Yes           |                                                      |
|                                   |               |                                                      |
| multipleOf                        | Yes           |                                                      |
| maximum                           | Yes           |                                                      |
| exclusiveMaximum                  | Yes           |                                                      |
| minimum                           | Yes           |                                                      |
| exclusiveMinimum                  | Yes           |                                                      |
| maxLength                         | Yes           |                                                      |
| minLength                         | Yes           |                                                      |
| pattern                           | Yes           |                                                      |
| maxItems                          | Yes           |                                                      |
| minItems                          | Yes           |                                                      |
| uniqueItems                       | Yes           |                                                      |
| maxProperties                     | Yes           |                                                      |
| minProperties                     | Yes           |                                                      |
| required                          | Yes           |                                                      |
| enum                              | Yes           |                                                      |
|                                   |               |                                                      |
| type                              | Yes           |                                                      |
| allOf                             | Yes           |                                                      |
| oneOf                             | Yes           |                                                      |
| anyOf                             | Yes           |                                                      |
| not                               | Yes           |                                                      |
| items                             | Yes           |                                                      |
| properties                        | Yes           |                                                      |
| patternProperties                 | Yes           |                                                      |
| additionalProperties              | Yes           |                                                      |
| format                            | Yes           | See below for supported formats                      |
|                                   |               |                                                      |
| nullable                          | Yes           |                                                      |
| discriminator                     | Yes           |                                                      |
| readOnly                          | No            | Irrelevant here, use Operation Validator module      |
| writeOnly                         | No            | Irrelevant here, use Operation Validator module      |
|                                   |               |                                                      |
| **Discriminator Object**          |               |                                                      |
|                                   |               |                                                      |
| propertyName                      | Yes           |                                                      |
| mapping                           | Yes           |                                                      |
|                                   |               |                                                      |
| **Supported Data Type Formats**   |               |                                                      |
|                                   |               |                                                      |
| int32                             | Yes           | as Integer                                           |
| int64                             | Yes           | as Long                                              |
| float                             | Yes           | as Float                                             |
| double                            | Yes           | as Double                                            |
| byte                              | Yes           | as base64 encoded characters                         |
| binary                            | Yes           | as textual                                           |
| password                          | Yes           | as textual                                           |
|                                   |               |                                                      |
| date                              | Yes           | as defined by full-date - RFC3339                    |
| date-time                         | Yes           | as defined by date-time - RFC3339                    |
| email                             | Yes           |                                                      |
| hostname                          | Yes           |                                                      |
| ipv4                              | Yes           |                                                      |
| ipv6                              | Yes           |                                                      |
| uri                               | Yes           |                                                      |
| uriref                            | Yes           |                                                      |
| uri-reference                     | Yes           |                                                      |


## License

[See main page](https://github.com/openapi4j/openapi4j#license)

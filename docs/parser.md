---
layout: default
title: Parser
nav_order: 2
---

# Parser
{:.no_toc}

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Features

* [Open API specification](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md) parser and validator.
* Manipulate the models and serialize back the modified API description.

The validation is made internally by specific classes. Schema validator project could be used, but many parts can't be checked via SchemaObject.  
That said, you can, if you want to, do the validation from it. Check at the integration tests to get an example.

## Installation

Add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>org.openapi4j</groupId>
    <artifactId>openapi-parser</artifactId>
</dependency>
```
[![Release version](https://img.shields.io/nexus/r/org.openapi4j/openapi-schema-validator?style=for-the-badge&color=blue&label=Release&server=https%3A%2F%2Foss.sonatype.org)](https://search.maven.org/search?q=g:org.openapi4j%20a:openapi-parser)
[![Snapshot version](https://img.shields.io/nexus/s/org.openapi4j/openapi-schema-validator?style=for-the-badge&color=blue&label=Snapshot&server=https%3A%2F%2Foss.sonatype.org)](https://oss.sonatype.org/content/repositories/snapshots/org/openapi4j/openapi-parser/)

## Usage

```java
// Parse without validation, setting to true is strongly recommended for further data validation.
OpenApi3 api = new OpenApi3Parser().parse(specURL, false);
// Explicit validation of the API spec
ValidationResults results = OpenApi3Validator.instance().validate(api);
```

If your document has restricted access, you're able to load it with authentication values :
```java
// You must provide all the values for the expected chain of documents.
List<AuthOption> authOptions = new ArrayList<>();
// A value in query for requests
authOptions.add(new AuthOption(QUERY, "sessionId", "xyz"));
// A value in header for localhost only
authOptions.add(new AuthOption(HEADER, "api_key", "xyz", url -> url.getHost().equals("localhost")));
...

OpenApi3 api = new OpenApi3Parser().parse(specURL, authOptions, true);
```

## Serialisation
```java
// Output as JsonNode
JsonNode node = myModel.toNode()
// Output as String; SerializationFlag values: OUT_AS_JSON or OUT_AS_YAML
String out = myModel.toString(EnumSet<SerializationFlag> flags);
```

## $Ref
When manipulating models you can get/set $ref from/to your model.

```java
// To keep consistency all across you current document use the following method.
// OAIContext context: to add/replace the reference in the registry.
// URL url: The base URL of your document.
// String ref: the $ref string value.
Reference ref = myModel.setReference(OAIContext context, URL url, String ref);
Reference ref = myModel.getReference(OAIContext context);
```java

## Limitations

* Serialisation: The module is not able to (re-)split the given input if any.
* Discriminator: Since Schema Object can be outside of `components/schemas`, mapping with schema name is not supported, you must use JSON reference.


## License

[See main page](index.md#license)

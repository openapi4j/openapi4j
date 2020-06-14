# OpenAPI Schema Object validator

Implementation of the [Schema Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#schemaObject) specification for Open API 3.
As a reminder, Schema Object is a subset of [JSON schema draft #00](https://tools.ietf.org/html/draft-wright-json-schema-validation-00) with additions.

## Features

This module allows the validation of your data against a given schema.
The aim of this module is accuracy and compliancy with the specifications.

That said, there's options to enrich to current definitions with the `ValidationContext` object :
* ADDITIONAL_PROPS_RESTRICT : By default, Schema Object can have additional properties. This option let's you invert the behaviour.
* You can override keywords and add your own validators. More on this with [extensions](#extensions).

Credits to [JSON-Schema-Test-Suite](https://github.com/json-schema-org/JSON-Schema-Test-Suite) where the majority of tests are coming from.

## Documentation

[Documentation is available here](https://www.openapi4j.org/schema-validator.html)

## Installation

Add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>org.openapi4j</groupId>
    <artifactId>openapi-schema-validator</artifactId>
</dependency>
```
[![Release version](https://img.shields.io/nexus/r/org.openapi4j/openapi-schema-validator?style=for-the-badge&color=blue&label=Release&server=https%3A%2F%2Foss.sonatype.org)](https://search.maven.org/search?q=g:org.openapi4j%20a:openapi-schema-validator)
[![Snapshot version](https://img.shields.io/nexus/s/org.openapi4j/openapi-schema-validator?style=for-the-badge&color=blue&label=Snapshot&server=https%3A%2F%2Foss.sonatype.org)](https://oss.sonatype.org/content/repositories/snapshots/org/openapi4j/openapi-schema-validator/)


## License

[See main page](https://github.com/openapi4j/openapi4j#license)

---
layout: default
title: Operation validator
nav_order: 4
---

# Operation validator
{:.no_toc}

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Features

* Requests and/or responses validation.
* Collection of requests/responses adapters for your environment (with associated helpers).

This module is on top of both OpenAPI parser and Schema Object validator.

⚠ The Operation validators are lazily created and cached for re-use by `RequestValidator` object. ⚠

⚠ Manipulating the OpenAPI models is discouraged in conjunction with this module. ⚠

## Installation

Add the following to your `pom.xml` :

```xml
<dependency>
    <groupId>org.openapi4j</groupId>
    <artifactId>openapi-operation-validator</artifactId>
</dependency>
```

[![Release version](https://img.shields.io/nexus/r/org.openapi4j/openapi-schema-validator?style=for-the-badge&color=blue&label=Release&server=https%3A%2F%2Foss.sonatype.org)](https://search.maven.org/search?q=g:org.openapi4j%20a:openapi-operation-validator)
[![Snapshot version](https://img.shields.io/nexus/s/org.openapi4j/openapi-schema-validator?style=for-the-badge&color=blue&label=Snapshot&server=https%3A%2F%2Foss.sonatype.org)](https://oss.sonatype.org/content/repositories/snapshots/org/openapi4j/openapi-operation-validator/)

## Usage

Recommended:

```java
// openAPI & operation objects are from openapi4j parser
RequestValidator val = new RequestValidator(openAPI);

// Default usage
val.validate(Request request);
// other usages
val.validate(Request request, Path path, Operation peration);
val.validate(Request request, ValidationData<?> validation); // If you need to get back info/warn content

// With response
val.validate(Response response, Path path, Operation operation);
// ...
```

Raw:

```java
OperationValidator val = new OperationValidator(openAPI, operation);
val.validateQuery(Request request, ValidationData<?> validation);
val.validateHeaders(Request request, ValidationData<?> validation);
val.validateBody(Request request, ValidationData<?> validation);
// ...
```

Requests and responses objects are wrappers from the specific adapter.

```java
// Pseudo code
Request request = [Adapter]Request.of([AdapterRequestObject] rq);
Response response = [Adapter]Response.of([AdapterResponseObject] resp);

// validate with the methods shown above...
```

## Supported adapters

See [openapi-operation-adapters](operation-validator-adapters) to get
the list of currently available adapters, needed dependencies and further documentation.

Feel free to contribute to add more adapters and additional features.
It should be very straightforward to implement a builder. Look at the code of current adapters as a starter.

## Supported body content types

* ```JSON (i.e pseudo (application|text)/(json|*+json))```
* ```Form URL encoded (application/x-www-form-urlencoded)```
* Or _whatever_ if you can provide a JsonNode or Map<String, Object> when building the body wrapper.

Optional additions (add the corresponding dependencies):

* ```Multipart (i.e pseudo multipart/(form-data|mixed))``` [See Apache Commons FileUpload >= 1.3](https://github.com/apache/commons-fileupload)
* ```XML (i.e pseudo (application|text)/(xml|*+xml))``` [See JSON-java](https://github.com/stleary/JSON-java)

Those additions are only mandatory if you can't provide JsonNode or Map<String, Object> or prefer use the provided additions.

Other content types are considered as a single text node to cover direct file uploads (i.e string/binary or string/base64).

## Limitations

* Security and *functional* related aspects (roles/scopes, token/cookie validity, ...) are not validated by this module unless specified on each adapter `README.md`.
In such case, you MUST refer and fallback to your middleware layer for documentation.

Here's a list of currently non supported keywords:

* allowedReserved (maybe forever).
* allowEmptyValue (will be removed in later version OAS).
* XML :
    * Note, prefix and namespace are not considered too since namespace information is always removed before processing. Not really an issue, the information is useless.
    * Note, attributes are kept and always converted to direct children JSON properties.

## License

[See main page](index.md#license)

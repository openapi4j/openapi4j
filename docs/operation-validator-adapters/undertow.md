---
layout: default
title: Undertow
parent: Operation validator adapters
---

# Undertow
{:.no_toc}

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Installation

Add the following to your `pom.xml` :

```xml
<dependency>
    <groupId>org.openapi4j</groupId>
    <artifactId>openapi-operation-undertow</artifactId>
</dependency>
```
[![Release version](https://img.shields.io/nexus/r/org.openapi4j/openapi-schema-validator?style=for-the-badge&color=blue&label=Release&server=https%3A%2F%2Foss.sonatype.org)](https://search.maven.org/search?q=g:org.openapi4j%20a:openapi-operation-undertow)
[![Snapshot version](https://img.shields.io/nexus/s/org.openapi4j/openapi-schema-validator?style=for-the-badge&color=blue&label=Snapshot&server=https%3A%2F%2Foss.sonatype.org)](https://oss.sonatype.org/content/repositories/snapshots/org/openapi4j/openapi-operation-undertow/)

## Usage

Requests and responses are wrapped with the specific adapter.
```java
// Undertow
Request request = UndertowRequest.of(HttpServerExchange hse);
// validate...
```

| Library         | Version     | Client | Server                  | Dependency                   |
|-----------------|-------------|--------|-------------------------|------------------------------|
| Undertow        | `> 2.0`     | No     | UndertowRequest         | io.undertow:undertow-core    |

## License

[See main page](https://github.com/openapi4j/openapi4j#license)

---
layout: default
title: Spring
parent: Operation validator adapters
---

# Spring

{:.no_toc}

## Table of contents

{: .no_toc .text-delta }

1. TOC {:toc}

---

## Installation

Add the following to your `pom.xml` :

```xml

<dependency>
  <groupId>org.openapi4j</groupId>
  <artifactId>openapi-operation-spring</artifactId>
  <!-- for MockMVC testing, not for client side validation: -->
  <scope>test</scope>
</dependency>
```

[![Release version](https://img.shields.io/nexus/r/org.openapi4j/openapi-operation-spring?style=for-the-badge&color=blue&label=Release&server=https%3A%2F%2Foss.sonatype.org)](https://search.maven.org/search?q=g:org.openapi4j%20a:openapi-operation-spring)
[![Snapshot version](https://img.shields.io/nexus/s/org.openapi4j/openapi-operation-spring?style=for-the-badge&color=blue&label=Snapshot&server=https%3A%2F%2Foss.sonatype.org)](https://oss.sonatype.org/content/repositories/snapshots/org/openapi4j/openapi-operation-spring/)

## Usage

### Client side validation

Client side validation of requests and reponses using an interceptor for the `RestTemplate`.

```java
Resource spec = new ClassPathResource("openapi3.yaml");
RestTemplate client = new RestTemplate();
// add the open API interceptor, should be the last in case of multiple interceptors
client.setInterceptors(Collections.singletonList(OpenApiClientInterceptor.openApi(spec)));
```

### Mock MVC tests

Mock MVC tests get automatic request and response validation.
A dependency of `spring-test` is needed and `openapi-operation-spring` can also be of scope `test`
if client validation is not needed.

```java
// Test setup
Resource spec = new ClassPathResource("openapi3.yaml");
MockMvc mvc = MockMvcBuilders.standaloneSetup(new TestController())
  .apply(OpenApiMatchers.openApi(spec)) // customize for request and response validation
  .build()

// Test
  mvc.perform(get("/examples")) // perform fails for an undocumented request
  // always expect a documented response first
  .andExpect(status().isOk());
```

| Library | Version  | Client                        | Server                  | Dependency                             |
|---------|----------|-------------------------------|-------------------------|----------------------------------------|
| Spring  | `>= 5.0` | ClientRequest, ClientResponse | MvcRequest, MvcResponse | The Spring web/test dependency you use |

## License

[See main page](../index.md#license)

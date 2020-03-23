# Changelog

## [0.8](https://github.com/openapi4j/openapi4j/tree/0.7) (2020-03-23)

[Full Changelog](https://github.com/openapi4j/openapi4j/compare/0.7...0.8)

**Implemented enhancements:**

- Chain custom validation [\#75](https://github.com/openapi4j/openapi4j/issues/75)
- Rework 'x-www-form-urlencoded' content type conversion [\#68](https://github.com/openapi4j/openapi4j/issues/68)
- Add code to each validation control point [\#66](https://github.com/openapi4j/openapi4j/issues/66)
- Operation validator: add request validation from URL only [\#59](https://github.com/openapi4j/openapi4j/issues/59)

**Fixed bugs:**

- Relative url support in OAuthFlow Object [\#71](https://github.com/openapi4j/openapi4j/issues/71)
- Map\<String,Object\> incorrectly validated [\#64](https://github.com/openapi4j/openapi4j/issues/64)
- Operator validator: Operation path parameters validation fails if server URL has path parameters/fragments [\#60](https://github.com/openapi4j/openapi4j/issues/60)
- allOf schema validator reports incorrect errors [\#57](https://github.com/openapi4j/openapi4j/issues/57)
- Parser: nested reference cycling is not detected [\#56](https://github.com/openapi4j/openapi4j/issues/56)
- Operation validator: detection of media type variance [\#53](https://github.com/openapi4j/openapi4j/issues/53)
- AbsOpenApiSchema.copyMap\(\) doesn't honor map type [\#51](https://github.com/openapi4j/openapi4j/issues/51)

## [0.7](https://github.com/openapi4j/openapi4j/tree/0.7) (2020-02-26)

[Full Changelog](https://github.com/openapi4j/openapi4j/compare/0.6...0.7)

**Implemented enhancements:**

- Allow native compilation with GraalVM [\#49](https://github.com/openapi4j/openapi4j/issues/49)
- Operation validator : support status code ranges for response description [\#41](https://github.com/openapi4j/openapi4j/issues/41)

**Fixed bugs:**

- Operation validator: detection of media type variance [\#53](https://github.com/openapi4j/openapi4j/issues/53)
- relative url support in Server Object [\#47](https://github.com/openapi4j/openapi4j/issues/47)
- Schema composition with discriminator causes validation errors [\#44](https://github.com/openapi4j/openapi4j/issues/44)
- Validation error on responses http status code ranges [\#39](https://github.com/openapi4j/openapi4j/issues/39)

**Closed issues:**

- Validate against discriminator-related "subclass" schemas [\#46](https://github.com/openapi4j/openapi4j/issues/46)


## [0.6](https://github.com/openapi4j/openapi4j/tree/0.6) (2020-01-16)

[Full Changelog](https://github.com/openapi4j/openapi4j/compare/0.5...0.6)

**Implemented enhancements:**

- Do not follow refs for discriminator properties [\#35](https://github.com/openapi4j/openapi4j/issues/35)
- Open API parser : slim down Schema Object \(de-\)serializers [\#25](https://github.com/openapi4j/openapi4j/issues/25)
- Add support for creating an OpenApi3 object from a JsonNode [\#24](https://github.com/openapi4j/openapi4j/issues/24)

**Fixed bugs:**

- Support $ref in Path and SecurityScheme [\#38](https://github.com/openapi4j/openapi4j/issues/38)
- Validation error on nullable parameters with a format [\#34](https://github.com/openapi4j/openapi4j/issues/34)
- Query parameters not marked as required still fail NullableValidator [\#31](https://github.com/openapi4j/openapi4j/issues/31)
- Root-level `security` declaration causes deserialization error [\#29](https://github.com/openapi4j/openapi4j/issues/29)
- Similar looking $ref strings can collide [\#28](https://github.com/openapi4j/openapi4j/issues/28)
- additionalProperties using a $ref are resolved to `false` when calling toNode [\#21](https://github.com/openapi4j/openapi4j/issues/21)
- Enum's in Schema are stored as strings [\#20](https://github.com/openapi4j/openapi4j/issues/20)

Thanks to all contributors!

## [0.5](https://github.com/openapi4j/openapi4j/tree/0.5) (2019-12-06)

[Full Changelog](https://github.com/openapi4j/openapi4j/compare/0.4...0.5)

**Fixed bugs:**

- Global - tests improved with minor fixes.

## [0.4](https://github.com/openapi4j/openapi4j/tree/0.4) (2019-12-03)

[Full Changelog](https://github.com/openapi4j/openapi4j/compare/0.3...0.4)

**Implemented enhancements:**

- Schema Validator - Add fast failure behaviour. [\#17](https://github.com/openapi4j/openapi4j/issues/17)
- Operation validator - Check parameter from content property [\#15](https://github.com/openapi4j/openapi4j/issues/15)

**Fixed bugs:**

- OpenAPI parser - validation tests improved.

## [0.3](https://github.com/openapi4j/openapi4j/tree/0.3) (2019-11-15)

[Full Changelog](https://github.com/openapi4j/openapi4j/compare/0.2...0.3)

**Implemented enhancements:**

- Operation validator - Add response validator [\#5](https://github.com/openapi4j/openapi4j/issues/5)

**Fixed bugs:**

- Operation validator - Fix missing checks on malformed content [\#6](https://github.com/openapi4j/openapi4j/issues/6)

**Merged pull requests:**

- Operation validator - Fix missing checks on malformed content [\#16](https://github.com/openapi4j/openapi4j/pull/16) ([llfbandit](https://github.com/llfbandit))
- Operation validator - Add response validator [\#14](https://github.com/openapi4j/openapi4j/pull/14) ([llfbandit](https://github.com/llfbandit))
- Improve travis build speed [\#13](https://github.com/openapi4j/openapi4j/pull/13) ([llfbandit](https://github.com/llfbandit))
- feat : improve performance on input stream to string conversion. [\#12](https://github.com/openapi4j/openapi4j/pull/12) ([llfbandit](https://github.com/llfbandit))
- Perf checker - Complete implementation [\#11](https://github.com/openapi4j/openapi4j/pull/11) ([llfbandit](https://github.com/llfbandit))

## [0.2](https://github.com/openapi4j/openapi4j/tree/0.2) (2019-11-12)

**Implemented enhancements:**

- Operation validator - Enable validation context overrides [\#7](https://github.com/openapi4j/openapi4j/issues/7)
- Operation validator - Add multipart with complex values (multipart/mixed) [\#2](https://github.com/openapi4j/openapi4j/issues/2)

**Merged pull requests:**

- Improve tests [\#8](https://github.com/openapi4j/openapi4j/pull/8) ([llfbandit](https://github.com/llfbandit))

## [0.1](https://github.com/openapi4j/openapi4j/tree/0.1) (2019-11-08)

**Implemented enhancements:**

- Add JSON reference implementation.
- Add OpenAPI parser and validation.
- Add OpenAPI schema object validation.
- Add OpenAPI operation validation.
- Add Vert.x router factory [\#1](https://github.com/openapi4j/openapi4j/issues/1)
- Add Untertow adapter.
- Add Servlet adapter.

**Merged pull requests:**

- core : code coverage [\#4](https://github.com/openapi4j/openapi4j/pull/4) ([llfbandit](https://github.com/llfbandit))


\* *This Changelog was automatically generated by [github_changelog_generator](https://github.com/github-changelog-generator/github-changelog-generator)*


\* *This Changelog was automatically generated by [github_changelog_generator](https://github.com/github-changelog-generator/github-changelog-generator)*

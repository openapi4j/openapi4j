# OpenAPI for java Operation validator project home

This is the home page of the openapi4j Operation validator project for Java (or JVM platform in general).

Operation validator module includes the following features :
* Operation validator for requests and/or responses.
* Collection of requests/responses adapters for your environment (with associated helpers).

This module is on top of both OpenAPI parser and Schema Object validator and intensively uses it.

The Operation validators are lazily created and cached for re-use.  
/!\ Manipulating the OpenAPI models is discouraged in conjunction with this module. /!\

## Installation

Add the following to your `pom.xml` :

```xml
<dependency>
    <groupId>org.openapi4j</groupId>
    <artifactId>openapi4j-operation-validator</artifactId>
    <version>VERSION</version>
</dependency>
```

## Usage

Standard :
```java
// openAPI & operation objects are from openapi4j parser
new RequestValidator(openAPI).validate(request, operation); // throws ValidationException
new ResponseValidator(openAPI).validate(response, operation); // throws ValidationException
```

Raw :
```java
OperationValidator val = new OperationValidator(openAPI, operation);
val.validate...(request, results); // ... stands for query, path, headers, ...
```

Requests and responses are wrapped with the specific adapter.
```java
// Pseudo
Request request = [Adapter]Request.of([AdapterRequestObject] rq);

// validate...
```

## Supported adapters

Adapters' dependencies are not provided, you must add the one you need.

See [openapi-operation-adapters](https://github.com/openapi4j/openapi4j/tree/master/openapi-operation-adapters) to get
the list of currently available adapters and further documentation.

Feel free to contribute to add more adapters and additional features.  
It should be very straightforward to implement a builder. Look at the code of current adapters as a starter.

## Supported body content types

* JSON (i.e pseudo (application|text)/(json|*+json))
* Form URL encoded (application/x-www-form-urlencoded)

Optional additions (add the corresponding dependencies) :   
* Multipart (i.e pseudo multipart/(form-data|mixed)) [See Apache Commons FileUpload >= 1.3](https://github.com/apache/commons-fileupload)
* XML (i.e pseudo (application|text)/(xml|*+xml)) [See JSON-java](https://github.com/stleary/JSON-java)

Other content types are considered as a single text node to cover direct file uploads (i.e string/binary or string/base64).

## TODO

* Enable validation context as input for schema validator overrides and options.
* Response validator.
* Multipart mixed.
* More tests.

## Limitations

* Security and *functional* related aspects (roles/scopes, token/cookie validity, ...) are not validated by this module unless specified on each adapter README.md.  
In such case, you MUST refer and fallback to your middleware layer for documentation.  

Here's a list of currently non supported keywords :  
* allowedReserved (maybe forever).
* allowEmptyValue (will be removed in later version OAS).
* XML : 
    * name, wrapped (always false).
    * Note, prefix and namespace are not considered too since namespace information is always removed before processing. Not really an issue, the information is useless.
    * Note, attributes are kept and always converted to direct children JSON properties.

## Supported versions

OpenAPI Specification (OAS) version 3.0.2.  
See related projects for limitations and issues.

## License

openapi4j and all the modules are released under the Apache 2.0 license. See [LICENSE](https://github.com/openapi4j/openapi4j/blob/master/LICENSE.md) for details.

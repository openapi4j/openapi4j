package org.openapi4j.parser;

import org.junit.Assert;
import org.junit.Test;
import org.openapi4j.parser.model.v3.Callback;
import org.openapi4j.parser.model.v3.Components;
import org.openapi4j.parser.model.v3.EncodingProperty;
import org.openapi4j.parser.model.v3.Example;
import org.openapi4j.parser.model.v3.Extensions;
import org.openapi4j.parser.model.v3.Header;
import org.openapi4j.parser.model.v3.Link;
import org.openapi4j.parser.model.v3.Parameter;
import org.openapi4j.parser.model.v3.Path;
import org.openapi4j.parser.model.v3.RequestBody;
import org.openapi4j.parser.model.v3.Response;
import org.openapi4j.parser.model.v3.Schema;
import org.openapi4j.parser.model.v3.SecurityScheme;

import java.util.HashMap;
import java.util.Map;

public class ModelTest {
  @Test
  public void callbackTest() {
    Callback callback = new Callback();

    // Path
    final String PATHSTR = "/foo";
    Assert.assertFalse(callback.hasCallbackPath(PATHSTR));
    Assert.assertNull(callback.getCallbackPath(PATHSTR));
    // Path single add
    Path path = new Path();
    callback.setCallbackPath(PATHSTR, path);
    Assert.assertTrue(callback.hasCallbackPath(PATHSTR));
    Assert.assertEquals(path, callback.getCallbackPath(PATHSTR));
    // Path multi add
    Map<String, Path> paths = new HashMap<>();
    paths.put(PATHSTR, path);
    callback.setCallbackPaths(paths);
    Assert.assertTrue(callback.hasCallbackPath(PATHSTR));
    Assert.assertEquals(path, callback.getCallbackPath(PATHSTR));
    callback.removeCallbackPath(PATHSTR);
    Assert.assertFalse(callback.hasCallbackPath(PATHSTR));
    // Extension
    Assert.assertNull(callback.getExtensions());
    Extensions extensions = new Extensions();
    callback.setExtensions(extensions);
    Assert.assertEquals(extensions, callback.getExtensions());
  }

  @Test
  public void componentsTest() {
    Components components = new Components();

    // Schemas
    final String SCHEMANAME = "fooschema";
    Assert.assertFalse(components.hasSchema(SCHEMANAME));
    Assert.assertNull(components.getSchema(SCHEMANAME));
    // schema single add
    Schema schema = new Schema();
    components.setSchema(SCHEMANAME, schema);
    Assert.assertTrue(components.hasSchema(SCHEMANAME));
    Assert.assertEquals(schema, components.getSchema(SCHEMANAME));
    // schema multi add
    Map<String, Schema> schemas = new HashMap<>();
    schemas.put(SCHEMANAME, schema);
    components.setSchemas(schemas);
    Assert.assertTrue(components.hasSchema(SCHEMANAME));
    Assert.assertEquals(schema, components.getSchema(SCHEMANAME));
    components.removeSchema(SCHEMANAME);
    Assert.assertFalse(components.hasSchema(SCHEMANAME));

    // Responses
    final String RESPONSENAME = "fooresponse";
    Assert.assertFalse(components.hasResponse(RESPONSENAME));
    Assert.assertNull(components.getResponse(RESPONSENAME));
    // schema single add
    Response response = new Response();
    components.setResponse(RESPONSENAME, response);
    Assert.assertTrue(components.hasResponse(RESPONSENAME));
    Assert.assertEquals(response, components.getResponse(RESPONSENAME));
    // schema multi add
    Map<String, Response> responses = new HashMap<>();
    responses.put(RESPONSENAME, response);
    components.setResponses(responses);
    Assert.assertTrue(components.hasResponse(RESPONSENAME));
    Assert.assertEquals(response, components.getResponse(RESPONSENAME));
    components.removeResponse(RESPONSENAME);
    Assert.assertFalse(components.hasResponse(RESPONSENAME));

    // Parameters
    final String PARAMETERNAME = "fooparameter";
    Assert.assertFalse(components.hasParameter(PARAMETERNAME));
    Assert.assertNull(components.getParameter(PARAMETERNAME));
    // Parameter single add
    Parameter parameter = new Parameter();
    components.setParameter(PARAMETERNAME, parameter);
    Assert.assertTrue(components.hasParameter(PARAMETERNAME));
    Assert.assertEquals(parameter, components.getParameter(PARAMETERNAME));
    // Parameter multi add
    Map<String, Parameter> parameters = new HashMap<>();
    parameters.put(PARAMETERNAME, parameter);
    components.setParameters(parameters);
    Assert.assertTrue(components.hasParameter(PARAMETERNAME));
    Assert.assertEquals(parameter, components.getParameter(PARAMETERNAME));
    components.removeParameter(PARAMETERNAME);
    Assert.assertFalse(components.hasParameter(PARAMETERNAME));

    // Examples
    final String EXAMPLENAME = "fooexample";
    Assert.assertFalse(components.hasExample(EXAMPLENAME));
    Assert.assertNull(components.getExample(EXAMPLENAME));
    // Example single add
    Example example = new Example();
    components.setExample(EXAMPLENAME, example);
    Assert.assertTrue(components.hasExample(EXAMPLENAME));
    Assert.assertEquals(example, components.getExample(EXAMPLENAME));
    // Example multi add
    Map<String, Example> examples = new HashMap<>();
    examples.put(EXAMPLENAME, example);
    components.setExamples(examples);
    Assert.assertTrue(components.hasExample(EXAMPLENAME));
    Assert.assertEquals(example, components.getExample(EXAMPLENAME));
    components.removeExample(EXAMPLENAME);
    Assert.assertFalse(components.hasExample(EXAMPLENAME));

    // Requestbodies
    final String REQUESTBODYNAME = "foorequestbody";
    Assert.assertFalse(components.hasRequestBody(REQUESTBODYNAME));
    Assert.assertNull(components.getRequestBody(REQUESTBODYNAME));
    // Requestbody single add
    RequestBody requestbody = new RequestBody();
    components.setRequestBody(REQUESTBODYNAME, requestbody);
    Assert.assertTrue(components.hasRequestBody(REQUESTBODYNAME));
    Assert.assertEquals(requestbody, components.getRequestBody(REQUESTBODYNAME));
    // Requestbody multi add
    Map<String, RequestBody> requestbodys = new HashMap<>();
    requestbodys.put(REQUESTBODYNAME, requestbody);
    components.setRequestBodies(requestbodys);
    Assert.assertTrue(components.hasRequestBody(REQUESTBODYNAME));
    Assert.assertEquals(requestbody, components.getRequestBody(REQUESTBODYNAME));
    components.removeRequestBody(REQUESTBODYNAME);
    Assert.assertFalse(components.hasRequestBody(REQUESTBODYNAME));

    // Requestbodies
    final String HEADERNAME = "fooheader";
    Assert.assertFalse(components.hasHeader(HEADERNAME));
    Assert.assertNull(components.getHeader(HEADERNAME));
    // Header single add
    Header header = new Header();
    components.setHeader(HEADERNAME, header);
    Assert.assertTrue(components.hasHeader(HEADERNAME));
    Assert.assertEquals(header, components.getHeader(HEADERNAME));
    // Header multi add
    Map<String, Header> headers = new HashMap<>();
    headers.put(HEADERNAME, header);
    components.setHeaders(headers);
    Assert.assertTrue(components.hasHeader(HEADERNAME));
    Assert.assertEquals(header, components.getHeader(HEADERNAME));
    components.removeHeader(HEADERNAME);
    Assert.assertFalse(components.hasHeader(HEADERNAME));

    // securitySchemes
    final String SECURITYSCHEMENAME = "foosecurityScheme";
    Assert.assertFalse(components.hasSecurityScheme(SECURITYSCHEMENAME));
    Assert.assertNull(components.getSecurityScheme(SECURITYSCHEMENAME));
    // SecurityScheme single add
    SecurityScheme securityScheme = new SecurityScheme();
    components.setSecurityScheme(SECURITYSCHEMENAME, securityScheme);
    Assert.assertTrue(components.hasSecurityScheme(SECURITYSCHEMENAME));
    Assert.assertEquals(securityScheme, components.getSecurityScheme(SECURITYSCHEMENAME));
    // SecurityScheme multi add
    Map<String, SecurityScheme> securitySchemes = new HashMap<>();
    securitySchemes.put(SECURITYSCHEMENAME, securityScheme);
    components.setSecuritySchemes(securitySchemes);
    Assert.assertTrue(components.hasSecurityScheme(SECURITYSCHEMENAME));
    Assert.assertEquals(securityScheme, components.getSecurityScheme(SECURITYSCHEMENAME));
    components.removeSecurityScheme(SECURITYSCHEMENAME);
    Assert.assertFalse(components.hasSecurityScheme(SECURITYSCHEMENAME));

    // links
    final String LINKNAME = "foolink";
    Assert.assertFalse(components.hasLink(LINKNAME));
    Assert.assertNull(components.getLink(LINKNAME));
    // Link single add
    Link link = new Link();
    components.setLink(LINKNAME, link);
    Assert.assertTrue(components.hasLink(LINKNAME));
    Assert.assertEquals(link, components.getLink(LINKNAME));
    // Link multi add
    Map<String, Link> links = new HashMap<>();
    links.put(LINKNAME, link);
    components.setLinks(links);
    Assert.assertTrue(components.hasLink(LINKNAME));
    Assert.assertEquals(link, components.getLink(LINKNAME));
    components.removeLink(LINKNAME);
    Assert.assertFalse(components.hasLink(LINKNAME));

    // callbacks
    final String CALLBACKNAME = "foocallback";
    Assert.assertFalse(components.hasCallback(CALLBACKNAME));
    Assert.assertNull(components.getCallback(CALLBACKNAME));
    // Callback single add
    Callback callback = new Callback();
    components.setCallback(CALLBACKNAME, callback);
    Assert.assertTrue(components.hasCallback(CALLBACKNAME));
    Assert.assertEquals(callback, components.getCallback(CALLBACKNAME));
    // Callback multi add
    Map<String, Callback> callbacks = new HashMap<>();
    callbacks.put(CALLBACKNAME, callback);
    components.setCallbacks(callbacks);
    Assert.assertTrue(components.hasCallback(CALLBACKNAME));
    Assert.assertEquals(callback, components.getCallback(CALLBACKNAME));
    components.removeCallback(CALLBACKNAME);
    Assert.assertFalse(components.hasCallback(CALLBACKNAME));

    // Extension
    Assert.assertNull(components.getExtensions());
    Extensions extensions = new Extensions();
    components.setExtensions(extensions);
    Assert.assertEquals(extensions, components.getExtensions());
  }

  @Test
  public void encodingPropertyTest() {
    EncodingProperty encProperty = new EncodingProperty();

    // headers
    final String HEADERNAME = "fooheader";
    final String HEADERVALUE = "fooheadervalue";
    Assert.assertFalse(encProperty.hasHeader(HEADERNAME));
    Assert.assertNull(encProperty.getHeader(HEADERNAME));
    // Header single add
    encProperty.setHeader(HEADERNAME, HEADERVALUE);
    Assert.assertTrue(encProperty.hasHeader(HEADERNAME));
    Assert.assertEquals(HEADERVALUE, encProperty.getHeader(HEADERNAME));
    // Header multi add
    Map<String, String> headers = new HashMap<>();
    headers.put(HEADERNAME, HEADERVALUE);
    encProperty.setHeaders(headers);
    Assert.assertTrue(encProperty.hasHeader(HEADERNAME));
    Assert.assertEquals(HEADERVALUE, encProperty.getHeader(HEADERNAME));
    encProperty.removeHeader(HEADERNAME);
    Assert.assertFalse(encProperty.hasHeader(HEADERNAME));

    // Extension
    Assert.assertNull(encProperty.getExtensions());
    Extensions extensions = new Extensions();
    encProperty.setExtensions(extensions);
    Assert.assertEquals(extensions, encProperty.getExtensions());
  }
}

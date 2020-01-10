package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.openapi4j.core.model.OAIContext;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Response extends AbsExtendedRefOpenApiSchema<Response> {
  private String description;
  private Map<String, Header> headers;
  @JsonProperty("content")
  private Map<String, MediaType> contentMediaTypes;
  private Map<String, Link> links;

  // Description
  public String getDescription() {
    return description;
  }

  public Response setDescription(String description) {
    this.description = description;
    return this;
  }

  // Header
  public Map<String, Header> getHeaders() {
    return headers;
  }

  public Response setHeaders(Map<String, Header> headers) {
    this.headers = headers;
    return this;
  }

  public boolean hasHeader(String name) {
    return mapHas(headers, name);
  }

  public Header getHeader(String name) {
    return mapGet(headers, name);
  }

  public Response setHeader(String name, Header header) {
    if (headers == null) {
      headers = new HashMap<>();
    }
    headers.put(name, header);
    return this;
  }

  public Response removeHeader(String name) {
    mapRemove(headers, name);
    return this;
  }

  // ContentMediaType
  public Map<String, MediaType> getContentMediaTypes() {
    return contentMediaTypes;
  }

  public Response setContentMediaTypes(Map<String, MediaType> contentMediaTypes) {
    this.contentMediaTypes = contentMediaTypes;
    return this;
  }

  public boolean hasContentMediaType(String name) {
    return mapHas(contentMediaTypes, name);
  }

  public MediaType getContentMediaType(String name) {
    return mapGet(contentMediaTypes, name);
  }

  public Response setContentMediaType(String name, MediaType contentMediaType) {
    if (contentMediaTypes == null) {
      contentMediaTypes = new HashMap<>();
    }
    contentMediaTypes.put(name, contentMediaType);
    return this;
  }

  public Response removeContentMediaType(String name) {
    mapRemove(contentMediaTypes, name);
    return this;
  }

  // Link
  public Map<String, Link> getLinks() {
    return links;
  }

  public Response setLinks(Map<String, Link> links) {
    this.links = links;
    return this;
  }

  public boolean hasLink(String name) {
    return mapHas(links, name);
  }

  public Link getLink(String name) {
    return mapGet(links, name);
  }

  public Response setLink(String name, Link link) {
    if (links == null) {
      links = new HashMap<>();
    }
    links.put(name, link);
    return this;
  }

  public Response removeLink(String name) {
    mapRemove(links, name);
    return this;
  }

  @Override
  protected Response copyReference(OAIContext context) {
    Response copy = new Response();
    copy.setRef(getRef());
    copy.setCanonicalRef(getCanonicalRef());
    return copy;
  }

  @Override
  protected Response copyContent(OAIContext context, boolean followRefs) {
    Response copy = new Response();

    copy.setDescription(getDescription());
    copy.setHeaders(copyMap(getHeaders(), context, followRefs));
    copy.setContentMediaTypes(copyMap(getContentMediaTypes(), context, followRefs));
    copy.setLinks(copyMap(getLinks(), context, followRefs));
    copy.setExtensions(copyMap(getExtensions()));

    return copy;
  }
}

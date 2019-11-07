package org.openapi4j.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import org.openapi4j.core.model.OAIContext;
import org.openapi4j.parser.model.AbsOpenApiSchema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class SecurityRequirement extends AbsOpenApiSchema<SecurityRequirement> {
  private Map<String, List<String>> requirements;

  // Requirement
  @JsonAnyGetter
  public Map<String, List<String>> getRequirements() {
    return requirements;
  }

  public SecurityRequirement setRequirements(Map<String, List<String>> requirements) {
    this.requirements = requirements;
    return this;
  }

  public boolean hasRequirement(String name) {
    return mapHas(requirements, name);
  }

  public boolean hasRequirementScopes(String name) {
    List<String> scopes = mapGet(requirements, name);
    return scopes != null && !scopes.isEmpty();
  }

  public List<String> getRequirementScopes(String name) {
    return mapGet(requirements, name);
  }

  @JsonAnySetter
  public SecurityRequirement setRequirement(String name, List<String> scopes) {
    if (requirements == null) {
      requirements = new HashMap<>();
    }
    requirements.put(name, scopes);
    return this;
  }

  public SecurityRequirement removeRequirement(String name) {
    mapRemove(requirements, name);
    return this;
  }

  @Override
  public SecurityRequirement copy(OAIContext context, boolean followRefs) {
    SecurityRequirement copy = new SecurityRequirement();

    copy.setRequirements(copyMap(getRequirements()));

    return copy;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SecurityRequirement that = (SecurityRequirement) o;

    return Objects.equals(requirements, that.requirements);
  }

  @Override
  public int hashCode() {
    return requirements != null ? requirements.hashCode() : 0;
  }
}

package org.openapi4j.operation.validator.adapters.server.vertx.v3.impl;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.parser.model.v3.SecurityRequirement;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

class SecurityRequirementHelper {
  private static final String SEC_HANDLER_REQUIRED_ERR_MSG = "Security requirement handler is required.";
  private static final String SEC_SCOPE_REQUIRED_ERR_MSG = "Security requirement scope name is required.";
  private static final String SEC_NAME_REQUIRED_ERR_MSG = "Security requirement name is required.";
  private static final String SEC_MISSING_ERR_MSG = "Security requirement '%s' not found.";
  private static final String SEC_SCOPED_MISSING_ERR_MSG = "Security requirement '%s' with scope '%s' not found.";

  private final Map<OperationSecurityRequirement, List<Handler<RoutingContext>>> securityHandlers;

  SecurityRequirementHelper() {
    securityHandlers = new HashMap<>();
  }

  void addSecurityHandler(String securityRequirementName, Handler<RoutingContext> handler) {
    requireNonNull(handler, SEC_HANDLER_REQUIRED_ERR_MSG);

    securityHandlers
      .computeIfAbsent(new OperationSecurityRequirement(securityRequirementName), k -> new ArrayList<>())
      .add(handler);
  }

  void addSecurityScopedHandler(String securityRequirementName, String scopeName, Handler<RoutingContext> handler) {
    requireNonNull(scopeName, SEC_SCOPE_REQUIRED_ERR_MSG);
    requireNonNull(handler, SEC_HANDLER_REQUIRED_ERR_MSG);

    securityHandlers
      .computeIfAbsent(new OperationSecurityRequirement(securityRequirementName, scopeName), k -> new ArrayList<>())
      .add(handler);
  }

  Collection<Handler<RoutingContext>> getHandlers(Collection<SecurityRequirement> securityRequirements) throws ResolutionException {
    List<OperationSecurityRequirement> osrs = translateRequirements(securityRequirements);
    Set<Handler<RoutingContext>> handlers = new HashSet<>();

    for (OperationSecurityRequirement osr : osrs) {
      handlers.addAll(getHandlers(osr));
    }

    return handlers;
  }

  private List<Handler<RoutingContext>> getHandlers(OperationSecurityRequirement osr) throws ResolutionException {
    List<Handler<RoutingContext>> handlers = securityHandlers.get(osr);

    if (handlers == null) {
      if (osr.scopeName != null) {
        throw new ResolutionException(String.format(SEC_SCOPED_MISSING_ERR_MSG, osr.securityRequirementName, osr.scopeName));
      } else {
        throw new ResolutionException(String.format(SEC_MISSING_ERR_MSG, osr.securityRequirementName));
      }
    }

    return handlers;
  }

  private List<OperationSecurityRequirement> translateRequirements(Collection<SecurityRequirement> keys) {
    if (keys != null) {
      return keys
        .stream()
        .flatMap(m -> m.getRequirements().entrySet().stream().flatMap(e -> {
          if (e.getValue() == null || e.getValue().isEmpty())
            return Stream.of(new OperationSecurityRequirement(e.getKey()));
          else
            return e.getValue().stream().map(s -> new OperationSecurityRequirement(e.getKey(), s));
        }))
        .collect(Collectors.toList());
    } else {
      return new ArrayList<>();
    }
  }
  private static class OperationSecurityRequirement {
    private final String securityRequirementName;
    private final String scopeName;

    OperationSecurityRequirement(String securityRequirementName) {
      this(securityRequirementName, null);
    }

    OperationSecurityRequirement(String securityRequirementName, String scopeName) {
      requireNonNull(securityRequirementName, SEC_NAME_REQUIRED_ERR_MSG);

      this.securityRequirementName = securityRequirementName;
      this.scopeName = scopeName;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      OperationSecurityRequirement that = (OperationSecurityRequirement) o;

      if (!securityRequirementName.equals(that.securityRequirementName)) return false;
      return Objects.equals(scopeName, that.scopeName);
    }

    @Override
    public int hashCode() {
      int result = securityRequirementName.hashCode();
      result = 31 * result + (scopeName != null ? scopeName.hashCode() : 0);
      return result;
    }
  }
}

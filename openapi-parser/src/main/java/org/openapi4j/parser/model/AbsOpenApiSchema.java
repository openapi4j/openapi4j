package org.openapi4j.parser.model;

import org.openapi4j.core.exception.EncodeException;
import org.openapi4j.core.model.OAI;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.core.util.Json;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbsOpenApiSchema<O extends OAI, M extends OpenApiSchema<O, M>> implements OpenApiSchema<O, M> {
  @Override
  @SuppressWarnings("unchecked")
  public <T> T toJson(OAIContext<O> context, EnumSet<SerializationFlag> flags) throws EncodeException {
    if (flags == null) {
      flags = EnumSet.of(SerializationFlag.OUT_AS_NODE);
    }

    OpenApiSchema<O, M> model
      = flags.contains(SerializationFlag.FOLLOW_REFS)
      ? copy(context, true)
      : this;

    if (flags.contains(SerializationFlag.OUT_AS_STRING)) {
      if (flags.contains(SerializationFlag.OUT_AS_YAML)) {
        return (T) Json.toYaml(model);
      } else {
        return (T) Json.toJson(model);
      }
    }

    if (flags.contains(SerializationFlag.OUT_AS_YAML)) {
      return (T) Json.toYamlNode(model);
    } else {
      return (T) Json.toJsonNode(model);
    }
  }

  @Override
  public String toJson() throws EncodeException {
    return Json.toJson(this);
  }

  protected <T> List<T> copyList(List<T> original) {
    if (original != null) {
      return new ArrayList<>(original);
    }

    return null;
  }

  protected <K, T> Map<K, T> copyMap(Map<K, T> original) {
    if (original != null) {
      return new HashMap<>(original);
    }

    return null;
  }

  protected <T extends OpenApiSchema<O, T>> T copyField(T original, OAIContext<O> context, boolean followRefs) {
    if (original != null) {
      return original.copy(context, followRefs);
    }

    return null;
  }

  protected <T extends OpenApiSchema<O, T>> List<T> copyList(List<T> original, OAIContext<O> context, boolean followRefs) {
    if (original != null) {
      List<T> copy = new ArrayList<>(original.size());
      for (T element : original) {
        copy.add(element.copy(context, followRefs));
      }

      return copy;
    }

    return null;
  }

  protected <K, T extends OpenApiSchema<O, T>> Map<K, T> copyMap(Map<K, T> original, OAIContext<O> context, boolean followRefs) {
    if (original != null) {
      Map<K, T> copy = new HashMap<>(original.size());
      for (Map.Entry<K, T> element : original.entrySet()) {
        OpenApiSchema<O, T> schema = element.getValue();
        if (schema != null) {
          copy.put(element.getKey(), schema.copy(context, followRefs));
        } else {
          copy.put(element.getKey(), null);
        }

      }

      return copy;
    }

    return null;
  }

  protected <K, V> boolean has(Map<K, V> map, K key) {
    return map != null && map.containsKey(key);
  }

  protected <K, V> V get(Map<K, V> map, K key) {
    if (map == null) {
      return null;
    }
    return map.get(key);
  }

  protected <K, V> void remove(Map<K, V> map, K key) {
    if (map != null) {
      map.remove(key);
    }
  }
}

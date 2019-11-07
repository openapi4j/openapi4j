package org.openapi4j.parser.model;

import org.openapi4j.core.exception.EncodeException;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.core.util.TreeUtil;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbsOpenApiSchema<M extends OpenApiSchema<M>> implements OpenApiSchema<M> {
  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings("unchecked")
  public <T> T toJson(OAIContext context, EnumSet<SerializationFlag> flags) throws EncodeException {
    if (flags == null) {
      flags = EnumSet.of(SerializationFlag.OUT_AS_JSON);
    }

    OpenApiSchema<M> model
      = flags.contains(SerializationFlag.FOLLOW_REFS)
      ? copy(context, true)
      : this;

    if (flags.contains(SerializationFlag.OUT_AS_STRING)) {
      if (flags.contains(SerializationFlag.OUT_AS_YAML)) {
        return (T) TreeUtil.toYaml(model);
      } else {
        return (T) TreeUtil.toJson(model);
      }
    }

    return (T) TreeUtil.toJsonNode(model);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toJson() throws EncodeException {
    return TreeUtil.toJson(this);
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

  protected <T extends OpenApiSchema<T>> T copyField(T original, OAIContext context, boolean followRefs) {
    if (original != null) {
      return original.copy(context, followRefs);
    }

    return null;
  }

  protected <T extends OpenApiSchema<T>> List<T> copyList(List<T> original, OAIContext context, boolean followRefs) {
    if (original != null) {
      List<T> copy = new ArrayList<>(original.size());
      for (T element : original) {
        copy.add(element.copy(context, followRefs));
      }

      return copy;
    }

    return null;
  }

  protected <K, T extends OpenApiSchema<T>> Map<K, T> copyMap(Map<K, T> original, OAIContext context, boolean followRefs) {
    if (original != null) {
      Map<K, T> copy = new HashMap<>(original.size());
      for (Map.Entry<K, T> element : original.entrySet()) {
        OpenApiSchema<T> schema = element.getValue();
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

  protected <K, V> boolean mapHas(Map<K, V> map, K key) {
    return map != null && map.containsKey(key);
  }

  protected <K, V> V mapGet(Map<K, V> map, K key) {
    if (map == null) {
      return null;
    }
    return map.get(key);
  }

  protected <K, V> void mapRemove(Map<K, V> map, K key) {
    if (map != null) {
      map.remove(key);
    }
  }

  protected <K> K listGet(List<K> list, int index) {
    if (list == null) {
      return null;
    }
    return list.get(index);
  }

  protected <K> List<K> listAdd(List<K> list, K value) {
    if (list == null) {
      list = new ArrayList<>();
    }
    list.add(value);

    return list;
  }

  protected <K> List<K> listAdd(List<K> list, int index, K value) {
    if (list == null) {
      list = new ArrayList<>();
    }
    list.add(index, value);

    return list;
  }

  protected <K> K listRemove(List<K> list, int index) {
    if (list == null) return null;

    return list.remove(index);
  }
}

package org.openapi4j.parser.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.openapi4j.core.exception.EncodeException;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.core.util.TreeUtil;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openapi4j.core.util.TreeUtil.ENCODE_ERR_MSG;

public abstract class AbsOpenApiSchema<M extends OpenApiSchema<M>> implements OpenApiSchema<M> {
  protected static class Views {
    private Views() {
    }

    public static class Public {
      private Public() {
      }
    }

    public static class Internal extends Public {
      private Internal() {
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JsonNode toNode(OAIContext context, boolean followRefs) throws EncodeException {
    OpenApiSchema<M> model
      = followRefs
      ? copy(context, true)
      : this;

    try {
      byte[] content = TreeUtil.json
        .writerWithView(Views.Public.class)
        .writeValueAsBytes(model);

      return TreeUtil.json.readTree(content);
    } catch (Exception e) {
      throw new EncodeException(String.format(ENCODE_ERR_MSG, e.getMessage()));
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString(OAIContext context, EnumSet<SerializationFlag> flags) throws EncodeException {
    OpenApiSchema<M> model
      = flags.contains(SerializationFlag.FOLLOW_REFS)
      ? copy(context, true)
      : this;

    ObjectMapper mapper
      = flags.contains(SerializationFlag.OUT_AS_YAML)
      ? TreeUtil.yaml
      : TreeUtil.json;

    try {
      return mapper
        .writerWithView(Views.Public.class)
        .writeValueAsString(model);
    } catch (Exception e) {
      throw new EncodeException(String.format(ENCODE_ERR_MSG, e.getMessage()));
    }
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

  protected <K, V> Map<K, V> mapPut(Map<K, V> map, K key, V value) {
    if (map == null) {
      map = new HashMap<>();
    }
    map.put(key, value);

    return map;
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

  protected <K> boolean listRemove(List<K> list, K value) {
    if (list == null) return false;

    return list.remove(value);
  }
}

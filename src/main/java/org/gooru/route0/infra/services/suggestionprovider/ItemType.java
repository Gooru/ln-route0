package org.gooru.route0.infra.services.suggestionprovider;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ashish.
 */
public enum ItemType {
  ASSESSMENT("assessment"),
  COLLECTION("collection");

  private final String name;

  ItemType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  private static final Map<String, ItemType> LOOKUP = new HashMap<>(values().length);

  static {
    for (ItemType currentItemType : values()) {
      LOOKUP.put(currentItemType.name, currentItemType);
    }
  }

  public static ItemType builder(String type) {
    ItemType result = LOOKUP.get(type);
    if (result == null) {
      throw new IllegalArgumentException("Invalid ItemType: " + type);
    }
    return result;
  }
}

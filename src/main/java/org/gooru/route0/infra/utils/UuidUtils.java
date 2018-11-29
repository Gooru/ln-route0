package org.gooru.route0.infra.utils;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author ashish.
 */
public final class UuidUtils {

  private static final int VALID_UUID_LEN = UUID.randomUUID().toString().length();

  public static UUID valueFromJsonObjectAsUUID(JsonObject input, String key) {
    String value = input.getString(key);
    return convertStringToUuid(value);
  }

  public static UUID convertStringToUuid(String value) {
    if (value == null || value.isEmpty() || value.length() != VALID_UUID_LEN) {
      return null;
    }
    return UUID.fromString(value);
  }

  public static String uuidToString(UUID uuid) {
    if (uuid == null) {
      return null;
    }
    return uuid.toString();
  }

  public static List<UUID> convertToUUIDList(JsonArray array) {
    if (array == null || array.isEmpty()) {
      return Collections.emptyList();
    }
    List<UUID> result = new ArrayList<>(array.size());
    for (Object o : array) {
      if (o.toString().length() != VALID_UUID_LEN) {
        throw new IllegalArgumentException("Invalid UUID format");
      }
      result.add(convertStringToUuid(o.toString()));
    }
    return result;
  }

  public static List<UUID> convertToUUIDListIgnoreInvalidItems(JsonArray array) {
    if (array == null || array.isEmpty()) {
      return Collections.emptyList();
    }
    List<UUID> result = new ArrayList<>(array.size());
    for (Object o : array) {
      if (o.toString().length() == VALID_UUID_LEN) {
        result.add(convertStringToUuid(o.toString()));
      }
    }
    return result;
  }

  private UuidUtils() {
    throw new AssertionError();
  }
}

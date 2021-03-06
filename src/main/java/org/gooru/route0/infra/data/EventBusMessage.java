package org.gooru.route0.infra.data;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import java.util.UUID;
import org.gooru.route0.infra.constants.Constants;

/**
 * This class encapsulates the Event Bus message and is useful only if the request has come via
 * Authenticated handlers.
 *
 * This is because, values like session/session token/user id context will be present only in cases
 * where request is authenticated. It may not make sense to use this for non authenticated API.
 *
 * @author ashish
 */
public final class EventBusMessage {

  private final String sessionToken;
  private final JsonObject requestBody;
  private final UUID userId;
  private final JsonObject session;
  private final boolean isAuthenticated;

  public String getSessionToken() {
    return sessionToken;
  }

  public JsonObject getRequestBody() {
    return requestBody;
  }

  public UUID getUserId() {
    return userId;
  }

  public JsonObject getSession() {
    return session;
  }

  private EventBusMessage(String sessionToken, JsonObject requestBody, UUID userId,
      JsonObject session,
      boolean isAuthenticated) {
    this.sessionToken = sessionToken;
    this.requestBody = requestBody;
    this.userId = userId;
    this.session = session;
    this.isAuthenticated = isAuthenticated;
  }

  public static EventBusMessage eventBusMessageBuilder(Message<JsonObject> message) {
    String sessionToken = message.body().getString(Constants.Message.MSG_SESSION_TOKEN);
    String userId = message.body().getString(Constants.Message.MSG_USER_ID);
    JsonObject requestBody = message.body().getJsonObject(Constants.Message.MSG_HTTP_BODY);
    JsonObject session = message.body().getJsonObject(Constants.Message.MSG_KEY_SESSION);

    return new EventBusMessage(sessionToken, requestBody, UUID.fromString(userId), session, true);
  }

  public static EventBusMessage eventBusMessageBuilderForNonAuthenticatedInternalRequests(
      Message<JsonObject> message) {
    String userId = Constants.Misc.USER_PLACEHOLDER;
    JsonObject requestBody = message.body().getJsonObject(Constants.Message.MSG_HTTP_BODY);

    return new EventBusMessage(null, requestBody, UUID.fromString(userId), new JsonObject(), false);
  }

}

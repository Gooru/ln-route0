package org.gooru.route0.infra.constants;

import java.util.UUID;

/**
 * @author ashish.
 */
public final class Constants {

  public static final class EventBus {

    public static final String MBEP_AUTH = "org.gooru.route0.eventbus.auth";
    public static final String MBEP_ROUTE0 = "org.gooru.route0.eventbus.route0";
    public static final String MBEP_ROUTE0_QUEUE_PROCESSOR = "org.gooru.route0.eventbus.queueprocessor";

    public static final String MBUS_TIMEOUT = "event.bus.send.timeout.seconds";

    private EventBus() {
      throw new AssertionError();
    }
  }

  public static final class Message {

    public static final String MSG_OP = "mb.operation";
    public static final String MSG_API_VERSION = "api.version";
    public static final String MSG_SESSION_TOKEN = "session.token";
    public static final String MSG_OP_AUTH = "auth";
    public static final String MSG_KEY_SESSION = "session";
    public static final String MSG_OP_STATUS = "mb.op.status";
    public static final String MSG_OP_STATUS_SUCCESS = "mb.op.status.success";
    public static final String MSG_OP_STATUS_FAIL = "mb.op.status.fail";
    public static final String MSG_USER_ANONYMOUS = "anonymous";
    public static final String MSG_USER_ID = "user_id";
    public static final String MSG_HTTP_STATUS = "http.status";
    public static final String MSG_HTTP_BODY = "http.body";
    public static final String MSG_HTTP_HEADERS = "http.headers";

    public static final String MSG_OP_ROUTE0_GET = "route0.get";
    public static final String MSG_OP_ROUTE0_SET = "route0.set";
    public static final String MSG_OP_ROUTE0_SET_STATUS = "route0.status.set";
    public static final String MSG_OP_ROUTE0_COMPETENCY_ROUTE_INTERNAL = "route0.internal.competency.route";
    public static final String MSG_OP_ROUTE0_COMPETENCY_CONTENT_ROUTE_INTERNAL =
        "route0.internal.competency.content.route";

    public static final String MSG_MESSAGE = "message";
    public static final String ACCESS_TOKEN_VALIDITY = "access_token_validity";

    private Message() {
      throw new AssertionError();
    }
  }

  public static final class Response {

    private Response() {
      throw new AssertionError();
    }
  }

  public static final class Params {

    public static final String PARAM_MEMBER_ID = "member_id";
    public static final String PARAM_CLASS_ID = "class_id";

    private Params() {
      throw new AssertionError();
    }
  }

  public static final class Route {

    public static final String API_AUTH_ROUTE = "/api/route0/*";

    private static final String API_BASE_ROUTE = "/api/route0/:version/";
    public static final String API_ROUTE0_FETCH = API_BASE_ROUTE + "rtd";
    public static final String API_ROUTE0_SET_STATUS = API_BASE_ROUTE + "rtd/status";

    public static final String API_INTERNAL_ROUTE0_CALCULATE = "/api/internal/rtd";
    public static final String API_INTERNAL_COMPETENCY_ROUTE = "/api/internal/rtd/competency";
    public static final String API_INTERNAL_COMPETENCY_CONTENT_ROUTE = "/api/internal/rtd/competency/content";

    private Route() {
      throw new AssertionError();
    }
  }

  public static final class Misc {

    public static final String COMPETENCY_PLACEHOLDER = new UUID(0, 0).toString();
    public static final String USER_PLACEHOLDER = new UUID(0, 0).toString();

    private Misc() {
      throw new AssertionError();
    }
  }

  private Constants() {
    throw new AssertionError();
  }
}

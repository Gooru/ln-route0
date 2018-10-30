package org.gooru.route0.routes.utils;

import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.util.Objects;
import org.gooru.route0.infra.constants.HttpConstants;
import org.gooru.route0.infra.exceptions.HttpResponseWrapperException;
import org.gooru.route0.responses.writers.ResponseWriterBuilder;
import org.slf4j.Logger;

/**
 * Created by ashish.
 */
public final class RouteResponseUtility {

  private RouteResponseUtility() {
    throw new AssertionError();
  }

  public static void responseHandler(final RoutingContext routingContext,
      final AsyncResult<Message<JsonObject>> reply, final Logger LOG) {
    if (reply.succeeded()) {
      ResponseWriterBuilder.build(routingContext, reply).writeResponse();
    } else {
      LOG.error("Not able to send message", reply.cause());
      routingContext.response().setStatusCode(HttpConstants.HttpStatus.ERROR.getCode()).end();
    }
  }

  public static void responseHandlerStatusOnlyNoBodyOrHeaders(final RoutingContext routingContext,
      HttpConstants.HttpStatus status) {
    routingContext.response().setStatusCode(status.getCode()).end();
  }

  public static void responseHandler(final RoutingContext routingContext,
      final HttpResponseWrapperException exception) {
    String body = Objects.toString(exception.getBody(), null);
    if (body != null) {
      routingContext.response().setStatusCode(exception.getStatus())
          .putHeader(HttpConstants.HEADER_CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON).end(body);
    } else {
      routingContext.response().setStatusCode(exception.getStatus())
          .putHeader(HttpConstants.HEADER_CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON).end();
    }
  }

  public static void responseHandlerForContext(final RoutingContext routingContext,
      final AsyncResult<Message<JsonObject>> reply, final Logger logger) {
    if (reply.succeeded()) {
      if (reply.result() != null && reply.result().body() != null && !reply.result().body()
          .isEmpty()) {
        routingContext.response().setStatusCode(HttpConstants.HttpStatus.SUCCESS.getCode())
            .putHeader(HttpConstants.HEADER_CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON)
            .end(reply.result().body().toString());
      } else {
        // Communication with Redis successful but we do not have anything as context
        routingContext.response().setStatusCode(HttpConstants.HttpStatus.SUCCESS.getCode())
            .putHeader(HttpConstants.HEADER_CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON)
            .end(new JsonObject().toString());
      }
    } else {
      logger.error("Not able to send message", reply.cause());
      routingContext.response().setStatusCode(HttpConstants.HttpStatus.ERROR.getCode()).end();
    }
  }
}

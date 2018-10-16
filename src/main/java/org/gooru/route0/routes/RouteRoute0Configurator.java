package org.gooru.route0.routes;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.gooru.route0.infra.constants.Constants;
import org.gooru.route0.infra.constants.HttpConstants;
import org.gooru.route0.routes.utils.DeliveryOptionsBuilder;
import org.gooru.route0.routes.utils.RouteRequestUtility;
import org.gooru.route0.routes.utils.RouteResponseUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish
 */
public class RouteRoute0Configurator implements RouteConfigurator {

  private static final Logger LOGGER = LoggerFactory.getLogger(RouteRoute0Configurator.class);
  private EventBus eb = null;
  private long mbusTimeout;

  @Override
  public void configureRoutes(Vertx vertx, Router router, JsonObject config) {
    eb = vertx.eventBus();
    mbusTimeout = config.getLong(Constants.EventBus.MBUS_TIMEOUT, 30L) * 1000;
    router.get(Constants.Route.API_ROUTE0_FETCH).handler(this::fetchRoute0Content);
    router.post(Constants.Route.API_ROUTE0_CALCULATE).handler(this::doRoute0OfContent);
    router.put(Constants.Route.API_ROUTE0_SET_STATUS).handler(this::acceptRejectRoute0);
  }

  private void fetchRoute0Content(RoutingContext routingContext) {
    DeliveryOptions options = DeliveryOptionsBuilder.buildWithApiVersion(routingContext)
        .setSendTimeout(mbusTimeout)
        .addHeader(Constants.Message.MSG_OP, Constants.Message.MSG_OP_ROUTE0_GET);
    eb.<JsonObject>send(Constants.EventBus.MBEP_ROUTE0,
        RouteRequestUtility.getBodyForMessage(routingContext),
        options, reply -> RouteResponseUtility.responseHandler(routingContext, reply, LOGGER));
  }

  private void doRoute0OfContent(RoutingContext routingContext) {
    DeliveryOptions options = DeliveryOptionsBuilder.buildWithApiVersion(routingContext)
        .setSendTimeout(mbusTimeout)
        .addHeader(Constants.Message.MSG_OP, Constants.Message.MSG_OP_ROUTE0_SET);
    eb.<JsonObject>send(Constants.EventBus.MBEP_ROUTE0,
        RouteRequestUtility.getBodyForMessage(routingContext),
        options);
    RouteResponseUtility
        .responseHandlerStatusOnlyNoBodyOrHeaders(routingContext, HttpConstants.HttpStatus.SUCCESS);
  }

  private void acceptRejectRoute0(RoutingContext routingContext) {
    DeliveryOptions options = DeliveryOptionsBuilder.buildWithApiVersion(routingContext)
        .setSendTimeout(mbusTimeout)
        .addHeader(Constants.Message.MSG_OP, Constants.Message.MSG_OP_ROUTE0_SET_STATUS);
    eb.<JsonObject>send(Constants.EventBus.MBEP_ROUTE0,
        RouteRequestUtility.getBodyForMessage(routingContext),
        options, reply -> RouteResponseUtility.responseHandler(routingContext, reply, LOGGER));
  }

}

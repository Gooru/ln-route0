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
 * @author ashish.
 */
class RouteInternalConfigurator implements RouteConfigurator {

  private static final Logger LOGGER = LoggerFactory.getLogger(RouteInternalConfigurator.class);
  private EventBus eb = null;
  private long mbusTimeout;

  @Override
  public void configureRoutes(Vertx vertx, Router router, JsonObject config) {
    LOGGER.debug("Configuring routes for internal route");
    eb = vertx.eventBus();
    mbusTimeout = config.getLong(Constants.EventBus.MBUS_TIMEOUT, 30L) * 1000;
    router.get(Constants.Route.API_INTERNAL_COMPETENCY_ROUTE)
        .handler(this::calculateRoute0CompetencyMap);
    router.get(Constants.Route.API_INTERNAL_COMPETENCY_CONTENT_ROUTE)
        .handler(this::calculateRoute0CompetencyContentMap);
    router.post(Constants.Route.API_INTERNAL_ROUTE0_CALCULATE).handler(this::doRoute0OfContent);

  }

  private void calculateRoute0CompetencyContentMap(RoutingContext routingContext) {
    DeliveryOptions options = DeliveryOptionsBuilder.buildWithoutApiVersion(mbusTimeout)
        .addHeader(Constants.Message.MSG_OP,
            Constants.Message.MSG_OP_ROUTE0_COMPETENCY_CONTENT_ROUTE_INTERNAL);
    eb.<JsonObject>send(Constants.EventBus.MBEP_ROUTE0,
        RouteRequestUtility.getBodyForMessage(routingContext),
        options, reply -> RouteResponseUtility.responseHandler(routingContext, reply, LOGGER));
  }

  private void calculateRoute0CompetencyMap(RoutingContext routingContext) {
    DeliveryOptions options = DeliveryOptionsBuilder.buildWithoutApiVersion(mbusTimeout)
        .addHeader(Constants.Message.MSG_OP,
            Constants.Message.MSG_OP_ROUTE0_COMPETENCY_ROUTE_INTERNAL);
    eb.<JsonObject>send(Constants.EventBus.MBEP_ROUTE0,
        RouteRequestUtility.getBodyForMessage(routingContext),
        options, reply -> RouteResponseUtility.responseHandler(routingContext, reply, LOGGER));
  }

  private void doRoute0OfContent(RoutingContext routingContext) {
    DeliveryOptions options = DeliveryOptionsBuilder
        .buildWithoutApiVersion(routingContext, mbusTimeout, Constants.Message.MSG_OP_ROUTE0_SET);
    eb.<JsonObject>send(Constants.EventBus.MBEP_ROUTE0,
        RouteRequestUtility.getBodyForMessage(routingContext),
        options);
    RouteResponseUtility
        .responseHandlerStatusOnlyNoBodyOrHeaders(routingContext, HttpConstants.HttpStatus.SUCCESS);
  }

}

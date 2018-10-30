package org.gooru.route0.processors.calculatecompetencyroute;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.gooru.route0.infra.data.EventBusMessage;
import org.gooru.route0.infra.jdbi.DBICreator;
import org.gooru.route0.processors.AsyncMessageProcessor;
import org.gooru.route0.responses.MessageResponse;
import org.gooru.route0.responses.MessageResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */
public class CalculateCompetencyRouteProcessor implements AsyncMessageProcessor {

  private final Message<JsonObject> message;
  private final Vertx vertx;
  private final Future<MessageResponse> result;
  private EventBusMessage eventBusMessage;
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CalculateCompetencyRouteProcessor.class);
  private CalculateCompetencyRouteService calculateCompetencyRouteService =
      new CalculateCompetencyRouteService(DBICreator.getDbiForDefaultDS(),
          DBICreator.getDbiForDsdbDS());

  public CalculateCompetencyRouteProcessor(Vertx vertx, Message<JsonObject> message) {
    this.message = message;
    this.vertx = vertx;
    this.result = Future.future();
  }

  @Override
  public Future<MessageResponse> process() {
    vertx.<MessageResponse>executeBlocking(future -> {
      try {
        this.eventBusMessage =
            EventBusMessage.eventBusMessageBuilderForNonAuthenticatedInternalRequests(message);

        CalculateCompetencyRouteCommand command = CalculateCompetencyRouteCommand
            .builder(eventBusMessage);
        JsonObject competencyRoute = calculateCompetencyRouteService
            .calculateCompetencyRoute(command);
        future.complete(MessageResponseFactory.createOkayResponse(competencyRoute));
      } catch (Throwable throwable) {
        LOGGER.warn("Encountered exception", throwable);
        future.fail(throwable);
      }
    }, asyncResult -> {
      if (asyncResult.succeeded()) {
        result.complete(asyncResult.result());
      } else {
        result.fail(asyncResult.cause());
      }
    });
    return result;
  }
}

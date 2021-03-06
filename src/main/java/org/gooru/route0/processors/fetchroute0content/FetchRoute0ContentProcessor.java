package org.gooru.route0.processors.fetchroute0content;

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
public class FetchRoute0ContentProcessor implements AsyncMessageProcessor {

  private final Message<JsonObject> message;
  private final Vertx vertx;
  private final Future<MessageResponse> result;
  private EventBusMessage eventBusMessage;
  private static final Logger LOGGER = LoggerFactory.getLogger(FetchRoute0ContentProcessor.class);
  private final FetchRoute0ContentService fetchRoute0ContentService =
      new FetchRoute0ContentService(DBICreator.getDbiForDefaultDS());

  public FetchRoute0ContentProcessor(Vertx vertx, Message<JsonObject> message) {
    this.message = message;
    this.vertx = vertx;
    this.result = Future.future();
  }

  @Override
  public Future<MessageResponse> process() {
    vertx.<MessageResponse>executeBlocking(future -> {
      try {
        this.eventBusMessage = EventBusMessage.eventBusMessageBuilder(message);

        FetchRoute0ContentCommand command = FetchRoute0ContentCommand.builder(eventBusMessage);
        FetchRoute0ContentResponse route0Content = fetchRoute0ContentService
            .fetchRoute0Content(command);
        future.complete(createResponse(route0Content));
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

  private MessageResponse createResponse(FetchRoute0ContentResponse route0Content) {
    if (route0Content == null) {
      return MessageResponseFactory.createNotFoundResponse("Route0 content not found");
    }
    return MessageResponseFactory.createOkayResponse(route0Content.asJson());
  }

}

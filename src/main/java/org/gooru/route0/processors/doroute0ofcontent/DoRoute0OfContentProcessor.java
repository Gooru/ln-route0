package org.gooru.route0.processors.doroute0ofcontent;

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
public class DoRoute0OfContentProcessor implements AsyncMessageProcessor {

  private final Message<JsonObject> message;
  private final Vertx vertx;
  private final Future<MessageResponse> result;
  private EventBusMessage eventBusMessage;
  private static final Logger LOGGER = LoggerFactory.getLogger(DoRoute0OfContentProcessor.class);

  private final DoRoute0OfContentService doRoute0OfContentService = new DoRoute0OfContentService(
      DBICreator.getDbiForDefaultDS());

  public DoRoute0OfContentProcessor(Vertx vertx, Message<JsonObject> message) {
    this.vertx = vertx;
    this.message = message;
    this.result = Future.future();
  }

  @Override
  public Future<MessageResponse> process() {
    vertx.<MessageResponse>executeBlocking(future -> {
      try {
        this.eventBusMessage = EventBusMessage.eventBusMessageBuilder(message);
        DoRoute0OfContentCommand command = DoRoute0OfContentCommand
            .builder(eventBusMessage.getRequestBody());
        doRoute0OfContentService.doRoute0(command);
        future.complete(createResponse());
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

  private MessageResponse createResponse() {
    return MessageResponseFactory.createOkayResponse(new JsonObject());
  }

}

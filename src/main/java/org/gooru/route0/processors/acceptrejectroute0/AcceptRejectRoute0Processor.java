package org.gooru.route0.processors.acceptrejectroute0;

import org.gooru.route0.infra.data.EventBusMessage;
import org.gooru.route0.infra.jdbi.DBICreator;
import org.gooru.route0.processors.AsyncMessageProcessor;
import org.gooru.route0.responses.MessageResponse;
import org.gooru.route0.responses.MessageResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * @author ashish.
 */
public class AcceptRejectRoute0Processor implements AsyncMessageProcessor {

    private final Message<JsonObject> message;
    private final Vertx vertx;
    private final Future<MessageResponse> result;
    private EventBusMessage eventBusMessage;
    private static final Logger LOGGER = LoggerFactory.getLogger(AcceptRejectRoute0Processor.class);
    private AcceptRejectRoute0Service acceptRejectRoute0Service =
        new AcceptRejectRoute0Service(DBICreator.getDbiForDefaultDS());

    public AcceptRejectRoute0Processor(Vertx vertx, Message<JsonObject> message) {
        this.message = message;
        this.vertx = vertx;
        this.result = Future.future();
    }

    @Override
    public Future<MessageResponse> process() {
        vertx.<MessageResponse>executeBlocking(future -> {
            try {
                this.eventBusMessage = EventBusMessage.eventBusMessageBuilder(message);

                AcceptRejectRoute0Command command = AcceptRejectRoute0Command.builder(eventBusMessage);
                acceptRejectRoute0Service.acceptRejectRoute0(command);
                future.complete(MessageResponseFactory.createNoContentResponse());
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

package org.gooru.route0.bootstrap.verticles;

import org.gooru.route0.infra.constants.Constants;
import org.gooru.route0.infra.exceptions.HttpResponseWrapperException;
import org.gooru.route0.infra.exceptions.MessageResponseWrapperException;
import org.gooru.route0.processors.ProcessorBuilder;
import org.gooru.route0.responses.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * @author ashish.
 */
public class Route0Verticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(Route0Verticle.class);

    @Override
    public void start(Future<Void> startFuture) {

        EventBus eb = vertx.eventBus();
        eb.localConsumer(Constants.EventBus.MBEP_ROUTE0, this::processMessage).completionHandler(result -> {
            if (result.succeeded()) {
                LOGGER.info("Route0 end point ready to listen");
                startFuture.complete();
            } else {
                LOGGER.error("Error registering the Route0 handler. Halting the machinery");
                startFuture.fail(result.cause());
                Runtime.getRuntime().halt(1);
            }
        });
    }

    private void processMessage(Message<JsonObject> message) {
        String op = message.headers().get(Constants.Message.MSG_OP);
        Future<MessageResponse> future;
        boolean replyNeeded = true;
        switch (op) {
        case Constants.Message.MSG_OP_ROUTE0_GET:
            future = ProcessorBuilder.buildFetchRoute0ContentProcessor(vertx, message).process();
            break;
        case Constants.Message.MSG_OP_ROUTE0_SET:
            future = ProcessorBuilder.buildDoRoute0OfContentProcessor(vertx, message).process();
            replyNeeded = false;
            break;
        case Constants.Message.MSG_OP_ROUTE0_SET_STATUS:
            future = ProcessorBuilder.buildAcceptRejectRoute0Processor(vertx, message).process();
            break;
        case Constants.Message.MSG_OP_ROUTE0_COMPETENCY_ROUTE_INTERNAL:
            future = ProcessorBuilder.buildCalculateCompetencyMapProcessor(vertx, message).process();
            break;
        case Constants.Message.MSG_OP_ROUTE0_COMPETENCY_CONTENT_ROUTE_INTERNAL:
            future = ProcessorBuilder.buildCalculateCompetencyContentMapProcessor(vertx, message).process();
            break;
        default:
            LOGGER.warn("Invalid operation type");
            future = ProcessorBuilder.buildPlaceHolderExceptionProcessor(vertx, message).process();
        }

        futureResultHandler(message, future, replyNeeded);
    }

    private static void futureResultHandler(Message<JsonObject> message, Future<MessageResponse> future,
        boolean replyNeeded) {
        future.setHandler(event -> {
            if (event.succeeded() && replyNeeded) {
                message.reply(event.result().reply(), event.result().deliveryOptions());
            } else if (replyNeeded) {
                LOGGER.warn("Failed to process command", event.cause());
                if (event.cause() instanceof HttpResponseWrapperException) {
                    HttpResponseWrapperException exception = (HttpResponseWrapperException) event.cause();
                    message.reply(new JsonObject().put(Constants.Message.MSG_HTTP_STATUS, exception.getStatus())
                        .put(Constants.Message.MSG_HTTP_BODY, exception.getBody())
                        .put(Constants.Message.MSG_HTTP_HEADERS, new JsonObject()));
                } else if (event.cause() instanceof MessageResponseWrapperException) {
                    MessageResponseWrapperException exception = (MessageResponseWrapperException) event.cause();
                    message.reply(exception.getMessageResponse().reply(),
                        exception.getMessageResponse().deliveryOptions());
                } else {
                    message.reply(new JsonObject().put(Constants.Message.MSG_HTTP_STATUS, 500)
                        .put(Constants.Message.MSG_HTTP_BODY, new JsonObject())
                        .put(Constants.Message.MSG_HTTP_HEADERS, new JsonObject()));
                }
            }
        });
    }

    @Override
    public void stop(Future<Void> stopFuture) {
    }
}

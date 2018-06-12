package org.gooru.route0.bootstrap.verticles;

import org.gooru.route0.infra.constants.Constants;
import org.gooru.route0.infra.data.Route0QueueModel;
import org.gooru.route0.infra.services.Route0ProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

/**
 * @author ashish.
 */
public class Route0ProcessingVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(Route0ProcessingVerticle.class);

    private static final String SUCCESS = "SUCCESS";
    private static final String FAIL = "FAIL";

    @Override
    public void start(Future<Void> startFuture) {

        EventBus eb = vertx.eventBus();
        eb.localConsumer(Constants.EventBus.MBEP_ROUTE0_QUEUE_PROCESSOR, this::processMessage)
            .completionHandler(result -> {
                if (result.succeeded()) {
                    LOGGER.info("Route0 processor point ready to listen");
                    startFuture.complete();
                } else {
                    LOGGER.error("Error registering the Route0 processor handler. Halting the machinery");
                    startFuture.fail(result.cause());
                    Runtime.getRuntime().halt(1);
                }
            });
    }

    private void processMessage(Message<String> message) {
        String payload = message.body();
        vertx.executeBlocking(future -> {
            try {
                Route0ProcessingService.build().doRoute0(Route0QueueModel.fromJson(message.body()));
                future.complete();
            } catch (Exception e) {
                LOGGER.warn("Not able to do route0 for the model. '{}'", message.body());
                future.fail(e);
            }
        }, asyncResult -> {
            if (asyncResult.succeeded()) {
                message.reply(SUCCESS);
            } else {
                LOGGER.warn("Rescoping not done for model: '{}'", message.body());
                message.reply(FAIL);
            }
        });
    }

    @Override
    public void stop(Future<Void> stopFuture) {
    }
}

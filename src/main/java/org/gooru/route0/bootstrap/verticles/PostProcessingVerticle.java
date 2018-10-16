package org.gooru.route0.bootstrap.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import java.util.Objects;
import org.gooru.route0.infra.constants.Constants;
import org.gooru.route0.processors.ProcessorBuilder;
import org.gooru.route0.responses.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */
public class PostProcessingVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(PostProcessingVerticle.class);
  private HttpClient client;
  private String lpbaselineUri;

  @Override
  public void start(Future<Void> startFuture) {

    EventBus eb = vertx.eventBus();
    eb.localConsumer(Constants.EventBus.MBEP_ROUTE0_POST_PROCESSOR, this::processMessage)
        .completionHandler(result -> {
          if (result.succeeded()) {
            LOGGER.info("Route0 post processor point ready to listen");
            startFuture.complete();
          } else {
            LOGGER.error(
                "Error registering the Route0 post processor handler. Halting the machinery");
            startFuture.fail(result.cause());
            Runtime.getRuntime().halt(1);
          }
        });
    initializeHttpClient();
  }

  private void processMessage(Message<JsonObject> message) {
    String op = message.headers().get(Constants.Message.MSG_OP);
    Future<MessageResponse> future;
    switch (op) {
      case Constants.Message.MSG_OP_ROUTE0_LP_BASELINE:
        future = ProcessorBuilder.buildLPBaselineProcessor(vertx, message, client, lpbaselineUri)
            .process();
        break;
      default:
        LOGGER.warn("Invalid operation type");
        future = ProcessorBuilder.buildPlaceHolderExceptionProcessor(vertx, message).process();
    }

    future.setHandler(event -> {
      if (event.succeeded()) {
        LOGGER.info("Post processing op completed.");
      } else {
        LOGGER.warn("Post processing op failed", event.cause());
      }
    });
  }

  @Override
  public void stop(Future<Void> stopFuture) {
  }

  private void initializeHttpClient() {

    final Integer timeout = config().getInteger("http.timeout");
    final Integer poolSize = config().getInteger("http.poolSize");
    client = vertx.createHttpClient(
        new HttpClientOptions().setConnectTimeout(timeout).setMaxPoolSize(poolSize));
    lpbaselineUri = config().getString("lpbaseline.uri");

    Objects.requireNonNull(timeout);
    Objects.requireNonNull(poolSize);
    Objects.requireNonNull(lpbaselineUri);

  }

}

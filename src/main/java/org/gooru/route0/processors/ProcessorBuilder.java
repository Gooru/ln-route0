package org.gooru.route0.processors;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.gooru.route0.processors.acceptrejectroute0.AcceptRejectRoute0Processor;
import org.gooru.route0.processors.calculatecompetencycontentroute.CalculateCompetencyContentRouteProcessor;
import org.gooru.route0.processors.calculatecompetencyroute.CalculateCompetencyRouteProcessor;
import org.gooru.route0.processors.doroute0ofcontent.DoRoute0OfContentProcessor;
import org.gooru.route0.processors.fetchroute0content.FetchRoute0ContentProcessor;
import org.gooru.route0.responses.MessageResponse;

/**
 * @author ashish.
 */
public final class ProcessorBuilder {

  public static AsyncMessageProcessor buildFetchRoute0ContentProcessor(Vertx vertx,
      Message<JsonObject> message) {
    return new FetchRoute0ContentProcessor(vertx, message);
  }

  public static AsyncMessageProcessor buildDoRoute0OfContentProcessor(Vertx vertx,
      Message<JsonObject> message) {
    return new DoRoute0OfContentProcessor(vertx, message);
  }

  public static AsyncMessageProcessor buildPlaceHolderExceptionProcessor(Vertx vertx,
      Message<JsonObject> message) {
    return () -> {
      Future<MessageResponse> future = Future.future();
      future.fail(new IllegalStateException("Illegal State for processing command"));
      return future;
    };
  }

  public static AsyncMessageProcessor buildCalculateCompetencyMapProcessor(Vertx vertx,
      Message<JsonObject> message) {
    return new CalculateCompetencyRouteProcessor(vertx, message);
  }

  public static AsyncMessageProcessor buildAcceptRejectRoute0Processor(Vertx vertx,
      Message<JsonObject> message) {
    return new AcceptRejectRoute0Processor(vertx, message);
  }

  public static AsyncMessageProcessor buildCalculateCompetencyContentMapProcessor(Vertx vertx,
      Message<JsonObject> message) {
    return new CalculateCompetencyContentRouteProcessor(vertx, message);
  }
}

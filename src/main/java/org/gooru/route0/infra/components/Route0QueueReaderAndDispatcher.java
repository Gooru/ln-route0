package org.gooru.route0.infra.components;

import org.gooru.route0.infra.constants.Constants;
import org.gooru.route0.infra.data.Route0QueueModel;
import org.gooru.route0.infra.services.Route0QueueInitializerService;
import org.gooru.route0.infra.services.Route0QueueRecordDispatcherService;
import org.gooru.route0.routes.utils.DeliveryOptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * This is the timer based runner class which is responsible to read the Persisted queued requests and send them to
 * Event bus so that they can be processed by listeners.
 * It does wait for reply, so that we do increase the backpressure on TCP bus too much, however what is replied is does
 * not matter as we do schedule another one shot timer to do the similar stuff.
 * For the first run, it re-initializes the status in the DB so that any tasks that were under processing when the
 * application shut down happened would be picked up again.
 *
 * @author ashish.
 */
public final class Route0QueueReaderAndDispatcher implements Initializer, Finalizer {
    private static final Route0QueueReaderAndDispatcher ourInstance = new Route0QueueReaderAndDispatcher();
    private static final int delay = 1000;
    private static long timerId;
    private static boolean firstTrigger = false;
    private static final Logger LOGGER = LoggerFactory.getLogger(Route0QueueReaderAndDispatcher.class);
    private Vertx vertx;
    private static final int ROUTE0_PROCESS_TIMEOUT = 300;

    public static Route0QueueReaderAndDispatcher getInstance() {
        return ourInstance;
    }

    private Route0QueueReaderAndDispatcher() {
    }

    @Override
    public void finalizeComponent() {
        vertx.cancelTimer(timerId);
    }

    @Override
    public void initializeComponent(Vertx vertx, JsonObject config) {
        this.vertx = vertx;

        timerId = vertx.setTimer(delay, new TimerHandler(vertx, firstTrigger));
    }

    static final class TimerHandler implements Handler<Long> {

        private final Vertx vertx;

        TimerHandler(Vertx vertx, boolean firstTrigger) {
            this.vertx = vertx;
        }

        @Override
        public void handle(Long event) {
            vertx.<Route0QueueModel>executeBlocking(future -> {
                if (firstTrigger) {
                    LOGGER.debug("Timer handling for first trigger");
                    Route0QueueInitializerService.build().initializeQueue();
                    firstTrigger = false;
                }
                LOGGER.debug("Timer handling to dispatch next record");
                Route0QueueModel model = Route0QueueRecordDispatcherService.build().getNextRecordToDispatch();
                future.complete(model);
            }, asyncResult -> {
                if (asyncResult.succeeded()) {
                    if (asyncResult.result().isModelPersisted()) {
                        vertx.eventBus().send(Constants.EventBus.MBEP_ROUTE0_QUEUE_PROCESSOR, asyncResult.result().toJson(),
                            DeliveryOptionsBuilder.buildWithoutApiVersion(ROUTE0_PROCESS_TIMEOUT),
                            eventBusResponse -> {
                                timerId = vertx.setTimer(delay, new TimerHandler(vertx, firstTrigger));
                            });
                    } else {
                        timerId = vertx.setTimer(delay, new TimerHandler(vertx, firstTrigger));
                    }
                } else {
                    LOGGER.warn("Processing of record from queue failed. ", asyncResult.cause());
                    timerId = vertx.setTimer(delay, new TimerHandler(vertx, firstTrigger));
                }
            });

        }
    }

}

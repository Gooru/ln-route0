package org.gooru.route0;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.gooru.route0.infra.data.Route0QueueModel;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * @author ashish.
 */
public class MiscTest {

    public static void main(String[] args) {
        MiscTest miscTest = new MiscTest();
        miscTest.run();
    }

    private List<String> getDummies() {
        return Arrays.asList(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
    }

    private Route0QueueModel createNewRoute0QueueModel() {
        Route0QueueModel model;
        model = new Route0QueueModel();
        model.setId(100L);
        model.setClassId(null);
        model.setCourseId(UUID.randomUUID());
        model.setUserId(UUID.randomUUID());
        model.setStatus(Route0QueueModel.RQ_STATUS_DISPATCHED);
        model.setPriority(1);
        return model;
    }

    private void run() {
        boolean result =  doTest();
        System.out.println(result);
    }

    private boolean doTest() {
        String setting = "{}";
        JsonObject jsonSetting = new JsonObject(setting);
        return Boolean.TRUE.equals(jsonSetting.getBoolean("route0"));
    }

    public void initializeComponent(Vertx vertx, JsonObject config) {
        System.out.println("Context.isOnVertxThread : " + Context.isOnVertxThread());
        System.out.println("Computing with : " + Thread.currentThread());

        vertx.setTimer(1000, id -> {
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println("-----------SetTimerHandler");
            System.out.println("Computing with : " + Thread.currentThread());
            System.out.println("Context#isEventLoopContext : " + Vertx.currentContext().isEventLoopContext());
            System.out.println("Context#isWorkerContext : " + Vertx.currentContext().isWorkerContext());
            System.out.println(
                "Context#isMultiThreadedWorkerContext : " + Vertx.currentContext().isMultiThreadedWorkerContext());
            System.out.println("Context#isOnEventLoopThread : " + Context.isOnEventLoopThread());
            System.out.println("Context.isOnWorkerThread : " + Context.isOnWorkerThread());
            System.out.println("Context.isOnVertxThread : " + Context.isOnVertxThread());
            vertx.executeBlocking(future -> {
                System.out.println("-----------ExecuteBlockingHandler");
                System.out.println("============================");
                System.out.println("Computing with : " + Thread.currentThread());
                System.out.println("Context#isEventLoopContext : " + Vertx.currentContext().isEventLoopContext());
                System.out.println("Context#isWorkerContext : " + Vertx.currentContext().isWorkerContext());
                System.out.println(
                    "Context#isMultiThreadedWorkerContext : " + Vertx.currentContext().isMultiThreadedWorkerContext());
                System.out.println("Context#isOnEventLoopThread : " + Context.isOnEventLoopThread());
                System.out.println("Context.isOnWorkerThread : " + Context.isOnWorkerThread());
                System.out.println("Context.isOnVertxThread : " + Context.isOnVertxThread());
                future.complete();
            }, asyncResult -> {
                System.out.println("============================");
                System.out.println("-----------ExecuteBlockingCallback");
                System.out.println("Computing with : " + Thread.currentThread());
                System.out.println("Context#isEventLoopContext : " + Vertx.currentContext().isEventLoopContext());
                System.out.println("Context#isWorkerContext : " + Vertx.currentContext().isWorkerContext());
                System.out.println(
                    "Context#isMultiThreadedWorkerContext : " + Vertx.currentContext().isMultiThreadedWorkerContext());
                System.out.println("Context#isOnEventLoopThread : " + Context.isOnEventLoopThread());
                System.out.println("Context.isOnWorkerThread : " + Context.isOnWorkerThread());
                System.out.println("Context.isOnVertxThread : " + Context.isOnVertxThread());
            });
        });
    }


}

package org.gooru.route0.infra.services;

import java.util.UUID;

import org.gooru.route0.DBITestHelper;
import org.gooru.route0.infra.data.Route0QueueModel;

import io.vertx.core.json.JsonObject;

/**
 * @author ashish.
 */
public class JdbiTest {
    public static void main(String[] args) {
        new JdbiTest().testInsertIntoUserRoute0Content();
    }

    private void testInsertIntoUserRoute0Content() {
        Route0RequestQueueDao dao = new DBITestHelper().getDBI().onDemand(Route0RequestQueueDao.class);
        Route0QueueModel model = new Route0QueueModel();
        model.setClassId(UUID.randomUUID());
        model.setUserId(UUID.randomUUID());
        model.setCourseId(UUID.randomUUID());

        dao.persistRoute0Content(model, new JsonObject().put("a", "b").put("c", "d").toString());
    }

}

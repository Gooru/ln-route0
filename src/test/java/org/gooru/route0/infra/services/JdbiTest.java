package org.gooru.route0.infra.services;

import java.util.List;
import java.util.UUID;

import org.gooru.route0.DBITestHelper;
import org.gooru.route0.infra.data.Route0QueueModel;
import org.gooru.route0.infra.services.competencyroutecalculator.CompetencyFetcher;
import org.gooru.route0.infra.services.competencyroutecalculator.SubjectInferer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

/**
 * @author ashish.
 */
public class JdbiTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbiTest.class);
    public static void main(String[] args) {
        new JdbiTest().testCompetencyFetcher();
    }

    private void testInsertIntoUserRoute0Content() {
        Route0RequestQueueDao dao = new DBITestHelper().getDBI().onDemand(Route0RequestQueueDao.class);
        Route0QueueModel model = new Route0QueueModel();
        model.setClassId(UUID.randomUUID());
        model.setUserId(UUID.randomUUID());
        model.setCourseId(UUID.randomUUID());

        dao.persistRoute0Content(model, new JsonObject().put("a", "b").put("c", "d").toString());
    }

    private void testSubjectInferer() {
        UUID courseId = UUID.fromString("c3c5a610-0c97-4ecc-852c-8586a7ba3b52");
        SubjectInferer subjectInferer = SubjectInferer.build(new DBITestHelper().getDBI());
        String result = subjectInferer.inferSubjectForCourse(courseId);
        LOGGER.info("Subject for course '{}' is '{}'", courseId, result);
    }

    private void testCompetencyFetcher() {
        UUID courseId = UUID.fromString("c3c5a610-0c97-4ecc-852c-8586a7ba3b52");
        CompetencyFetcher competencyFetcher = CompetencyFetcher.build(new DBITestHelper().getDBI());
        List<String> result = competencyFetcher.fetchCompetenciesForCourse(courseId);
        LOGGER.info("Competencies for course '{}' is '{}'", courseId, result);
    }

}

package org.gooru.route0.infra.services;

import java.util.UUID;
import org.gooru.route0.infra.data.Route0Context;
import org.gooru.route0.infra.data.Route0QueueModel;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */
class Route0RequestQueueServiceImpl implements Route0RequestQueueService {

  private final DBI dbi;
  private static final Logger LOGGER = LoggerFactory.getLogger(Route0RequestQueueService.class);
  private Route0Context context;
  private Route0RequestQueueDao queueDao;

  Route0RequestQueueServiceImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public void enqueue(Route0Context context) {
    this.context = context;
    System.out.println("Received request for queueing");
    System.out.println(context.toString());
    queueDao = dbi.onDemand(Route0RequestQueueDao.class);
    if (context.getClassId() != null) {
      doQueueingForClass();
    } else {
      doQueueingForIL();
    }

  }

  private void doQueueingForIL() {
    if (context.getCourseId() == null) {
      LOGGER.warn("Route0 fired for IL without courseId. Abort.");
      return;
    }
    if (!queueDao.isCourseNotDeleted(context.getCourseId())) {
      LOGGER.warn("Route0 fired for deleted or not existing course: '{}'. Abort.",
          context.getCourseId());
      return;
    }
    queueInDb();
  }

  private void doQueueingForClass() {
    if (!queueDao.isClassNotDeletedAndNotArchived(context.getClassId())) {
      LOGGER.warn("Route0 fired for archived or deleted class: '{}'", context.getClassId());
      return;
    }
    UUID courseId = queueDao.fetchCourseForClass(context.getClassId());
    if (courseId == null) {
      LOGGER
          .warn("No course associated with class: '{}'. Will not do route0", context.getClassId());
      return;
    }

    if (context.getCourseId() != null && !context.getCourseId().equals(courseId)) {
      LOGGER.warn(
          "Course specified in request '{}' does not match course associated with class '{}'. Will use "
              + "the one associated with class", context.getCourseId(), courseId);
    }
    if (!queueDao.isValidMemberOfClass(context.getClassId(), context.getUserId())) {
      LOGGER.warn("User '{}' is not valid member of class '{}'", context.getUserId(),
          context.getClassId());
    }

    queueInDb();
  }

  private void queueInDb() {
    queueDao.queueRequest(Route0QueueModel.fromRoute0ContextNoMembers(context));
  }

}

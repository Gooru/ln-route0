package org.gooru.route0.processors.doroute0ofcontent;

import org.gooru.route0.infra.services.Route0RequestQueueService;
import org.gooru.route0.infra.services.r0applicable.Route0ApplicableService;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */
class DoRoute0OfContentService {

  private final DBI dbi;
  private DoRoute0OfContentCommand command;
  private DoRoute0OfContentDao dao;
  private static final Logger LOGGER = LoggerFactory.getLogger(DoRoute0OfContentService.class);

  DoRoute0OfContentService(DBI dbi) {
    this.dbi = dbi;
  }

  void doRoute0(DoRoute0OfContentCommand command) {
    this.command = command;
    if (command.getClassId() != null) {
      doRoute0InClass();
    } else {
      doRoute0ForIL();
    }
  }

  private void doRoute0ForIL() {
    if (Route0ApplicableService.build(dbi).isRoute0ApplicableToCourseInIL(command.getCourseId())) {
      if (!route0WasAlreadyDone()) {
        Route0RequestQueueService service = Route0RequestQueueService.build();
        service.enqueue(command.asRoute0Context());
      } else {
        LOGGER.warn("Route0 already done for command: '{}'", command.toString());
      }
    } else {
      LOGGER.warn("Route0 not applicable for command: '{}'", command.toString());
    }
  }

  private void doRoute0InClass() {
    if (Route0ApplicableService.build(dbi).isRoute0ApplicableToClass(command.getClassId())) {
      if (!route0WasAlreadyDone()) {
        Route0RequestQueueService service = Route0RequestQueueService.build();
        service.enqueue(command.asRoute0Context());
      } else {
        LOGGER.warn("Route0 already done for command: '{}'", command.toString());
      }
    } else {
      LOGGER.warn("Route0 not applicable for command: '{}'", command.toString());
    }
  }

  private boolean route0WasAlreadyDone() {
    if (command.getClassId() == null) {
      return fetchDao().route0DoneForUserInIL(command.getUserId(), command.getCourseId());
    }
    return fetchDao()
        .route0DoneForUserInClass(command.getUserId(), command.getCourseId(), command.getClassId());
  }

  private DoRoute0OfContentDao fetchDao() {
    if (dao == null) {
      dao = dbi.onDemand(DoRoute0OfContentDao.class);
    }
    return dao;
  }
}

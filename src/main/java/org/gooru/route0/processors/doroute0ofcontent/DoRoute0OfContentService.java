package org.gooru.route0.processors.doroute0ofcontent;

import org.gooru.route0.infra.services.Route0ApplicableService;
import org.gooru.route0.infra.services.Route0RequestQueueService;
import org.skife.jdbi.v2.DBI;

/**
 * @author ashish.
 */
class DoRoute0OfContentService {

  private final DBI dbi;
  private DoRoute0OfContentCommand command;
  private DoRoute0OfContentDao dao;

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
    if (Route0ApplicableService.isRoute0ApplicableToCourseInIL(command.getCourseId())) {
      fetchDao().resetRoute0ForUserInIL(command.getUserId(), command.getCourseId());
      Route0RequestQueueService service = Route0RequestQueueService.build();
      service.enqueue(command.asRoute0Context());
    }
  }

  private void doRoute0InClass() {
    if (Route0ApplicableService.isRoute0ApplicableToClass(command.getClassId())) {
      fetchDao().resetRoute0ForUserInClass(command.getUserId(), command.getCourseId(),
          command.getClassId());
      Route0RequestQueueService service = Route0RequestQueueService.build();
      service.enqueue(command.asRoute0Context());
    }
  }

  private DoRoute0OfContentDao fetchDao() {
    if (dao == null) {
      dao = dbi.onDemand(DoRoute0OfContentDao.class);
    }
    return dao;
  }
}

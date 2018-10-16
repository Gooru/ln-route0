package org.gooru.route0.processors.doroute0ofcontent;

import org.gooru.route0.infra.services.Route0ApplicableService;
import org.gooru.route0.infra.services.Route0RequestQueueService;

/**
 * @author ashish.
 */
class DoRoute0OfContentService {

  private DoRoute0OfContentCommand command;

  DoRoute0OfContentService() {
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
      Route0RequestQueueService service = Route0RequestQueueService.build();
      service.enqueue(command.asRoute0Context());
    }
  }

  private void doRoute0InClass() {
    if (Route0ApplicableService.isRoute0ApplicableToClass(command.getClassId())) {
      Route0RequestQueueService service = Route0RequestQueueService.build();
      service.enqueue(command.asRoute0Context());
    }
  }
}

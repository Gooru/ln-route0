package org.gooru.route0.infra.services;

import org.gooru.route0.infra.jdbi.DBICreator;

/**
 * This service will be used once at the start of application. This service will mark all the record
 * in DB queue which are marked as either dispatched or in process, to queued state. This is to
 * handle cases where some records were being processed while the system shut down, and thus those
 * record need to be reprocessed.
 *
 * @author ashish.
 */
public interface Route0QueueInitializerService {

  void initializeQueue();

  static Route0QueueInitializerService build() {
    return new Route0QueueInitializerServiceImpl(DBICreator.getDbiForDefaultDS());
  }
}

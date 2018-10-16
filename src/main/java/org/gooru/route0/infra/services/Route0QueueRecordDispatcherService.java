package org.gooru.route0.infra.services;

import org.gooru.route0.infra.data.Route0QueueModel;
import org.gooru.route0.infra.jdbi.DBICreator;

/**
 * This service is responsible to read the record from the queue and return to caller. Caller needs
 * to decides as to what they want to do with the record. This means updating the status of record
 * to indicate that they are being processed. However, fetching the record using this service will
 * mark the record for being dispatched.
 *
 * @author ashish.
 */
public interface Route0QueueRecordDispatcherService {

  Route0QueueModel getNextRecordToDispatch();

  static Route0QueueRecordDispatcherService build() {
    return new Route0QueueRecordDispatcherServiceImpl(DBICreator.getDbiForDefaultDS());
  }
}

package org.gooru.route0.infra.services;

import org.gooru.route0.infra.data.Route0Context;
import org.gooru.route0.infra.jdbi.DBICreator;

/**
 * @author ashish.
 */
public interface Route0RequestQueueService {

  void enqueue(Route0Context context);

  static Route0RequestQueueService build() {
    return new Route0RequestQueueServiceImpl(DBICreator.getDbiForDefaultDS());
  }
}

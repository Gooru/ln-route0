package org.gooru.route0.infra.services;

import org.skife.jdbi.v2.DBI;

/**
 * @author ashish.
 */
class Route0QueueInitializerServiceImpl implements Route0QueueInitializerService {

  private final DBI dbi;

  Route0QueueInitializerServiceImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public void initializeQueue() {
    Route0RequestQueueDao dao = dbi.onDemand(Route0RequestQueueDao.class);
    dao.initializeQueueStatus();
  }
}

package org.gooru.route0.infra.services;

import org.gooru.route0.infra.data.Route0QueueModel;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */
class Route0QueueRecordDispatcherServiceImpl implements Route0QueueRecordDispatcherService {

    private final DBI dbi;
    private static final Logger LOGGER = LoggerFactory.getLogger(Route0QueueRecordDispatcherService.class);

    Route0QueueRecordDispatcherServiceImpl(DBI dbi) {
        this.dbi = dbi;
    }

    @Override
    public Route0QueueModel getNextRecordToDispatch() {
        Route0RequestQueueDao dao = dbi.onDemand(Route0RequestQueueDao.class);
        Route0QueueModel model = dao.getNextDispatchableModel();
        if (model == null) {
            LOGGER.trace("No records present for processing");
            model = Route0QueueModel.createNonPersistedEmptyModel();
        } else {
            dao.setQueuedRecordStatusAsDispatched(model.getId());
        }
        return model;
    }
}

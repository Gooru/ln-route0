package org.gooru.route0.infra.services;

import org.gooru.route0.infra.data.Route0QueueModel;
import org.gooru.route0.infra.jdbi.DBICreator;

/**
 * @author ashish.
 */
public interface Route0ProcessingService {

    void doRoute0(Route0QueueModel model);

    static Route0ProcessingService build() {
        return new Route0ProcessingServiceImpl(DBICreator.getDbiForDefaultDS());
    }
}

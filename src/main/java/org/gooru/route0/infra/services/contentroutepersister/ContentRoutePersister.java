package org.gooru.route0.infra.services.contentroutepersister;

import org.gooru.route0.infra.jdbi.DBICreator;
import org.gooru.route0.infra.services.competencyroutecalculator.CompetencyRouteModel;
import org.gooru.route0.infra.services.competencyroutetocontentroutemapper.ContentRouteModel;
import org.skife.jdbi.v2.DBI;

/**
 * @author ashish.
 */
public interface ContentRoutePersister {

    void persist(ContentRouteInfo info, ContentRouteModel model, CompetencyRouteModel competencyRouteModel);

    static ContentRoutePersister builder() {
        return new ContentRoutePersisterService(DBICreator.getDbiForDefaultDS());
    }

    static ContentRoutePersister builder(DBI dbi) {
        return new ContentRoutePersisterService(dbi);
    }

}

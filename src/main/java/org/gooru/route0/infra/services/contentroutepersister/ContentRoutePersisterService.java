package org.gooru.route0.infra.services.contentroutepersister;

import java.util.List;

import org.gooru.route0.infra.data.Route0StatusValues;
import org.gooru.route0.infra.data.UserRoute0ContentDetailModel;
import org.gooru.route0.infra.services.competencyroutetocontentroutemapper.ContentRouteModel;
import org.skife.jdbi.v2.DBI;

import io.vertx.core.json.JsonObject;

/**
 * @author ashish.
 */
class ContentRoutePersisterService implements ContentRoutePersister {

    private final DBI dbi;
    private ContentRoutePersisterDao dao;
    private long contentRouteId;
    private ContentRouteInfo info;
    private ContentRouteModel model;

    ContentRoutePersisterService(DBI dbi) {
        this.dbi = dbi;
    }

    @Override
    public void persist(ContentRouteInfo info, ContentRouteModel model) {
        this.info = info;
        this.model = model;

        persistRouteInfo();
        persistRouteDetails();
    }

    private void persistRouteDetails() {
        List<UserRoute0ContentDetailModel> detailModels =
            new UserRoute0ContentDetailModelsBuilder().build(model, contentRouteId);
        getPersisterDao().persistRoute0ContentDetails(detailModels);
    }

    private void persistRouteInfo() {
        JsonObject route0Content = model.toJson();
        if (route0Content.isEmpty()) {
            contentRouteId = getPersisterDao()
                .persistRoute0Content(info, Route0StatusValues.getStatusNa(), model.toJson().toString());
        } else {
            contentRouteId = getPersisterDao()
                .persistRoute0Content(info, Route0StatusValues.getStatusPending(), model.toJson().toString());
        }
    }

    private ContentRoutePersisterDao getPersisterDao() {
        if (dao == null) {
            dao = dbi.onDemand(ContentRoutePersisterDao.class);
        }
        return dao;
    }
}

package org.gooru.route0.infra.services.contentroutepersister;

import java.util.List;

import org.gooru.route0.infra.data.Route0StatusValues;
import org.gooru.route0.infra.data.UserRoute0ContentDetailModel;
import org.gooru.route0.infra.services.competencyroutecalculator.CompetencyRouteModel;
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
    private ContentRouteModel contentRouteModel;
    private CompetencyRouteModel competencyRouteModel;
    private static String emptyJson = new JsonObject().toString();
    private List<UserRoute0ContentDetailModel> detailModels;
    private ContentRouteModel contentRouteModelWithPathIds;

    ContentRoutePersisterService(DBI dbi) {
        this.dbi = dbi;
    }

    @Override
    public void persist(ContentRouteInfo info, ContentRouteModel model, CompetencyRouteModel competencyRouteModel) {
        this.info = info;
        this.contentRouteModel = model;
        this.competencyRouteModel = competencyRouteModel;

        persistRouteInfo();
        createRouteDetails();
        persistRouteDetails();
        fetchRoute0DetailsWithId();
        if (!isRoute0Empty(contentRouteModel.toJson())) {
            hydrateDetailsWithPathId();
            updateRouteInfoForContentRouteWithPath();
        }
    }

    private void updateRouteInfoForContentRouteWithPath() {
        getPersisterDao()
            .updateRouteInfoForRoute0Content(contentRouteId, contentRouteModelWithPathIds.toJson().toString());
    }

    private void hydrateDetailsWithPathId() {
        contentRouteModelWithPathIds = contentRouteModel.hydrateWithContentRouteDetail(detailModels);
    }

    private void fetchRoute0DetailsWithId() {
        detailModels = getPersisterDao().fetchDetailModelsForSpecifiedRoute0(contentRouteId);
    }

    private void persistRouteDetails() {
        getPersisterDao().persistRoute0ContentDetails(detailModels);
    }

    private void createRouteDetails() {
        detailModels = new UserRoute0ContentDetailModelsBuilder().build(contentRouteModel, contentRouteId);
    }

    private void persistRouteInfo() {
        JsonObject route0Content = contentRouteModel.toJson();
        if (isRoute0Empty(route0Content)) {
            contentRouteId =
                getPersisterDao().persistRoute0Content(info, Route0StatusValues.getStatusNa(), emptyJson, emptyJson);
        } else {
            contentRouteId = getPersisterDao()
                .persistRoute0Content(info, Route0StatusValues.getStatusPending(), route0Content.toString(),
                    competencyRouteModel.toJson().toString());
        }
    }

    private boolean isRoute0Empty(JsonObject route0Content) {
        return route0Content.isEmpty() || detailModels == null || detailModels.isEmpty();
    }

    private ContentRoutePersisterDao getPersisterDao() {
        if (dao == null) {
            dao = dbi.onDemand(ContentRoutePersisterDao.class);
        }
        return dao;
    }
}

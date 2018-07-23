package org.gooru.route0.infra.services.contentroutepersister;

import java.util.List;

import org.gooru.route0.infra.data.Route0StatusValues;
import org.gooru.route0.infra.data.UserRoute0ContentDetailModel;
import org.gooru.route0.infra.services.competencyroutecalculator.CompetencyRouteModel;
import org.gooru.route0.infra.services.competencyroutetocontentroutemapper.ContentRouteModel;
import org.gooru.route0.infra.services.competencyroutetocontentroutemapper.UnitModel;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

/**
 * @author ashish.
 */
class ContentRoutePersisterService implements ContentRoutePersister {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentRoutePersisterService.class);
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

        LOGGER.debug("Persisting route info, first pass");
        persistRouteInfo();
        LOGGER.debug("Creating route details object");
        createRouteDetails();
        LOGGER.debug("Persisting route details object");
        persistRouteDetails();
        LOGGER.debug("Fetching route details to get path ids");
        fetchRoute0DetailsWithId();
        if (!isRoute0Empty()) {
            LOGGER.debug("Route0 is not empty, will hydrate with path ids");
            hydrateDetailsWithPathId();
            LOGGER.debug("Will update the route0Content for route info with path ids");
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
        if (isRoute0Empty()) {
            contentRouteId =
                getPersisterDao().persistRoute0Content(info, Route0StatusValues.getStatusNa(), emptyJson, emptyJson);
        } else {
            contentRouteId = getPersisterDao()
                .persistRoute0Content(info, Route0StatusValues.getStatusPending(), route0Content.toString(),
                    competencyRouteModel.toJson().toString());
        }
    }

    private boolean isRoute0Empty() {
        List<UnitModel> unitsOrdered = contentRouteModel.getUnitsOrdered();
        return unitsOrdered == null || unitsOrdered.isEmpty();
    }

    private ContentRoutePersisterDao getPersisterDao() {
        if (dao == null) {
            dao = dbi.onDemand(ContentRoutePersisterDao.class);
        }
        return dao;
    }
}

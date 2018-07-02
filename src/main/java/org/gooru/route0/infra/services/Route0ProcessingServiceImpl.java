package org.gooru.route0.infra.services;

import org.gooru.route0.infra.data.Route0QueueModel;
import org.gooru.route0.infra.data.RouteCalculatorModel;
import org.gooru.route0.infra.services.competencyroutecalculator.CompetencyRouteCalculator;
import org.gooru.route0.infra.services.competencyroutecalculator.CompetencyRouteModel;
import org.gooru.route0.infra.services.competencyroutetocontentroutemapper.CompetencyRouteToContentRouteMapper;
import org.gooru.route0.infra.services.competencyroutetocontentroutemapper.ContentRouteModel;
import org.gooru.route0.infra.services.contentroutepersister.ContentRouteInfo;
import org.gooru.route0.infra.services.contentroutepersister.ContentRoutePersister;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ashish.
 */
class Route0ProcessingServiceImpl implements Route0ProcessingService {
    private final DBI dbi;
    private Route0QueueModel model;
    private Route0RequestQueueDao dao;
    private static final Logger LOGGER = LoggerFactory.getLogger(Route0ProcessingService.class);

    Route0ProcessingServiceImpl(DBI dbi) {
        this.dbi = dbi;
    }

    @Override
    public void doRoute0(Route0QueueModel model) {
        this.model = model;
        this.dao = dbi.onDemand(Route0RequestQueueDao.class);
        if (!recordIsStillInDispatchedState()) {
            LOGGER.debug("Record is not found to be in dispatched state");
            return;
        }
        if (route0WasAlreadyDone()) {
            LOGGER.debug("Route0 was already done");
            dequeueRecord();
            return;
        }
        processRecord();
    }

    private void dequeueRecord() {
        LOGGER.debug("Dequeueing record");
        dao.dequeueRecord(model.getId());
    }

    private void processRecord() {
        LOGGER.debug("Doing real processing");
        try {

            CompetencyRouteCalculator competencyRouteCalculator = CompetencyRouteCalculator.build();
            CompetencyRouteModel competencyRouteModel =
                competencyRouteCalculator.calculateCompetencyRoute(RouteCalculatorModel.fromRoute0QueueModel(model));
            // TODO: Persist competencyRoute here as new requirement

            CompetencyRouteToContentRouteMapper competencyRouteToContentRouteMapper =
                CompetencyRouteToContentRouteMapper.build();
            ContentRouteModel contentRouteModel = competencyRouteToContentRouteMapper
                .calculateContentRouteForCompetencyRoute(model.getUserId(), competencyRouteModel);

            ContentRoutePersister persister = ContentRoutePersister.builder();
            ContentRouteInfo info = createContentRouteInfo();
            persister.persist(info, contentRouteModel);

        } catch (Exception e) {
            LOGGER.warn("Not able to calculate route0 for model: '{}'. Will dequeue record.", e);
        }
        dequeueRecord();
    }

    private ContentRouteInfo createContentRouteInfo() {
        return new ContentRouteInfo(model.getUserId(), model.getCourseId(), model.getClassId());
    }

    private boolean route0WasAlreadyDone() {
        if (model.getClassId() == null) {
            return dao.route0DoneForUserInIL(model);
        }
        return dao.route0DoneForUserInClass(model);
    }

    private boolean recordIsStillInDispatchedState() {
        return dao.isQueuedRecordStillDispatched(model.getId());
    }
}

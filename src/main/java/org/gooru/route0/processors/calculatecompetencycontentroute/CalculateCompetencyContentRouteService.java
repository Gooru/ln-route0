package org.gooru.route0.processors.calculatecompetencycontentroute;

import org.gooru.route0.infra.services.competencyroutecalculator.CompetencyRouteCalculator;
import org.gooru.route0.infra.services.competencyroutecalculator.CompetencyRouteModel;
import org.gooru.route0.infra.services.competencyroutetocontentroutemapper.CompetencyRouteToContentRouteMapper;
import org.gooru.route0.infra.services.competencyroutetocontentroutemapper.ContentRouteModel;
import org.skife.jdbi.v2.DBI;

import io.vertx.core.json.JsonObject;

/**
 * @author ashish.
 */
class CalculateCompetencyContentRouteService {

    private final DBI dbiForDefaultDS;
    private final DBI dbiForDsdbDS;

    CalculateCompetencyContentRouteService(DBI dbiForDefaultDS, DBI dbiForDsdbDS) {

        this.dbiForDefaultDS = dbiForDefaultDS;
        this.dbiForDsdbDS = dbiForDsdbDS;
    }

    JsonObject calculateCompetencyRoute(CalculateCompetencyContentRouteCommand command) {
        CompetencyRouteModel competencyRouteModel =
            CompetencyRouteCalculator.build().calculateCompetencyRoute(command.asRouteCalculatorModel());

        ContentRouteModel contentRouteModel = CompetencyRouteToContentRouteMapper.build()
            .calculateContentRouteForCompetencyRoute(command.getUserId(), competencyRouteModel);

        return contentRouteModel.toJson();
    }
}

package org.gooru.route0.processors.calculatecompetencyroute;

import org.gooru.route0.infra.services.competencyroutecalculator.CompetencyRouteCalculator;
import org.gooru.route0.infra.services.competencyroutecalculator.CompetencyRouteModel;
import org.skife.jdbi.v2.DBI;

import io.vertx.core.json.JsonObject;

/**
 * @author ashish.
 */
class CalculateCompetencyRouteService {

    private final DBI dbiForDefaultDS;
    private final DBI dbiForDsdbDS;

    CalculateCompetencyRouteService(DBI dbiForDefaultDS, DBI dbiForDsdbDS) {

        this.dbiForDefaultDS = dbiForDefaultDS;
        this.dbiForDsdbDS = dbiForDsdbDS;
    }

    JsonObject calculateCompetencyRoute(CalculateCompetencyRouteCommand command) {
        CompetencyRouteCalculator competencyRouteCalculator = CompetencyRouteCalculator.build();
        CompetencyRouteModel competencyRouteModel =
            competencyRouteCalculator.calculateCompetencyRoute(command.asRouteCalculatorModel());
        return competencyRouteModel.toJson();
    }
}

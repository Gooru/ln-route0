package org.gooru.route0.processors.calculatecompetencycontentroute;

import org.gooru.route0.infra.services.competencyroutecalculator.CompetencyRouteCalculator;
import org.gooru.route0.infra.services.competencyroutecalculator.CompetencyRouteModel;
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
        CompetencyRouteCalculator competencyRouteCalculator = CompetencyRouteCalculator.build();
        CompetencyRouteModel competencyRouteModel =
            competencyRouteCalculator.calculateCompetencyRoute(command.asRouteCalculatorModel());
        // TODO: Now that we have competency model, we need to map it to Content Model
        return competencyRouteModel.toJson();
    }
}

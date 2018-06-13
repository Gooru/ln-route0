package org.gooru.route0.infra.services.competencyroutecalculator;

import org.gooru.route0.infra.data.RouteCalculatorModel;
import org.gooru.route0.infra.jdbi.DBICreator;

/**
 * @author ashish.
 */
public interface CompetencyRouteCalculator {

    CompetencyRouteModel calculateCompetencyRoute(RouteCalculatorModel model);

    static CompetencyRouteCalculator build() {
        return new CompetencyRouteCalculatorService(DBICreator.getDbiForDefaultDS(), DBICreator.getDbiForDsdbDS());
    }
}

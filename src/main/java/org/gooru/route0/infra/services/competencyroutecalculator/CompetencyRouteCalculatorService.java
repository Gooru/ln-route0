package org.gooru.route0.infra.services.competencyroutecalculator;

import org.gooru.route0.infra.data.RouteCalculatorModel;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */
class CompetencyRouteCalculatorService implements CompetencyRouteCalculator {

    private final DBI defaultDS;
    private final DBI dsdbDS;
    private RouteCalculatorModel model;
    private static final Logger LOGGER = LoggerFactory.getLogger(CompetencyRouteCalculator.class);

    CompetencyRouteCalculatorService(DBI defaultDS, DBI dsdbDS) {
        this.defaultDS = defaultDS;
        this.dsdbDS = dsdbDS;
    }

    @Override
    public CompetencyRouteModel calculateCompetencyRoute(RouteCalculatorModel model) {
        this.model = model;
        String subjectCode = SubjectInferer.build().inferSubjectForCourse(model.getCourseId());
        if (subjectCode == null) {
            LOGGER.warn("Not able to find subject code for specified course '{}'", model.getCourseId());
            return null;
        }
        return null;
    }
}

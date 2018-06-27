package org.gooru.route0.infra.services.competencyroutetocontentroutemapper;

import org.gooru.route0.infra.services.competencyroutecalculator.CompetencyRouteModel;

/**
 * @author ashish.
 */
public interface CompetencyRouteToContentRouteMapper {

    ContentRouteModel calculateContentRouteForCompetencyRoute(CompetencyRouteModel competencyRouteModel);

}

package org.gooru.route0.infra.services.competencyroutetocontentroutemapper;

import java.util.UUID;
import org.gooru.route0.infra.jdbi.DBICreator;
import org.gooru.route0.infra.services.competencyroutecalculator.CompetencyRouteModel;
import org.skife.jdbi.v2.DBI;

/**
 * @author ashish.
 */
public interface CompetencyRouteToContentRouteMapper {

  ContentRouteModel calculateContentRouteForCompetencyRoute(UUID userId,
      CompetencyRouteModel competencyRouteModel);

  static CompetencyRouteToContentRouteMapper build() {
    return new CompetencyRouteToContentRouteMapperService(DBICreator.getDbiForDefaultDS());
  }

  static CompetencyRouteToContentRouteMapper build(DBI dbi) {
    return new CompetencyRouteToContentRouteMapperService(dbi);
  }

}

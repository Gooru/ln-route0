package org.gooru.route0.infra.services.competencyroutetocontentroutemapper;

import java.util.UUID;
import org.gooru.route0.infra.services.competencyroutecalculator.CompetencyRouteModel;
import org.skife.jdbi.v2.DBI;

/**
 * @author ashish.
 */
class CompetencyRouteToContentRouteMapperService implements CompetencyRouteToContentRouteMapper {

  private final DBI dbi;
  private UUID userId;
  private Integer primaryLanguage;
  private CompetencyRouteModel competencyRouteModel;

  CompetencyRouteToContentRouteMapperService(DBI dbi) {

    this.dbi = dbi;
  }

  @Override
  public ContentRouteModel calculateContentRouteForCompetencyRoute(UUID userId,
      CompetencyRouteModel competencyRouteModel, Integer primaryLanguage) {
    this.userId = userId;
    this.primaryLanguage = primaryLanguage;
    this.competencyRouteModel = competencyRouteModel;
    return new ContentRouteModelBuilder().build(userId, competencyRouteModel, primaryLanguage);
  }

}

package org.gooru.route0.infra.services.competencyroutecalculator;

import java.util.List;
import java.util.UUID;

import org.gooru.route0.infra.jdbi.DBICreator;
import org.skife.jdbi.v2.DBI;

/**
 * @author ashish.
 */
interface CompetencyFetcher {
    List<String> fetchCompetenciesForCourse(UUID courseId);

    static CompetencyFetcher build() {
        return new CompetencyFetcherImpl(DBICreator.getDbiForDefaultDS());
    }

    static CompetencyFetcher build(DBI dbi) {
        return new CompetencyFetcherImpl(dbi);
    }
}

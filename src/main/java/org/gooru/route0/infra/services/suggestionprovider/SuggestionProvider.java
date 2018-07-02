package org.gooru.route0.infra.services.suggestionprovider;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gooru.route0.infra.data.competency.CompetencyCode;
import org.gooru.route0.infra.jdbi.DBICreator;
import org.skife.jdbi.v2.DBI;

/**
 * Fetch the suggestions from competency_content_map, personalized for specified user for all specified competencies.
 *
 * With current implementation, no personalization is applied though weight is used for ordering. The list should
 * contain one collection and one assessment for each competency at most, in that order.
 *
 * @author ashish.
 */
public interface SuggestionProvider {

    Map<CompetencyCode, List<SuggestedItem>> suggest(UUID userId, List<CompetencyCode> competencies);

    static SuggestionProvider build(DBI dbi) {
        return new SuggestionProviderService(dbi);
    }

    static SuggestionProvider build() {
        return new SuggestionProviderService(DBICreator.getDbiForDefaultDS());
    }
}

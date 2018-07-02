package org.gooru.route0.infra.services.suggestionprovider;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gooru.route0.infra.data.competency.CompetencyCode;
import org.skife.jdbi.v2.DBI;

/**
 * @author ashish.
 */
class SuggestionProviderService implements SuggestionProvider {

    private final DBI dbi;

    SuggestionProviderService(DBI dbi) {
        this.dbi = dbi;
    }

    @Override
    public Map<CompetencyCode, List<SuggestedItem>> suggest(UUID userId, List<CompetencyCode> competencies) {
        //TODO: Implement this
        throw new AssertionError("Not implemented");
    }
}

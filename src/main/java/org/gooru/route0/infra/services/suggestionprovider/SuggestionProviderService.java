package org.gooru.route0.infra.services.suggestionprovider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gooru.route0.infra.data.competency.CompetencyCode;
import org.gooru.route0.infra.utils.CollectionUtils;
import org.skife.jdbi.v2.DBI;

/**
 * @author ashish.
 */
class SuggestionProviderService implements SuggestionProvider {

    private final DBI dbi;
    private SuggestionProviderDao suggestionProviderDao;

    SuggestionProviderService(DBI dbi) {
        this.dbi = dbi;
    }

    @Override
    public Map<CompetencyCode, List<SuggestedItem>> suggest(UUID userId, List<CompetencyCode> competencies) {
        if (competencies == null || competencies.isEmpty()) {
            return Collections.emptyMap();
        }
        List<String> competencyList = new ArrayList<>(competencies.size());

        for (CompetencyCode competencyCode : competencies) {
            competencyList.add(competencyCode.getCode());
        }

        List<SuggestedItem> suggestedItems = getSuggestionProvideDao()
            .fetchSuggestionsForCompetencies(CollectionUtils.convertToSqlArrayOfString(competencyList));

        return transformSuggestedItemsListToMap(suggestedItems);
    }

    private Map<CompetencyCode, List<SuggestedItem>> transformSuggestedItemsListToMap(
        List<SuggestedItem> suggestedItems) {
        Map<CompetencyCode, List<SuggestedItem>> result = new HashMap<>(suggestedItems.size());

        for (SuggestedItem suggestedItem : suggestedItems) {
            List<SuggestedItem> items = result.get(suggestedItem.getCompetencyCode());
            if (items == null) {
                items = new ArrayList<>();
                items.add(suggestedItem);
                result.put(suggestedItem.getCompetencyCode(), items);
            } else {
                items.add(suggestedItem);
            }
        }

        return result;
    }

    private SuggestionProviderDao getSuggestionProvideDao() {
        if (suggestionProviderDao == null) {
            suggestionProviderDao = dbi.onDemand(SuggestionProviderDao.class);
        }
        return suggestionProviderDao;
    }
}

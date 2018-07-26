package org.gooru.route0.infra.services.suggestionprovider;

import java.util.List;

import org.gooru.route0.infra.jdbi.PGArray;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

/**
 * @author ashish.
 */
interface SuggestionProviderDao {

    @Mapper(SuggestedItemMapper.class)
    @SqlQuery("select filt.competency, filt.content_type, filt.item_id from (select competency, content_type,  "
                  + " item_id, row_number() over (partition by competency, content_type  order by weight desc, "
                  + " content_type desc) from  competency_content_map where competency = any(:competencies)) filt "
                  + " where  row_number < 2")
    List<SuggestedItem> fetchSuggestionsForCompetencies(@Bind("competencies") PGArray<String> competencies);

}

package org.gooru.route0.infra.services.suggestionprovider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.gooru.route0.infra.data.competency.CompetencyCode;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 * @author ashish.
 */
public class SuggestedItemMapper implements ResultSetMapper<SuggestedItem> {

  @Override
  public SuggestedItem map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String competency = r.getString(MapperFields.TX_COMPETENCY);
    String contentType = r.getString(MapperFields.TX_CONTENT_TYPE);
    String item = r.getString(MapperFields.TX_ITEM);
    UUID itemId = item == null ? null : UUID.fromString(item);
    return new SuggestedItemImpl(new CompetencyCode(competency), itemId,
        ItemType.builder(contentType));
  }

  private static class MapperFields {

    private static final String TX_COMPETENCY = "competency";
    private static final String TX_CONTENT_TYPE = "content_type";
    private static final String TX_ITEM = "item_id";
  }
}

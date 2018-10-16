package org.gooru.route0.infra.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.gooru.route0.infra.utils.CollectionUtils;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 * @author ashish.
 */
class ContentFetcherServiceImpl implements ContentFetcherService {

  private final DBI dbi;

  ContentFetcherServiceImpl(DBI dbi) {

    this.dbi = dbi;
  }

  @Override
  public Map<String, String> fetchCollectionIdTitleMap(List<UUID> collectionIds) {

    ContentFetcherDao dao = dbi.onDemand(ContentFetcherDao.class);
    List<IdTitleHolder> idTitleHolders =
        dao.fetchCollectionIdAndTitle(
            CollectionUtils.convertFromListUUIDToSqlArrayOfUUID(collectionIds));

    if (idTitleHolders == null || idTitleHolders.isEmpty()) {
      return Collections.emptyMap();
    }

    Map<String, String> result = new HashMap<>(idTitleHolders.size());
    for (IdTitleHolder idTitleHolder : idTitleHolders) {
      result.put(idTitleHolder.id, idTitleHolder.title);
    }
    return result;
  }

  public static class IdTitleHolder {

    private String id;
    private String title;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }
  }

  public static class IdTitleHolderMapper implements ResultSetMapper<IdTitleHolder> {

    @Override
    public IdTitleHolder map(int index, ResultSet r, StatementContext ctx) throws SQLException {
      IdTitleHolder result = new IdTitleHolder();
      result.setId(r.getString("id"));
      result.setTitle(r.getString("title"));
      return result;
    }
  }

}

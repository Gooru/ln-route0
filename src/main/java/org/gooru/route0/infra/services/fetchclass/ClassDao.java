package org.gooru.route0.infra.services.fetchclass;

import java.util.UUID;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

/**
 * @author renuka
 */
interface ClassDao {

  @SqlQuery("select primary_language from class where id = :classId and is_deleted = false")
  Integer fetchPrimaryLanguageOfClass(@Bind("classId") UUID classId);

}

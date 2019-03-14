package org.gooru.route0.infra.services.fetchclass;

import java.util.UUID;
import org.gooru.route0.infra.jdbi.DBICreator;
import org.skife.jdbi.v2.DBI;

/**
 * @author renuka.
 */

public interface ClassService {
  
  static ClassService build(DBI dbi) {
    return new ClassServiceImpl(dbi);
  }

  static ClassService build() {
    return new ClassServiceImpl(DBICreator.getDbiForDefaultDS());
  }

  Integer fetchPrimaryLanguageOfClass(UUID classId);
}

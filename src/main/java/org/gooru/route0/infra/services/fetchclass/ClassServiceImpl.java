package org.gooru.route0.infra.services.fetchclass;

import java.util.UUID;

import org.skife.jdbi.v2.DBI;

/**
 * @author renuka.
 */

class ClassServiceImpl implements ClassService {

  private final DBI dbi;
  private ClassDao dao;

  ClassServiceImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public Integer fetchPrimaryLanguageOfClass(UUID classId) {
    return fetchDao().fetchPrimaryLanguageOfClass(classId);
  }

  private ClassDao fetchDao() {
    if (dao == null) {
      dao = dbi.onDemand(ClassDao.class);
    }
    return dao;
  }
}

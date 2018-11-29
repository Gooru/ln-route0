package org.gooru.route0.infra.services;

import org.gooru.route0.infra.jdbi.DBICreator;
import org.skife.jdbi.v2.DBI;

/**
 * @author ashish.
 */
public class ContentFetcherServiceBuilder {

  public static ContentFetcherService build() {
    return new ContentFetcherServiceImpl(DBICreator.getDbiForDefaultDS());
  }

  public static ContentFetcherService build(DBI dbi) {
    return new ContentFetcherServiceImpl(dbi);
  }
}

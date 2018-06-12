package org.gooru.route0;

import org.gooru.route0.infra.jdbi.PostgresIntegerArrayArgumentFactory;
import org.gooru.route0.infra.jdbi.PostgresStringArrayArgumentFactory;
import org.gooru.route0.infra.jdbi.PostgresUUIDArrayArgumentFactory;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

/**
 * @author ashish.
 */
public class DBITestHelper {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/nucleus";
    private static final String DB_USER = "nucleus";

    public Handle getHandle() {
        Handle handle = DBI.open(DB_URL, DB_USER, DB_USER);
        handle.registerArgumentFactory(new PostgresIntegerArrayArgumentFactory());
        handle.registerArgumentFactory(new PostgresStringArrayArgumentFactory());
        handle.registerArgumentFactory(new PostgresUUIDArrayArgumentFactory());
        return handle;
    }

    public DBI getDBI() {
        DBI dbi = new DBI(DB_URL, DB_USER, DB_USER);
        dbi.registerArgumentFactory(new PostgresIntegerArrayArgumentFactory());
        dbi.registerArgumentFactory(new PostgresStringArrayArgumentFactory());
        dbi.registerArgumentFactory(new PostgresUUIDArrayArgumentFactory());
        return dbi;
    }
}

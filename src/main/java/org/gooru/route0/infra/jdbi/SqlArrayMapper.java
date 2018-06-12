package org.gooru.route0.infra.jdbi;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 * Mapper to map the text[] column from Postgres to List<String>
 * <p>
 * Note that this mapper assumes that first column selected is text[] and thus is generic (instead of hard coding the
 * column name)
 *
 * @author ashish.
 */
public class SqlArrayMapper implements ResultSetMapper<List<String>> {

    @Override
    public List<String> map(final int index, final ResultSet resultSet, final StatementContext statementContext)
        throws SQLException {
        Array result = resultSet.getArray(1);
        if (result != null) {
            List<String> originalList = Arrays.asList((String[]) result.getArray());
            return new ArrayList<>(originalList);
        }
        return Collections.emptyList();
    }

}


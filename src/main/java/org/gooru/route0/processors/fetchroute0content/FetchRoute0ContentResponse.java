package org.gooru.route0.processors.fetchroute0content;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import io.vertx.core.json.JsonObject;

/**
 * @author ashish.
 */
public class FetchRoute0ContentResponse {

    private String status;
    private String route0Content;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRoute0Content() {
        return route0Content;
    }

    public void setRoute0Content(String route0Content) {
        this.route0Content = route0Content;
    }

    public JsonObject asJson() {
        return new JsonObject().put("status", status).put("route0Content", new JsonObject(route0Content));
    }

    public static class FetchRoute0ContentResponseMapper implements ResultSetMapper<FetchRoute0ContentResponse> {

        @Override
        public FetchRoute0ContentResponse map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            FetchRoute0ContentResponse response = new FetchRoute0ContentResponse();
            response.setStatus(r.getString(MapperFields.STATUS));
            response.setRoute0Content(r.getString(MapperFields.ROUTE0_CONTENT));
            return response;
        }
    }

    static class MapperFields {
        public static final String STATUS = "status";
        public static final String ROUTE0_CONTENT = "route0_content";
    }
}


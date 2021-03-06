package org.gooru.route0.processors.fetchroute0content;

import io.vertx.core.json.JsonObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import org.gooru.route0.infra.data.Route0StatusValues;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 * @author ashish.
 */
public class FetchRoute0ContentResponse {

  private String status;
  private String route0Content;
  private String userCompetencyRoute;
  private Date createdAt;

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

  public String getUserCompetencyRoute() {
    return userCompetencyRoute;
  }

  public void setUserCompetencyRoute(String userCompetencyRoute) {
    this.userCompetencyRoute = userCompetencyRoute;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public JsonObject asJson() {
    JsonObject result = new JsonObject().put("status", status).put("route0Content",
        new JsonObject(route0Content)).put("createdAt", createdAt.getTime());
    if (Route0StatusValues.isStatusAccepted(status) || Route0StatusValues
        .isStatusNotApplicable(status)) {
      return result;
    }
    result.put("userCompetencyRoute", new JsonObject(userCompetencyRoute));
    return result;
  }

  public static class FetchRoute0ContentResponseMapper implements
      ResultSetMapper<FetchRoute0ContentResponse> {

    @Override
    public FetchRoute0ContentResponse map(int index, ResultSet r, StatementContext ctx)
        throws SQLException {
      FetchRoute0ContentResponse response = new FetchRoute0ContentResponse();
      response.setStatus(r.getString(MapperFields.STATUS));
      response.setRoute0Content(r.getString(MapperFields.ROUTE0_CONTENT));
      response.setUserCompetencyRoute(r.getString(MapperFields.USER_COMPETENCY_ROUTE));
      response.setCreatedAt(r.getDate(MapperFields.CREATED_AT));
      return response;
    }
  }

  static class MapperFields {

    public static final String STATUS = "status";
    public static final String ROUTE0_CONTENT = "route0_content";
    public static final String USER_COMPETENCY_ROUTE = "user_competency_route";
    public static final String CREATED_AT = "created_at";
  }
}


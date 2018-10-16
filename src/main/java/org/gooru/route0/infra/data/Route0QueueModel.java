package org.gooru.route0.infra.data;

import io.vertx.core.json.JsonObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.gooru.route0.infra.utils.UuidUtils;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */
public class Route0QueueModel {

  private Long id;
  private UUID userId;
  private UUID courseId;
  private UUID classId;
  private int priority;
  private int status;

  private static final Logger LOGGER = LoggerFactory.getLogger(Route0QueueModel.class);

  public static final int RQ_STATUS_QUEUED = 0;
  public static final int RQ_STATUS_DISPATCHED = 1;
  public static final int RQ_STATUS_INPROCESS = 2;

  public String toJson() {
    return new JsonObject().put("id", id).put("userId", UuidUtils.uuidToString(userId))
        .put("courseId", UuidUtils.uuidToString(courseId))
        .put("classId", UuidUtils.uuidToString(classId))
        .put("priority", priority).put("status", status).toString();
  }

  public static Route0QueueModel fromJson(String input) {
    JsonObject json = new JsonObject(input);
    Route0QueueModel model = new Route0QueueModel();
    model.priority = json.getInteger("priority");
    model.status = json.getInteger("status");
    model.classId = UuidUtils.convertStringToUuid(json.getString("classId"));
    model.courseId = UuidUtils.convertStringToUuid(json.getString("courseId"));
    model.userId = UuidUtils.convertStringToUuid(json.getString("userId"));
    model.id = json.getLong("id");
    return model;
  }

  public static Route0QueueModel fromRoute0ContextNoMembers(Route0Context context) {
    Route0QueueModel result = new Route0QueueModel();
    result.courseId = context.getCourseId();
    result.classId = context.getClassId();
    result.status = RQ_STATUS_QUEUED;
    result.priority = context.getSource().getOrder();
    return result;
  }

  public static Route0QueueModel createNonPersistedEmptyModel() {
    return new Route0QueueModel();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public UUID getCourseId() {
    return courseId;
  }

  public void setCourseId(UUID courseId) {
    this.courseId = courseId;
  }

  public UUID getClassId() {
    return classId;
  }

  public void setClassId(UUID classId) {
    this.classId = classId;
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public boolean isModelPersisted() {
    return id != null;
  }

  public static class Route0QueueModelMapper implements ResultSetMapper<Route0QueueModel> {

    @Override
    public Route0QueueModel map(final int index, final ResultSet resultSet,
        final StatementContext statementContext)
        throws SQLException {
      Route0QueueModel model = new Route0QueueModel();
      model.setId(resultSet.getLong("id"));
      model.setPriority(resultSet.getInt("priority"));
      model.setStatus(resultSet.getInt("status"));
      model.setUserId(UuidUtils.convertStringToUuid(resultSet.getString("user_id")));
      model.setCourseId(UuidUtils.convertStringToUuid(resultSet.getString("course_id")));
      model.setClassId(UuidUtils.convertStringToUuid(resultSet.getString("class_id")));
      return model;
    }

  }

}

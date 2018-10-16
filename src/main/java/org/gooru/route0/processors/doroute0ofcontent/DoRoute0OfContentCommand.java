package org.gooru.route0.processors.doroute0ofcontent;

import io.vertx.core.json.JsonObject;
import java.util.UUID;
import org.gooru.route0.infra.data.Route0Context;
import org.gooru.route0.infra.utils.UuidUtils;

/**
 * @author ashish.
 */
class DoRoute0OfContentCommand {

  private UUID classId;
  private UUID userId;
  private UUID courseId;

  public UUID getCourseId() {
    return courseId;
  }

  public UUID getClassId() {
    return classId;
  }

  public UUID getUserId() {
    return userId;
  }

  private DoRoute0OfContentCommand() {

  }

  public Route0Context asRoute0Context() {
    return Route0Context.build(userId, courseId, classId);
  }

  static DoRoute0OfContentCommand builder(JsonObject requestBody) {
    DoRoute0OfContentCommand result = new DoRoute0OfContentCommand();
    result.classId = UuidUtils
        .convertStringToUuid(requestBody.getString(CommandAttributes.CLASS_ID));
    result.userId = UuidUtils
        .convertStringToUuid(requestBody.getString(CommandAttributes.USER_ID));
    result.courseId = UuidUtils
        .convertStringToUuid(requestBody.getString(CommandAttributes.COURSE_ID));
    result.validate();
    return result;
  }

  private void validate() {
    if (classId == null && courseId == null) {
      throw new IllegalArgumentException("Both class and course should not be absent");
    }
    if (userId == null) {
      throw new IllegalArgumentException("User id should not be absent");
    }
  }

  final class CommandAttributes {

    static final String COURSE_ID = "courseId";
    static final String CLASS_ID = "classId";
    static final String USER_ID = "userId";

    private CommandAttributes() {
      throw new AssertionError();
    }
  }
}

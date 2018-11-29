package org.gooru.route0.infra.data;

import java.util.UUID;

/**
 * @author ashish.
 */
public class Route0Context {

  private UUID classId;
  private UUID userId;
  private UUID courseId;

  private Route0Context(UUID userId, UUID courseId, UUID classId) {
    this.classId = classId;
    this.userId = userId;
    this.courseId = courseId;
  }

  public UUID getUserId() {
    return userId;
  }

  public UUID getClassId() {
    return classId;
  }

  public UUID getCourseId() {
    return courseId;
  }

  public static Route0Context build(UUID userId, UUID courseId, UUID classId) {
    return new Route0Context(userId, courseId, classId);
  }

  @Override
  public String toString() {
    return "Route0Context{" +
        "classId=" + classId +
        ", userId=" + userId +
        ", courseId=" + courseId +
        '}';
  }
}

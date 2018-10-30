package org.gooru.route0.processors.calculatecompetencyroute;

import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.UUID;
import org.gooru.route0.infra.constants.HttpConstants;
import org.gooru.route0.infra.data.EventBusMessage;
import org.gooru.route0.infra.data.RouteCalculatorModel;
import org.gooru.route0.infra.exceptions.HttpResponseWrapperException;
import org.gooru.route0.infra.utils.UuidUtils;

/**
 * @author ashish.
 */
class CalculateCompetencyRouteCommand {

  private UUID courseId;
  private UUID userId;

  public UUID getCourseId() {
    return courseId;
  }

  public UUID getUserId() {
    return userId;
  }

  public static CalculateCompetencyRouteCommand builder(EventBusMessage input) {
    CalculateCompetencyRouteCommand command = buildFromJsonObject(input.getRequestBody());
    command.validate();
    return command;
  }

  private static CalculateCompetencyRouteCommand buildFromJsonObject(JsonObject request) {
    CalculateCompetencyRouteCommand command = new CalculateCompetencyRouteCommand();
    try {
      command.courseId = validateSingleValuedListAndGetFirstItem(
          UuidUtils.convertToUUIDList(request.getJsonArray(CommandAttributes.COURSE_ID)));
      command.userId = validateSingleValuedListAndGetFirstItem(
          UuidUtils.convertToUUIDList(request.getJsonArray(CommandAttributes.USER_ID)));
      return command;
    } catch (IllegalArgumentException e) {
      throw new HttpResponseWrapperException(HttpConstants.HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  private static <T> T validateSingleValuedListAndGetFirstItem(List<T> inputList) {
    if (inputList == null || inputList.isEmpty()) {
      return null;
    }

    if (inputList.size() > 1) {
      throw new IllegalArgumentException("Single values expected in requested, multiple supplied");
    }

    return inputList.get(0);
  }

  private void validate() {
    if (userId == null) {
      throw new HttpResponseWrapperException(HttpConstants.HttpStatus.BAD_REQUEST,
          "Invalid user id");
    }
    if (courseId == null) {
      throw new HttpResponseWrapperException(HttpConstants.HttpStatus.BAD_REQUEST,
          "Invalid course id");
    }
  }

  RouteCalculatorModel asRouteCalculatorModel() {
    return new RouteCalculatorModel(userId, courseId, null);
  }

  public static final class CommandAttributes {

    static final String COURSE_ID = "courseId";
    static final String USER_ID = "userId";

    private CommandAttributes() {
      throw new AssertionError();
    }
  }

}

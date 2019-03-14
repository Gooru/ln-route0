package org.gooru.route0.processors.calculatecompetencycontentroute;

import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.UUID;
import org.gooru.route0.infra.constants.HttpConstants;
import org.gooru.route0.infra.data.EventBusMessage;
import org.gooru.route0.infra.data.RouteCalculatorModel;
import org.gooru.route0.infra.exceptions.HttpResponseWrapperException;
import org.gooru.route0.infra.utils.CollectionUtils;
import org.gooru.route0.infra.utils.UuidUtils;

/**
 * @author ashish.
 */
class CalculateCompetencyContentRouteCommand {

  private UUID courseId;
  private UUID userId;
  private Integer preferredLanguage;

  public UUID getCourseId() {
    return courseId;
  }

  public UUID getUserId() {
    return userId;
  }
  
  public Integer getPreferredLanguage() {
    return preferredLanguage;
  }

  public static CalculateCompetencyContentRouteCommand builder(EventBusMessage input) {
    CalculateCompetencyContentRouteCommand command = buildFromJsonObject(input.getRequestBody());
    command.validate();
    return command;
  }

  private static CalculateCompetencyContentRouteCommand buildFromJsonObject(JsonObject request) {
    CalculateCompetencyContentRouteCommand command = new CalculateCompetencyContentRouteCommand();
    try {
      command.courseId = validateSingleValuedListAndGetFirstItem(
          UuidUtils.convertToUUIDList(request.getJsonArray(CommandAttributes.COURSE_ID)));
      command.userId = validateSingleValuedListAndGetFirstItem(
          UuidUtils.convertToUUIDList(request.getJsonArray(CommandAttributes.USER_ID)));
      command.preferredLanguage = validateSingleValuedListAndGetFirstItem(CollectionUtils
          .convertToIntegerList(request.getJsonArray(CommandAttributes.PREFERRED_LANGUAGE)));
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
    if (preferredLanguage == null) {
      throw new HttpResponseWrapperException(HttpConstants.HttpStatus.BAD_REQUEST,
          "Invalid preferred language");
    }
  }

  RouteCalculatorModel asRouteCalculatorModel() {
    return new RouteCalculatorModel(userId, courseId, null);
  }

  public static final class CommandAttributes {

    static final String COURSE_ID = "courseId";
    static final String USER_ID = "userId";
    static final String PREFERRED_LANGUAGE = "preferredLanguage";

    private CommandAttributes() {
      throw new AssertionError();
    }
  }

}

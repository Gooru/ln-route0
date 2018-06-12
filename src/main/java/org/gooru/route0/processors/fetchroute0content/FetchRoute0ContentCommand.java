package org.gooru.route0.processors.fetchroute0content;

import java.util.List;
import java.util.UUID;

import org.gooru.route0.infra.constants.HttpConstants;
import org.gooru.route0.infra.data.EventBusMessage;
import org.gooru.route0.infra.data.Route0Context;
import org.gooru.route0.infra.exceptions.HttpResponseWrapperException;
import org.gooru.route0.infra.utils.UuidUtils;

import io.vertx.core.json.JsonObject;

/**
 * @author ashish.
 */
class FetchRoute0ContentCommand {
    private UUID classId;
    private UUID courseId;
    private UUID userId;
    private UUID teacherId;

    UUID getClassId() {
        return classId;
    }

    UUID getCourseId() {
        return courseId;
    }

    UUID getUserId() {
        return userId;
    }

    boolean isTeacherContext() {
        return teacherId != null;
    }

    static FetchRoute0ContentCommand builder(EventBusMessage input) {
        FetchRoute0ContentCommand command = buildFromJsonObject(input.getUserId(), input.getRequestBody());
        command.validate();
        return command;
    }

    private void validate() {
        if (userId == null) {
            throw new HttpResponseWrapperException(HttpConstants.HttpStatus.BAD_REQUEST, "Invalid user id");
        }
        if (courseId == null) {
            throw new HttpResponseWrapperException(HttpConstants.HttpStatus.BAD_REQUEST, "Invalid course id");
        }
    }

    private static FetchRoute0ContentCommand buildFromJsonObject(UUID userId, JsonObject request) {
        FetchRoute0ContentCommand command = new FetchRoute0ContentCommand();
        try {
            command.classId = validateSingleValuedListAndGetFirstItem(
                UuidUtils.convertToUUIDListIgnoreInvalidItems(request.getJsonArray(CommandAttributes.CLASS_ID)));
            command.courseId = validateSingleValuedListAndGetFirstItem(
                UuidUtils.convertToUUIDList(request.getJsonArray(CommandAttributes.COURSE_ID)));
            UUID userInRequestBody = validateSingleValuedListAndGetFirstItem(
                UuidUtils.convertToUUIDList(request.getJsonArray(CommandAttributes.USER_ID)));
            if (userInRequestBody != null) {
                command.userId = userInRequestBody;
                command.teacherId = userId;
            } else {
                command.userId = userId;
                command.teacherId = null;
            }
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

    FetchRoute0ContentCommandBean asBean() {
        FetchRoute0ContentCommandBean bean = new FetchRoute0ContentCommandBean();
        bean.setClassId(classId);
        bean.setCourseId(courseId);
        bean.setUserId(userId);
        bean.setTeacherId(teacherId);
        return bean;
    }

    Route0Context asRoute0Context() {
        return Route0Context.buildForOOB(classId, courseId, userId);
    }

    public static final class FetchRoute0ContentCommandBean {
        private UUID classId;
        private UUID courseId;
        private UUID userId;
        private UUID teacherId;

        public UUID getUserId() {
            return userId;
        }

        public void setUserId(UUID userId) {
            this.userId = userId;
        }

        public UUID getClassId() {
            return classId;
        }

        public void setClassId(UUID classId) {
            this.classId = classId;
        }

        public UUID getCourseId() {
            return courseId;
        }

        public void setCourseId(UUID courseId) {
            this.courseId = courseId;
        }

        public UUID getTeacherId() {
            return teacherId;
        }

        public void setTeacherId(UUID teacherId) {
            this.teacherId = teacherId;
        }
    }

    public static final class CommandAttributes {

        static final String CLASS_ID = "classId";
        static final String COURSE_ID = "courseId";
        static final String USER_ID = "userId";

        private CommandAttributes() {
            throw new AssertionError();
        }
    }

}

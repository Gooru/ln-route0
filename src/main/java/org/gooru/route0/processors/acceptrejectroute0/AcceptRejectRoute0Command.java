package org.gooru.route0.processors.acceptrejectroute0;

import java.util.UUID;

import org.gooru.route0.infra.constants.HttpConstants;
import org.gooru.route0.infra.data.EventBusMessage;
import org.gooru.route0.infra.exceptions.HttpResponseWrapperException;
import org.gooru.route0.infra.utils.UuidUtils;

import io.vertx.core.json.JsonObject;

/**
 * @author ashish.
 */
class AcceptRejectRoute0Command {
    private UUID courseId;
    private UUID userId;
    private UUID classId;
    private String status;

    public UUID getCourseId() {
        return courseId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getClassId() {
        return classId;
    }

    public String getStatus() {
        return status;
    }

    public static AcceptRejectRoute0Command builder(EventBusMessage input) {
        AcceptRejectRoute0Command command = buildFromJsonObject(input.getUserId(), input.getRequestBody());
        command.validate();
        return command;
    }

    public AcceptRejectRoute0CommandBean asBean() {
        AcceptRejectRoute0CommandBean result = new AcceptRejectRoute0CommandBean();
        result.classId = classId;
        result.userId = userId;
        result.courseId = courseId;
        result.status = status;
        return result;
    }

    private static AcceptRejectRoute0Command buildFromJsonObject(UUID userId, JsonObject requestBody) {
        AcceptRejectRoute0Command command = new AcceptRejectRoute0Command();
        try {
            command.classId = UuidUtils.convertStringToUuid(requestBody.getString(CommandAttributes.CLASS_ID));
            command.courseId = UuidUtils.convertStringToUuid(requestBody.getString(CommandAttributes.COURSE_ID));
            command.userId = userId;
            command.status = requestBody.getString(CommandAttributes.STATUS);
            return command;
        } catch (IllegalArgumentException e) {
            throw new HttpResponseWrapperException(HttpConstants.HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private void validate() {
        if (userId == null) {
            throw new HttpResponseWrapperException(HttpConstants.HttpStatus.BAD_REQUEST, "Invalid user id");
        }
        if (courseId == null) {
            throw new HttpResponseWrapperException(HttpConstants.HttpStatus.BAD_REQUEST, "Invalid course id");
        }
        if (status == null || !Route0StatusValues.getValidUpdateableStatusValues().contains(status)) {
            throw new HttpResponseWrapperException(HttpConstants.HttpStatus.BAD_REQUEST, "Invalid status value");
        }
    }

    public static class AcceptRejectRoute0CommandBean {
        private UUID courseId;
        private UUID userId;
        private UUID classId;
        private String status;

        public UUID getCourseId() {
            return courseId;
        }

        public void setCourseId(UUID courseId) {
            this.courseId = courseId;
        }

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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static final class CommandAttributes {

        static final String COURSE_ID = "courseId";
        static final String STATUS = "status";
        static final String CLASS_ID = "classId";

        private CommandAttributes() {
            throw new AssertionError();
        }
    }

}

package org.gooru.route0.processors.doroute0ofcontent;

import java.util.List;
import java.util.UUID;

import org.gooru.route0.infra.data.Route0Context;
import org.gooru.route0.infra.data.Route0SourceType;
import org.gooru.route0.infra.utils.UuidUtils;

import io.vertx.core.json.JsonObject;

/**
 * @author ashish.
 */
class DoRoute0OfContentCommand {

    private Route0SourceType source;
    private UUID classId;
    private List<UUID> memberIds;
    private UUID courseId;

    public UUID getCourseId() {
        return courseId;
    }

    public Route0SourceType getSource() {
        return source;
    }

    public UUID getClassId() {
        return classId;
    }

    public List<UUID> getMemberIds() {
        return memberIds;
    }

    private DoRoute0OfContentCommand() {

    }

    public Route0Context asRoute0Context() {
        switch (source) {
        case ClassJoinByMembers:
            return Route0Context.buildForClassJoin(classId, memberIds);
        case Route0SettingChanged:
            return Route0Context.buildForRoute0Setting(classId);
        case CourseAssignmentToClass:
            return Route0Context.buildForCourseAssignedToClass(classId, courseId);
        case OOB:
            return Route0Context.buildForOOB(classId, courseId, memberIds);
        default:
            throw new IllegalStateException("Invalid route0 source type");
        }
    }

    static DoRoute0OfContentCommand builder(JsonObject requestBody) {
        DoRoute0OfContentCommand result = new DoRoute0OfContentCommand();
        result.classId = UuidUtils.convertStringToUuid(requestBody.getString(CommandAttributes.CLASS_ID));
        result.source = Route0SourceType.builder(requestBody.getString(CommandAttributes.SOURCE));
        result.memberIds = UuidUtils.convertToUUIDList(requestBody.getJsonArray(CommandAttributes.MEMBER_IDS));
        result.courseId = UuidUtils.convertStringToUuid(requestBody.getString(CommandAttributes.COURSE_ID));
        result.validate();
        return result;
    }

    private void validate() {
        if (classId == null && courseId == null) {
            throw new IllegalArgumentException("Both class and course should not be absent");
        }
        if (source == null) {
            throw new IllegalArgumentException("Invalid source");
        }
        if (((memberIds == null || memberIds.isEmpty()) && (source == Route0SourceType.OOB
            || source == Route0SourceType.ClassJoinByMembers)) || (memberIds != null && !memberIds.isEmpty()
            && source != Route0SourceType.OOB && source != Route0SourceType.ClassJoinByMembers)) {
            throw new IllegalArgumentException("Members should be provided only for OOB/class join type route0");
        }
    }

    final class CommandAttributes {

        public static final String COURSE_ID = "courseId";
        static final String SOURCE = "source";
        static final String CLASS_ID = "classId";
        static final String MEMBER_IDS = "memberIds";

        private CommandAttributes() {
            throw new AssertionError();
        }
    }
}

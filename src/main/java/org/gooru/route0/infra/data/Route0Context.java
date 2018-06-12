package org.gooru.route0.infra.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author ashish.
 */
public class Route0Context {
    private Route0SourceType source;
    private UUID classId;
    private List<UUID> memberIds;
    private UUID courseId;

    private Route0Context(Route0SourceType source, UUID classId, List<UUID> memberIds, UUID courseId) {
        this.source = source;
        this.classId = classId;
        this.memberIds = memberIds;
        this.courseId = courseId;
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

    public UUID getCourseId() {
        return courseId;
    }

    @Override
    public String toString() {
        String members = memberIds.stream().map(UUID::toString).collect(Collectors.joining(","));
        return "Route0Context{" + "source=" + source.getName() + ", classId=" + classId + ", memberIds=" + members
            + ", courseId=" + courseId + '}';
    }

    public static Route0Context buildForClassJoin(UUID classId, List<UUID> members) {
        return new Route0Context(Route0SourceType.ClassJoinByMembers, classId, members, null);
    }

    public static Route0Context buildForOOB(UUID classId, UUID courseId, UUID memberId) {
        List<UUID> members = new ArrayList<>();
        members.add(memberId);
        return new Route0Context(Route0SourceType.OOB, classId, members, courseId);
    }

    public static Route0Context buildForOOB(UUID classId, UUID courseId, List<UUID> members) {
        return new Route0Context(Route0SourceType.OOB, classId, members, courseId);
    }

    public static Route0Context buildForRoute0Setting(UUID classId) {
        return new Route0Context(Route0SourceType.Route0SettingChanged, classId, Collections.emptyList(), null);
    }

    public static Route0Context buildForCourseAssignedToClass(UUID classId, UUID courseId) {
        return new Route0Context(Route0SourceType.CourseAssignmentToClass, classId, Collections.emptyList(), courseId);
    }

    public Route0Context createNewContext(List<UUID> members) {
        return new Route0Context(source, classId, members, courseId);
    }

    public Route0Context createNewContext(List<UUID> members, UUID courseId) {
        return new Route0Context(source, classId, members, courseId);
    }

    public boolean areUsersJoiningClass() {
        return source == Route0SourceType.ClassJoinByMembers;
    }

    public boolean hasClassSettingForRoute0BeenTurnedOn() {
        return source == Route0SourceType.Route0SettingChanged;
    }

    public boolean hasCourseBeenAssignedToClass() {
        return source == Route0SourceType.CourseAssignmentToClass;
    }

    public boolean isOOBRequestForRoute0() {
        return source == Route0SourceType.OOB;
    }
}

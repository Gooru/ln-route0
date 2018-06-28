package org.gooru.route0.infra.services.contentroutepersister;

import java.util.UUID;

/**
 * @author ashish.
 */
public class ContentRouteInfo {
    private final UUID userId;
    private final UUID courseId;
    private final UUID classId;

    public ContentRouteInfo(UUID userId, UUID courseId, UUID classId) {
        this.userId = userId;
        this.courseId = courseId;
        this.classId = classId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public UUID getClassId() {
        return classId;
    }
}

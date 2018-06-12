package org.gooru.route0.infra.data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ashish.
 */
public enum Route0SourceType {

    Route0SettingChanged("route0", 1),
    CourseAssignmentToClass("assign.course.to.class", 2),
    ClassJoinByMembers("join.class", 3),
    OOB("oob", 4);

    private final String name;
    private final int order;

    Route0SourceType(String name, int order) {
        this.name = name;
        this.order = order;
    }

    public String getName() {
        return this.name;
    }

    public int getOrder() {
        return order;
    }

    private static final Map<String, Route0SourceType> LOOKUP = new HashMap<>(values().length);

    static {
        for (Route0SourceType route0SourceType : values()) {
            LOOKUP.put(route0SourceType.name, route0SourceType);
        }
    }

    public static Route0SourceType builder(String type) {
        Route0SourceType result = LOOKUP.get(type);
        if (result == null) {
            throw new IllegalArgumentException("Invalid route0 source type: " + type);
        }
        return result;
    }

}

package org.gooru.route0.infra.services;

import java.util.UUID;

import org.gooru.route0.infra.jdbi.DBICreator;

import io.vertx.core.json.JsonObject;

/**
 * @author ashish.
 */
public final class Route0ApplicableService {

    private static final String ROUTE0_SETTING_KEY = "route0";

    public static boolean isRoute0ApplicableToClass(UUID classId) {
        Route0ApplicableDao dao = getRoute0ApplicableDao();
        String setting = dao.fetchClassSetting(classId);
        if (setting == null) {
            throw new IllegalStateException("Class setting should not be null");
        }
        JsonObject jsonSetting = new JsonObject(setting);
        return Boolean.TRUE.equals(jsonSetting.getBoolean(ROUTE0_SETTING_KEY));
    }

    public static boolean isRoute0ApplicableToCourseInIL(UUID courseId) {
        Route0ApplicableDao dao = getRoute0ApplicableDao();
        return (dao.fetchCourseVersion(courseId) != null);
    }

    private static Route0ApplicableDao getRoute0ApplicableDao() {
        return DBICreator.getDbiForDefaultDS().onDemand(Route0ApplicableDao.class);
    }
}

package org.gooru.route0.infra.services;

import java.util.UUID;
import org.gooru.route0.infra.components.AppConfiguration;
import org.gooru.route0.infra.jdbi.DBICreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */
public final class Route0ApplicableService {

  private static final Logger LOGGER = LoggerFactory.getLogger(Route0ApplicableService.class);

  public static boolean isRoute0ApplicableToClass(UUID classId) {
    Route0ApplicableDao dao = getRoute0ApplicableDao();
    String courseId = dao.fetchCourseForClass(classId);
    if (courseId == null) {
      LOGGER.info("Course is not assigned to class '{}' hence route0 not applicable",
          classId.toString());
      return false;
    }
    return AppConfiguration.getInstance().getRoute0ApplicableCourseVersion()
        .equals(dao.fetchCourseVersion(courseId));
  }

  public static boolean isRoute0ApplicableToCourseInIL(UUID courseId) {
    Route0ApplicableDao dao = getRoute0ApplicableDao();
    return AppConfiguration.getInstance().getRoute0ApplicableCourseVersion()
        .equals(dao.fetchCourseVersion(courseId));
  }

  private static Route0ApplicableDao getRoute0ApplicableDao() {
    return DBICreator.getDbiForDefaultDS().onDemand(Route0ApplicableDao.class);
  }
}

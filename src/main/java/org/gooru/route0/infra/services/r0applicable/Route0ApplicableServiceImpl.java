package org.gooru.route0.infra.services.r0applicable;

import java.util.UUID;
import org.gooru.route0.infra.components.AppConfiguration;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */

class Route0ApplicableServiceImpl implements Route0ApplicableService {

  private final DBI dbi;
  private Route0ApplicableDao dao;
  private static final Logger LOGGER = LoggerFactory.getLogger(Route0ApplicableServiceImpl.class);

  Route0ApplicableServiceImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public boolean isRoute0ApplicableToClass(UUID classId) {
    String courseId = fetchDao().fetchCourseForClass(classId);
    if (courseId == null) {
      LOGGER.info("Course is not assigned to class '{}' hence route0 not applicable",
          classId.toString());
      return false;
    }
    if (!AppConfiguration.getInstance().getRoute0ApplicableCourseVersion()
        .equals(fetchDao().fetchCourseVersion(courseId))) {
      LOGGER.info(
          "Course '{}' for class '{}' is not having correct version hence route0 not applicable",
          courseId, classId.toString());
      return false;
    }
    return fetchDao().fetchRoute0Applicable(classId);
  }

  @Override
  public boolean isRoute0ApplicableToCourseInIL(UUID courseId) {
    return AppConfiguration.getInstance().getRoute0ApplicableCourseVersion()
        .equals(fetchDao().fetchCourseVersion(courseId));
  }

  private Route0ApplicableDao fetchDao() {
    if (dao == null) {
      dao = dbi.onDemand(Route0ApplicableDao.class);
    }
    return dao;
  }
}

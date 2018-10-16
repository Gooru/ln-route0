package org.gooru.route0.infra.services.r0applicable;

import java.util.UUID;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

/**
 * @author ashish
 */
interface Route0ApplicableDao {

  @SqlQuery("select course_id from class where id = :classId and is_deleted = false")
  String fetchCourseForClass(@Bind("classId") UUID classId);

  @SqlQuery("select version from course where id = :courseId and is_deleted = false")
  String fetchCourseVersion(@Bind("courseId") UUID courseId);

  @SqlQuery("select version from course where id = :courseId::uuid and is_deleted = false")
  String fetchCourseVersion(@Bind("courseId") String courseId);

  @SqlQuery("select route0_applicable from class where id = :classId and is_deleted = false")
  boolean fetchRoute0Applicable(@Bind("classId") UUID classId);
}

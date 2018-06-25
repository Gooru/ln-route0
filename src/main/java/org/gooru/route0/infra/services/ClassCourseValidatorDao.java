package org.gooru.route0.infra.services;

import java.util.UUID;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

/**
 * @author ashish.
 */
public interface ClassCourseValidatorDao {

    @SqlQuery("select exists(select 1 from course where id = :courseId and is_deleted = false)")
    boolean validateCourse(@Bind("courseId") UUID courseId);

    @SqlQuery("select exists (select 1 from class where id = :classId and course_id = :courseId and "
                  + " is_deleted = false and is_archived = false)")
    boolean validateClassCourse(@Bind("classId") UUID classId, @Bind("courseId") UUID courseId);
}

package org.gooru.route0.infra.services;

import java.util.UUID;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

/**
 * @author ashish.
 */
interface Route0ApplicableDao {

    @SqlQuery("select setting from class where id = :classId")
    String fetchClassSetting(@Bind("classId") UUID classId);

    @SqlQuery("select version from course where id = :courseId")
    String fetchCourseVersion(@Bind("courseId") UUID courseId);

}

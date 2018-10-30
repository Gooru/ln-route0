package org.gooru.route0.infra.services;

import java.util.UUID;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

/**
 * @author ashish.
 */
public interface ValidatorDao {

  /* NOTE that first two would require DBI pointing to core DB while latter ones will require
   * DS db
   */

  @SqlQuery("select exists(select 1 from course where id = :courseId and is_deleted = false)")
  boolean validateCourse(@Bind("courseId") UUID courseId);

  @SqlQuery("select exists (select 1 from class where id = :classId and course_id = :courseId and "
      + " is_deleted = false and is_archived = false)")
  boolean validateClassCourse(@Bind("classId") UUID classId, @Bind("courseId") UUID courseId);

  @SqlQuery("select exists (select 1 from learner_profile_baselined where user_id = :userId "
      + " and course_id = :courseId and tx_subject_code = :subjectBucket and class_id = :classId)")
  boolean validateLPBaselinePresenceInClass(@Bind("userId") String userId,
      @Bind("courseId") String courseId, @Bind("classId") String classId,
      @Bind("subjectBucket") String subjectBucket);

  @SqlQuery("select exists (select 1 from learner_profile_baselined where user_id = :userId "
      + " and course_id = :courseId and tx_subject_code = :subjectBucket and class_id is null)")
  boolean validateLPBaselinePresenceForIL(@Bind("userId") String userId,
      @Bind("courseId") String courseId, @Bind("subjectBucket") String subjectBucket);

}

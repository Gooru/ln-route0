package org.gooru.route0.processors.doroute0ofcontent;

import java.util.UUID;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 * @author ashish.
 */

interface DoRoute0OfContentDao {

  @SqlUpdate("delete from user_route0_content where user_id = :userId and course_id = :courseId and class_id = :classId")
  void resetRoute0ForUserInClass(@Bind("userId") UUID userId, @Bind("courseId") UUID courseId,
      @Bind("classId") UUID classId);

  @SqlUpdate("delete from user_route0_content where user_id = :userId and course_id = :courseId and class_id is null")
  void resetRoute0ForUserInIL(@Bind("userId") UUID userId, @Bind("courseId") UUID courseId);

  @SqlQuery(
      "select exists (select 1 from user_route0_content where user_id = :userId and course_id = :courseId  "
          + "and class_id = :classId)")
  boolean route0DoneForUserInClass(@Bind("userId") UUID userId, @Bind("courseId") UUID courseId,
      @Bind("classId") UUID classId);

  @SqlQuery(
      "select exists (select 1 from user_route0_content where user_id = :userId and course_id = :courseId  "
          + "and class_id is null)")
  boolean route0DoneForUserInIL(@Bind("userId") UUID userId, @Bind("courseId") UUID courseId);


}

package org.gooru.route0.processors.acceptrejectroute0;

import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 * @author ashish.
 */
interface AcceptRejectRoute0Dao {

  @SqlQuery(
      "select status from user_route0_content where user_id = :userId and course_id = :courseId and class_id "
          + " is null")
  String fetchStatusForRoute0InIL(
      @BindBean AcceptRejectRoute0Command.AcceptRejectRoute0CommandBean model);

  @SqlQuery(
      "select status from user_route0_content where user_id = :userId and course_id = :courseId and class_id "
          + " = :classId")
  String fetchStatusForRoute0InClass(
      @BindBean AcceptRejectRoute0Command.AcceptRejectRoute0CommandBean model);

  @SqlUpdate(
      "update user_route0_content set status = :status where user_id = :userId and course_id = :courseId and"
          + " class_id is null")
  void updateStatusForRoute0InIL(
      @BindBean AcceptRejectRoute0Command.AcceptRejectRoute0CommandBean model);

  @SqlUpdate(
      "update user_route0_content set status = :status where user_id = :userId and course_id = :courseId and"
          + " class_id = :classId")
  void updateStatusForRoute0InClass(
      @BindBean AcceptRejectRoute0Command.AcceptRejectRoute0CommandBean model);
}

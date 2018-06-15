package org.gooru.route0.processors.fetchroute0content;

import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

/**
 * @author ashish.
 */
interface FetchRoute0ContentDao {

    @SqlQuery("select skipped_content from user_route0_content where user_id = :userId and course_id = :courseId "
                  + " and class_id = :classId")
    String fetchRoute0ContentForUserInClass(@BindBean FetchRoute0ContentCommand.FetchRoute0ContentCommandBean bean);

    @SqlQuery("select skipped_content from user_route0_content where user_id = :userId and course_id = :courseId "
                  + " and class_id is null")
    String fetchRoute0ContentForUserInIL(@BindBean FetchRoute0ContentCommand.FetchRoute0ContentCommandBean bean);

    @SqlQuery("select exists (select 1 from class where id = :classId and (creator_id = :teacherId or collaborator ?? "
                  + ":teacherId::text) and is_deleted = false )")
    boolean isUserTeacherOrCollaboratorForClass(@BindBean FetchRoute0ContentCommand.FetchRoute0ContentCommandBean bean);

}

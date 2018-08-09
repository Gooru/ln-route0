package org.gooru.route0.processors.fetchroute0content;

import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

/**
 * @author ashish.
 */
interface FetchRoute0ContentDao {

    @Mapper(FetchRoute0ContentResponse.FetchRoute0ContentResponseMapper.class)
    @SqlQuery("select status, route0_content, user_competency_route, created_at from user_route0_content where " +
        "user_id = :userId  and course_id = :courseId  and class_id = :classId")
    FetchRoute0ContentResponse fetchRoute0ContentForUserInClass(
        @BindBean FetchRoute0ContentCommand.FetchRoute0ContentCommandBean bean);

    @Mapper(FetchRoute0ContentResponse.FetchRoute0ContentResponseMapper.class)
    @SqlQuery("select status, route0_content, user_competency_route, created_at from user_route0_content where " +
        "user_id = :userId  and course_id = :courseId  and class_id is null")
    FetchRoute0ContentResponse fetchRoute0ContentForUserInIL(
        @BindBean FetchRoute0ContentCommand.FetchRoute0ContentCommandBean bean);

    @SqlQuery("select exists (select 1 from class where id = :classId and (creator_id = :teacherId or collaborator ?? "
        + ":teacherId::text) and is_deleted = false )")
    boolean isUserTeacherOrCollaboratorForClass(@BindBean FetchRoute0ContentCommand.FetchRoute0ContentCommandBean bean);

}

package org.gooru.route0.infra.services;

import java.util.List;
import java.util.UUID;

import org.gooru.route0.infra.data.Route0QueueModel;
import org.gooru.route0.infra.jdbi.PGArray;
import org.gooru.route0.infra.jdbi.UUIDMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

/**
 * @author ashish.
 */
interface Route0RequestQueueDao {

    @SqlQuery("select exists (select 1 from class where id = :classId and is_deleted = false and is_archived = false)")
    boolean isClassNotDeletedAndNotArchived(@Bind("classId") UUID classId);

    @Mapper(UUIDMapper.class)
    @SqlQuery("select user_id from class_member where class_id = :classId and user_id is not null")
    List<UUID> fetchMembersOfClass(@Bind("classId") UUID classId);

    @Mapper(UUIDMapper.class)
    @SqlQuery("select user_id from class_member where class_id = :classId and user_id = any(:usersList)")
    List<UUID> fetchSpecifiedMembersOfClass(@Bind("classId") UUID classId, @Bind("usersList") PGArray<UUID> members);

    @Mapper(UUIDMapper.class)
    @SqlQuery("select course_id from class where id = :classId")
    UUID fetchCourseForClass(@Bind("classId") UUID classId);

    @SqlQuery("select exists(select 1 from course where id = :courseId and is_deleted = false)")
    boolean isCourseNotDeleted(@Bind("courseId") UUID courseId);

    @SqlBatch("insert into route0_queue(user_id, course_id, class_id, priority, status) values (:members, :courseId,"
                  + " :classId, :priority, :status) ON CONFLICT DO NOTHING")
    void queueRequests(@Bind("members") List<UUID> userId, @BindBean Route0QueueModel route0QueueModel);

    @SqlUpdate("update route0_queue set status = 0 where status != 0")
    void initializeQueueStatus();

    @Mapper(Route0QueueModel.Route0QueueModelMapper.class)
    @SqlQuery("select id, user_id, course_id, class_id, priority, status from route0_queue where status = 0 order by"
                  + " priority desc limit 1")
    Route0QueueModel getNextDispatchableModel();

    @SqlUpdate("update route0_queue set status = 1 where id = :modelId")
    void setQueuedRecordStatusAsDispatched(@Bind("modelId") Long id);

    @SqlUpdate("delete from route0_queue where id = :modelId")
    void dequeueRecord(@Bind("modelId") Long id);

    @SqlQuery("select exists (select 1 from route0_queue where id = :id and status = 1)")
    boolean isQueuedRecordStillDispatched(@Bind("id") Long modelId);

    @SqlQuery("select exists (select 1 from user_route0_content where user_id = :userId and course_id = :courseId  "
                  + "and class_id = :classId)")
    boolean route0DoneForUserInClass(@BindBean Route0QueueModel model);

    @SqlQuery("select exists (select 1 from user_route0_content where user_id = :userId and course_id = :courseId  "
                  + "and class_id is null)")
    boolean route0DoneForUserInIL(@BindBean Route0QueueModel model);

    @SqlUpdate("insert into user_route0_content(user_id, class_id, course_id, skipped_content) values (:userId, "
                   + ":classId, :courseId, :skippedContent::jsonb)")
    void persistRoute0Content(@BindBean Route0QueueModel model, @Bind("skippedContent") String skippedContent);
}

package org.gooru.route0.infra.services.contentroutepersister;

import java.util.List;

import org.gooru.route0.infra.data.UserRoute0ContentDetailModel;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 * @author ashish.
 */
interface ContentRoutePersisterDao {

    @GetGeneratedKeys
    @SqlUpdate("insert into user_route0_content(user_id, class_id, course_id, status, route0_content) values (:userId, "
                   + " :classId, :courseId, :status, :route0Content::jsonb)")
    long persistRoute0Content(@BindBean ContentRouteInfo info, @Bind("status") String status,
        @Bind("route0Content") String route0Content);

    @SqlBatch("insert into user_route0_content_detail (user_route0_content_id, unit_id, unit_title, unit_sequence, "
                  + " lesson_id, lesson_title, lesson_sequence, collection_id, collection_type, collection_sequence, "
                  + " route0_sequence) values (:userRoute0ContentId, :unitId, :unitTitle, :unitSequence, :lessonId, "
                  + " :lessonTitle, :lessonSequence, :collectionId, :collectionType, :collectionSequence, "
                  + " :route0Sequence)")
    void persistRoute0ContentDetails(@BindBean List<UserRoute0ContentDetailModel> detailModels);
}

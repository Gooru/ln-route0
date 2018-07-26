package org.gooru.route0.infra.services.contentroutepersister;

import java.util.List;

import org.gooru.route0.infra.data.UserRoute0ContentDetailModel;
import org.gooru.route0.infra.data.UserRoute0ContentDetailModelMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

/**
 * @author ashish.
 */
interface ContentRoutePersisterDao {

    @GetGeneratedKeys
    @SqlUpdate("insert into user_route0_content(user_id, class_id, course_id, status, route0_content, "
                   + " user_competency_route) values (:userId, :classId, :courseId, :status, :route0Content::jsonb, "
                   + " :userCompetencyRoute::jsonb)")
    long persistRoute0Content(@BindBean ContentRouteInfo info, @Bind("status") String status,
        @Bind("route0Content") String route0Content, @Bind("userCompetencyRoute") String userCompetencyRoute);

    @SqlBatch("insert into user_route0_content_detail (user_route0_content_id, unit_id, unit_title, unit_sequence, "
                  + " lesson_id, lesson_title, lesson_sequence, collection_id, collection_type, collection_sequence, "
                  + " route0_sequence) values (:userRoute0ContentId, :unitId, :unitTitle, :unitSequence, :lessonId, "
                  + " :lessonTitle, :lessonSequence, :collectionId, :collectionType, :collectionSequence, "
                  + " :route0Sequence)")
    void persistRoute0ContentDetails(@BindBean List<UserRoute0ContentDetailModel> detailModels);

    @Mapper(UserRoute0ContentDetailModelMapper.class)
    @SqlQuery("select id, user_route0_content_id, unit_id, unit_title, unit_sequence, lesson_id, lesson_title, "
                  + "lesson_sequence, collection_id, collection_type, collection_sequence, route0_sequence from "
                  + "user_route0_content_detail where user_route0_content_id = :contentRouteId")
    List<UserRoute0ContentDetailModel> fetchDetailModelsForSpecifiedRoute0(@Bind("contentRouteId") long contentRouteId);

    @SqlUpdate("update user_route0_content set route0_content = :route0Content::jsonb where id = :id")
    void updateRouteInfoForRoute0Content(@Bind("id") Long id, @Bind("route0Content") String route0Content);
}

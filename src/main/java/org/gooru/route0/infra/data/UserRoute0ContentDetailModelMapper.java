package org.gooru.route0.infra.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.gooru.route0.infra.services.competencyroutetocontentroutemapper.CollectionModel;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 * @author ashish.
 */
public class UserRoute0ContentDetailModelMapper implements ResultSetMapper<UserRoute0ContentDetailModel> {
    @Override
    public UserRoute0ContentDetailModel map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        UserRoute0ContentDetailModel model = new UserRoute0ContentDetailModel();
        model.setId(r.getLong(Attributes.ID));
        model.setRoute0Sequence(r.getInt(Attributes.ROUTE0_SEQUENCE));
        model.setUnitId(UUID.fromString(r.getString(Attributes.UNIT_ID)));
        model.setUnitTitle(r.getString(Attributes.UNIT_TITLE));
        model.setUnitSequence(r.getInt(Attributes.UNIT_SEQUENCE));
        model.setLessonId(UUID.fromString(r.getString(Attributes.LESSON_ID)));
        model.setLessonTitle(r.getString(Attributes.LESSON_TITLE));
        model.setLessonSequence(r.getInt(Attributes.LESSON_SEQUENCE));
        model.setCollectionId(UUID.fromString(r.getString(Attributes.COLLECTION_ID)));
        model.setCollectionType(CollectionModel.CollectionModelType.builder(r.getString(Attributes.COLLECTION_TYPE)));
        model.setCollectionSequence(r.getInt(Attributes.COLLECTION_SEQUENCE));
        model.setUserRoute0ContentId(r.getLong(Attributes.USER_ROUTE0_CONTENT_ID));
        return model;
    }

    final static class Attributes {
        private Attributes() {
            throw new AssertionError();
        }

        static final String ID = "id";
        static final String USER_ROUTE0_CONTENT_ID = "user_route0_content_id";
        static final String UNIT_ID = "unit_id";
        static final String UNIT_TITLE = "unit_title";
        static final String UNIT_SEQUENCE = "unit_sequence";
        static final String LESSON_ID = "lesson_id";
        static final String LESSON_TITLE = "lesson_title";
        static final String LESSON_SEQUENCE = "lesson_sequence";
        static final String COLLECTION_ID = "collection_id";
        static final String COLLECTION_TYPE = "collection_type";
        static final String COLLECTION_SEQUENCE = "collection_sequence";
        static final String ROUTE0_SEQUENCE = "route0_sequence";
    }
}

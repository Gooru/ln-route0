package org.gooru.route0.infra.services.competencyroutecalculator;

import java.util.List;
import java.util.UUID;

import org.gooru.route0.infra.jdbi.SqlArrayMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

/**
 * @author ashish.
 */
interface CompetencyFetcherDao {

    @Mapper(SqlArrayMapper.class)
    @SqlQuery("select gut_codes from course where id = :courseId and is_deleted = false")
    List<List<String>> findCompetenciesForCourse(@Bind("courseId") UUID course);

}

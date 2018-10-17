package org.gooru.route0.infra.services.competencyroutecalculator;

import java.util.List;
import org.gooru.route0.infra.data.competency.Competency;
import org.gooru.route0.infra.data.competency.CompetencyModel;
import org.gooru.route0.infra.data.competency.DomainModel;
import org.gooru.route0.infra.jdbi.PGArray;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

/**
 * @author ashish.
 */
interface TaxonomyDao {

  @Mapper(CompetencyMapper.class)
  @SqlQuery(
      "SELECT dcmt.tx_subject_code, dcmt.tx_domain_code, dcmt.tx_comp_code, dcmt.tx_comp_seq FROM   "
          + " domain_competency_matrix dcmt WHERE  dcmt.tx_subject_code = :subjectCode AND "
          + " dcmt.tx_comp_code = any(:gutCodes)")
  List<Competency> transformGutCodesToCompetency(@Bind("subjectCode") String subjectCode,
      @Bind("gutCodes") PGArray<String> gutCodes);

  @Mapper(CompetencyMapper.class)
  @SqlQuery(
      "SELECT dcmt.tx_subject_code, dcmt.tx_domain_code, tx_comp_code ,tx_comp_seq FROM domain_competency_matrix dcmt "
          + " where tx_subject_code = :subjectCode and tx_domain_code = any(:domainCodes) and "
          + " tx_comp_code = any(select unnest(gut_codes) from learner_profile_baselined where user_id = :userId "
          + " and course_id = :courseId and class_id = :classId and tx_subject_code = :subjectCode)")
  List<Competency> fetchLearnerProfileBaselinedInClass(
      @Bind("userId") String userId, @Bind("subjectCode") String subjectCode,
      @Bind("courseId") String courseId, @Bind("classId") String classId,
      @Bind("domainCodes") PGArray<String> domainCodes);

  @Mapper(CompetencyMapper.class)
  @SqlQuery(
      "SELECT dcmt.tx_subject_code, dcmt.tx_domain_code, tx_comp_code ,tx_comp_seq FROM domain_competency_matrix dcmt "
          + " where tx_subject_code = :subjectCode and tx_domain_code = any(:domainCodes) and "
          + " tx_comp_code = any(select unnest(gut_codes) from learner_profile_baselined where user_id = :userId "
          + " and course_id = :courseId and class_id is null and tx_subject_code = :subjectCode)")
  List<Competency> fetchLearnerProfileBaselinedInIL(
      @Bind("userId") String userId, @Bind("subjectCode") String subjectCode,
      @Bind("courseId") String courseId, @Bind("domainCodes") PGArray<String> domainCodes);


  @Mapper(DomainModelMapper.class)
  @SqlQuery(
      "select tx_subject_code, tx_domain_code, tx_domain_name, tx_domain_seq from tx_domains where "
          + "tx_domain_code = any(:domains) and tx_subject_code = :subjectCode order by tx_domain_seq")
  List<DomainModel> fetchDomainModelsForSpecifiedDomainsInOrder(
      @Bind("subjectCode") String subjectCode,
      @Bind("domains") PGArray<String> domains);

  @Mapper(CompetencyModelMapper.class)
  @SqlQuery("SELECT tx_subject_code, tx_domain_code, tx_comp_code, tx_comp_name, tx_comp_desc,"
      + " tx_comp_student_desc, tx_comp_seq FROM domain_competency_matrix WHERE "
      + " tx_subject_code = :subjectCode AND tx_domain_code = any(:domains) ORDER BY "
      + " tx_domain_code, tx_comp_seq")
  List<CompetencyModel> fetchDomainCompetencyMatrixForSpecifiedDomains(
      @Bind("subjectCode") String subjectCode,
      @Bind("domains") PGArray<String> domains);
}

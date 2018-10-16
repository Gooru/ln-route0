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
      "SELECT tx_subject_code, tx_domain_code, tx_comp_code, tx_comp_seq FROM domain_competency_matrix "
          + " WHERE (tx_subject_code, tx_domain_code, tx_comp_seq) IN (SELECT dcmt.tx_subject_code, "
          + " dcmt.tx_domain_code, Max(dcmt.tx_comp_seq) tx_comp_seq FROM domain_competency_matrix dcmt "
          + " INNER JOIN learner_profile_competency_status lpcs ON lpcs.gut_code = dcmt.tx_comp_code AND "
          + " dcmt.tx_subject_code = lpcs.tx_subject_code AND dcmt.tx_subject_code = :subjectCode AND "
          + " lpcs.user_id = :userId AND lpcs.status >= 4 AND dcmt.tx_domain_code =  any(:domainCodes) "
          + " GROUP BY dcmt.tx_subject_code, dcmt.tx_domain_code)")
  List<Competency> fetchProficiencyForUserInSpecifiedSubjectAndDomains(
      @Bind("userId") String userId,
      @Bind("subjectCode") String subjectCode, @Bind("domainCodes") PGArray<String> domainCodes);

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

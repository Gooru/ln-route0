package org.gooru.route0.infra.services.competencyroutecalculator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.gooru.route0.infra.data.RouteCalculatorModel;
import org.gooru.route0.infra.data.competency.Competency;
import org.gooru.route0.infra.data.competency.CompetencyMap;
import org.gooru.route0.infra.data.competency.CompetencyRoute;
import org.gooru.route0.infra.data.competency.DomainCode;
import org.gooru.route0.infra.data.competency.SubjectCode;
import org.gooru.route0.infra.services.ValidatorDao;
import org.gooru.route0.infra.utils.CollectionUtils;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */
class CompetencyRouteCalculatorService implements CompetencyRouteCalculator {

  private final DBI dbi4core;
  private final DBI dbi4ds;
  private RouteCalculatorModel model;
  private static final Logger LOGGER = LoggerFactory.getLogger(CompetencyRouteCalculator.class);
  private String subjectCode;
  private List<String> destinationGutCodes;
  private List<Competency> destinationCompetencies, sourceCompetencies;
  private CompetencyMap destinationCompetencyMap, proficiencyCompetencyMap;
  private TaxonomyDao taxonomyDao;
  private CompetencyRoute competencyRouteToDestination;

  CompetencyRouteCalculatorService(DBI dbi4core, DBI dbi4ds) {
    this.dbi4core = dbi4core;
    this.dbi4ds = dbi4ds;
  }

  @Override
  public CompetencyRouteModel calculateCompetencyRoute(RouteCalculatorModel model) {
    this.model = model;
    try {
      return doProcess();
    } catch (RuntimeException re) {
      LOGGER.warn("Not able to compute route for class: '{}', course: '{}' and user: '{}'",
          model.getCourseId(),
          model.getClassId(), model.getUserId());
      throw re;
    }
  }

  private CompetencyRouteModel doProcess() {
    LOGGER.debug("Validating class/course");
    validateClassCourse();
    LOGGER.debug("Initializing subject code");
    initializeSubjectCode();
    LOGGER.debug("Validating presence of baselined learner profile");
    validateLearnerProfileBaselineIsDone();
    LOGGER.debug("Fetching destination gut codes");
    fetchDestinationGutCodes();
    LOGGER.debug("Filtering gut codes for competencies");
    filterGutCodesForCompetencyForSpecifiedSubject();
    LOGGER.debug("Creating destination competency map");
    createDestinationCompetencyMap();
    LOGGER.debug("User user proficiency for specified subject and domains");
    fetchUserProficiencyInSpecifiedSubjectAndDomains();
    LOGGER.debug("Creating proficiency competency map");
    createProficiencyCompetencyMap();
    LOGGER.debug("Calculating competency route");
    calculateCompetencyRoute();
    LOGGER.debug("Building competency route model");
    return new CompetencyRouteModelBuilder(getTaxonomyDao())
        .build(new SubjectCode(subjectCode), competencyRouteToDestination);
  }

  private void calculateCompetencyRoute() {
    competencyRouteToDestination =
        proficiencyCompetencyMap.getCeilingLine()
            .getRouteToCompetencyLine(destinationCompetencyMap.getFloorLine());
  }

  private void createProficiencyCompetencyMap() {
    proficiencyCompetencyMap = CompetencyMap.build(sourceCompetencies);
  }

  private void fetchUserProficiencyInSpecifiedSubjectAndDomains() {
    List<String> domains =
        destinationCompetencyMap.getDomains().stream().map(DomainCode::getCode)
            .collect(Collectors.toList());
    if (model.getClassId() != null) {
      sourceCompetencies = getTaxonomyDao()
          .fetchLearnerProfileBaselinedInClass(model.getUserId().toString(), subjectCode,
              model.getCourseId().toString(), model.getClassId().toString(),
              CollectionUtils.convertToSqlArrayOfString(domains));
    } else {
      sourceCompetencies = getTaxonomyDao()
          .fetchLearnerProfileBaselinedInIL(model.getUserId().toString(), subjectCode,
              model.getCourseId().toString(),
              CollectionUtils.convertToSqlArrayOfString(domains));
    }
  }

  private void createDestinationCompetencyMap() {
    destinationCompetencyMap = CompetencyMap.build(destinationCompetencies);
  }

  private void filterGutCodesForCompetencyForSpecifiedSubject() {
    // Query the db based on gut codes, and create competencies out of it
    destinationCompetencies = getTaxonomyDao()
        .transformGutCodesToCompetency(subjectCode,
            CollectionUtils.convertToSqlArrayOfString(destinationGutCodes));
    if (destinationCompetencies == null || destinationCompetencies.isEmpty()) {
      LOGGER.warn(
          "No destination competencies found after filtering aggregated gut codes for course: '{}'",
          model.getCourseId().toString());
      throw new IllegalStateException(
          "Destination competencies not found for course: " + model.getCourseId());
    }
  }

  private void fetchDestinationGutCodes() {
    CompetencyFetcher competencyFetcher = CompetencyFetcher.build(dbi4core);
    destinationGutCodes = competencyFetcher.fetchCompetenciesForCourse(model.getCourseId());
    if (destinationGutCodes == null || destinationGutCodes.isEmpty()) {
      LOGGER.warn("No aggregated competencies found for course: '{}'", model.getCourseId());
      throw new IllegalStateException(
          "No aggregated competencies found for course: " + model.getCourseId());
    }
  }

  private void validateClassCourse() {
    ValidatorDao validatorDao = dbi4core.onDemand(ValidatorDao.class);
    if (model.getClassId() != null) {
      if (!validatorDao.validateClassCourse(model.getClassId(), model.getCourseId())) {
        LOGGER.warn("Invalid class/course for request, course: '{}', class: '{}' ",
            model.getCourseId().toString(), Objects.toString(model.getClassId()));
        throw new IllegalStateException(
            "Invalid class/course for request, class:" + model.getClassId() + ", course: " + model
                .getCourseId());
      }
    } else {
      if (!validatorDao.validateCourse(model.getCourseId())) {
        throw new IllegalStateException(
            "Invalid course for request course: " + model.getCourseId());
      }
    }
  }

  private void validateLearnerProfileBaselineIsDone() {
    ValidatorDao validatorDao = dbi4ds.onDemand(ValidatorDao.class);
    if (model.getClassId() != null) {
      if (!validatorDao.validateLPBaselinePresenceInClass(model.getUserId().toString(),
          model.getCourseId().toString(), model.getClassId().toString(), subjectCode)) {
        LOGGER.warn("Learner profile baseline does not exist for model: '{}' and subject: '{}'",
            model.toString(), subjectCode);
        throw new IllegalStateException(
            "Learner profile baseline does not exist for model: " + model.toString()
                + "  and subject: " + subjectCode);
      }
    } else {
      if (!validatorDao.validateLPBaselinePresenceForIL(model.getUserId().toString(),
          model.getCourseId().toString(), subjectCode)) {
        LOGGER.warn("Learner profile baseline does not exist for model: '{}' and subject: '{}'",
            model.toString(), subjectCode);
        throw new IllegalStateException(
            "Learner profile baseline does not exist for model: " + model.toString()
                + "  and subject: " + subjectCode);
      }
    }
  }


  private void initializeSubjectCode() {
    subjectCode = SubjectInferer.build().inferSubjectForCourse(model.getCourseId());
    if (subjectCode == null) {
      LOGGER.warn("Not able to find subject code for specified course '{}'", model.getCourseId());
      throw new IllegalStateException(
          "Not able to find subject code for specified course " + model.getCourseId());
    }
  }

  private TaxonomyDao getTaxonomyDao() {
    if (taxonomyDao == null) {
      taxonomyDao = dbi4ds.onDemand(TaxonomyDao.class);
    }
    return taxonomyDao;
  }

}

package org.gooru.route0.infra.services.competencyroutecalculator;

import java.util.List;
import java.util.stream.Collectors;

import org.gooru.route0.infra.data.RouteCalculatorModel;
import org.gooru.route0.infra.data.competency.Competency;
import org.gooru.route0.infra.data.competency.CompetencyMap;
import org.gooru.route0.infra.data.competency.CompetencyRoute;
import org.gooru.route0.infra.data.competency.DomainCode;
import org.gooru.route0.infra.data.competency.SubjectCode;
import org.gooru.route0.infra.services.ClassCourseValidatorDao;
import org.gooru.route0.infra.utils.CollectionUtils;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */
class CompetencyRouteCalculatorService implements CompetencyRouteCalculator {

    private final DBI defaultDSDbi;
    private final DBI dsdbDSDbi;
    private RouteCalculatorModel model;
    private static final Logger LOGGER = LoggerFactory.getLogger(CompetencyRouteCalculator.class);
    private String subjectCode;
    private List<String> destinationGutCodes;
    private List<Competency> destinationCompetencies, sourceCompetencies;
    private CompetencyMap destinationCompetencyMap, proficiencyCompetencyMap;
    private TaxonomyDao taxonomyDao;
    private CompetencyRoute competencyRouteToDestination;

    CompetencyRouteCalculatorService(DBI defaultDSDbi, DBI dsdbDSDbi) {
        this.defaultDSDbi = defaultDSDbi;
        this.dsdbDSDbi = dsdbDSDbi;
    }

    @Override
    public CompetencyRouteModel calculateCompetencyRoute(RouteCalculatorModel model) {
        this.model = model;
        try {
            return doProcess();
        } catch (RuntimeException re) {
            LOGGER.warn("Not able to compute route for class: '{}', course: '{}' and user: '{}'", model.getCourseId(),
                model.getClassId(), model.getUserId());
        }
        return null;
    }

    private CompetencyRouteModel doProcess() {
        validateClassCourse();
        initializeSubjectCode();
        fetchDestinationGutCodes();
        filterGutCodesForCompetencyForSpecifiedSubject();
        createDestinationCompetencyMap();
        fetchUserProficiencyInSpecifiedSubjectAndDomains();
        createProficiencyCompetencyMap();
        calculateCompetencyRoute();
        return new CompetencyRouteModelBuilder(getTaxonomyDao())
            .build(new SubjectCode(subjectCode), competencyRouteToDestination);
    }

    private void calculateCompetencyRoute() {
        competencyRouteToDestination =
            proficiencyCompetencyMap.getCeilingLine().getRouteToCompetencyLine(destinationCompetencyMap.getFloorLine());
    }

    private void createProficiencyCompetencyMap() {
        proficiencyCompetencyMap = CompetencyMap.build(sourceCompetencies);
    }

    private void fetchUserProficiencyInSpecifiedSubjectAndDomains() {
        List<String> domains =
            destinationCompetencyMap.getDomains().stream().map(DomainCode::getCode).collect(Collectors.toList());
        sourceCompetencies = getTaxonomyDao()
            .fetchProficiencyForUserInSpecifiedSubjectAndDomains(model.getUserId().toString(), subjectCode,
                CollectionUtils.convertToSqlArrayOfString(domains));
    }

    private void createDestinationCompetencyMap() {
        destinationCompetencyMap = CompetencyMap.build(destinationCompetencies);
    }

    private void filterGutCodesForCompetencyForSpecifiedSubject() {
        // Query the db based on gut codes, and create competencies out of it
        destinationCompetencies = getTaxonomyDao()
            .transformGutCodesToCompetency(subjectCode, CollectionUtils.convertToSqlArrayOfString(destinationGutCodes));
        if (destinationCompetencies == null || destinationCompetencies.isEmpty()) {
            throw new IllegalStateException("Destination competencies not found for course: " + model.getCourseId());
        }
    }

    private void fetchDestinationGutCodes() {
        CompetencyFetcher competencyFetcher = CompetencyFetcher.build(defaultDSDbi);
        destinationGutCodes = competencyFetcher.fetchCompetenciesForCourse(model.getCourseId());
        if (destinationGutCodes == null || destinationGutCodes.isEmpty()) {
            LOGGER.warn("No aggregated competencies found for course: '{}'", model.getCourseId());
            throw new IllegalStateException("No aggregated competencies found for course: " + model.getCourseId());
        }
    }

    private void validateClassCourse() {
        ClassCourseValidatorDao classCourseValidatorDao = defaultDSDbi.onDemand(ClassCourseValidatorDao.class);
        if (model.getClassId() != null) {
            if (!classCourseValidatorDao.validateClassCourse(model.getClassId(), model.getCourseId())) {
                throw new IllegalStateException(
                    "Invalid class/course for request, class:" + model.getClassId() + ", course: " + model
                        .getCourseId());
            }
        } else {
            if (!classCourseValidatorDao.validateCourse(model.getCourseId())) {
                throw new IllegalStateException("Invalid course for request course: " + model.getCourseId());
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
            taxonomyDao = dsdbDSDbi.onDemand(TaxonomyDao.class);
        }
        return taxonomyDao;
    }

}

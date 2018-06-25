package org.gooru.route0.infra.services.competencyroutecalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.gooru.route0.infra.data.competency.CompetencyModel;
import org.gooru.route0.infra.data.competency.CompetencyPath;
import org.gooru.route0.infra.data.competency.CompetencyRoute;
import org.gooru.route0.infra.data.competency.DomainCode;
import org.gooru.route0.infra.data.competency.DomainModel;
import org.gooru.route0.infra.data.competency.ProgressionLevel;
import org.gooru.route0.infra.data.competency.SubjectCode;
import org.gooru.route0.infra.utils.CollectionUtils;
import org.skife.jdbi.v2.DBI;

/**
 * Builder class to help create {@link CompetencyRouteModel} from
 * {@link org.gooru.route0.infra.data.competency.CompetencyRoute}
 *
 * @author ashish.
 */
class CompetencyRouteModelBuilder {

    private final TaxonomyDao taxonomyDao;
    private SubjectCode subjectCode;
    private List<DomainModel> domains;  // This list is ordered
    private Map<DomainCode, List<CompetencyModel>> domainCodeCompetencyModelMap;
    private Map<DomainCode, List<CompetencyModel>> domainCompetencyMatrix;
    private List<String> domainsAsString;   // This is unordered
    private CompetencyRoute competencyRoute;

    CompetencyRouteModelBuilder(TaxonomyDao taxonomyDao) {
        this.taxonomyDao = taxonomyDao;
    }

    CompetencyRouteModelBuilder(DBI dbi) {
        this.taxonomyDao = dbi.onDemand(TaxonomyDao.class);
    }

    CompetencyRouteModel build(SubjectCode subjectCode, CompetencyRoute competencyRoute) {
        this.subjectCode = subjectCode;
        this.competencyRoute = competencyRoute;
        domainsAsString = competencyRoute.getDomains().stream().map(DomainCode::getCode).collect(Collectors.toList());
        fetchOrderedDomainModels();
        fetchDomainCompetencyMatrix();
        enrichCompetencyRouteWithDetails();
        return new CompetencyRouteModelImpl(subjectCode, domains, domainCodeCompetencyModelMap);
    }

    private void enrichCompetencyRouteWithDetails() {
        domainCodeCompetencyModelMap = new HashMap<>();

        for (DomainModel domainModel : domains) {
            CompetencyPath competencyPath = competencyRoute.getPathForDomain(domainModel.getDomainCode());
            if (!competencyPath.isPathInProgressionOrder() || competencyPath.getPath().isEmpty()) {
                continue;
            }
            enrichCompetencyPathForSpecifiedDomainModel(domainModel, competencyPath.getPath());
        }
    }

    private void enrichCompetencyPathForSpecifiedDomainModel(DomainModel domainModel,
        List<ProgressionLevel> progressionPath) {
        DomainCode domainCode = domainModel.getDomainCode();
        List<CompetencyModel> competenciesInDomain = domainCompetencyMatrix.get(domainCode);
        List<CompetencyModel> competenciesOnPath = new ArrayList<>();

        int currentItemInProgression = 0;

        for (CompetencyModel competencyModel : competenciesInDomain) {
            ProgressionLevel currentLevel = progressionPath.get(currentItemInProgression);
            if (currentLevel.getProgressionLevel() < competencyModel.getSequence()) {
                while (currentLevel.getProgressionLevel() < competencyModel.getSequence()) {
                    currentItemInProgression++;
                    if (currentItemInProgression >= progressionPath.size()) {
                        break;
                    }
                }
            }

            if (currentLevel.getProgressionLevel() < competencyModel.getSequence()) {
                competenciesOnPath.add(competencyModel);
                break;
            }

            if (currentLevel.getProgressionLevel() == competencyModel.getSequence()) {
                competenciesOnPath.add(competencyModel);
                currentItemInProgression++;
                if (currentItemInProgression >= progressionPath.size()) {
                    break;
                }
            }
        }
        domainCodeCompetencyModelMap.put(domainCode, competenciesOnPath);
    }

    private void fetchDomainCompetencyMatrix() {
        List<CompetencyModel> competencyModels = taxonomyDao
            .fetchDomainCompetencyMatrixForSpecifiedDomains(subjectCode.getCode(),
                CollectionUtils.convertToSqlArrayOfString(domainsAsString));
        DomainCode previousDomain = null;
        List<CompetencyModel> currentDomainsCompetencyModels = Collections.emptyList();
        domainCompetencyMatrix = new HashMap<>();

        for (CompetencyModel competencyModel : competencyModels) {
            DomainCode currentDomain = competencyModel.getDomainCode();
            if (previousDomain == null) {
                currentDomainsCompetencyModels = new ArrayList<>();
            } else if (!previousDomain.equals(currentDomain)) {
                domainCompetencyMatrix.put(previousDomain, currentDomainsCompetencyModels);
                currentDomainsCompetencyModels = new ArrayList<>();
            }
            previousDomain = currentDomain;
            currentDomainsCompetencyModels.add(competencyModel);
        }
        domainCompetencyMatrix.put(previousDomain, currentDomainsCompetencyModels);
    }

    private void fetchOrderedDomainModels() {
        domains = taxonomyDao.fetchDomainModelsForSpecifiedDomainsInOrder(subjectCode.getCode(),
            CollectionUtils.convertToSqlArrayOfString(domainsAsString));
    }
}

package org.gooru.route0.infra.services.competencyroutecalculator;

import java.util.ArrayList;
import java.util.List;

import org.gooru.route0.infra.data.competency.CompetencyModel;
import org.gooru.route0.infra.data.competency.DomainModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.json.JsonObject;

/**
 * The renderer which is used to render the {@link CompetencyRouteModel} in different formats.
 * Currently only supported format is JSON.
 *
 * @author ashish.
 */
class CompetencyRouteModelRenderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompetencyRouteModelRenderer.class);

    JsonObject asJson(CompetencyRouteModel competencyRouteModel) {
        List<DomainModel> domainModels = competencyRouteModel.getDomainsOrdered();
        List<DomainsJson> domainsJsonList = new ArrayList<>();
        for (DomainModel domainModel : domainModels) {
            List<CompetencyModel> competencyModels = competencyRouteModel.getPathForDomain(domainModel.getDomainCode());
            List<CompetencyJson> competenciesJson = new ArrayList<>();
            for (CompetencyModel competencyModel : competencyModels) {
                competenciesJson.add(CompetencyJson.fromCompetencyModel(competencyModel));
            }
            DomainsJson domainsJson = DomainsJson.fromDomainModel(domainModel);
            domainsJson.setPath(competenciesJson);
            domainsJsonList.add(domainsJson);
        }
        CompetencyRouteModelJson result = new CompetencyRouteModelJson();
        result.setSubjectCode(competencyRouteModel.getSubject().getCode());
        result.setDomains(domainsJsonList);

        try {
            return new JsonObject(new ObjectMapper().writeValueAsString(result));
        } catch (JsonProcessingException e) {
            LOGGER.warn("Not able to parse route as JSON", e);
            throw new IllegalStateException("Json parsing error", e);
        }
    }

    private static class CompetencyRouteModelJson {
        private String subjectCode;
        List<DomainsJson> domains;

        public String getSubjectCode() {
            return subjectCode;
        }

        public void setSubjectCode(String subjectCode) {
            this.subjectCode = subjectCode;
        }

        public List<DomainsJson> getDomains() {
            return domains;
        }

        public void setDomains(List<DomainsJson> domains) {
            this.domains = domains;
        }
    }

    private static class DomainsJson {
        private String domainCode;
        private String domainName;
        private Integer sequence;
        List<CompetencyJson> path;

        static DomainsJson fromDomainModel(DomainModel model) {
            DomainsJson domainsJson = new DomainsJson();
            domainsJson.domainCode = model.getDomainCode().getCode();
            domainsJson.domainName = model.getDomainName();
            domainsJson.sequence = model.getSequence();
            return domainsJson;
        }

        public String getDomainCode() {
            return domainCode;
        }

        public void setDomainCode(String domainCode) {
            this.domainCode = domainCode;
        }

        public String getDomainName() {
            return domainName;
        }

        public void setDomainName(String domainName) {
            this.domainName = domainName;
        }

        public Integer getSequence() {
            return sequence;
        }

        public void setSequence(Integer sequence) {
            this.sequence = sequence;
        }

        public List<CompetencyJson> getPath() {
            return path;
        }

        public void setPath(List<CompetencyJson> path) {
            this.path = path;
        }
    }

    private static class CompetencyJson {
        private String competencyCode;
        private String competencyName;
        private String competencyDescription;
        private String competencyStudentDescription;
        private Integer sequence;

        static CompetencyJson fromCompetencyModel(CompetencyModel model) {
            CompetencyJson competencyJson = new CompetencyJson();
            competencyJson.competencyCode = model.getCompetencyCode().getCode();
            competencyJson.competencyName = model.getCompetencyName();
            competencyJson.competencyDescription = model.getCompetencyDescription();
            competencyJson.competencyStudentDescription = model.getCompetencyStudentDescription();
            competencyJson.sequence = model.getSequence();
            return competencyJson;
        }

        public String getCompetencyCode() {
            return competencyCode;
        }

        public void setCompetencyCode(String competencyCode) {
            this.competencyCode = competencyCode;
        }

        public String getCompetencyName() {
            return competencyName;
        }

        public void setCompetencyName(String competencyName) {
            this.competencyName = competencyName;
        }

        public String getCompetencyDescription() {
            return competencyDescription;
        }

        public void setCompetencyDescription(String competencyDescription) {
            this.competencyDescription = competencyDescription;
        }

        public String getCompetencyStudentDescription() {
            return competencyStudentDescription;
        }

        public void setCompetencyStudentDescription(String competencyStudentDescription) {
            this.competencyStudentDescription = competencyStudentDescription;
        }

        public Integer getSequence() {
            return sequence;
        }

        public void setSequence(Integer sequence) {
            this.sequence = sequence;
        }
    }
}

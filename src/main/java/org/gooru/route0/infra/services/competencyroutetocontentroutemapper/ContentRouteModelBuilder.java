package org.gooru.route0.infra.services.competencyroutetocontentroutemapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gooru.route0.infra.data.competency.CompetencyCode;
import org.gooru.route0.infra.data.competency.CompetencyModel;
import org.gooru.route0.infra.data.competency.DomainModel;
import org.gooru.route0.infra.services.competencyroutecalculator.CompetencyRouteModel;
import org.gooru.route0.infra.services.suggestionprovider.SuggestedItem;
import org.gooru.route0.infra.services.suggestionprovider.SuggestionProvider;

/**
 * @author ashish.
 */
class ContentRouteModelBuilder {

    private final List<UnitModel> unitModels = new ArrayList<>();
    private final Map<UnitModel, List<LessonModel>> unitModelToLessonModelsMap = new HashMap<>();
    private final Map<LessonModel, List<CollectionModel>> lessonModelToCollectionModelsMap = new HashMap<>();
    private final Map<LessonModel, CompetencyModel> lessonModelCompetencyModelMap = new HashMap<>();

    private UUID userId;
    private CompetencyRouteModel competencyRouteModel;
    private List<CompetencyCode> competencyCodesCovered = new ArrayList<>();
    private Map<CompetencyCode, List<SuggestedItem>> competencyCodeToSuggestionsListMap;

    ContentRouteModel build(UUID userId, CompetencyRouteModel competencyRouteModel) {

        this.userId = userId;
        this.competencyRouteModel = competencyRouteModel;
        createUnitLessonData();
        fetchSuggestionsForCompetencies();
        populateCollectionAssessmentData();
        return new ContentRouteModelImpl(unitModels, unitModelToLessonModelsMap, lessonModelToCollectionModelsMap);
    }

    private void populateCollectionAssessmentData() {
        for (UnitModel unitModel : unitModels) {
            List<LessonModel> lessonModels = unitModelToLessonModelsMap.get(unitModel);
            for (LessonModel lessonModel : lessonModels) {
                CompetencyModel competencyModel = lessonModelCompetencyModelMap.get(lessonModel);
                List<SuggestedItem> suggestedItems =
                    competencyCodeToSuggestionsListMap.get(competencyModel.getCompetencyCode());
                int i = 0;
                List<CollectionModel> collectionModels = new ArrayList<>();
                for (SuggestedItem suggestedItem : suggestedItems) {
                    CollectionModel collectionModel = new CollectionModel(ModelIdGenerator.generateId(), null, ++i,
                        CollectionModel.CollectionModelType.builder(suggestedItem.getItemType().getName()));
                    collectionModels.add(collectionModel);
                }
                lessonModelToCollectionModelsMap.put(lessonModel, collectionModels);
            }
        }
    }

    private void fetchSuggestionsForCompetencies() {
        competencyCodeToSuggestionsListMap = SuggestionProvider.build().suggest(userId, competencyCodesCovered);
    }

    private void createUnitLessonData() {
        List<DomainModel> domainModels = competencyRouteModel.getDomainsOrdered();
        int i = 0;
        for (DomainModel domainModel : domainModels) {
            UnitModel unitModel = new UnitModel(ModelIdGenerator.generateId(), domainModel.getDomainName(), ++i);
            unitModelToLessonModelsMap.put(unitModel, createLessonsForDomain(domainModel));
            unitModels.add(unitModel);
        }
    }

    private List<LessonModel> createLessonsForDomain(DomainModel domainModel) {
        List<CompetencyModel> competencyModels = competencyRouteModel.getPathForDomain(domainModel.getDomainCode());
        List<LessonModel> lessonModels = new ArrayList<>();

        int i = 0;
        for (CompetencyModel competencyModel : competencyModels) {
            competencyCodesCovered.add(competencyModel.getCompetencyCode());

            String title = competencyModel.getCompetencyStudentDescription();
            if (title == null || title.isEmpty()) {
                title = competencyModel.getCompetencyName();
            }
            LessonModel lessonModel = new LessonModel(ModelIdGenerator.generateId(), title, i++);
            lessonModels.add(lessonModel);
            lessonModelCompetencyModelMap.put(lessonModel, competencyModel);
        }
        return lessonModels;
    }

}
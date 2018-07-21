package org.gooru.route0.infra.services.competencyroutetocontentroutemapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gooru.route0.infra.data.competency.CompetencyCode;
import org.gooru.route0.infra.data.competency.CompetencyModel;
import org.gooru.route0.infra.data.competency.DomainModel;
import org.gooru.route0.infra.services.ContentFetcherService;
import org.gooru.route0.infra.services.ContentFetcherServiceBuilder;
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
    private List<UUID> allSuggestedItems = new ArrayList<>();
    private Map<String, String> collectionIdTitleMap = new HashMap<>();

    ContentRouteModel build(UUID userId, CompetencyRouteModel competencyRouteModel) {

        this.userId = userId;
        this.competencyRouteModel = competencyRouteModel;
        createUnitLessonData();
        fetchSuggestionsForCompetencies();
        populateCollectionAssessmentData();
        filterEmptyContentPathFromRoutes();
        return new ContentRouteModelImpl(unitModels, unitModelToLessonModelsMap, lessonModelToCollectionModelsMap);
    }

    private void filterEmptyContentPathFromRoutes() {
        List<LessonModel> emptyLessons = new ArrayList<>(lessonModelToCollectionModelsMap.size());
        List<CollectionModel> collectionModels;
        for (Map.Entry<LessonModel, List<CollectionModel>> lessonModelListEntry : lessonModelToCollectionModelsMap
            .entrySet()) {
            collectionModels = lessonModelListEntry.getValue();
            if (collectionModels == null || collectionModels.isEmpty()) {
                emptyLessons.add(lessonModelListEntry.getKey());
            }
        }
        removeEmptyLessons(emptyLessons);
    }

    private void removeEmptyLessons(List<LessonModel> emptyLessons) {
        List<UnitModel> emptyUnitModels = new ArrayList<>();
        for (LessonModel emptyLesson : emptyLessons) {
            lessonModelToCollectionModelsMap.remove(emptyLesson);
        }
        for (Map.Entry<UnitModel, List<LessonModel>> unitModelListEntry : unitModelToLessonModelsMap.entrySet()) {
            List<LessonModel> lessonModels = unitModelListEntry.getValue();
            lessonModels.removeAll(emptyLessons);
            if (lessonModels.isEmpty()) {
                emptyUnitModels.add(unitModelListEntry.getKey());
            }
        }
        removeEmptyUnits(emptyUnitModels);
    }

    private void removeEmptyUnits(List<UnitModel> emptyUnitModels) {
        if (emptyUnitModels == null || emptyUnitModels.isEmpty()) {
            return;
        }
        unitModels.removeAll(emptyUnitModels);
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
                if (suggestedItems != null) {
                    for (SuggestedItem suggestedItem : suggestedItems) {
                        CollectionModel collectionModel = new CollectionModel(suggestedItem.getItemId(),
                            collectionIdTitleMap.get(suggestedItem.getItemId().toString()), ++i,
                            CollectionModel.CollectionModelType.builder(suggestedItem.getItemType().getName()));
                        collectionModels.add(collectionModel);
                    }
                }
                lessonModelToCollectionModelsMap.put(lessonModel, collectionModels);
            }
        }
    }

    private void fetchSuggestionsForCompetencies() {
        competencyCodeToSuggestionsListMap = SuggestionProvider.build().suggest(userId, competencyCodesCovered);
        fetchTitlesForSuggestions();
    }

    private void fetchTitlesForSuggestions() {
        accumulateAllSuggestedItemIds();
        ContentFetcherService contentFetcherService = ContentFetcherServiceBuilder.build();
        collectionIdTitleMap = contentFetcherService.fetchCollectionIdTitleMap(allSuggestedItems);

    }

    private void accumulateAllSuggestedItemIds() {
        for (Map.Entry<CompetencyCode, List<SuggestedItem>> entry : competencyCodeToSuggestionsListMap.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                for (SuggestedItem suggestedItem : entry.getValue()) {
                    allSuggestedItems.add(suggestedItem.getItemId());
                }
            }
        }
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

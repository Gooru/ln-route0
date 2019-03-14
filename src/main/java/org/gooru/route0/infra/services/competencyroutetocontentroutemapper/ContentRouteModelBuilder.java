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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */
class ContentRouteModelBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContentRouteModelBuilder.class);
  private final List<UnitModel> unitModels = new ArrayList<>();
  private final Map<UnitModel, List<LessonModel>> unitModelToLessonModelsMap = new HashMap<>();
  private final Map<LessonModel, List<CollectionModel>> lessonModelToCollectionModelsMap = new HashMap<>();

  private UUID userId;
  private Integer primaryLanguage;
  private CompetencyRouteModel competencyRouteModel;
  private List<CompetencyCode> competencyCodesCovered = new ArrayList<>();
  private Map<CompetencyCode, List<SuggestedItem>> competencyCodeToSuggestionsListMap;
  private List<UUID> allSuggestedItems = new ArrayList<>();
  private Map<String, String> collectionIdTitleMap = new HashMap<>();
  private final List<DomainModel> nonEmptyDomains = new ArrayList<>();
  private final Map<DomainModel, List<CompetencyModel>> nonEmptyDomainCompetencyMap = new HashMap<>();
  private final Map<LessonModel, CompetencyModel> lessonModelCompetencyModelMap = new HashMap<>();

  ContentRouteModel build(UUID userId, CompetencyRouteModel competencyRouteModel, Integer primaryLanguage) {

    this.userId = userId;
    this.competencyRouteModel = competencyRouteModel;
    this.primaryLanguage = primaryLanguage;
    LOGGER.debug("Initialize competency codes for which R0 suggestions are to be fetched");
    initializeCompetenciesCovered();
    LOGGER.debug("Fetch suggestions for specified competencies");
    fetchSuggestionsForCompetencies();
    LOGGER.debug("Populate valid tree by pruning empty items");
    populateValidTree();
    LOGGER.debug("Creating units/lesson data");
    createUnitLessonData();
    LOGGER.debug("Populating collections/assessment data");
    populateCollectionAssessmentData();

    return new ContentRouteModelImpl(unitModels, unitModelToLessonModelsMap,
        lessonModelToCollectionModelsMap);
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

  private void createUnitLessonData() {
    int i = 0;
    for (DomainModel domainModel : nonEmptyDomains) {
      UnitModel unitModel = new UnitModel(ModelIdGenerator.generateId(),
          domainModel.getDomainName(), ++i);
      unitModelToLessonModelsMap.put(unitModel, createLessonsForDomain(domainModel));
      unitModels.add(unitModel);
    }
  }

  private List<LessonModel> createLessonsForDomain(DomainModel domainModel) {
    List<CompetencyModel> competencyModels = nonEmptyDomainCompetencyMap.get(domainModel);
    List<LessonModel> lessonModels = new ArrayList<>();

    int i = 0;
    for (CompetencyModel competencyModel : competencyModels) {
      String title = competencyModel.getCompetencyStudentDescription();
      if (title == null || title.isEmpty()) {
        title = competencyModel.getCompetencyName();
      }
      LessonModel lessonModel = new LessonModel(ModelIdGenerator.generateId(), title, ++i);
      lessonModels.add(lessonModel);
      lessonModelCompetencyModelMap.put(lessonModel, competencyModel);
    }
    return lessonModels;
  }


  private void populateValidTree() {
    List<DomainModel> domainsOrdered = competencyRouteModel.getDomainsOrdered();
    for (DomainModel domainModel : domainsOrdered) {
      List<CompetencyModel> pathForDomain = competencyRouteModel
          .getPathForDomain(domainModel.getDomainCode());
      List<CompetencyModel> nonEmptyCompetencyModel = new ArrayList<>();
      for (CompetencyModel competencyModel : pathForDomain) {
        if (competencyCodeToSuggestionsListMap.get(competencyModel.getCompetencyCode()) != null) {
          nonEmptyCompetencyModel.add(competencyModel);
        }
      }
      if (!nonEmptyCompetencyModel.isEmpty()) {
        nonEmptyDomains.add(domainModel);
        nonEmptyDomainCompetencyMap.put(domainModel, nonEmptyCompetencyModel);
      }
    }
  }


  private void fetchSuggestionsForCompetencies() {
    competencyCodeToSuggestionsListMap = SuggestionProvider.build()
        .suggest(userId, competencyCodesCovered, primaryLanguage);
    fetchTitlesForSuggestions();
  }

  private void fetchTitlesForSuggestions() {
    accumulateAllSuggestedItemIds();
    ContentFetcherService contentFetcherService = ContentFetcherServiceBuilder.build();
    collectionIdTitleMap = contentFetcherService.fetchCollectionIdTitleMap(allSuggestedItems);

  }

  private void accumulateAllSuggestedItemIds() {
    for (Map.Entry<CompetencyCode, List<SuggestedItem>> entry : competencyCodeToSuggestionsListMap
        .entrySet()) {
      if (entry.getValue() != null && !entry.getValue().isEmpty()) {
        for (SuggestedItem suggestedItem : entry.getValue()) {
          allSuggestedItems.add(suggestedItem.getItemId());
        }
      }
    }
  }

  private void initializeCompetenciesCovered() {
    List<DomainModel> domainsOrdered = competencyRouteModel.getDomainsOrdered();
    for (DomainModel domainModel : domainsOrdered) {
      List<CompetencyModel> pathForDomain = competencyRouteModel
          .getPathForDomain(domainModel.getDomainCode());
      for (CompetencyModel competencyModel : pathForDomain) {
        competencyCodesCovered.add(competencyModel.getCompetencyCode());
      }
    }
  }

}

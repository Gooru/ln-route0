package org.gooru.route0.infra.services.competencyroutetocontentroutemapper;

import io.vertx.core.json.JsonObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gooru.route0.infra.data.UserRoute0ContentDetailModel;

/**
 * @author ashish.
 */
class ContentRouteModelImpl implements ContentRouteModel {

  private final List<UnitModel> unitModels;
  private final Map<UnitModel, List<LessonModel>> unitModelToLessonModelsMap;
  private final Map<LessonModel, List<CollectionModel>> lessonModelToCollectionModelsMap;
  private JsonObject jsonRepresentation;

  ContentRouteModelImpl(List<UnitModel> unitModels,
      Map<UnitModel, List<LessonModel>> unitModelToLessonModelsMap,
      Map<LessonModel, List<CollectionModel>> lessonModelToCollectionModelsMap) {
    this.unitModels = Collections.unmodifiableList(unitModels);
    this.unitModelToLessonModelsMap = Collections.unmodifiableMap(unitModelToLessonModelsMap);
    this.lessonModelToCollectionModelsMap = Collections
        .unmodifiableMap(lessonModelToCollectionModelsMap);
  }

  @Override
  public List<UnitModel> getUnitsOrdered() {
    return unitModels;
  }

  @Override
  public List<LessonModel> getLessonsOrderedForUnit(UnitModel unitModel) {
    return unitModelToLessonModelsMap.get(unitModel);
  }

  @Override
  public List<CollectionModel> getCollectionsOrderedInLesson(LessonModel lessonModel) {
    return lessonModelToCollectionModelsMap.get(lessonModel);
  }

  @Override
  public ContentRouteModel hydrateWithContentRouteDetail(
      List<UserRoute0ContentDetailModel> models) {
    Map<String, CollectionModel> dummyIdCollectionModelMap = new HashMap<>();
    // Note that we are using dummy key which is combination of lesson id and collection id just to handle the
    // situation where because of content scarcity if we end up mapping multiple competencies to same item, then
    // with the collection id as key, we shall lose one of the items
    String dummyKey;
    for (Map.Entry<LessonModel, List<CollectionModel>> lessonModelListEntry : lessonModelToCollectionModelsMap
        .entrySet()) {
      LessonModel lessonModel = lessonModelListEntry.getKey();
      List<CollectionModel> collectionModels = lessonModelListEntry.getValue();
      for (CollectionModel collectionModel : collectionModels) {
        dummyKey = lessonModel.getId().toString() + collectionModel.getId().toString();
        dummyIdCollectionModelMap.put(dummyKey, collectionModel);
      }
    }

    for (UserRoute0ContentDetailModel model : models) {
      dummyKey = model.getLessonId().toString() + model.getCollectionId().toString();
      CollectionModel collectionModel = dummyIdCollectionModelMap.get(dummyKey);
      if (collectionModel != null) {
        collectionModel.setPathId(model.getId());
      }
    }

    return new ContentRouteModelImpl(this.unitModels, this.unitModelToLessonModelsMap,
        this.lessonModelToCollectionModelsMap);
  }

  @Override
  public JsonObject toJson() {
    if (jsonRepresentation == null) {
      jsonRepresentation = new ContentRouteModelJsonRenderer().render(this);
    }
    return jsonRepresentation;
  }
}

package org.gooru.route0.infra.services.competencyroutetocontentroutemapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.vertx.core.json.JsonObject;

/**
 * @author ashish.
 */
class ContentRouteModelImpl implements ContentRouteModel {
    private final List<UnitModel> unitModels;
    private final Map<UnitModel, List<LessonModel>> unitModelToLessonModelsMap;
    private final Map<LessonModel, List<CollectionModel>> lessonModelToCollectionModelsMap;
    private JsonObject jsonRepresentation;

    ContentRouteModelImpl(List<UnitModel> unitModels, Map<UnitModel, List<LessonModel>> unitModelToLessonModelsMap,
        Map<LessonModel, List<CollectionModel>> lessonModelToCollectionModelsMap) {
        this.unitModels = Collections.unmodifiableList(unitModels);
        this.unitModelToLessonModelsMap = Collections.unmodifiableMap(unitModelToLessonModelsMap);
        this.lessonModelToCollectionModelsMap = Collections.unmodifiableMap(lessonModelToCollectionModelsMap);
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
    public JsonObject toJson() {
        if (jsonRepresentation == null) {
            jsonRepresentation = new ContentRouteModelJsonRenderer().render(this);
        }
        return jsonRepresentation;
    }
}

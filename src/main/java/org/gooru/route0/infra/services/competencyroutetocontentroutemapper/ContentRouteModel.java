package org.gooru.route0.infra.services.competencyroutetocontentroutemapper;

import java.util.List;

import io.vertx.core.json.JsonObject;

/**
 * @author ashish.
 */
public interface ContentRouteModel {

    List<UnitModel> getUnitsOrdered();

    List<LessonModel> getLessonsOrderedForUnit(UnitModel unitModel);

    List<CollectionModel> getCollectionsOrderedInLesson(LessonModel lessonModel);

    JsonObject toJson();

}

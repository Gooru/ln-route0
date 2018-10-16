package org.gooru.route0.infra.services.competencyroutetocontentroutemapper;

import io.vertx.core.json.JsonObject;
import java.util.List;
import org.gooru.route0.infra.data.UserRoute0ContentDetailModel;

/**
 * @author ashish.
 */
public interface ContentRouteModel {

  List<UnitModel> getUnitsOrdered();

  List<LessonModel> getLessonsOrderedForUnit(UnitModel unitModel);

  List<CollectionModel> getCollectionsOrderedInLesson(LessonModel lessonModel);

  ContentRouteModel hydrateWithContentRouteDetail(List<UserRoute0ContentDetailModel> models);

  JsonObject toJson();

}

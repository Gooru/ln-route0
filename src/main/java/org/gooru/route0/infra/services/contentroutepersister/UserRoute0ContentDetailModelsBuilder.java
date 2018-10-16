package org.gooru.route0.infra.services.contentroutepersister;

import java.util.ArrayList;
import java.util.List;
import org.gooru.route0.infra.data.UserRoute0ContentDetailModel;
import org.gooru.route0.infra.services.competencyroutetocontentroutemapper.CollectionModel;
import org.gooru.route0.infra.services.competencyroutetocontentroutemapper.ContentRouteModel;
import org.gooru.route0.infra.services.competencyroutetocontentroutemapper.LessonModel;
import org.gooru.route0.infra.services.competencyroutetocontentroutemapper.UnitModel;

/**
 * @author ashish.
 */
class UserRoute0ContentDetailModelsBuilder {

  private List<UserRoute0ContentDetailModel> result = new ArrayList<>();
  private ContentRouteModel model;
  private int route0Index = 0;
  private Long route0ContentId = null;

  List<UserRoute0ContentDetailModel> build(ContentRouteModel model) {
    return build(model, null);
  }

  List<UserRoute0ContentDetailModel> build(ContentRouteModel model, Long route0ContentId) {
    this.model = model;
    this.route0ContentId = route0ContentId;

    List<UnitModel> unitModels = model.getUnitsOrdered();
    for (UnitModel unitModel : unitModels) {
      buildFromLessonModelsForUnit(unitModel);
    }
    return result;
  }

  private void buildFromLessonModelsForUnit(UnitModel unitModel) {
    List<LessonModel> lessonModels = model.getLessonsOrderedForUnit(unitModel);
    for (LessonModel lessonModel : lessonModels) {
      buildFromCollectionModelsForLesson(unitModel, lessonModel);
    }
  }

  private void buildFromCollectionModelsForLesson(UnitModel unitModel, LessonModel lessonModel) {
    List<CollectionModel> collectionModels = model.getCollectionsOrderedInLesson(lessonModel);
    for (CollectionModel collectionModel : collectionModels) {
      route0Index++;
      UserRoute0ContentDetailModel resultModel =
          buildUserRoute0ContentDetailModel(unitModel, lessonModel, collectionModel);
      result.add(resultModel);
    }
  }

  private UserRoute0ContentDetailModel buildUserRoute0ContentDetailModel(UnitModel unitModel,
      LessonModel lessonModel,
      CollectionModel collectionModel) {
    UserRoute0ContentDetailModel resultModel = new UserRoute0ContentDetailModel();
    resultModel.setUnitId(unitModel.getId());
    resultModel.setUnitTitle(unitModel.getTitle());
    resultModel.setUnitSequence(unitModel.getSequence());
    resultModel.setLessonId(lessonModel.getId());
    resultModel.setLessonTitle(lessonModel.getTitle());
    resultModel.setLessonSequence(lessonModel.getSequence());
    resultModel.setCollectionId(collectionModel.getId());
    resultModel.setCollectionType(collectionModel.getType());
    resultModel.setCollectionSequence(collectionModel.getSequence());
    resultModel.setRoute0Sequence(route0Index);
    resultModel.setUserRoute0ContentId(route0ContentId);
    return resultModel;
  }
}

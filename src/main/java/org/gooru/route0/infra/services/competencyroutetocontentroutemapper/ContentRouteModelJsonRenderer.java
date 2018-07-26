package org.gooru.route0.infra.services.competencyroutetocontentroutemapper;

import static org.gooru.route0.infra.services.competencyroutetocontentroutemapper.ContentRouteModelJsonRepresentation.*;

import java.util.ArrayList;
import java.util.List;

import org.gooru.route0.infra.constants.HttpConstants;
import org.gooru.route0.infra.exceptions.HttpResponseWrapperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.json.JsonObject;

/**
 * @author ashish.
 */
class ContentRouteModelJsonRenderer {

    private ContentRouteModel model;
    private final ContentRouteModelJsonRepresentation modelJsonRepresentation =
        new ContentRouteModelJsonRepresentation();
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentRouteModelJsonRenderer.class);

    JsonObject render(ContentRouteModel model) {
        this.model = model;
        List<UnitModel> unitsOrdered = model.getUnitsOrdered();
        List<UnitModelJsonRepresentation> unitModelJsonRepresentations = new ArrayList<>();
        for (UnitModel unitModel : unitsOrdered) {
            UnitModelJsonRepresentation unitModelJsonRepresentation = new UnitModelJsonRepresentation();
            unitModelJsonRepresentation.setUnitId(unitModel.getId().toString());
            unitModelJsonRepresentation.setUnitTitle(unitModel.getTitle());
            unitModelJsonRepresentation.setUnitSequence(unitModel.getSequence());
            unitModelJsonRepresentation.setLessons(getLessonModelJsonRepresentationForUnit(unitModel));
            unitModelJsonRepresentations.add(unitModelJsonRepresentation);
        }
        modelJsonRepresentation.setUnits(unitModelJsonRepresentations);

        try {
            String jsonString = new ObjectMapper().writeValueAsString(modelJsonRepresentation);
            return new JsonObject(jsonString);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Not able to convert object to JSON", e);
            throw new HttpResponseWrapperException(HttpConstants.HttpStatus.ERROR, "Json conversion error");
        }
    }

    private List<LessonModelJsonRepresentation> getLessonModelJsonRepresentationForUnit(UnitModel unitModel) {
        List<LessonModel> lessonModels = model.getLessonsOrderedForUnit(unitModel);
        List<LessonModelJsonRepresentation> lessonModelJsonRepresentations = new ArrayList<>();
        for (LessonModel lessonModel : lessonModels) {
            LessonModelJsonRepresentation lessonModelJsonRepresentation = new LessonModelJsonRepresentation();
            lessonModelJsonRepresentation.setLessonId(lessonModel.getId().toString());
            lessonModelJsonRepresentation.setLessonTitle(lessonModel.getTitle());
            lessonModelJsonRepresentation.setLessonSequence(lessonModel.getSequence());
            lessonModelJsonRepresentation.setCollections(getCollectionModelJsonRepresentationForLesson(lessonModel));
            lessonModelJsonRepresentations.add(lessonModelJsonRepresentation);
        }
        return lessonModelJsonRepresentations;
    }

    private List<CollectionModelJsonRepresentation> getCollectionModelJsonRepresentationForLesson(
        LessonModel lessonModel) {
        List<CollectionModel> collectionModels = model.getCollectionsOrderedInLesson(lessonModel);
        List<CollectionModelJsonRepresentation> collectionModelJsonRepresentations = new ArrayList<>();
        for (CollectionModel collectionModel : collectionModels) {
            CollectionModelJsonRepresentation collectionModelJsonRepresentation =
                new CollectionModelJsonRepresentation();
            collectionModelJsonRepresentation.setCollectionId(collectionModel.getId().toString());
            collectionModelJsonRepresentation.setCollectionSequence(collectionModel.getSequence());
            collectionModelJsonRepresentation.setTitle(collectionModel.getTitle());
            collectionModelJsonRepresentation.setCollectionType(collectionModel.getType().getName());
            collectionModelJsonRepresentation.setPathId(collectionModel.getPathId());
            collectionModelJsonRepresentations.add(collectionModelJsonRepresentation);
        }
        return collectionModelJsonRepresentations;
    }
}

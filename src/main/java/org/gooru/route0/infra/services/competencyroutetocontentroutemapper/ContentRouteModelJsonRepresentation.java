package org.gooru.route0.infra.services.competencyroutetocontentroutemapper;

import java.util.List;

/**
 * @author ashish.
 */
class ContentRouteModelJsonRepresentation {
    private List<UnitModelJsonRepresentation> units;

    public List<UnitModelJsonRepresentation> getUnits() {
        return units;
    }

    public void setUnits(List<UnitModelJsonRepresentation> units) {
        this.units = units;
    }

    static class UnitModelJsonRepresentation {
        private String unitId;
        private String unitTitle;
        private int unitSequence;
        private List<LessonModelJsonRepresentation> lessons;

        public String getUnitId() {
            return unitId;
        }

        public void setUnitId(String unitId) {
            this.unitId = unitId;
        }

        public String getUnitTitle() {
            return unitTitle;
        }

        public void setUnitTitle(String unitTitle) {
            this.unitTitle = unitTitle;
        }

        public int getUnitSequence() {
            return unitSequence;
        }

        public void setUnitSequence(int unitSequence) {
            this.unitSequence = unitSequence;
        }

        public List<LessonModelJsonRepresentation> getLessons() {
            return lessons;
        }

        public void setLessons(List<LessonModelJsonRepresentation> lessons) {
            this.lessons = lessons;
        }
    }

    static class LessonModelJsonRepresentation {
        private String lessonId;
        private String lessonTitle;
        private int lessonSequence;
        private List<CollectionModelJsonRepresentation> collections;

        public String getLessonId() {
            return lessonId;
        }

        public void setLessonId(String lessonId) {
            this.lessonId = lessonId;
        }

        public String getLessonTitle() {
            return lessonTitle;
        }

        public void setLessonTitle(String lessonTitle) {
            this.lessonTitle = lessonTitle;
        }

        public int getLessonSequence() {
            return lessonSequence;
        }

        public void setLessonSequence(int lessonSequence) {
            this.lessonSequence = lessonSequence;
        }

        public List<CollectionModelJsonRepresentation> getCollections() {
            return collections;
        }

        public void setCollections(List<CollectionModelJsonRepresentation> collections) {
            this.collections = collections;
        }
    }

    static class CollectionModelJsonRepresentation {
        private String collectionId;
        private String collectionType;
        private int collectionSequence;

        public String getCollectionId() {
            return collectionId;
        }

        public void setCollectionId(String collectionId) {
            this.collectionId = collectionId;
        }

        public String getCollectionType() {
            return collectionType;
        }

        public void setCollectionType(String collectionType) {
            this.collectionType = collectionType;
        }

        public int getCollectionSequence() {
            return collectionSequence;
        }

        public void setCollectionSequence(int collectionSequence) {
            this.collectionSequence = collectionSequence;
        }
    }

}

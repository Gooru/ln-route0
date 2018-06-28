package org.gooru.route0.infra.services.competencyroutetocontentroutemapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author ashish.
 */
public class CollectionModel {
    private final UUID id;
    private final String title;
    private final int sequence;
    private final CollectionModelType type;

    public CollectionModel(UUID id, String title, int sequence, CollectionModelType type) {
        this.id = id;
        this.title = title;
        this.sequence = sequence;
        this.type = type;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getSequence() {
        return sequence;
    }

    public CollectionModelType getType() {
        return type;
    }

    public boolean isAssessment() {
        return type == CollectionModelType.ASSESSMENT;
    }

    public boolean isCollection() {
        return type == CollectionModelType.COLLECTION;
    }

    public boolean isAssessmentExternal() {
        return type == CollectionModelType.ASSESSMENT_EXTERAL;
    }

    public boolean isCollectionExternal() {
        return type == CollectionModelType.COLLECTION_EXTERNAL;
    }

    public enum CollectionModelType {
        ASSESSMENT("assessment"),
        COLLECTION("collection"),
        COLLECTION_EXTERNAL("collection-external"),
        ASSESSMENT_EXTERAL("assessment-external");

        private final String name;

        CollectionModelType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        private static final Map<String, CollectionModelType> LOOKUP = new HashMap<>(values().length);

        static {
            for (CollectionModelType currentItemType : values()) {
                LOOKUP.put(currentItemType.name, currentItemType);
            }
        }

        public static CollectionModelType builder(String type) {
            CollectionModelType result = LOOKUP.get(type);
            if (result == null) {
                throw new IllegalArgumentException("Invalid CollectionModelType: " + type);
            }
            return result;
        }

    }

}

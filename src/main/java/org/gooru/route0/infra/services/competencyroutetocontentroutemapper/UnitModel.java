package org.gooru.route0.infra.services.competencyroutetocontentroutemapper;

import java.util.UUID;

/**
 * @author ashish.
 */
public class UnitModel {
    private final UUID id;
    private final String title;
    private final int sequence;

    public UnitModel(UUID id, String title, int sequence) {
        this.id = id;
        this.title = title;
        this.sequence = sequence;
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
}

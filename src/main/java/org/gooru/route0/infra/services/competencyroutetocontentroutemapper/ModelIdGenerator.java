package org.gooru.route0.infra.services.competencyroutetocontentroutemapper;

import java.util.UUID;

/**
 * @author ashish.
 */
final class ModelIdGenerator {

    private ModelIdGenerator() {
        throw new AssertionError();
    }

    public static UUID generateId() {
        return UUID.randomUUID();
    }
}

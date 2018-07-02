package org.gooru.route0.infra.services.suggestionprovider;

import java.util.UUID;

/**
 * @author ashish.
 */
public interface SuggestedItem {

    UUID getItemId();
    ItemType getItemType();
}

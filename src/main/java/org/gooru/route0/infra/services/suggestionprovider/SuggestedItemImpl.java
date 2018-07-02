package org.gooru.route0.infra.services.suggestionprovider;

import java.util.UUID;

import org.gooru.route0.infra.data.competency.CompetencyCode;

/**
 * @author ashish.
 */
public class SuggestedItemImpl implements SuggestedItem {
    private final CompetencyCode competencyCode;
    private final UUID itemId;
    private final ItemType itemType;

    public SuggestedItemImpl(CompetencyCode competencyCode, UUID itemId, ItemType itemType) {
        this.competencyCode = competencyCode;
        this.itemId = itemId;
        this.itemType = itemType;
    }

    @Override
    public CompetencyCode getCompetencyCode() {
        return competencyCode;
    }

    @Override
    public UUID getItemId() {
        return itemId;
    }

    @Override
    public ItemType getItemType() {
        return itemType;
    }
}

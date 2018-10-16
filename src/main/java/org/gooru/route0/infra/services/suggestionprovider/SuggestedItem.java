package org.gooru.route0.infra.services.suggestionprovider;

import java.util.UUID;
import org.gooru.route0.infra.data.competency.CompetencyCode;

/**
 * @author ashish.
 */
public interface SuggestedItem {

  CompetencyCode getCompetencyCode();

  UUID getItemId();

  ItemType getItemType();
}

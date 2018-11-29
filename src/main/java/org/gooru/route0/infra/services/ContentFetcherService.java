package org.gooru.route0.infra.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author ashish.
 */
public interface ContentFetcherService {

  Map<String, String> fetchCollectionIdTitleMap(List<UUID> collectionIds);

}

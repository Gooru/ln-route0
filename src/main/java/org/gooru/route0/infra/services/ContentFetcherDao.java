package org.gooru.route0.infra.services;

import java.util.List;
import java.util.UUID;

import org.gooru.route0.infra.jdbi.PGArray;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

/**
 * @author ashish.
 */
interface ContentFetcherDao {

    @Mapper(ContentFetcherServiceImpl.IdTitleHolderMapper.class)
    @SqlQuery("select id::text, title from collection where id = any(:collectionIds) and is_deleted = false")
    List<ContentFetcherServiceImpl.IdTitleHolder> fetchCollectionIdAndTitle(
        @Bind("collectionIds") PGArray<UUID> collectionIds);

}

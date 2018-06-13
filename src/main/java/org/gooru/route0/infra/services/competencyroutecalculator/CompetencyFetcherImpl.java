package org.gooru.route0.infra.services.competencyroutecalculator;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.skife.jdbi.v2.DBI;

/**
 * @author ashish.
 */
class CompetencyFetcherImpl implements CompetencyFetcher {

    private final DBI dbi;
    private CompetencyFetcherDao dao;

    CompetencyFetcherImpl(DBI dbi) {
        this.dbi = dbi;
    }

    @Override
    public List<String> fetchCompetenciesForCourse(UUID courseId) {
        if (courseId != null) {
            List<List<String>> listOfListOfComps = getCompetencyFetcherDao().findCompetenciesForCourse(courseId);
            return (listOfListOfComps != null && !listOfListOfComps.isEmpty()) ? listOfListOfComps.get(0) :
                Collections.emptyList();
        }
        return Collections.emptyList();
    }

    private CompetencyFetcherDao getCompetencyFetcherDao() {
        if (dao == null) {
            dao = dbi.onDemand(CompetencyFetcherDao.class);
        }
        return dao;
    }

}

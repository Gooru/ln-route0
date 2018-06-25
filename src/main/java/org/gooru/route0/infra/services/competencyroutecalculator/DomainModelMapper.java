package org.gooru.route0.infra.services.competencyroutecalculator;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.gooru.route0.infra.data.competency.DomainCode;
import org.gooru.route0.infra.data.competency.DomainModel;
import org.gooru.route0.infra.data.competency.SubjectCode;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */
public class DomainModelMapper implements ResultSetMapper<DomainModel> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainModelMapper.class);

    @Override
    public DomainModel map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        String subjectCode = r.getString(MapperFields.TX_SUBJECT_CODE);
        String domainCode = r.getString(MapperFields.TX_DOMAIN_CODE);
        String domainName = r.getString(MapperFields.TX_DOMAIN_NAME);
        Integer domainSeq = r.getInt(MapperFields.TX_DOMAIN_SEQ);

        return new DomainModel(new SubjectCode(subjectCode), new DomainCode(domainCode), domainName, domainSeq);
    }

    private static class MapperFields {
        private static final String TX_SUBJECT_CODE = "tx_subject_code";
        private static final String TX_DOMAIN_CODE = "tx_domain_code";
        private static final String TX_DOMAIN_NAME = "tx_domain_name";
        private static final String TX_DOMAIN_SEQ = "tx_domain_seq";
    }

}

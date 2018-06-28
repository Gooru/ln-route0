package org.gooru.route0.processors.acceptrejectroute0;

import org.gooru.route0.infra.constants.HttpConstants;
import org.gooru.route0.infra.data.Route0StatusValues;
import org.gooru.route0.infra.exceptions.HttpResponseWrapperException;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Accept or Reject the Route0 for user.
 * <p>
 * Do not allow update if current status is NA.
 * Also if user has already accepted it, they cannot reject it.
 * However, if it is pending user can accept or reject it.
 * If it is rejected, user can accept it. As an optimization,
 * if the status value in DB is same as user provided value,
 * do not update db. Just short circuit and return success.
 *
 * @author ashish.
 */
class AcceptRejectRoute0Service {
    private final DBI dbi;
    private AcceptRejectRoute0Dao dao;
    private AcceptRejectRoute0Command command;
    private AcceptRejectRoute0Command.AcceptRejectRoute0CommandBean model;
    private String route0Status;
    private static final Logger LOGGER = LoggerFactory.getLogger(AcceptRejectRoute0Service.class);

    AcceptRejectRoute0Service(DBI dbi) {
        this.dbi = dbi;
    }

    void acceptRejectRoute0(AcceptRejectRoute0Command command) {
        this.command = command;
        dao = dbi.onDemand(AcceptRejectRoute0Dao.class);
        model = command.asBean();
        fetchStatusForSpecifiedRoute0();
        updateRoute0Status();
    }

    private void updateRoute0Status() {
        if (route0Status.equals(command.getStatus())) {
            LOGGER.info("Accepting or rejecting already accepted or rejected Route0 respectively. Doing nothing.");
            return;
        }
        if (command.getClassId() == null) {
            dao.updateStatusForRoute0InIL(model);
        } else {
            dao.updateStatusForRoute0InClass(model);
        }
    }

    private void fetchStatusForSpecifiedRoute0() {
        if (command.getClassId() == null) {
            route0Status = dao.fetchStatusForRoute0InIL(model);
        } else {
            route0Status = dao.fetchStatusForRoute0InClass(model);
        }
        validateRoute0Status();
    }

    private void validateRoute0Status() {
        if (route0Status == null) {
            LOGGER.warn("Record not found for context: user: '{}', class: '{}', course: '{}'", command.getUserId(),
                command.getClassId(), command.getCourseId());
            throw new HttpResponseWrapperException(HttpConstants.HttpStatus.NOT_FOUND, "Route0 not found for user");
        }

        if (Route0StatusValues.isStatusNotApplicable(route0Status)) {
            LOGGER.warn("User is already at destination. Cannot update status.");
            throw new HttpResponseWrapperException(HttpConstants.HttpStatus.BAD_REQUEST,
                "Route0 not applicable as user is already at destination");
        }

        if (Route0StatusValues.isStatusAccepted(route0Status) && Route0StatusValues
            .isStatusRejected(model.getStatus())) {
            LOGGER.warn("User has already accepted route0. Cannot reject now");
            throw new HttpResponseWrapperException(HttpConstants.HttpStatus.BAD_REQUEST,
                "Cannot reject Route0 once it is accepted");
        }
    }
}

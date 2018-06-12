package org.gooru.route0.processors.fetchroute0content;

import org.gooru.route0.infra.constants.HttpConstants;
import org.gooru.route0.infra.exceptions.HttpResponseWrapperException;
import org.gooru.route0.infra.services.Route0ApplicableService;
import org.gooru.route0.infra.services.Route0RequestQueueService;
import org.skife.jdbi.v2.DBI;

/**
 * @author ashish.
 */
class FetchRoute0dContentService {

    private final DBI dbi;
    private FetchRoute0ContentCommand command;

    FetchRoute0dContentService(DBI dbi) {
        this.dbi = dbi;
    }

    String fetchRoute0Content(FetchRoute0ContentCommand command) {
        this.command = command;
        String result;

        if (command.getClassId() != null) {
            if (command.isTeacherContext()) {
                validateUserIsReallyTeacher();
            }
            result = fetchRoute0ContentForClass();
        } else {
            result = fetchRoute0ContentForIL();
        }
        queueRoute0ContentRequestIfNeeded(result);
        return result;
    }

    private void validateUserIsReallyTeacher() {
        if (!getDao().isUserTeacherOrCollaboratorForClass(command.asBean())) {
            throw new HttpResponseWrapperException(HttpConstants.HttpStatus.FORBIDDEN,
                "You need to be teacher or co-teacher for this class");
        }
    }

    private void queueRoute0ContentRequestIfNeeded(String result) {
        if (result == null) {
            Route0RequestQueueService service = Route0RequestQueueService.build();
            service.enqueue(command.asRoute0Context());
        }
    }

    private String fetchRoute0ContentForIL() {
        if (Route0ApplicableService.isRoute0ApplicableToCourseInIL(command.getCourseId())) {
            return getDao().fetchRoute0ContentForUserInIL(command.asBean());
        } else {
            throw new HttpResponseWrapperException(HttpConstants.HttpStatus.BAD_REQUEST,
                "Route0 not applicable to specified course/class");
        }

    }

    private String fetchRoute0ContentForClass() {
        if (Route0ApplicableService.isRoute0ApplicableToClass(command.getClassId())) {
            return getDao().fetchRoute0ContentForUserInClass(command.asBean());
        } else {
            throw new HttpResponseWrapperException(HttpConstants.HttpStatus.BAD_REQUEST,
                "Route0 not applicable to specified course/class");
        }
    }

    private FetchRoute0ContentDao getDao() {
        return dbi.onDemand(FetchRoute0ContentDao.class);
    }
}
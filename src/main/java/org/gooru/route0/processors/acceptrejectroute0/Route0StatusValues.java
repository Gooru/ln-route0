package org.gooru.route0.processors.acceptrejectroute0;

import java.util.Arrays;
import java.util.List;

/**
 * @author ashish.
 */
final class Route0StatusValues {
    private static final String STATUS_ACCEPTED = "accepted";
    private static final String STATUS_REJECTED = "rejected";
    private static final String STATUS_PENDING = "pending";
    private static final String STATUS_NA = "na";
    private static final List<String> VALID_UPDATEABLE_STATUS_VALUES = Arrays.asList(STATUS_ACCEPTED, STATUS_REJECTED);

    static List<String> getValidUpdateableStatusValues() {
        return VALID_UPDATEABLE_STATUS_VALUES;
    }

    static boolean isStatusNotApplicable(String status) {
        return STATUS_NA.equals(status);
    }

    static boolean isStatusPending(String status) {
        return STATUS_PENDING.equals(status);
    }

    static boolean isStatusAccepted(String status) {
        return STATUS_ACCEPTED.equals(status);
    }

    static boolean isStatusRejected(String status) {
        return STATUS_REJECTED.equals(status);
    }

    private Route0StatusValues() {
        throw new AssertionError();
    }
}

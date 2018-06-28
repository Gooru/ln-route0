package org.gooru.route0.infra.data;

import java.util.Arrays;
import java.util.List;

/**
 * @author ashish.
 */
public final class Route0StatusValues {
    private static final String STATUS_ACCEPTED = "accepted";
    private static final String STATUS_REJECTED = "rejected";
    private static final String STATUS_PENDING = "pending";
    private static final String STATUS_NA = "na";
    private static final List<String> VALID_UPDATEABLE_STATUS_VALUES = Arrays.asList(STATUS_ACCEPTED, STATUS_REJECTED);

    public static String getStatusAccepted() {
        return STATUS_ACCEPTED;
    }

    public static String getStatusRejected() {
        return STATUS_REJECTED;
    }

    public static String getStatusPending() {
        return STATUS_PENDING;
    }

    public static String getStatusNa() {
        return STATUS_NA;
    }

    public static List<String> getValidUpdateableStatusValues() {
        return VALID_UPDATEABLE_STATUS_VALUES;
    }

    public static boolean isStatusNotApplicable(String status) {
        return STATUS_NA.equals(status);
    }

    public static boolean isStatusPending(String status) {
        return STATUS_PENDING.equals(status);
    }

    public static boolean isStatusAccepted(String status) {
        return STATUS_ACCEPTED.equals(status);
    }

    public static boolean isStatusRejected(String status) {
        return STATUS_REJECTED.equals(status);
    }

    private Route0StatusValues() {
        throw new AssertionError();
    }
}

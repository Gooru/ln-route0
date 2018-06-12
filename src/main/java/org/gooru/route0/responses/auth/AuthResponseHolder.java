package org.gooru.route0.responses.auth;

public interface AuthResponseHolder {
    boolean isAuthorized();

    boolean isAnonymous();

    String getUser();
}

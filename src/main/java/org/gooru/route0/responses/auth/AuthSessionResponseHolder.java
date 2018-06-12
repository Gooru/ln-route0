package org.gooru.route0.responses.auth;

import io.vertx.core.json.JsonObject;

/**
 * @author ashish.
 */
public interface AuthSessionResponseHolder extends AuthResponseHolder {
    JsonObject getSession();
}

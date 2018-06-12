package org.gooru.route0.responses.transformers;

import org.gooru.route0.infra.exceptions.HttpResponseWrapperException;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * @author ashish.
 */
public final class ResponseTransformerBuilder {

    public static ResponseTransformer build(Message<JsonObject> message) {
        return new HttpResponseTransformer(message);
    }

    public static ResponseTransformer buildHttpResponseWrapperExceptionBuild(HttpResponseWrapperException ex) {
        return new HttpResponseWrapperExceptionTransformer(ex);
    }

    private ResponseTransformerBuilder() {
        throw new AssertionError();
    }
}

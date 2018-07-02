package org.gooru.route0.processors.learnerprofilebaselineprocessor;

import org.gooru.route0.infra.constants.HttpConstants;
import org.gooru.route0.processors.AsyncMessageProcessor;
import org.gooru.route0.responses.MessageResponse;
import org.gooru.route0.responses.MessageResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.json.JsonObject;

/**
 * @author ashish.
 */
public class LearnerProfileBaselineProcessor implements AsyncMessageProcessor {
    private final Vertx vertx;
    private final Message<JsonObject> message;
    private final HttpClient client;
    private final String lpbaselineUri;
    private final Future<MessageResponse> result = Future.future();
    private static final Logger LOGGER = LoggerFactory.getLogger(LearnerProfileBaselineProcessor.class);

    public LearnerProfileBaselineProcessor(Vertx vertx, Message<JsonObject> message, HttpClient client,
        String lpbaselineUri) {

        this.vertx = vertx;
        this.message = message;
        this.client = client;
        this.lpbaselineUri = lpbaselineUri;
    }

    @Override
    public Future<MessageResponse> process() {
        doBaseline();
        return result;
    }

    private void doBaseline() {
        String uri = createUriToCommunicate();
        HttpClientRequest request = client.getAbs(uri, response -> {
            if (response.statusCode() != HttpConstants.HttpStatus.SUCCESS.getCode()) {
                LOGGER.warn("Remote fetch failed, status code: '{}'", response.statusCode());
                result.fail(new Exception("Received non 200 status code from remote"));
            } else {
                LOGGER.debug("Communication with remote successful");
                result.complete(MessageResponseFactory.createNoContentResponse());
            }
        }).exceptionHandler(ex -> {
            LOGGER.warn("Error while communicating with remote server: ", ex);
            result.fail(ex);
        });
        request.putHeader(HttpConstants.HEADER_CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON).end();
    }

    private String createUriToCommunicate() {
        JsonObject requestBody = message.body();
        String userId = requestBody.getString(LearnerProfileBaselinePayloadConstants.USER_ID);
        String courseId = requestBody.getString(LearnerProfileBaselinePayloadConstants.COURSE_ID);
        String classId = requestBody.getString(LearnerProfileBaselinePayloadConstants.CLASS_ID);

        String uri = lpbaselineUri + "?userId=" + userId + "&courseId=" + courseId;
        if (classId != null) {
            uri += "&classId=" + classId;
        }
        // TODO: Verify if we need URL encoding here
        return uri;
    }

}

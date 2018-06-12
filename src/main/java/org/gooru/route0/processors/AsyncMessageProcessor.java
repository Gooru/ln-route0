package org.gooru.route0.processors;

import org.gooru.route0.responses.MessageResponse;

import io.vertx.core.Future;

/**
 * @author ashish.
 */
public interface AsyncMessageProcessor {

    Future<MessageResponse> process();

}

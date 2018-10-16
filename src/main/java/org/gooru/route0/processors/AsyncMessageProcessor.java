package org.gooru.route0.processors;

import io.vertx.core.Future;
import org.gooru.route0.responses.MessageResponse;

/**
 * @author ashish.
 */
public interface AsyncMessageProcessor {

  Future<MessageResponse> process();

}

package org.gooru.route0.infra.exceptions;

import org.gooru.route0.responses.MessageResponse;

/**
 * @author ashish
 */
public class MessageResponseWrapperException extends RuntimeException {

  private final MessageResponse response;

  public MessageResponseWrapperException(MessageResponse response) {
    this.response = response;
  }

  public MessageResponse getMessageResponse() {
    return this.response;
  }
}

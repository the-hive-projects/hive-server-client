package org.thehive.hiveserverclient.net.websocket.message;

public class MessageMarshallingException extends RuntimeException {

    public MessageMarshallingException() {
    }

    public MessageMarshallingException(String message) {
        super(message);
    }

    public MessageMarshallingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageMarshallingException(Throwable cause) {
        super(cause);
    }

    public static MessageMarshallingException wrap(Throwable t) {
        return new MessageMarshallingException(t);
    }

}

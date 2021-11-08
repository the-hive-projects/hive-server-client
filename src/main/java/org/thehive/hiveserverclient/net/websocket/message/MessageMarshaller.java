package org.thehive.hiveserverclient.net.websocket.message;

import org.thehive.hiveserverclient.net.websocket.message.payload.Payload;

public interface MessageMarshaller {

    RawMessage marshall(TypeMessage<? extends Payload> message) throws MessageMarshallingException;

    <T extends Payload> TypeMessage<T> unmarshall(RawMessage message, Class<T> type) throws MessageMarshallingException;

}

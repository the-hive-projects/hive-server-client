package org.thehive.hiveserverclient.net.websocket.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.thehive.hiveserverclient.net.websocket.message.payload.Payload;

@RequiredArgsConstructor
public class MessageMarshallerImpl implements MessageMarshaller {

    private final ObjectMapper objectMapper;

    @Override
    public RawMessage marshall(@NonNull TypeMessage<? extends Payload> message) throws MessageMarshallingException {
        try {
            return new RawMessage(
                    message.getType(),
                    message.getHeaders(),
                    objectMapper.writeValueAsString(message.getPayload()));
        } catch (JsonProcessingException e) {
            throw MessageMarshallingException.wrap(e);
        }
    }

    @Override
    public <T extends Payload> TypeMessage<T> unmarshall(RawMessage message, Class<T> type) throws MessageMarshallingException {
        try {
            if (type != message.getType().payloadType)
                throw new IllegalArgumentException("Message's type and parameter type is not compatible");
            return new TypeMessage<>(
                    message.getType(),
                    message.getHeaders(),
                    objectMapper.readValue(message.getPayload(), type));
        } catch (JsonProcessingException | IllegalArgumentException e) {
            throw MessageMarshallingException.wrap(e);
        }
    }

}

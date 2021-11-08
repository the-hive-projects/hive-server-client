package org.thehive.hiveserverclient.net.websocket.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.thehive.hiveserverclient.net.websocket.message.payload.Chat;
import org.thehive.hiveserverclient.net.websocket.message.payload.Information;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
class MessageMarshallerImplTest {

    ObjectMapper objectMapper;
    MessageMarshallerImpl messageMarshaller;

    @BeforeEach
    void init() {
        this.objectMapper = new ObjectMapper();
        this.messageMarshaller = new MessageMarshallerImpl(objectMapper);
    }

    @DisplayName("Marshall")
    @Test
    void marshall() throws JsonProcessingException {
        var chat = new Chat(System.currentTimeMillis(), "chat-text");
        var typeMessage = new TypeMessage<>(MessageType.CHAT, chat);
        var rawMessage = messageMarshaller.marshall(typeMessage);
        log.info("Type message: {}", typeMessage);
        log.info("Raw message: {}", rawMessage);
        assertEquals(typeMessage.getType(), rawMessage.getType());
        assertEquals(typeMessage.getHeaders(), rawMessage.getHeaders());
        var payload = objectMapper.writeValueAsString(chat);
        assertEquals(payload, rawMessage.getPayload());
    }

    @DisplayName("Unmarshall")
    @Test
    void unmarshall() throws JsonProcessingException {
        var chat = new Chat(System.currentTimeMillis(), "chat-text");
        var payload = objectMapper.writeValueAsString(chat);
        var rawMessage = new RawMessage(MessageType.CHAT, payload);
        var typeMessage = messageMarshaller.unmarshall(rawMessage, Chat.class);
        log.info("Raw message: {}", rawMessage);
        log.info("Type message: {}", typeMessage);
        assertEquals(rawMessage.getType(), typeMessage.getType());
        assertEquals(rawMessage.getHeaders(), typeMessage.getHeaders());
        assertEquals(chat, typeMessage.getPayload());
    }

    @DisplayName("Unmarshall to incorrect type")
    @Test
    void unmarshallToIncorrectType() throws JsonProcessingException {
        var chat = new Chat(System.currentTimeMillis(), "chat-text");
        var payload = objectMapper.writeValueAsString(chat);
        var rawMessage = new RawMessage(MessageType.CHAT, payload);
        log.info("Raw message: {}", rawMessage);
        assertThrows(MessageMarshallingException.class,
                () -> messageMarshaller.unmarshall(rawMessage, Information.class));
    }

}
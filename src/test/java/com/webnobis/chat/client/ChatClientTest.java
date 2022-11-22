package com.webnobis.chat.client;

import com.webnobis.chat.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatClientTest {

    private static final String PATH = "/such/a/nice/path";

    private static final String ID = "the id";

    private ChatClient chatClient;

    @BeforeEach
    void setUp() {
        chatClient = new ChatClient(PATH, ID);
    }

    @Test
    void createChatClientPage() {
        List<Message> messages = LongStream.rangeClosed(1, 100).mapToObj(l -> new Message(l, "a user", "text " + (l * Math.PI))).toList();
        String page = chatClient.createChatClientPage(messages);
        assertTrue(Stream.concat(Stream.of(PATH, ID), messages.stream().map(Message::toString)).allMatch(page::contains));
    }

    @Test
    void createSentClientPage() {
        String message = new Message(Long.MAX_VALUE, "such a user", "text text text").toString();
        String page = chatClient.createSentClientPage(message);
        assertTrue(Stream.of(PATH, message).allMatch(page::contains));
    }
}
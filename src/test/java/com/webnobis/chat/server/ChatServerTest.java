package com.webnobis.chat.server;

import com.webnobis.chat.model.Message;
import com.webnobis.chat.model.Password;
import com.webnobis.chat.test.TestFreePortFinder;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.multipart.MultipartForm;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@ExtendWith(VertxExtension.class)
class ChatServerTest {

    private static final String REGISTER_PATH = "/a/path/to/register/";

    private static final String CHAT_PATH = "/an/other/path/to/chat/";

    private static final String USERNAME = "nicky123";

    private static final String PASSWORD = "k}#!:m.4\"hWdo;rV7_@BLAo*~{N/6,Y|MF\"";

    private static final Password PASSWORD_OBJ = new Password(PASSWORD);

    private static final String TEXT = "a test text";

    private int port;

    private WebClient client;

    @BeforeEach
    void setUp(Vertx vertx, VertxTestContext testContext) throws IOException {
        port = TestFreePortFinder.getFreePort();
        client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(port));
        assumeTrue(port > 0);
        vertx.deployVerticle(new ChatServer(port, REGISTER_PATH, CHAT_PATH)).onSuccess(unused -> vertx.setTimer(500, id -> testContext.completeNow())
        ).onFailure(testContext::failNow);
    }

    @AfterEach
    void tearDown(Vertx vertx, VertxTestContext testContext) {
        client.close();
        Checkpoint close = testContext.checkpoint(2);
        Stream.of(ChatServer.NAMES_KEY, ChatServer.MESSAGES_KEY).forEach(name ->
                vertx.sharedData().getAsyncMap(name).compose(AsyncMap::clear)
                        .onSuccess(unused -> close.flag()).onFailure(testContext::failNow));
    }

    @Test
    void register(Vertx vertx, VertxTestContext testContext) {
        client.get(REGISTER_PATH).basicAuthentication(USERNAME, PASSWORD).send().onSuccess(res -> {
            assertEquals(200, res.statusCode());

            vertx.sharedData().getAsyncMap(ChatServer.NAMES_KEY).compose(map -> map.get(USERNAME)).onSuccess(password -> {
                assertEquals(PASSWORD_OBJ, password);
                testContext.completeNow();
            }).onFailure(testContext::failNow);
        }).onFailure(testContext::failNow);
    }

    @Test
    void sendMessage(Vertx vertx, VertxTestContext testContext) {
        vertx.sharedData().getAsyncMap(ChatServer.NAMES_KEY).compose(map -> map.put(USERNAME, PASSWORD_OBJ))
                .onSuccess(unused -> {
                    MultipartForm form = MultipartForm.create();
                    form.attribute(ChatServer.NEW_MESSAGE_FIELD_ID, TEXT);
                    client.post(CHAT_PATH).basicAuthentication(USERNAME, PASSWORD).sendMultipartForm(form).onSuccess(res -> {
                        assertEquals(200, res.statusCode());
                        String body = res.bodyAsString();
                        assertTrue(body.contains(USERNAME));
                        assertTrue(body.contains(TEXT));

                        vertx.sharedData().<String, List<Message>>getAsyncMap(ChatServer.MESSAGES_KEY).compose(map -> map.get(USERNAME)).onSuccess(list -> {
                            assertEquals(1, list.size());
                            Message message = list.iterator().next();
                            assertNotNull(message);
                            assertEquals(USERNAME, message.name());
                            assertEquals(TEXT, message.text());
                            testContext.completeNow();
                        }).onFailure(testContext::failNow);
                    });
                }).onFailure(testContext::failNow);
    }

    @Test
    void getMessages(Vertx vertx, VertxTestContext testContext) {
        final long timestamp = -42L;
        vertx.sharedData().getAsyncMap(ChatServer.NAMES_KEY).compose(map -> map.put(USERNAME, PASSWORD_OBJ))
                .compose(unused -> vertx.sharedData().getAsyncMap(ChatServer.MESSAGES_KEY))
                .compose(map -> map.put(USERNAME, Collections.singletonList(new Message(timestamp, USERNAME, TEXT))))
                .onSuccess(unused ->
                        client.get(CHAT_PATH).basicAuthentication(USERNAME, PASSWORD).send().onSuccess(res -> {
                            assertEquals(200, res.statusCode());
                            assertTrue(res.bodyAsString().contains(new Message(timestamp, USERNAME, TEXT).toString()));
                            testContext.completeNow();
                        })).onFailure(testContext::failNow);
    }

}
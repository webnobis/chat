package com.webnobis.chat.server;

import com.webnobis.chat.client.ChatClient;
import com.webnobis.chat.model.Message;
import com.webnobis.chat.test.TestEverAuthentication;
import com.webnobis.chat.test.TestFreePortFinder;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TestEverAuthentication.class)
@ExtendWith(VertxExtension.class)
class SendMessageHandlerTest {

    private static final String MESSAGES_KEY = "a nice message key";

    private static final String CHAT_PATH = "/a/path/to/chat";

    private static final String NEW_MESSAGE_FIELD_ID = "new-msg-field-id";

    private static final String MESSAGE = "the chat message to test";

    private WebClient client;

    private Future<HttpServer> serverFuture;

    @BeforeEach
    void setUp(Vertx vertx, AuthenticationHandler everAuthHandler, VertxTestContext testContext) throws IOException {
        int port = TestFreePortFinder.getFreePort();
        client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(port));
        Router router = Router.router(vertx);
        router.post().handler(BodyHandler.create()).handler(everAuthHandler)
                .handler(new SendMessageHandler(vertx, MESSAGES_KEY, new ChatClient(CHAT_PATH, NEW_MESSAGE_FIELD_ID)));
        serverFuture = vertx.createHttpServer(new HttpServerOptions().setHost(WebClientOptions.DEFAULT_DEFAULT_HOST).setPort(port)).requestHandler(router)
                .listen().onSuccess(unused -> testContext.completeNow()).onFailure(testContext::failNow);
    }

    @AfterEach
    void tearDown(Vertx vertx, VertxTestContext testContext) {
        Checkpoint closed = testContext.checkpoint();
        Checkpoint cleared = testContext.checkpoint();
        client.close();
        serverFuture.map(HttpServer::close).onSuccess(unused -> closed.flag()).onFailure(testContext::failNow);
        vertx.sharedData().getAsyncMap(MESSAGES_KEY).compose(map -> map.clear()).onSuccess(unused -> cleared.flag()).onFailure(testContext::failNow);
    }

    @Test
    void handle(Vertx vertx, String username, VertxTestContext testContext) {
        vertx.setTimer(500, id ->
                client.post(CHAT_PATH).sendForm(MultiMap.caseInsensitiveMultiMap().add(NEW_MESSAGE_FIELD_ID, MESSAGE)).onSuccess(res -> {
                    assertEquals(200, res.statusCode());
                    String body = res.bodyAsString();
                    assertTrue(body.contains(username));
                    assertTrue(body.contains(MESSAGE));

                    testContext.completeNow();
                }).onFailure(testContext::failNow)
        );
    }

    @Test
    void handleMap(Vertx vertx, String username, VertxTestContext testContext) {
        vertx.setTimer(500, id ->
                client.post(CHAT_PATH).sendForm(MultiMap.caseInsensitiveMultiMap().add(NEW_MESSAGE_FIELD_ID, MESSAGE)).compose(res -> vertx.sharedData().<String, List<Message>>getAsyncMap(MESSAGES_KEY).compose(map -> map.get(username)))
                        .onSuccess(list -> {
                            assertNotNull(list);
                            assertSame(1, list.size());
                            assertEquals(username + ": " + MESSAGE, list.iterator().next().toString());

                            testContext.completeNow();
                        })
                        .onFailure(testContext::failNow)
        );
    }

    @Test
    void handleFailed(Vertx vertx, VertxTestContext testContext) {
        Checkpoint posted = testContext.checkpoint(2);
        vertx.setTimer(500, id -> {
            client.post(CHAT_PATH).sendForm(MultiMap.caseInsensitiveMultiMap()).onSuccess(res -> {
                assertEquals(500, res.statusCode());

                posted.flag();
            }).onFailure(testContext::failNow);
            client.post(CHAT_PATH).send().onSuccess(res -> {
                assertEquals(500, res.statusCode());

                posted.flag();
            }).onFailure(testContext::failNow);
        });
    }
}
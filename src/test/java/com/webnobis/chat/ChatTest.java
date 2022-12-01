package com.webnobis.chat;

import com.webnobis.chat.model.ConfigCli;
import com.webnobis.chat.test.TestFreePortFinder;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@ExtendWith(VertxExtension.class)
class ChatTest {

    private static WebClient client;

    private static int port;

    @BeforeAll
    static void setUpAll(Vertx vertx) throws IOException {
        port = TestFreePortFinder.getFreePort();
        assumeTrue(port > 0);
        client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(port));
    }

    @AfterAll
    static void tearDownAll() {
        client.close();
    }

    @Test
    void main(Vertx vertx, VertxTestContext testContext) {
        Chat.main(new String[]{"-p", String.valueOf(port)});

        vertx.setTimer(500, id -> {
            client.get(ConfigCli.DEFAULT_REGISTER_PATH).basicAuthentication("x", "x").send()
                    .onSuccess(res -> {
                        assertEquals(200, res.statusCode());
                        testContext.completeNow();
                    }).onFailure(testContext::failNow);
        });
    }
}
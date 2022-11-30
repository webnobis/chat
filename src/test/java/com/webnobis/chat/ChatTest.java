package com.webnobis.chat;

import io.vertx.junit5.VertxExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class ChatTest {

//    private static WebClient client;
//
//    private static int port;
//
//    @BeforeAll
//    static void setUpAll(Vertx vertx) throws IOException {
//        Chat.vertxSupplier = () -> vertx;
//        port = TestFreePortFinder.getFreePort();
//        assumeTrue(port > 0);
//        client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(port));
//    }
//
//    @AfterAll
//    static void tearDownAll() {
//        client.close();
//    }
//
//    @Test
//    void main(Vertx vertx, VertxTestContext testContext) {
//        Chat.main(new String[]{String.valueOf(port)});
//
//        vertx.setTimer(500, id -> {
//            client.get(Chat.REGISTER_PATH).basicAuthentication("x", "x").send()
//                    .onSuccess(res -> {
//                        assertEquals(200, res.statusCode());
//                        testContext.completeNow();
//                    }).onFailure(testContext::failNow);
//        });
//    }
}
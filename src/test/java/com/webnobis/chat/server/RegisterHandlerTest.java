package com.webnobis.chat.server;

import com.webnobis.chat.model.Password;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(VertxExtension.class)
@ExtendWith(MockitoExtension.class)
class RegisterHandlerTest {

    private static final String NAMES_KEY = "key of names";

    private static final String USERNAME = "a test user";

    private static final String PASSWORD = "k}#!:m.4\"hWdo;rV7_@BLAo*~{N/6,Y|MF\"";

    private static final Password PASSWORD_OBJ = new Password(PASSWORD);

    @Mock
    private AsyncMap<String, Password> asyncMap;

    private RegisterHandler registerHandler;

    private UsernamePasswordCredentials credentials;

    @BeforeEach
    void setUp(Vertx vertx) {
        registerHandler = new RegisterHandler(vertx, NAMES_KEY);
        credentials = new UsernamePasswordCredentials(USERNAME, PASSWORD);
    }

    @AfterEach
    void tearDown(Vertx vertx, VertxTestContext testContext) {
        vertx.sharedData().getAsyncMap(NAMES_KEY).compose(AsyncMap::clear).onSuccess(unused -> testContext.completeNow())
                .onFailure(testContext::failNow);
    }

    @Test
    void authenticate(Vertx vertx, VertxTestContext testContext) {
        registerHandler.authenticate(credentials.toJson(), testContext.succeeding(user ->
                vertx.sharedData().getAsyncMap(NAMES_KEY).compose(map -> map.get(user.principal().getString("username")))
                        .onSuccess(password -> {
                            assertEquals(PASSWORD_OBJ, password);
                            testContext.completeNow();
                        }).onFailure(testContext::failNow)));
    }

    @Test
    void authenticateFailed(Vertx vertx, VertxTestContext testContext) {
        vertx.sharedData().getAsyncMap(NAMES_KEY).compose(map -> map.put(USERNAME, new Password(null))).onSuccess(unused ->
                registerHandler.authenticate(credentials.toJson(), testContext.failing(t -> {
                    assertEquals("same name found", t.getMessage());
                    testContext.completeNow();
                })));
    }

    @Test
    void authenticateMap(VertxTestContext testContext) {
        when(asyncMap.putIfAbsent(eq(USERNAME), eq(PASSWORD_OBJ))).thenReturn(Future.succeededFuture(), Future.succeededFuture(new Password(null)));

        Checkpoint authenticate = testContext.checkpoint(2);
        registerHandler.authenticate(credentials, asyncMap).onSuccess(user -> {
            assertNotNull(user);
            assertEquals(User.fromName(USERNAME), user);
            authenticate.flag();
        }).onFailure(testContext::failNow);
        registerHandler.authenticate(credentials, asyncMap)
                .onSuccess(unused -> testContext.failNow("invalid credentials expected")).onFailure(t -> {
                    assertEquals("same name found", t.getMessage());
                    authenticate.flag();
                });
    }
}
package com.webnobis.chat.server;

import com.webnobis.chat.model.Password;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(VertxExtension.class)
@ExtendWith(MockitoExtension.class)
class LoginHandlerTest {

    private static final String NAMES_KEY = "key of names";

    private static final String USERNAME = "a test user";

    private static final String PASSWORD = "k}#!:m.4\"hWdo;rV7_@BLAo*~{N/6,Y|MF\"";

    private static final Password PASSWORD_OBJ = new Password(PASSWORD);

    @Mock
    private AsyncMap<String, Password> asyncMap;

    @Mock
    private RoutingContext ctx;

    @Mock
    private AuthenticationHandler authenticationHandler;

    private LoginHandler loginHandler;

    private UsernamePasswordCredentials credentials;

    @BeforeEach
    void setUp(Vertx vertx) {
        loginHandler = new LoginHandler(vertx, NAMES_KEY);
        credentials = new UsernamePasswordCredentials(USERNAME, PASSWORD);
    }

    @AfterEach
    void tearDown(Vertx vertx, VertxTestContext testContext) {
        vertx.sharedData().getAsyncMap(NAMES_KEY).compose(AsyncMap::clear).onSuccess(unused -> testContext.completeNow())
                .onFailure(testContext::failNow);
    }

    @Test
    void authenticate(Vertx vertx, VertxTestContext testContext) {
        vertx.sharedData().getAsyncMap(NAMES_KEY).compose(map -> map.put(USERNAME, PASSWORD_OBJ)).onSuccess(unused ->
                loginHandler.authenticate(credentials).onSuccess(user -> {
                    assertNotNull(user);
                    assertEquals(User.fromName(USERNAME), user);
                    testContext.completeNow();
                })).onFailure(testContext::failNow);
    }

    @Test
    void authenticateFailed(VertxTestContext testContext) {
        loginHandler.authenticate(credentials.toJson(), testContext.failing(t -> {
            assertEquals("invalid credentials", t.getMessage());
            testContext.completeNow();
        }));
    }

    @Test
    void authenticateMap(VertxTestContext testContext) {
        when(asyncMap.get(eq(USERNAME))).thenReturn(Future.succeededFuture(PASSWORD_OBJ)).thenReturn(Future.succeededFuture());

        Checkpoint authenticate = testContext.checkpoint(2);
        loginHandler.authenticate(credentials, asyncMap).onSuccess(user -> {
            assertNotNull(user);
            assertEquals(User.fromName(USERNAME), user);
            authenticate.flag();
        }).onFailure(testContext::failNow);
        loginHandler.authenticate(credentials, asyncMap)
                .onSuccess(unused -> testContext.failNow("invalid credentials expected")).onFailure(t -> {
                    assertEquals("invalid credentials", t.getMessage());
                    authenticate.flag();
                });
    }

    @Test
    void handle(Vertx vertx) {
        AtomicReference<AuthenticationProvider> selfRef = new AtomicReference<>();
        AuthenticationHandler handler = new LoginHandler(vertx, "unused", self -> {
            selfRef.set(self);
            return authenticationHandler;
        });
        assertEquals(handler, selfRef.get());
        handler.handle(ctx);

        verify(authenticationHandler).handle(eq(ctx));
    }
}
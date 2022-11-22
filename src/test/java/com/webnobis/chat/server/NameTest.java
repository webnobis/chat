package com.webnobis.chat.server;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NameTest {

    private static final Object NAME = "a name";

    @Mock
    private RoutingContext ctx;

    @Mock
    private User user;

    @Test
    void from() {
        when(ctx.user()).thenReturn(user);
        when(user.principal()).thenReturn(new JsonObject().put("username", NAME));

        assertEquals(Optional.of(NAME), Name.from(ctx));
    }
}
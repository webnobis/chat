package com.webnobis.chat.test;

import io.vertx.ext.auth.User;
import io.vertx.ext.web.handler.AuthenticationHandler;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class TestEverAuthentication implements ParameterResolver {

    private static final String EVER_AUTHENTICATED_NAME = "test-authentication-name";

    private static final AuthenticationHandler authenticationHandler = ctx -> {
        ctx.setUser(User.fromName(EVER_AUTHENTICATED_NAME));
        ctx.next();
    };

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Class<?> type = parameterContext.getParameter().getType();
        return AuthenticationHandler.class.isAssignableFrom(type) || String.class.isAssignableFrom(type);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Class<?> type = parameterContext.getParameter().getType();
        if (AuthenticationHandler.class.isAssignableFrom(type)) {
            return authenticationHandler;
        } else if (String.class.isAssignableFrom(type)) {
            return EVER_AUTHENTICATED_NAME;
        }
        throw new IllegalArgumentException(AuthenticationHandler.class.getName().concat(" expected"));
    }
}

package com.webnobis.chat.server;

import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.web.RoutingContext;

import java.util.Optional;

/**
 * Name
 *
 * @author steffen
 */
public interface Name {

    /**
     * Reads the name from the user within the context
     *
     * @param ctx context
     * @return name optional, otherwise empty
     */
    static Optional<String> from(RoutingContext ctx) {
        return Optional.ofNullable(ctx.user()).map(User::principal).map(UsernamePasswordCredentials::new)
                .map(UsernamePasswordCredentials::getUsername);
    }

}

package com.webnobis.chat.server;

import com.webnobis.chat.model.Password;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;

import java.util.Objects;

/**
 * Register handler
 *
 * @author steffen
 */
public class RegisterHandler extends LoginHandler {

    /**
     * Create the Basic Authentication fields using register handler
     *
     * @param vertx    vertx
     * @param namesKey names key of credentials cache
     * @see LoginHandler#LoginHandler(Vertx, String)
     */
    public RegisterHandler(Vertx vertx, String namesKey) {
        super(vertx, namesKey);
    }

    /**
     * Caches the credentials within the map, if username isn't contained
     *
     * @param credentials new credentials
     * @param map         caching credentials map
     * @return user future if success, otherwise the failing future
     * @see RegisterHandler#authenticate(UsernamePasswordCredentials, AsyncMap)
     */
    @Override
    protected Future<User> authenticate(UsernamePasswordCredentials credentials, AsyncMap<String, Password> map) {
        return map.putIfAbsent(credentials.getUsername(), new Password(credentials.getPassword()))
                .map(Objects::isNull)
                .compose(ok -> ok ? Future.succeededFuture(User.fromName(credentials.getUsername())) : Future.failedFuture("same name found"));
    }
}

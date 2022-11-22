package com.webnobis.chat.server;

import com.webnobis.chat.model.Password;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.BasicAuthHandler;

import java.util.Objects;
import java.util.function.Function;

/**
 * Login handler
 *
 * @author steffen
 */
public class LoginHandler implements AuthenticationProvider, AuthenticationHandler {

    private final Vertx vertx;

    private final String namesKey;

    private final AuthenticationHandler authenticationHandler;

    /**
     * Create the Basic Authentication login handler
     *
     * @param vertx    vertx
     * @param namesKey names key of credentials cache
     * @see BasicAuthHandler#create(AuthenticationProvider)
     */
    public LoginHandler(Vertx vertx, String namesKey) {
        this(vertx, namesKey, BasicAuthHandler::create);
    }

    LoginHandler(Vertx vertx, String namesKey, Function<AuthenticationProvider, AuthenticationHandler> authenticationHandlerBuilder) {
        this.vertx = Objects.requireNonNull(vertx);
        this.namesKey = Objects.requireNonNull(namesKey);
        authenticationHandler = authenticationHandlerBuilder.apply(this);
    }

    /**
     * Authenticates the credentials if username is within the map and has the same password
     *
     * @param credentials credentials
     * @param map         registered credentials containing map
     * @return user future if success, otherwise the failing future
     * @see RegisterHandler#authenticate(UsernamePasswordCredentials, AsyncMap)
     */
    protected Future<User> authenticate(UsernamePasswordCredentials credentials, AsyncMap<String, Password> map) {
        return map.get(credentials.getUsername())
                .map(new Password(credentials.getPassword())::equals)
                .compose(ok -> ok ? Future.succeededFuture(User.fromName(credentials.getUsername())) : Future.failedFuture("invalid credentials"));
    }

    /**
     * Authenticates the json credentials
     *
     * @param json    json credentials
     * @param handler user handler
     * @see #authenticate(UsernamePasswordCredentials, AsyncMap)
     */
    @Override
    public void authenticate(JsonObject json, Handler<AsyncResult<User>> handler) {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(json);
        vertx.sharedData().<String, Password>getAsyncMap(namesKey).compose(map -> Objects.requireNonNull(authenticate(credentials, map))).onComplete(handler);
    }

    /**
     * Handles the Basic Authentication login
     *
     * @param ctx context
     * @see BasicAuthHandler#handle(Object)
     * @see #authenticate(UsernamePasswordCredentials, AsyncMap)
     */
    @Override
    public void handle(RoutingContext ctx) {
        authenticationHandler.handle(ctx);
    }
}

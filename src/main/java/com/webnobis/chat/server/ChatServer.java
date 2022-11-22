package com.webnobis.chat.server;

import com.webnobis.chat.client.ChatClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.SessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Chat server<br>
 * 1. GET: Register chat credentials with register path
 * 2. GET: Login with registered credentials and get chat page with chat path
 * 3. POST: chat message within chat page form send
 *
 * @author steffen
 */
public class ChatServer extends AbstractVerticle {

    /**
     * Cache key for chat credentials
     */
    public static final String NAMES_KEY = "Names";

    /**
     * Cache key for messages
     */
    public static final String MESSAGES_KEY = "Messages";

    /**
     * Id of new message form field
     */
    public static final String NEW_MESSAGE_FIELD_ID = "new_message";

    private static final Logger LOG = LoggerFactory.getLogger(ChatServer.class);

    private final int port;

    private final String registerPath;

    private final String chatPath;

    /**
     * Creates the chat server, bind with port and both paths
     *
     * @param port         port
     * @param registerPath register path
     * @param chatPath     chat path
     */
    public ChatServer(int port, String registerPath, String chatPath) {
        this.port = port;
        this.registerPath = Objects.requireNonNull(registerPath);
        this.chatPath = Objects.requireNonNull(chatPath);
    }

    /**
     * Start the chat server
     *
     * @param startPromise start promise
     */
    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.route().handler(SessionHandler.create(SessionStore.create(vertx)));
        router.get(registerPath).handler(new RegisterHandler(vertx, NAMES_KEY)).handler(ctx -> ctx.reroute(chatPath));
        router.route(chatPath).handler(BodyHandler.create(false)).handler(new LoginHandler(vertx, NAMES_KEY));
        ChatClient chatClient = new ChatClient(chatPath, NEW_MESSAGE_FIELD_ID);
        router.post(chatPath).handler(new SendMessageHandler(vertx, MESSAGES_KEY, chatClient));
        router.get(chatPath).handler(new GetMessagesHandler(vertx, MESSAGES_KEY, chatClient));
        vertx.createHttpServer(new HttpServerOptions().setHost(WebClientOptions.DEFAULT_DEFAULT_HOST).setPort(port))
                .requestHandler(router).listen().onSuccess(server ->
                        LOG.info("chat server started at {}:{}", WebClientOptions.DEFAULT_DEFAULT_HOST, server.actualPort()))
                .<Void>mapEmpty().onComplete(startPromise).onFailure(t -> LOG.error("chat server starting failed", t));
    }

}

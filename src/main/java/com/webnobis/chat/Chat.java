package com.webnobis.chat;

import com.webnobis.chat.server.ChatServer;
import io.vertx.core.Vertx;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Http chat with server and client.<br>
 * Each chat user needs to be registered and logged on.<br>
 * Basic Authentication ist used.
 *
 * @author steffen
 */
public class Chat {

    /**
     * Default port
     */
    public static final int DEFAULT_PORT = 8080;

    /**
     * Register path
     */
    public static final String REGISTER_PATH = "/register";

    /**
     * Chat path
     */
    public static final String CHAT_PATH = "/chat";

    static Supplier<Vertx> vertxSupplier = Vertx::vertx;

    /**
     * Starts the chat server with the over-given port as 1st commandline argument, otherwise the default port is used
     *
     * @param args commandline arguments
     */
    public static void main(String[] args) {
        vertxSupplier.get().deployVerticle(new ChatServer(Optional.ofNullable(args).filter(array -> array.length > 0).map(array -> array[0]).map(Integer::valueOf).orElse(DEFAULT_PORT), REGISTER_PATH, CHAT_PATH));
    }
}

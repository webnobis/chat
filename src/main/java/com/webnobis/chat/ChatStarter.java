package com.webnobis.chat;

import com.webnobis.chat.model.ConfigCli;
import com.webnobis.chat.server.ChatServer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Promise;

/**
 * Chat starter.
 *
 * @author steffen
 */
public class ChatStarter extends AbstractVerticle {

    /**
     * Reads the chat config and starts the chat server
     *
     * @param startPromise start promise
     * @see Context#config()
     * @see ChatServer#ChatServer(int, String, String)
     */
    @Override
    public void start(Promise<Void> startPromise) {
        ConfigCli configCli = ConfigCli.fromJson(context.config());
        vertx.deployVerticle(new ChatServer(configCli.getPort(), configCli.getRegisterPath(), configCli.getChatPath()))
                .<Void>mapEmpty().onComplete(startPromise);
    }
}

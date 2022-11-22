package com.webnobis.chat.server;

import com.webnobis.chat.client.ChatClient;
import com.webnobis.chat.model.Message;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import java.util.Comparator;
import java.util.List;

/**
 * Get messages handler
 *
 * @author steffen
 */
public record GetMessagesHandler(Vertx vertx, String messagesKey,
                                 ChatClient chatClient) implements Handler<RoutingContext> {

    /**
     * Reads all cached messages and creates with them the chat page
     *
     * @param ctx context
     * @see #messagesKey()
     * @see ChatClient#createChatClientPage(List)
     */
    @Override
    public void handle(RoutingContext ctx) {
        getMessages().map(chatClient::createChatClientPage)
                .onSuccess(ctx::end).onFailure(t -> ctx.fail(500, t));
    }

    private Future<List<Message>> getMessages() {
        return vertx.sharedData().<String, List<Message>>getAsyncMap(messagesKey).compose(map -> map.values().map(values -> values.stream().flatMap(List::stream).sorted(Comparator.comparingLong(Message::timestamp)).toList()));
    }
}

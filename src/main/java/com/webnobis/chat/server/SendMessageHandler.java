package com.webnobis.chat.server;

import com.webnobis.chat.client.ChatClient;
import com.webnobis.chat.model.Message;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Send message handler
 *
 * @author steffen
 */
public record SendMessageHandler(Vertx vertx, String messagesKey,
                                 ChatClient chatClient) implements Handler<RoutingContext> {

    /**
     * Adds the form field containing message on logged on user messages and creates with the message the sent page
     *
     * @param ctx context
     * @see #messagesKey()
     * @see ChatClient#createSentClientPage(String)
     */
    @Override
    public void handle(RoutingContext ctx) {
        ctx.response().setChunked(true);
        createMessage(ctx).map(this::sendMessage).orElse(Future.failedFuture("missing message or login"))
                .map(chatClient::createSentClientPage).onSuccess(ctx::end).onFailure(t -> ctx.fail(500, t));
    }

    private Optional<Message> createMessage(RoutingContext ctx) {
        return Name.from(ctx).flatMap(name -> Optional.ofNullable(ctx.request().getFormAttribute(chatClient.newMessageFieldId())).map(text -> new Message(System.currentTimeMillis(), name, text)));
    }

    private Future<String> sendMessage(Message msg) {
        return vertx().sharedData().<String, List<Message>>getAsyncMap(messagesKey)
                .compose(map -> map.get(msg.name()).map(list -> Optional.ofNullable(list).orElseGet(CopyOnWriteArrayList::new))
                        .map(list -> {
                            list.add(msg);
                            return map.put(msg.name(), list);
                        })).map(msg.toString().concat(" sent"));
    }
}

module com.webnobis.chat {

    requires java.base;
    requires org.slf4j;
    requires io.vertx.core;
    requires io.vertx.web;
    requires io.vertx.web.client;
    requires io.vertx.auth.common;

    exports com.webnobis.chat;
    exports com.webnobis.chat.model;

}
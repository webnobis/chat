package com.webnobis.chat.model;

import io.vertx.core.cli.annotations.Description;
import io.vertx.core.cli.annotations.Name;
import io.vertx.core.cli.annotations.Option;
import io.vertx.core.cli.annotations.Summary;
import io.vertx.core.json.JsonObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

/**
 * Chat configuration
 *
 * @author steffen
 */
@Name("config")
@Summary("chat configuration")
@Description("chat configuration options")
public class ConfigCli {

    /**
     * Default port
     */
    public static final int DEFAULT_PORT = 8080;

    /**
     * Default register path
     */
    public static final String DEFAULT_REGISTER_PATH = "/register";

    /**
     * Default chat path
     */
    public static final String DEFAULT_CHAT_PATH = "/chat";

    @JsonField
    private int port;

    @JsonField
    private String registerPath;

    @JsonField
    private String chatPath;

    /**
     * Sets the port
     *
     * @param port port
     */
    @Option(shortName = "p", longName = "port")
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Sets the register path
     *
     * @param registerPath register path
     */
    @Option(shortName = "rpath", longName = "register-path")
    public void setRegisterPath(String registerPath) {
        this.registerPath = registerPath;
    }

    /**
     * Sets the chat path
     *
     * @param chatPath chat path
     */
    @Option(shortName = "cpath", longName = "chat-path")
    public void setChatPath(String chatPath) {
        this.chatPath = chatPath;
    }

    /**
     * Gets the port, otherwise the default port
     *
     * @return port
     * @see #DEFAULT_PORT
     */
    public int getPort() {
        return port > 0 ? port : DEFAULT_PORT;
    }

    /**
     * Gets the register path, otherwise the default register path
     *
     * @return register path
     * @see #DEFAULT_REGISTER_PATH
     */
    public String getRegisterPath() {
        return Optional.ofNullable(registerPath).orElse(DEFAULT_REGISTER_PATH);
    }

    /**
     * Gets the chat path, otherwise the default chat path
     *
     * @return chat path
     * @see #DEFAULT_CHAT_PATH
     */
    public String getChatPath() {
        return Optional.ofNullable(chatPath).orElse(DEFAULT_CHAT_PATH);
    }

    /**
     * Transforms the fields to json
     *
     * @return json representation
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        transform(field -> json.put(field.getName(), field.get(this)));
        return json;
    }

    /**
     * Creates the chat configuration from json
     *
     * @param json json
     * @return chat configuration
     */
    public static ConfigCli fromJson(JsonObject json) {
        ConfigCli configCli = new ConfigCli();
        transform(field -> field.set(configCli, json.getValue(field.getName())));
        return configCli;
    }

    private static void transform(Transformer transformer) {
        Arrays.stream(ConfigCli.class.getDeclaredFields()).filter(field -> field.isAnnotationPresent(JsonField.class)).forEach(field -> {
            try {
                transformer.transform(field);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    @Retention(RetentionPolicy.RUNTIME)
    private @interface JsonField {
    }

    @FunctionalInterface
    private interface Transformer {
        void transform(Field field) throws IllegalAccessException;
    }
}

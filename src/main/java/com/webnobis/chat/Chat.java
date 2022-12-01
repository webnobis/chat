package com.webnobis.chat;

import com.webnobis.chat.model.ConfigCli;
import com.webnobis.chat.server.ChatServer;
import io.vertx.core.*;
import io.vertx.core.cli.CLI;
import io.vertx.core.cli.annotations.CLIConfigurator;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;

/**
 * Http chat with server and client.<br>
 * Each chat user needs to be registered and logged on.<br>
 * Basic Authentication ist used.
 *
 * @author steffen
 */
public class Chat extends AbstractVerticle {

    /**
     * Launch the chat with the over-given commandline argument options
     *
     * @param args commandline argument options
     * @see Launcher#dispatch(String[])
     * @see ConfigCli#toJson()
     * @see DeploymentOptions#setConfig(JsonObject)
     */
    public static void main(String[] args) {
        new ChatLauncher().dispatch(args);
    }

    /**
     * Reads the chat config and starts the chat server
     *
     * @param startPromise start promise
     * @see Context#config()
     * @see ConfigCli#fromJson(JsonObject)
     * @see ChatServer#ChatServer(int, String, String)
     */
    @Override
    public void start(Promise<Void> startPromise) {
        ConfigCli configCli = ConfigCli.fromJson(context.config());
        vertx.deployVerticle(new ChatServer(configCli.getPort(), configCli.getRegisterPath(), configCli.getChatPath()))
                .<Void>mapEmpty().onComplete(startPromise);
    }

    /**
     * With chat configuration extended launcher
     *
     * @author steffen
     */
    private static class ChatLauncher extends Launcher {

        private final ConfigCli configCli = new ConfigCli();

        /**
         * Evaluates the argument options of chat configuration and launch the chat
         *
         * @param args argument options
         * @see ConfigCli
         * @see Launcher#dispatch(String[])
         */
        @Override
        public void dispatch(String[] args) {
            CLI cli = CLI.create(ConfigCli.class);
            CLIConfigurator.inject(cli.parse(Arrays.asList(args)), configCli);
            super.dispatch(args);
        }

        /**
         * Gets the Chat class name
         *
         * @return Chat class name
         * @see Class#getName()
         */
        @Override
        protected String getMainVerticle() {
            return Chat.class.getName();
        }

        /**
         * Adds the evaluated chat configuration to the config json
         *
         * @param deploymentOptions deployment options
         * @see DeploymentOptions#setConfig(JsonObject)
         */
        @Override
        public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
            deploymentOptions.setConfig(configCli.toJson().mergeIn(deploymentOptions.getConfig()));
        }
    }

}
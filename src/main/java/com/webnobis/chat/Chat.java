package com.webnobis.chat;

import com.webnobis.chat.model.ConfigCli;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Launcher;
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
public class Chat extends Launcher {

    static Handler<String[]> launchHandler = new Chat()::dispatch;

    private final ConfigCli configCli = new ConfigCli();

    /**
     * Evaluates the argument options of chat configuration and launch the chat starter
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
     * Gets the chat starter name
     *
     * @return chat starter name
     * @see ChatStarter
     */
    @Override
    protected String getMainVerticle() {
        return ChatStarter.class.getName();
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

    /**
     * Launch the chat starter with the over-given commandline argument options
     *
     * @param args commandline argument options
     * @see #dispatch(String[])
     * @see #getMainVerticle()
     */
    public static void main(String[] args) {
        launchHandler.handle(args);
    }

}

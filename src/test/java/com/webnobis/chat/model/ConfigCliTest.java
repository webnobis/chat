package com.webnobis.chat.model;

import io.vertx.core.cli.CLI;
import io.vertx.core.cli.CommandLine;
import io.vertx.core.cli.annotations.CLIConfigurator;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigCliTest {

    private static final int PORT = 42;

    private static final String REGISTER_PATH = "a path";

    private static final String CHAT_PATH = "an other path";

    private static final List<String> CMD_LINE = Arrays.asList("-p", Integer.toString(PORT), "-rpath", REGISTER_PATH, "-cpath", CHAT_PATH);

    private ConfigCli configCli;

    @BeforeEach
    void setUp() {
        configCli = new ConfigCli();
    }

    @Test
    void cli() {
        CLIConfigurator.inject(CommandLine.create(CLI.create(ConfigCli.class)).cli().parse(CMD_LINE), configCli);
        assertEquals(PORT, configCli.getPort());
        assertEquals(REGISTER_PATH, configCli.getRegisterPath());
        assertEquals(CHAT_PATH, configCli.getChatPath());
    }

    @Test
    void port() {
        assertEquals(ConfigCli.DEFAULT_PORT, configCli.getPort());
        configCli.setPort(PORT);
        assertEquals(PORT, configCli.getPort());
    }

    @Test
    void registerPath() {
        assertEquals(ConfigCli.DEFAULT_REGISTER_PATH, configCli.getRegisterPath());
        configCli.setRegisterPath(REGISTER_PATH);
        assertEquals(REGISTER_PATH, configCli.getRegisterPath());
    }

    @Test
    void chatPath() {
        assertEquals(ConfigCli.DEFAULT_CHAT_PATH, configCli.getChatPath());
        configCli.setChatPath(CHAT_PATH);
        assertEquals(CHAT_PATH, configCli.getChatPath());
    }

    @Test
    void toJson() {
        assertEquals(new JsonObject().put("port", 0).put("registerPath", null).put("chatPath", null), configCli.toJson());
    }

    @Test
    void fromJson() {
        ConfigCli cli = ConfigCli.fromJson(new JsonObject().put("port", PORT).put("registerPath", REGISTER_PATH).put("chatPath", CHAT_PATH));
        assertEquals(PORT, cli.getPort());
        assertEquals(REGISTER_PATH, cli.getRegisterPath());
        assertEquals(CHAT_PATH, cli.getChatPath());
    }
}
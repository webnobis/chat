package com.webnobis.chat.test;

import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

public interface TestFreePortFinder {

    static int getFreePort() throws IOException {
        int port;
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            port = serverSocket.getLocalPort();
        }
        assumeTrue(port > 0);
        return port;
    }
}

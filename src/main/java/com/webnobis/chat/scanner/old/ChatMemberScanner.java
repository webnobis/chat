package com.webnobis.chat.scanner.old;

import io.vertx.core.AbstractVerticle;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.stream.IntStream;

public class ChatMemberScanner extends AbstractVerticle {

    private InetAddress self;

    @Override
    public void start() throws Exception {
        self = NetworkInterface.networkInterfaces().filter(networkInterface -> {
            System.out.println(networkInterface);
            return true;
//                    try {
//                        return !networkInterface.isLoopback();
//                    } catch (SocketException e) {
//                        e.printStackTrace();
//                        return false;
//                    }
        }).flatMap(networkInterface -> networkInterface.inetAddresses()).findFirst().orElseThrow();
        scan();
    }

    private void scan() {
        vertx.createSharedWorkerExecutor(ChatMemberScanner.class.getSimpleName(), 1)
                .executeBlocking(promise -> {
                    byte[] ip4 = self.getAddress();
                    promise.complete(IntStream.range(1, 255).mapToObj(i -> check(ip4, (byte) i))
                            .filter(Objects::nonNull).toList());
                }, true)
                .onSuccess(x -> System.out.println(x))
                .onFailure(t -> t.printStackTrace())
                .onComplete(unused1 -> vertx.setTimer(1000, unused2 -> scan()));
    }

    private InetAddress check(byte[] ip4, byte b) {
        ip4[3] = b;
        try {
            InetAddress address = InetAddress.getByAddress(ip4);
            if (address.isReachable(500)) {
                String host = address.toString().substring(1);
                System.out.println(host);
                return address;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

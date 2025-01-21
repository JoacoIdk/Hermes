package me.jdelg.hermes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface Network {
    void start(InetAddress address, int port);
    void stop();
    void disconnect(InetAddress address);
    boolean started();
    void connected(Consumer<InetAddress> consumer);
    void disconnected(Consumer<InetAddress> consumer);
    void receiver(BiConsumer<InetAddress, DataInputStream> consumer);
    void sender(BiConsumer<InetAddress, DataOutputStream> consumer);
}
package me.jdelg.hermes.network;

import lombok.SneakyThrows;
import me.jdelg.hermes.Network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ClientNetwork implements Network {
    private Consumer<InetAddress> connected;
    private Consumer<InetAddress> disconnected;
    private BiConsumer<InetAddress, DataInputStream> receiver;
    private BiConsumer<InetAddress, DataOutputStream> sender;
    private Socket socket;

    @SneakyThrows
    @Override
    public void start(InetAddress address, int port) {
        socket = new Socket(address, port);

        new Thread(this::loop).start();
    }

    public void loop() {
    }

    @SneakyThrows
    @Override
    public void stop() {
        socket.close();
    }

    @Override
    public boolean started() {
        return false;
    }

    @Override
    public void connected(Consumer<InetAddress> consumer) {
    }

    @Override
    public void disconnected(Consumer<InetAddress> consumer) {
    }

    @Override
    public void receiver(BiConsumer<InetAddress, DataInputStream> consumer) {
    }

    @Override
    public void sender(BiConsumer<InetAddress, DataOutputStream> consumer) {
    }
}
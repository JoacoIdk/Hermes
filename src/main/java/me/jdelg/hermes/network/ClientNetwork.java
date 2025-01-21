package me.jdelg.hermes.network;

import lombok.SneakyThrows;
import me.jdelg.hermes.Network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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

    @SneakyThrows
    private void loop() {
        InetAddress address = socket.getInetAddress();

        connected.accept(address);

        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        DataInputStream input = new DataInputStream(inputStream);
        DataOutputStream output = new DataOutputStream(outputStream);

        while (!socket.isClosed()) {
            receiver.accept(address, input);
            sender.accept(address, output);
        }

        input.close();
        output.close();

        disconnected.accept(address);

        if (!socket.isClosed())
            socket.close();
    }

    @SneakyThrows
    @Override
    public void stop() {
        if (!socket.isClosed())
            socket.close();
    }

    @Override
    public void disconnect(InetAddress address) {
        if (!socket.getInetAddress().equals(address))
            return;

        stop();
    }

    @Override
    public boolean started() {
        if (socket == null)
            return false;

        return socket.isConnected();
    }

    @Override
    public void connected(Consumer<InetAddress> consumer) {
        if (connected != null)
            throw new IllegalStateException("Connected is not null");

        this.connected = consumer;
    }

    @Override
    public void disconnected(Consumer<InetAddress> consumer) {
        if (disconnected != null)
            throw new IllegalStateException("Disconnected is not null");

        this.disconnected = consumer;
    }

    @Override
    public void receiver(BiConsumer<InetAddress, DataInputStream> consumer) {
        if (receiver != null)
            throw new IllegalStateException("Receiver is not null");

        this.receiver = consumer;
    }

    @Override
    public void sender(BiConsumer<InetAddress, DataOutputStream> consumer) {
        if (sender != null)
            throw new IllegalStateException("Sender is not null");

        this.sender = consumer;
    }
}
package me.jdelg.hermes.network;

import lombok.SneakyThrows;
import me.jdelg.hermes.Network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ServerNetwork implements Network {
    private Consumer<InetAddress> connected;
    private Consumer<InetAddress> disconnected;
    private BiConsumer<InetAddress, DataInputStream> receiver;
    private BiConsumer<InetAddress, DataOutputStream> sender;
    private ServerSocket serverSocket;
    private Map<InetAddress, Socket> sockets;

    @SneakyThrows
    @Override
    public void start(InetAddress address, int port) {
        serverSocket = new ServerSocket(port, 50, address);
        sockets = new HashMap<>();

        new Thread(this::loop).start();
    }

    @SneakyThrows
    private void loop() {
        if (serverSocket.isClosed())
            return;

        try {
            Socket socket = serverSocket.accept();

            if (socket != null)
                new Thread(() -> accept(socket)).start();
        } catch (SocketException ignored) {}

        loop();
    }

    @SneakyThrows
    private void accept(Socket socket) {
        InetAddress address = socket.getInetAddress();

        connected.accept(address);

        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        DataInputStream input = new DataInputStream(inputStream);
        DataOutputStream output = new DataOutputStream(outputStream);

        sockets.put(address, socket);

        while (!serverSocket.isClosed() && !socket.isClosed()) {
            receiver.accept(address, input);
            sender.accept(address, output);
        }

        sockets.remove(address);

        input.close();
        output.close();

        disconnected.accept(address);

        if (!socket.isClosed())
            socket.close();
    }

    @SneakyThrows
    @Override
    public void stop() {
        serverSocket.close();
    }

    @SneakyThrows
    @Override
    public void disconnect(InetAddress address) {
        Socket socket = sockets.get(address);

        if (socket == null)
            return;

        if (!socket.isClosed())
            socket.close();
    }

    @Override
    public boolean started() {
        if (serverSocket == null)
            return false;
        
        return !serverSocket.isClosed();
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
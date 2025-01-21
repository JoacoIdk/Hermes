package me.jdelg.hermes;

import lombok.Getter;
import lombok.SneakyThrows;
import me.jdelg.hermes.packets.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.util.*;

@Getter
public class Hermes {
    private final InetAddress address;
    private final int port;
    private final Network network;
    private final Map<Integer, Class<? extends Packet>> packets;
    private final List<Receiver> receivers;
    private final Map<InetAddress, Queue<Packet>> queues;
    private final Map<InetAddress, Packet> lastReceive;
    private final Map<InetAddress, Packet> lastSend;

    public Hermes(InetAddress address, int port, Network network) {
        this.address = address;
        this.port = port;
        this.network = network;
        this.packets = new HashMap<>();
        this.receivers = new ArrayList<>();
        this.queues = new HashMap<>();
        this.lastReceive = new HashMap<>();
        this.lastSend = new HashMap<>();

        if (network.started())
            throw new IllegalStateException("Network has already been started");

        registerPacket(0, ConnectionPacket.class);

        network.connected(this::connected);
        network.disconnected(this::disconnected);
        network.receiver(this::receiver);
        network.sender(this::sender);

        network.start(address, port);
    }

    public void registerReceiver(Receiver receiver) {
        receivers.add(receiver);
    }

    public void registerAllPackets() {
        registerPacket(1, IdentifyPacket.class);
        registerPacket(2, AlivePacket.class);
        registerPacket(3, StatusPacket.class);
        registerPacket(4, CommandPacket.class);
        registerPacket(5, MessagePacket.class);
        registerPacket(6, BroadcastPacket.class);
    }

    public void stop() {
        network.stop();
    }

    public void send(InetAddress address, Packet packet) {
        Queue<Packet> queue = queues.computeIfAbsent(address, k -> new ArrayDeque<>());

        queue.add(packet);
    }

    public void send(List<InetAddress> addresses, Packet packet) {
        for (InetAddress address : addresses)
            send(address, packet);
    }

    public void broadcast(Packet packet) {
        for (Queue<Packet> queue : queues.values())
            queue.add(packet);
    }

    public void disconnect(InetAddress address) {
        ConnectionPacket connectionPacket = new ConnectionPacket();

        connectionPacket.connected(false);
        connectionPacket.graceful(true);

        send(address, connectionPacket);

        network.disconnect(address);
    }

    private void triggerReceivers(InetAddress address, Packet packet) {
        for (Receiver receiver : receivers)
            receiver.receivePacket(address, packet);
    }

    private void connected(InetAddress address) {
        queues.put(address, new ArrayDeque<>());

        ConnectionPacket connectionPacket = new ConnectionPacket();

        connectionPacket.connected(true);
        connectionPacket.graceful(true);

        send(address, connectionPacket);
    }

    private void disconnected(InetAddress address) {
        Packet packet = lastReceive.get(address);

        if (packet != null && !(packet instanceof ConnectionPacket)) {
            ConnectionPacket connectionPacket = new ConnectionPacket();

            connectionPacket.connected(false);
            connectionPacket.graceful(false);

            triggerReceivers(address, connectionPacket);
        }

        queues.remove(address);
        lastReceive.remove(address);
        lastSend.remove(address);
    }

    @SneakyThrows
    private void receiver(InetAddress address, DataInputStream input) {
        while (input.available() >= Integer.BYTES) {
            int id = input.readInt();
            Class<? extends Packet> type = getPacket(id);
            Constructor<? extends Packet> constructor = type.getDeclaredConstructor();
            Packet packet = constructor.newInstance();

            packet.read(input);

            if (!lastReceive.containsKey(address) && !(packet instanceof ConnectionPacket)) {
                ConnectionPacket connectionPacket = new ConnectionPacket();

                connectionPacket.connected(true);
                connectionPacket.graceful(false);

                triggerReceivers(address, packet);
            }

            triggerReceivers(address, packet);
        }
    }

    @SneakyThrows
    private void sender(InetAddress address, DataOutputStream output) {
        Queue<Packet> queue = queues.get(address);

        if (queue == null) {
            queues.put(address, new ArrayDeque<>());
            return;
        }

        for (Packet packet = queue.remove(); packet != null; packet = queue.remove()) {
            Class<? extends Packet> type = packet.getClass();
            int id = getPacketId(type);

            if (id == -1)
                throw new IllegalStateException("Tried to send an unknown packet");

            output.writeInt(id);
            packet.write(output);
            output.flush();
        }
    }

    public void registerPacket(int id, Class<? extends Packet> packet) {
        if (packets.containsKey(id))
            throw new IllegalStateException("That ID has already been registered");

        packets.put(id, packet);
    }

    public int getPacketId(Class<? extends Packet> packet) {
        for (Map.Entry<Integer, Class<? extends Packet>> entry : packets.entrySet()) {
            if (entry.getValue().equals(packet))
                return entry.getKey();
        }

        return -1;
    }

    public Class<? extends Packet> getPacket(int id) {
        return packets.get(id);
    }
}

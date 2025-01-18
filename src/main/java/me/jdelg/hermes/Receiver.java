package me.jdelg.hermes;

import java.net.InetAddress;

public interface Receiver {
    void receivePacket(InetAddress address, Packet packet);
}
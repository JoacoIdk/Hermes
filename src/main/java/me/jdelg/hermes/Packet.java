package me.jdelg.hermes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Packet {
    void read(DataInputStream input) throws IOException;
    void write(DataOutputStream output) throws IOException;
}

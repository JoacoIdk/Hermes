package me.jdelg.hermes.packets;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.jdelg.hermes.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
@Setter
@NoArgsConstructor
public class ConnectionPacket implements Packet {
    private boolean connected;

    @Override
    public void read(DataInputStream input) throws IOException {
        this.connected = input.readBoolean();
    }

    @Override
    public void write(DataOutputStream output) throws IOException {
        output.writeBoolean(connected);
    }
}
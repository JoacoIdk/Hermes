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
public class IdentifyPacket implements Packet {
    private String source;
    private String secret;

    @Override
    public void read(DataInputStream input) throws IOException {
        this.source = input.readUTF();
        this.secret = input.readUTF();
    }

    @Override
    public void write(DataOutputStream output) throws IOException {
        output.writeUTF(source);
        output.writeUTF(secret);
    }
}
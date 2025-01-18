package me.jdelg.hermes.packets;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.jdelg.hermes.Packet;
import me.jdelg.hermes.type.Status;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
@Setter
@NoArgsConstructor
public class StatusPacket implements Packet {
    private String source;
    private Status status;

    @Override
    public void read(DataInputStream input) throws IOException {
        this.source = input.readUTF();
        this.status = Status.values()[input.readInt()];
    }

    @Override
    public void write(DataOutputStream output) throws IOException {
        output.writeUTF(source);
        output.writeInt(status.ordinal());
    }
}
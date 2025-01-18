package me.jdelg.hermes.packets;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.jdelg.hermes.Packet;
import me.jdelg.hermes.type.EntityType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
@Setter
@NoArgsConstructor
public class CommandPacket implements Packet {
    private String destination;
    private EntityType entityType;
    private String entityName;
    private String command;

    @Override
    public void read(DataInputStream input) throws IOException {
        this.destination = input.readUTF();
        this.entityType = EntityType.values()[input.readInt()];
        this.entityName = input.readUTF();
        this.command = input.readUTF();
    }

    @Override
    public void write(DataOutputStream output) throws IOException {
        output.writeUTF(destination);
        output.writeInt(entityType.ordinal());
        output.writeUTF(entityName);
        output.writeUTF(command);
    }
}
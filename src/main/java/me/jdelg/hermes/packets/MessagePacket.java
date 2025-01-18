package me.jdelg.hermes.packets;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.jdelg.hermes.Packet;
import me.jdelg.hermes.type.EntityType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
@Setter
@NoArgsConstructor
public class MessagePacket implements Packet {
    private EntityType entityType;
    private String entityName;
    private Component component;

    @Override
    public void read(DataInputStream input) throws IOException {
        this.entityType = EntityType.values()[input.readInt()];
        this.entityName = input.readUTF();
        this.component = GsonComponentSerializer.gson().deserialize(input.readUTF());
    }

    @Override
    public void write(DataOutputStream output) throws IOException {
        output.writeInt(entityType.ordinal());
        output.writeUTF(entityName);
        output.writeUTF(GsonComponentSerializer.gson().serialize(component));
    }
}
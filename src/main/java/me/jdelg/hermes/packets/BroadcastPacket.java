package me.jdelg.hermes.packets;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.jdelg.hermes.Packet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
@Setter
@NoArgsConstructor
public class BroadcastPacket implements Packet {
    private Component component;

    @Override
    public void read(DataInputStream input) throws IOException {
        this.component = GsonComponentSerializer.gson().deserialize(input.readUTF());
    }

    @Override
    public void write(DataOutputStream output) throws IOException {
        output.writeUTF(GsonComponentSerializer.gson().serialize(component));
    }
}
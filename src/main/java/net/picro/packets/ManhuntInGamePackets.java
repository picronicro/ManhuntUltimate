package net.picro.packets;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.picro.Main;

public class ManhuntInGamePackets {

    // packets identifiers
    // on death packet
    public static final Identifier PACKET_DEATH = new Identifier(Main.MOD_ID, "game_death");

    // death packet
    public static void packetDeath(ServerPlayerEntity player, boolean isFinal) {
        ServerPlayNetworking.send(player, PACKET_DEATH,
                PacketByteBufs.create().writeBoolean(isFinal));
    }

}

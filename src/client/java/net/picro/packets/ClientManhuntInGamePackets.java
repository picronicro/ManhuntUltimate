package net.picro.packets;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.picro.Main;

import static net.picro.packets.ManhuntInGamePackets.PACKET_RESPAWN;

public class ClientManhuntInGamePackets {

    // respawn packet
    public static void packetRespawn() {
        ClientPlayNetworking.send(PACKET_RESPAWN, PacketByteBufs.empty());
    }

}

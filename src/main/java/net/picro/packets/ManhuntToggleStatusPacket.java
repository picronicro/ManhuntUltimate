package net.picro.packets;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.picro.Main;

public class ManhuntToggleStatusPacket {

    // packets identifier
    public static final Identifier PACKET_ID = new Identifier(Main.MOD_ID, "custom_packet");

    // send manhunt toggle packet
    public static void packetManhuntToggleStatus(ServerPlayerEntity player, boolean isManhuntActivated) {
        ServerPlayNetworking.send(player, PACKET_ID, PacketByteBufs.create().writeBoolean(isManhuntActivated));
    }

}

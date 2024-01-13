package net.picro.packets;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.picro.Main;
import net.picro.ManhuntManager;

public class ManhuntGamePreparationPackets {

    // packets identifiers
    // assign roles
    public static final Identifier PACKET_ASSIGN_ROLES = new Identifier(Main.MOD_ID, "run_start_timeout");
    // timer
    public static final Identifier PACKET_TIMER_UPDATE = new Identifier(Main.MOD_ID, "timer_update");
    public static final Identifier PACKET_TIMER_END = new Identifier(Main.MOD_ID, "timer_end");

    public static void packetAssignRoles(ServerPlayerEntity player, ManhuntManager.PlayerRole role) {
        ServerPlayNetworking.send(player, PACKET_ASSIGN_ROLES,
                PacketByteBufs.create().writeEnumConstant(role));
    }

    // timer update
    public static void packetUpdateTimer(ServerPlayerEntity player, int time) {
        ServerPlayNetworking.send(player, PACKET_TIMER_UPDATE, PacketByteBufs.create().writeInt(time));
        ServerPlayNetworking.send(player, PACKET_TIMER_UPDATE, PacketByteBufs.create());
    }

    // timer end
    public static void packetEndTimer(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, PACKET_TIMER_END, PacketByteBufs.empty());
    }

}

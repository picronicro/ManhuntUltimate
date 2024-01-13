package net.picro.packets;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.picro.Main;
import net.picro.ManhuntManager;

public class ManhuntInGamePackets {

    // packets identifiers
    // from SERVER packets
    // on death packet
    public static final Identifier PACKET_DEATH = new Identifier(Main.MOD_ID, "game_death");
    // game
    public static final Identifier PACKET_GAME_END = new Identifier(Main.MOD_ID, "game_end");
    // runners pos (compass)
    public static final Identifier PACKET_RUNNER_POS = new Identifier(Main.MOD_ID, "game_runner_pos");

    // from CLIENT packets
    // respawn packet (CLIENT => SERVER)
    public static final Identifier PACKET_RESPAWN = new Identifier(Main.MOD_ID, "game_respawn");

    // just in case, all these methods are sending packets to the client (SERVER => CLIENT)
    // death packet
    public static void packetDeath(ServerPlayerEntity player, boolean isFinal) {
        ServerPlayNetworking.send(player, PACKET_DEATH,
                PacketByteBufs.create().writeBoolean(isFinal));
    }

    // game end
    public static void packetGameEnd(ServerPlayerEntity player, ManhuntManager.PlayerRole winner) {
        ServerPlayNetworking.send(player, PACKET_GAME_END, PacketByteBufs.create().writeEnumConstant(winner));
    }

    // runner pos (playerReceiver is probably hunter) (ALSO COUNTS AS INIT COMPASS)
    public static void packetRunnerPos(ServerPlayerEntity playerReceiver, ServerPlayerEntity runner) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(runner.getName().getString());
        buf.writeBlockPos(runner.getBlockPos());

        ServerPlayNetworking.send(playerReceiver, PACKET_RUNNER_POS, buf);
    }

}
